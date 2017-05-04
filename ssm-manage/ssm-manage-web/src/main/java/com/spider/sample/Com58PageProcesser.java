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
    //各模块处理单元
    private Com58Model modelProcess = new Com58Model();

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
//            .setUserAgent("Mozilla/5.0 (compatible; Baiduspider/2.0; +http://www.baidu.com/search/spider.html) ")
            ;

    public void changeUserAgent(Site site) {
        String randomUserAgent = HeaderBuilder.randomUserAgent();
        site.setUserAgent(randomUserAgent);
    }

    @Override
    public void process(Page page) {

        this.changeUserAgent(site);
        logger.info("site info:{}", site.getUserAgent());

        //XX出租
        modelProcess.processModelChuzu(page);
        //公寓
//        modelProcess.processModelBrand(page);
        //个人
//        modelProcess.processModelPersonal(page);
        //经纪人
//        modelProcess.processModelBroker(page);
        //热租房源
//        modelProcess.processModelHot(page);

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

        ExcelPipeline.exlName = "长沙出租";

        Spider.create(new Com58PageProcesser())
//                .setDownloader(new ClientDownloader())
                .setDownloader(new MogoroomDownloader())
//
//                /*.addUrl("http://hz.58.com/chuzu/")*/.addUrl("http://hz.58.com/pinpaigongyu/")//品牌公寓 //杭州

//                .addUrl("http://sh.58.com/chuzu/")//上海租房
//                .addUrl("http://sh.58.com/pinpaigongyu/")//品牌公寓
//                .addUrl("http://sh.58.com/chuzu/0/")//个人房源
//                .addUrl("http://sh.58.com/chuzu/1/")//经纪人
//                .addUrl("http://sh.58.com/chuzu/?isreal=true")//热租房源

                .addUrl("http://cs.58.com/chuzu/")
//                .addUrl("http://sz.58.com/chuzu/")
                .addPipeline(new ExcelPipeline("D:\\excel\\58\\changsha"))
                .addPipeline(new ConsolePipeline())
                .thread(10)
                .run();
    }

}
