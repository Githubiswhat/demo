package com.example.demo.server;
import com.example.demo.controller.HelloWorldController;
import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;


public class RecordVideoThread extends Thread {

    public String streamURL;// 流地址
    public String filePath;// 文件路径
    public String userName;
    public boolean isRecord = true;//该变量建议设置为全局控制变量，用于控制录制结束

    public void setStreamURL(String streamURL) {
        this.streamURL = streamURL;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public void setRecord(boolean isRecord){
        this.isRecord = isRecord;
    }

    public RecordVideoThread(String streamURL, String filePath, String userName) {
        this.streamURL = streamURL;
        this.filePath = filePath;
        this.userName = userName;
    }


    @Override
    public void run() {
        System.out.println(streamURL);
        // 获取视频源
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(streamURL);
        FFmpegFrameRecorder recorder = null;
        try {
            grabber.start();
            Frame frame = grabber.grabFrame();
            if (frame != null) {
                System.out.println("==========   file created  ==============");
                // 流媒体输出地址，分辨率（长，高），是否录制音频（0:不录制/1:录制）
                recorder = new FFmpegFrameRecorder(filePath, 1080, 1440, 1);
                recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);// 直播流格式
                recorder.setFormat("flv");// 录制的视频格式
                recorder.setFrameRate(25);// 帧数
                //百度翻译的比特率，默认400000，但是我400000贼模糊，调成800000比较合适
                recorder.setVideoBitrate(800000);
                recorder.start();
                while (isRecord &&(frame != null)) {
                    recorder.record(frame);// 录制
                    frame = grabber.grabFrame();// 获取下一帧
                }
                recorder.record(frame);
                // 停止录制
                recorder.stop();
                grabber.stop();
                System.out.println("==========    stop here  hope execute");
            }
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        } catch (FrameRecorder.Exception e) {
            e.printStackTrace();
        } finally {
            if (null != grabber) {
                try {
                    grabber.stop();
                } catch (FrameGrabber.Exception e) {
                    e.printStackTrace();
                }
            }
            if (recorder != null) {
                try {
                    recorder.stop();
                } catch (FrameRecorder.Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
    public static void recode(String rtmpaddr, String location, String name) {
        RecordVideoThread recordVideoThread = null;
        HelloWorldController.recordThreads.add( recordVideoThread = new RecordVideoThread(rtmpaddr,location, name));
        recordVideoThread.start();
    }
}







