package com.spider.excel;

import org.jsoup.helper.StringUtil;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;


class TestPageProcesser implements PageProcessor {

    public static final Task TASK = Site.me().toTask();


    //http://bj.58.com/zufang/?PGTID=0d300008-0000-1195-ed4a-f22edff56bc9&ClickID=4
    public static final String URL_LIST = "http://sh\\.58\\.com/zufang/(pn\\d+){0,}";
    //public static final String URL_POST = "http://bj\\.58\\.com/zufang/\\w+\\.shtml";

    private Site site = TASK.getSite()
            .me()
            .setDomain("sh.58.com")
            .setSleepTime(1000)
            .setUserAgent("Mozilla/5.0 (compatible; Baiduspider/2.0; +http://www.baidu.com/search/spider.html) ");
//            .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");


    @Override
    public void process(Page page) {

        //列表页
//            System.out.println(page.getHtml().xpath("//ul[@class=\"listUl\"]//li//div[@class='img_list']").links());
            page.addTargetRequests(page.getHtml().xpath("//ul[@class='listUl']//li//div[@class='img_list']").links().all());
            page.addTargetRequests(page.getHtml().links().regex(URL_LIST).all());

            //详情页
             page.putField("url",page.getUrl());
            page.putField("title",  page.getHtml().xpath("//div[@class='main-wrap']//div[@class='house-title']/h1/text()"));
//            page.putField("content",page.getHtml().xpath("//div[@id='articlebody']//div[@class='articalContent']"));
            page.putField("money",  page.getHtml().xpath("//div[@class='house-basic-info']//div[@class='house-pay-way f16']//b[1]/text()")+""+                                    page.getHtml().xpath("//div[@class='house-basic-info']//div[@class='house-pay-way f16']//span[1]/text()"));
            page.putField("addr",   page.getHtml().xpath("//div[@class='house-basic-info']//ul[@class='f14']//li//span[@class='dz']/text()"));
            page.putField("room",   page.getHtml().xpath("//div[@class='house-basic-info']//ul[@class='f14']//li[2]//span[2]/text()"));
//            page.putField("date",   page.getHtml().xpath("//div[@class='listliright']//div[@class='sendTime']/text()"));
            if ("null".equals(page.getResultItems().get("title")+"") || StringUtil.isBlank(page.getResultItems().get("title")+"")) {
                //skip this page
                page.setSkip(true);
            }
    }

    @Override
    public Site getSite() {
          TASK.getSite();
//        List<String[]> poolHosts = new ArrayList<String[]>();
//        poolHosts.add(new String[]{"125.118.175.4","8998"});
//        poolHosts.add(new String[]{"202.109.182.179","8998"});

//        Proxy originProxy1 = new Proxy("127.0.0.1", 1087);
//        Proxy originProxy2 = new Proxy("127.0.0.1", 1088);
//        SimpleProxyProvider proxyProvider = SimpleProxyProvider.from(originProxy1, originProxy2);
//
//        Proxy proxy = proxyProvider.getProxy(TASK);
//        assertThat(proxy).isEqualTo(originProxy1);
//        proxy = proxyProvider.getProxy(TASK);

        //httpProxyList输入是IP+PORT, isUseLastProxy是指重启时是否使用上一次的代理配置
//        site.setHttpProxyPool(poolHosts,false);
        return this.site; //To change body of implemented methods use File | Settings | File Templates.
    }

    public static void main(String[] args) {

        Spider.create(new TestPageProcesser()).addUrl("http://sh.58.com/zufang/")
                .addPipeline(new ConsolePipeline())
                .addPipeline(new ExcelPipeline("D:\\excel\\"))
                .thread(10)
                  .run();

    }
}
