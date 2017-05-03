package com.spider.proxy;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.*;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.utils.UrlUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
/**
 * @brief 概要描述
 * @details 详细描述
 * @date 2017/4/21 0021
 * @author huliangjun
 */
@ThreadSafe
public class MogoroomDownloader  implements Downloader{

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    // 代理服务器
    final static String  proxyHost = "proxy.abuyun.com";
    final static Integer proxyPort = 9020;

    private HttpClientGenerator httpClientGenerator = new HttpClientGenerator();

    private final Map<String, CloseableHttpClient> httpClients = new HashMap<String, CloseableHttpClient>();

    @Override
    public void setThread(int threadNum) {}

    @Override
    public Page download(Request request, Task task) {
        Site site = task.getSite();
        Set<Integer> acceptStatCode = site.getAcceptStatCode();
        String charset = site.getCharset();
        CloseableHttpResponse httpResponse = null;
        int statusCode = 0;
        this.logger.info("downloading page {}", request.getUrl());
        try {
            HttpHost target = new HttpHost(proxyHost, proxyPort, "http");
            site.setHttpProxy(target);
            request.putExtra(Request.PROXY, target);

            HttpClientContext localContext = HttpClientContext.create();
            AuthCache authCache = new BasicAuthCache();
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(site.getHttpProxy(), basicAuth);
            localContext.setAuthCache(authCache);

            String url = request.getUrl();
            try{
                request.setUrl(url.replace("|",""));
            }catch (Exception ex){}
            HttpGet httpGet = new HttpGet(request.getUrl());
            RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
                    .setConnectionRequestTimeout(site.getTimeOut()).setSocketTimeout(site.getTimeOut())
                    .setConnectTimeout(site.getTimeOut()).setCookieSpec(CookieSpecs.IGNORE_COOKIES);
            httpGet.setConfig(requestConfigBuilder.build());

            httpResponse = getHttpClient(site).execute(target, httpGet, localContext);

            statusCode = httpResponse.getStatusLine().getStatusCode();
            request.putExtra(Request.STATUS_CODE, statusCode);

            if (statusAccept(acceptStatCode, statusCode)) {
                Page page = handleResponse(request, charset, httpResponse, task);
                onSuccess(request);
                return page;
            } else {
                return null;
            }
        } catch (IOException e) {
            if (site.getCycleRetryTimes() > 0) {
                return addToCycleRetry(request, site);
            }
            onError(request);
            return null;
        } finally {
            request.putExtra(Request.STATUS_CODE, statusCode);
            try {
                if (httpResponse != null) {
                    EntityUtils.consume(httpResponse.getEntity());
                }
            }
            catch (IOException e) {
            }
        }
    }

    private CloseableHttpClient getHttpClient(Site site) {
        String domain = site.getDomain();
        CloseableHttpClient httpClient = httpClients.get(domain);

        if (httpClient == null) {
            synchronized (this) {
                httpClient = httpClients.get(domain);
                if (httpClient == null) {
                    httpClient = httpClientGenerator.getClient(site);
                    httpClients.put(domain, httpClient);
                }
            }
        }

        return httpClient;
    }

    protected boolean statusAccept(Set<Integer> acceptStatCode, int statusCode) {
        return acceptStatCode.contains(statusCode);
    }

    protected Page handleResponse(Request request, String charset, HttpResponse httpResponse, Task task)
            throws IOException {
        String content = getContent(charset, httpResponse);

        Page page = new Page();
        page.setRawText(content);
        page.setUrl(new PlainText(request.getUrl()));
        page.setRequest(request);
        page.setStatusCode(httpResponse.getStatusLine().getStatusCode());

        return page;
    }

    protected String getContent(String charset, HttpResponse httpResponse) throws IOException {
        if (charset == null) {
            byte[] contentBytes = IOUtils.toByteArray(httpResponse.getEntity().getContent());
            String htmlCharset = getHtmlCharset(httpResponse, contentBytes);

            if (htmlCharset != null) {
                return new String(contentBytes, htmlCharset);
            } else {
                return new String(contentBytes);
            }
        } else {
            return IOUtils.toString(httpResponse.getEntity().getContent(), charset);
        }
    }

    protected String getHtmlCharset(HttpResponse httpResponse, byte[] contentBytes) throws IOException {
        String charset = null;
        String value = httpResponse.getEntity().getContentType().getValue();
        charset = UrlUtils.getCharset(value);
        if (StringUtils.isNotBlank(charset)) {
            return charset;
        }

        Charset defaultCharset = Charset.defaultCharset();
        String content = new String(contentBytes, defaultCharset.name());
        if (StringUtils.isNotEmpty(content)) {
            Document document = Jsoup.parse(content);
            Elements links = document.select("meta");

            for (Element link : links) {
                String metaContent = link.attr("content");
                String metaCharset = link.attr("charset");

                if (metaContent.indexOf("charset") != -1) {
                    metaContent = metaContent.substring(metaContent.indexOf("charset"), metaContent.length());
                    charset = metaContent.split("=")[1];
                    break;
                } else if (StringUtils.isNotEmpty(metaCharset)) {
                    charset = metaCharset;
                    break;
                }
            }
        }

        return charset;
    }

    protected void onSuccess(Request request) {}

    protected void onError(Request request) {}

    protected Page addToCycleRetry(Request request, Site site) {
        Page page = new Page();
        Object cycleTriedTimesObject = request.getExtra(Request.CYCLE_TRIED_TIMES);

        if (cycleTriedTimesObject == null) {
            page.addTargetRequest(request.setPriority(0).putExtra(Request.CYCLE_TRIED_TIMES, 1));
        } else {
            int cycleTriedTimes = (Integer) cycleTriedTimesObject;
            cycleTriedTimes++;
            if (cycleTriedTimes >= site.getCycleRetryTimes()) {
                return null;
            }
            page.addTargetRequest(request.setPriority(0).putExtra(Request.CYCLE_TRIED_TIMES, cycleTriedTimes));
        }

        page.setNeedCycleRetry(true);

        return page;
    }
}

class HttpClientGenerator
{
    private PoolingHttpClientConnectionManager connectionManager;

    // 代理隧道验证信息
//    final static String proxyUser = "H01234567890123P";
//    final static String proxyPass = "0123456789012345";
    final static String proxyUser = "H9G503FUS705073D";
    final static String proxyPass = "53553B003311BE7A";

    public HttpClientGenerator() {
        Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", SSLConnectionSocketFactory.getSocketFactory()).build();
        connectionManager = new PoolingHttpClientConnectionManager(reg);
        connectionManager.setDefaultMaxPerRoute(100);
    }

    public HttpClientGenerator setPoolSize(int poolSize) {
        connectionManager.setMaxTotal(poolSize);
        return this;
    }

    public CloseableHttpClient getClient(Site site) {
        return generateClient(site);
    }

    private CloseableHttpClient generateClient(Site site) {
        List<Header> list = new ArrayList<Header>();
        BasicHeader header = new BasicHeader("Proxy-Switch-Ip", "yes");
        list.add(header);
        if (site.getHeaders() != null) {
            for (Map.Entry<String, String> headerEntry : site.getHeaders().entrySet()) {
                list.add(new BasicHeader(headerEntry.getKey(), headerEntry.getValue()));
            }
        }

        HttpHost target = site.getHttpProxy();
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(target.getHostName(), target.getPort()),
                new UsernamePasswordCredentials(proxyUser, proxyPass));

        HttpClientBuilder httpClientBuilder = HttpClients.custom().setConnectionManager(connectionManager)
                .setDefaultCredentialsProvider(credsProvider).setDefaultHeaders(list);

        if (site != null && site.getUserAgent() != null) {
            httpClientBuilder.setUserAgent(site.getUserAgent());
        } else {
            httpClientBuilder.setUserAgent("");
        }

        if (site == null || site.isUseGzip()) {
            httpClientBuilder.addInterceptorFirst(new HttpRequestInterceptor() {
                public void process(final HttpRequest request, final HttpContext context)
                        throws HttpException, IOException {
                    if (!request.containsHeader("Accept-Encoding")) {
                        request.addHeader("Accept-Encoding", "gzip");
                    }
                }
            });
        }

        SocketConfig socketConfig = SocketConfig.custom().setSoKeepAlive(true).setTcpNoDelay(true).build();
        httpClientBuilder.setDefaultSocketConfig(socketConfig);

        if (site != null) {
            httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(site.getRetryTimes(), true));
        }

        generateCookie(httpClientBuilder, site);

        return httpClientBuilder.build();
    }

    private void generateCookie(HttpClientBuilder httpClientBuilder, Site site) {
        CookieStore cookieStore = new BasicCookieStore();

        for (Map.Entry<String, String> cookieEntry : site.getCookies().entrySet()) {
            BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
            cookie.setDomain(site.getDomain());
            cookieStore.addCookie(cookie);
        }

        for (Map.Entry<String, Map<String, String>> domainEntry : site.getAllCookies().entrySet()) {
            for (Map.Entry<String, String> cookieEntry : domainEntry.getValue().entrySet()) {
                BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
                cookie.setDomain(domainEntry.getKey());
                cookieStore.addCookie(cookie);
            }
        }

        httpClientBuilder.setDefaultCookieStore(cookieStore);
    }
}
