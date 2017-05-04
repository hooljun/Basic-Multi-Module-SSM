package com.spider.sample;

import org.apache.commons.lang.StringUtils;
import us.codecraft.webmagic.Page;

/**
 * @brief 概要描述
 * @details 详细描述
 * @date 2017/4/27 0027
 * @author huliangjun
 */
public class Com58Model {

    //上海
//    public static final String PRE = "sh";
//    public static final String PRE = "bj";
//    public static final String PRE = "sz";
//    public static final String PRE = "nj";
//    public static final String PRE = "hz";
//    public static final String PRE = "wh";
//    public static final String PRE = "zz";
    public static final String PRE = "cs";

    public static final String URL_PAGE = "http://"+PRE+"\\.58\\.com/chuzu/\\w+";
    public static final String URL_QUERY = "http://"+PRE+"\\.58\\.com/\\w+/chuzu/";

    public static final String BRAND_LIST = "http://"+PRE+"\\.58\\.com/pinpaigongyu/\\w+\\.shtml";
    public static final String BRAND_PAGE = "http://"+PRE+"\\.58\\.com/pinpaigongyu/pn/\\w+/";

    public static final String BRO_PAGE = "http://"+PRE+"\\.58\\.com/chuzu/1/\\w+";
    public static final String HOT_PAGE = "http://"+PRE+"\\.58\\.com/chuzu/pn\\w+/?isreal=true";

    public static  String area = null;
    public static  String area2 = null;
    public static  String detail = null;
    /**
     * 58-出租模块
     */
    public void processModelChuzu(Page page){

        page.addTargetRequests(page.getHtml().xpath("//div[@class='main']//div[@class='content']//div[@class='listBox']//ul[@class='listUl']").links().all());
        page.addTargetRequests(page.getHtml().links().regex(URL_PAGE).all());
//        page.addTargetRequests(page.getHtml().links().regex(URL_QUERY).all());

        page.putField("url",page.getUrl());
        page.putField("title",  page.getHtml().xpath("//div[@class='main-wrap']//div[@class='house-title']/h1/text()"));

        page.putField("upTime",  page.getHtml().xpath("/html/body/div[4]/div[1]/p/text()"));

        area = page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/div[1]/div[1]/ul/li[5]/span[2]/a[1]/text()")+" ";
        area2 = page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/div[1]/div[1]/ul/li[5]/span[2]/a[2]/text()")+" ";
        detail = page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/div[1]/div[1]/ul/li[6]/span[2]/text()")+"";
        page.putField("addr",  area + area2 + detail);

        page.putField("mobile",  page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/div[2]/span[1]/em[2]/text()"));
        if(StringUtils.isBlank(page.getResultItems().get("mobile").toString()) || "null".equals(page.getResultItems().get("mobile") )){

            page.putField("mobile",  page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/div[2]/span[1]/em[2]/em/text()"));
        }
        page.putField("name",   page.getHtml().xpath("//div[@id='bigCustomer']/p[1]/a/text()"));
        if(StringUtils.isBlank(page.getResultItems().get("name").toString()) || "null".equals(page.getResultItems().get("name") )){

            page.putField("name",  page.getHtml().xpath("//div[@id='single']/p/a/text()"));
        }
        page.putField("room",   page.getHtml().xpath("//div[@class='house-basic-info']//ul[@class='f14']//li[2]//span[2]/text()"));
        if (StringUtils.isBlank(page.getResultItems().get("title").toString()) ||
                "null".equals(page.getResultItems().get("title") )) {
            //skip this page
            page.setSkip(true);
        }
    }

    /**
     * 58-上海-品牌公寓馆
     */
    public void processModelBrand(Page page){

        page.addTargetRequests(page.getHtml().links().regex(BRAND_LIST).all());
        page.addTargetRequests(page.getHtml().xpath("//div[@class='main']").links().all());
        page.addTargetRequests(page.getHtml().xpath("//div[@class='page']").links().all());
        page.addTargetRequests(page.getHtml().links().regex(BRAND_PAGE).all());

        page.putField("url",page.getUrl());

        page.putField("title",  page.getHtml().xpath("/html/body/div[3]/h2/text()"));
        if (StringUtils.isBlank(page.getResultItems().get("title").toString()) || "null".equals(page.getResultItems().get("title") )) {

//            page.putField("title",  page.getHtml().xpath("/html/body/div[2]/main/div[1]/div/p[1]/text()") +" "+page.getHtml().xpath("html/body/div[2]/main/div[2]/a[3]"));
            page.putField("title",  page.getHtml().xpath("/html/body/div[2]/main/div[1]/div/p[1]/text()"));
        }

        page.putField("mobile",  page.getHtml().xpath("/html/body/div[3]/div[3]/div/text()"));
        if(StringUtils.isBlank(page.getResultItems().get("mobile").toString()) || "null".equals(page.getResultItems().get("mobile") )){

            page.putField("mobile",  page.getHtml().xpath("/html/body/div[2]/main/div[3]/div[1]/div[2]/div[8]/div/text()"));
        }

        page.putField("name",   "房管员");

        page.putField("addr",   page.getHtml().xpath("/html/head/title/text()"));
        //公寓名称
        page.putField("room",   page.getHtml().xpath("//a[@id='gongyulist']/span/text()"));
        page.putField("upTime",   page.getHtml().xpath("/html/body/div[3]/div[2]/span/text()"));

        if(StringUtils.isBlank(page.getResultItems().get("room").toString()) || "null".equals(page.getResultItems().get("room") )){

            page.putField("room",  page.getHtml().xpath("/html/body/div[2]/main/div[1]/div/p[1]/text()"));
        }

        if (StringUtils.isBlank(page.getResultItems().get("title").toString()) || "null".equals(page.getResultItems().get("title") )) {
            //skip this page
            page.setSkip(true);
        }
    }

    /**
     * 58-上海-个人房源
     */
    public void processModelPersonal(Page page){

        page.addTargetRequests(page.getHtml().xpath("//div[@class='main']//div[@class='content']//div[@class='listBox']//ul[@class='listUl']").links().all());
        page.addTargetRequests(page.getHtml().links().regex(BRO_PAGE).all());

        page.putField("url",page.getUrl());
        page.putField("title",  page.getHtml().xpath("/html/body/div[4]/div[1]/h1/text()"));
        page.putField("mobile",  page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/div[2]/span[1]/em[2]/text()"));

        page.putField("upTime",  page.getHtml().xpath("/html/body/div[4]/div[1]/p/text()"));

        area = page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/div[1]/div[1]/ul/li[5]/span[2]/a[1]/text()")+" ";
        area2 = page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/div[1]/div[1]/ul/li[5]/span[2]/a[2]/text()")+" ";
        detail = page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/div[1]/div[1]/ul/li[6]/span[2]/text()")+"";
        page.putField("addr",  area + area2 + detail);

        page.putField("name",   page.getHtml().xpath("//div[@id='bigCustomer']/p[1]/a/text()"));
        if(StringUtils.isBlank(page.getResultItems().get("name").toString()) || "null".equals(page.getResultItems().get("name") )){

            page.putField("name",  page.getHtml().xpath("//div[@id='single']/p/a/text()"));
        }
        page.putField("room",   page.getHtml().xpath("//div[@class='house-basic-info']//ul[@class='f14']//li[2]//span[2]/text()"));
        if (StringUtils.isBlank(page.getResultItems().get("title").toString()) ||
                "null".equals(page.getResultItems().get("title") )) {

            //skip this page
            page.setSkip(true);
        }
    }

    /**
     * 58-上海-经纪人
     */
    public void processModelBroker(Page page){

        page.addTargetRequests(page.getHtml().xpath("/html/body/div[3]/div[1]/div[5]/div[2]/ul").links().all());
        page.addTargetRequests(page.getHtml().links().regex(BRO_PAGE).all());

        page.putField("url",page.getUrl());
        page.putField("title",  page.getHtml().xpath("/html/body/div[4]/div[1]/h1/text()"));
        page.putField("mobile",  page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/div[2]/span[1]/em[2]/text()"));
        page.putField("name",   page.getHtml().xpath("//div[@id='bigCustomer']/p[1]/a/text()"));
        page.putField("room",   page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/div[1]/div[1]/ul/li[2]/span[2]/text()"));

        page.putField("upTime",  page.getHtml().xpath("/html/body/div[4]/div[1]/p/text()"));

        area = page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/div[1]/div[1]/ul/li[5]/span[2]/a[1]/text()")+" ";
        area2 = page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/div[1]/div[1]/ul/li[5]/span[2]/a[2]/text()")+" ";
        detail = page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/div[1]/div[1]/ul/li[6]/span[2]/text()")+"";
        page.putField("addr",  area + area2 + detail);

        if (StringUtils.isBlank(page.getResultItems().get("title").toString()) ||
                "null".equals(page.getResultItems().get("title") )  ||
                page.getResultItems().get("title").toString().endsWith("(个人)")) {

            //skip this page
            page.setSkip(true);
        }
    }

    /**
     * 58-上海-热租房源
     */
    public void processModelHot(Page page){

        page.addTargetRequests(page.getHtml().links().regex(HOT_PAGE).all());
        page.addTargetRequests(page.getHtml().xpath("/html/body/div[3]/div[1]/div[5]/div[2]/ul").links().all());

        page.putField("url",page.getUrl());
        page.putField("title",  page.getHtml().xpath("/html/body/div[4]/div[1]/h1/text()"));
        page.putField("mobile",  page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/div[2]/span[1]/em[2]/text()"));
        page.putField("name",   page.getHtml().xpath("//div[@id='bigCustomer']/p[1]/a/text()"));
        page.putField("room",   page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/div[1]/div[1]/ul/li[2]/span[2]/text()"));

        page.putField("upTime",  page.getHtml().xpath("/html/body/div[4]/div[1]/p/text()"));

        area = page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/div[1]/div[1]/ul/li[5]/span[2]/a[1]/text()")+" ";
        area2 = page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/div[1]/div[1]/ul/li[5]/span[2]/a[2]/text()")+" ";
        detail = page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/div[1]/div[1]/ul/li[6]/span[2]/text()")+"";
        page.putField("addr",  area + area2 + detail);

        if (StringUtils.isBlank(page.getResultItems().get("title").toString()) || "null".equals(page.getResultItems().get("title") )) {
            //skip this page
            page.setSkip(true);
        }
    }
}
