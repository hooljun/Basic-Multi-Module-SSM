package com.youmeek.ssm.manage.controller;

import com.spider.excel.ExcelPipeline;
import com.spider.proxy.ClientDownloader;
import com.spider.proxy.MogoroomDownloader;
import com.spider.sample.Com58PageProcesser;
import com.spider.sample.SinaBlogProcessor;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.youmeek.ssm.manage.pojo.SysUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;

/**
 * @brief 概要描述
 * @details 详细描述
 * @date 2017/4/25 0025
 * @author huliangjun
 */
@Controller
@RequestMapping("/webmagicController")
public class WebmagicController {

    /**
     * 启动webmagic
     * @return
     */
    @RequestMapping(value="", method = RequestMethod.GET)
    @ResponseBody
    public String startWebmagic() throws Exception{

        Spider.create(new Com58PageProcesser())
                .setDownloader(new ClientDownloader())
//                .setDownloader(new MogoroomDownloader())
//                .addUrl("http://sh.58.com/chuzu/")
//                .addUrl("http://bj.58.com/chuzu/")
                .addUrl("http://sz.58.com/chuzu/")
//                .addPipeline(new ExcelPipeline("D:\\excel\\"))
                .addPipeline(new ConsolePipeline())
                .thread(10)
                .runAsync();

        System.out.println("webmagic started");
        return "webmagic started!";
    }
}
