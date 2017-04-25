package com.spider.excel;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.utils.FilePersistentBase;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Write results in console.<br>
 * Usually used in test.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
public class ExcelPipeline extends FilePersistentBase implements Pipeline {

    public static ConcurrentLinkedQueue<RoomInfo>  queue = new ConcurrentLinkedQueue();
    public static AtomicInteger count = new AtomicInteger(0);
    public static AtomicInteger excelLineNum  = new AtomicInteger(0);
    public static Lock lock = new ReentrantLock();
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * new JsonFilePageModelPipeline with default path "/data/webmagic/"
     */
    public ExcelPipeline() {
        setPath("/data/webmagic");
    }

    public ExcelPipeline(String path) {
        setPath(path);
    }
    @Override
    public void process(ResultItems resultItems, Task task) {

//        System.out.println("get page: " + resultItems.getRequest().getUrl());
        RoomInfo roomInfo = new RoomInfo();
        String value = null;
        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
            value = entry.getValue()+"";
            if("title".equals(entry.getKey())){
                roomInfo.setTitle(value);
            }else if("money".equals(entry.getKey())){
                roomInfo.setMoney(value);
            }else if("addr".equals(entry.getKey())){
                roomInfo.setAddr(value);
            }else if("room".equals(entry.getKey())){
                roomInfo.setRoom(value);
            }else if("url".equals(entry.getKey())){
                roomInfo.setUrl(value);
            }
        }
        if(roomInfo != null){
            this.add2QueueAndExport(roomInfo);

        }
    }

    public void add2QueueAndExport(RoomInfo roomInfo){
        queue.offer(roomInfo);
        count.incrementAndGet();
        lock.lock();
        try{

            if(count.get()%10 == 0){
                // 测试学生
                ExportExcel<RoomInfo> ex = new ExportExcel<RoomInfo>();
                String[] headers = null;
                List<RoomInfo> dataset = new ArrayList<RoomInfo>();
                while (!queue.isEmpty()) {
                    dataset.add(queue.poll());
                }
                try {

                    File file = new File(path + "export.xls");
                    if(!file.exists()){//追加
                        headers = new String[]{ "标题", "价格", "地址", "房型", "URL" };
                    }
                    OutputStream out = new FileOutputStream(file,true);
                    ex.exportExcel(headers, dataset, out,file ,excelLineNum.get());
                    excelLineNum.addAndGet(11);
                    out.close();
                    System.out.println("excel导出成功！");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }finally {
            lock.unlock();
        }
    }
}
