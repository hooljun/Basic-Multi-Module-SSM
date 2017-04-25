package com.spider.proxy;

import com.spider.sample.Com58PageProcesser;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;


/**
 * @brief 概要描述
 * @details 详细描述
 * @date 2017/4/21 0021
 * @author huliangjun
 */
public class WebMagicProxyDemo {

    public static void main(String[] args) {
        Spider.create(new PageProcessor()
            {
                @Override
                public void process(Page page) {
                    System.out.println(page.getHtml());
                }

                @Override
                public Site getSite() {
                    return Site.me();
                }
            }
        )
                .setDownloader(new MogoroomDownloader()).addUrl("http://bj.58.com/zufang/").thread(1).run();
    }
}
