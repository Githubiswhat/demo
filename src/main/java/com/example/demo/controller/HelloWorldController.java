package com.example.demo.controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.server.RecordVideoThread;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
public class HelloWorldController{
    private static int corePoolSize = Runtime.getRuntime().availableProcessors();
//    private static ThreadPoolExecutor executor  = new ThreadPoolExecutor(corePoolSize, corePoolSize+1, 10l, TimeUnit.SECONDS,
//            new LinkedBlockingQueue<Runnable>(1000));
    public static List<RecordVideoThread> recordThreads = new ArrayList<RecordVideoThread>();
    @PostMapping("/start")
    public void start(@RequestParam("name") String name) {
        Iterator<RecordVideoThread> iterator = recordThreads.iterator();
        while(iterator.hasNext()){
            RecordVideoThread thread = iterator.next();
            if(thread.userName.equals(name)){
                return;
            }
        }
        String location = name + System.currentTimeMillis() + ".flv";
        String rtmpaddr = "rtmp://127.0.0.1:1935/live/home";
        RecordVideoThread.recode(rtmpaddr,location,name);
//        RecordVideoThread recordVideoThread = null;
//        HelloWorldController.recordThreads.add( recordVideoThread = new RecordVideoThread(rtmpaddr,location,name));
//        executor.execute(recordVideoThread);
    }

    @PostMapping("/stop")
    public String stop(@RequestParam("name") String name) {
        Iterator<RecordVideoThread> iterator = recordThreads.iterator();
        while(iterator.hasNext()){
            RecordVideoThread thread = iterator.next();
            if(thread.userName.equals(name)){
                thread.setRecord(false);
                System.out.println("====================stop successful============================");
                iterator.remove();  //正确
                return "====================stop successful============================";
            }
        }
        return "not start";
    }
}
