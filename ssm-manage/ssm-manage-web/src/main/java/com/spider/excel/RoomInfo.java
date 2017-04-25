package com.spider.excel;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.ConsolePageModelPipeline;
import us.codecraft.webmagic.model.OOSpider;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.HelpUrl;
import us.codecraft.webmagic.model.annotation.TargetUrl;

/**
 * Room
 *
 * @author calwen 23:48
 * @create 2017-04-22 23:48
 */
@TargetUrl("http://bj.58.com/zufang/")
@HelpUrl("http://bj.58.com/zufang/")
//@ExtractBy("")
public class RoomInfo {

    @ExtractBy("//div[@class='main-wrap']//div[@class='house-title']/h1/text()")
    private String title ;
    @ExtractBy("//div[@class='house-basic-info']//div[@class='house-pay-way f16']//b[1]/text()")
    private String money ;
    @ExtractBy("//div[@class='house-basic-info']//ul[@class='f14']//li//span[@class='dz']/text()")
    private String addr;
    @ExtractBy("//div[@class='house-basic-info']//ul[@class='f14']//li[2]//span[2]/text()")
    private String room;
    private String url;

    public RoomInfo() {
    }

    public RoomInfo(String title, String money, String addr, String room, String url) {
        this.title = title;
        this.money = money;
        this.addr = addr;
        this.room = room;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static void main(String[] args) {

        OOSpider.create(Site.me(), new ConsolePageModelPipeline(), RoomInfo.class).addUrl("http://bj.58.com/zufang/").thread(4).run();
    }
}
