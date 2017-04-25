package com.spider.sample;

import com.spider.excel.ExcelPipeline;
import com.spider.proxy.ClientDownloader;
import com.spider.proxy.MogoroomDownloader;
import com.virjar.dungproxy.client.httpclient.HeaderBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.SimpleProxyPool;

import java.util.ArrayList;
import java.util.List;


public class Com58PageProcesser implements PageProcessor {

    //添加日志
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final Task TASK = Site.me().toTask();

    public static final String URL_PAGE = "http://sz\\.58\\.com/chuzu/\\w+/";
    public static final String URL_QUERY = "http://sz\\.58\\.com/\\w+/chuzu/";


//    private Site site = TASK.getSite()
//            .me()
//            .setDomain("bj.58.com")
//            .setSleepTime(1000)
//            .setUserAgent("Mozilla/5.0 (compatible; Baiduspider/2.0; +http://www.baidu.com/search/spider.html) ");
////            .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");


    private Site site = Site
            .me()
            .addHeader("Accept-Charset", "utf-8")
            .addHeader("Accept-Encoding", "gzip")
            .addHeader("Accept-Language", "zh-CN,zh")
            .setRetryTimes(3)
            .setSleepTime(1000)
            .setTimeOut(30000)
            .setCycleRetryTimes(3)
//            .setDomain("bj.58.com")
            .setCharset("utf-8")
//            .setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.160 Safari/537.22");
            .setUserAgent("Mozilla/5.0 (compatible; Baiduspider/2.0; +http://www.baidu.com/search/spider.html) ");

    public void changeUserAgent(Site site) {
        String randomUserAgent = HeaderBuilder.randomUserAgent();
        site.setUserAgent(randomUserAgent);
    }

    @Override
    public void process(Page page) {

//        this.changeUserAgent(site);
        logger.info("site info:{}", site.getUserAgent());
        System.out.println("=======CURR======="+page.getUrl());
//        page.addTargetRequests(page.getHtml().xpath("//div[@class='main']//div[@class='content']//div[@class='listTitle']").links().all());
        page.addTargetRequests(page.getHtml().xpath("//div[@class='main']//div[@class='content']//div[@class='listBox']//ul[@class='listUl']").links().all());
        page.addTargetRequests(page.getHtml().links().regex(URL_PAGE).all());
//        page.addTargetRequests(page.getHtml().links().regex(URL_QUERY).all());

        page.putField("url",page.getUrl());
        page.putField("title",  page.getHtml().xpath("//div[@class='main-wrap']//div[@class='house-title']/h1/text()"));
//        page.putField("content",page.getHtml().xpath("//div[@id='articlebody']//div[@class='articalContent']"));
        page.putField("money",  page.getHtml().xpath("//div[@class='house-basic-info']//div[@class='house-pay-way f16']//b[1]/text()")+""+                                    page.getHtml().xpath("//div[@class='house-basic-info']//div[@class='house-pay-way f16']//span[1]/text()"));
        page.putField("addr",   page.getHtml().xpath("//div[@class='house-basic-info']//ul[@class='f14']//li//span[@class='dz']/text()"));
        page.putField("room",   page.getHtml().xpath("//div[@class='house-basic-info']//ul[@class='f14']//li[2]//span[2]/text()"));
        if (StringUtils.isBlank(page.getResultItems().get("title").toString()) || "null".equals(page.getResultItems().get("title") )) {
            //skip this page
            page.setSkip(true);
        }
    }

    @Override
    public Site getSite() {
//        TASK.getSite();

//        List<String[]> poolHosts = new ArrayList<String[]>();
//        poolHosts.add(new String[]{"","","110.88.46.160","8118"});
//        poolHosts.add(new String[]{"","","61.186.164.98","8080"});
//        poolHosts.add(new String[]{"","","182.42.36.71","808"});
//        poolHosts.add(new String[]{"","","171.38.188.249","8123"});
//        poolHosts.add(new String[]{"","","183.153.12.208","808"});
//        poolHosts.add(new String[]{"","","61.191.173.31","808"});
//        poolHosts.add(new String[]{"","","222.85.39.36","808"});
//        poolHosts.add(new String[]{"","","175.155.141.178","808"});
//        poolHosts.add(new String[]{"","","114.231.157.63","808"});
//        poolHosts.add(new String[]{"","","218.22.219.133","808"});
//        poolHosts.add(new String[]{"","","221.229.46.7","808"});
//        poolHosts.add(new String[]{"","","114.230.122.84","41487"});
//        poolHosts.add(new String[]{"","","223.245.198.64","43763"});
//        poolHosts.add(new String[]{"","","175.155.25.54","808"});
//        poolHosts.add(new String[]{"","","116.208.106.85","8118"});
//        SimpleProxyPool spp =  new SimpleProxyPool(poolHosts);
//        site.setHttpProxyPool(spp);

//        site.setHttpProxyPool(poolHosts,false);

//        site.setHttpProxy(new HttpHost("175.155.25.54",808));
//        System.out.println("=========getSite==========");
        return this.site;
    }

    public static void main(String[] args) {

        Spider.create(new Com58PageProcesser())
                .setDownloader(new ClientDownloader())
//                .setDownloader(new MogoroomDownloader())
//                .addUrl("http://sh.58.com/chuzu/")
//                .addUrl("http://bj.58.com/chuzu/")
                .addUrl("http://sz.58.com/chuzu/")
//                .addPipeline(new ExcelPipeline("D:\\excel\\"))
                .addPipeline(new ConsolePipeline())
                .thread(10)
                .run();
    }
}
