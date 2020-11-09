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
//        System.out.println(streamURL);
//        // 获取视频源
//        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(streamURL);
//        FFmpegFrameRecorder recorder = null;
//        try {
//            grabber.start();
//            Frame frame = grabber.grabFrame();
//            if (frame != null) {
//                if (frame != null) {
//                    File outFile = new File(filePath);
//                    if (!outFile.isFile()) {
//                        try {
//                            outFile.createNewFile();
//                        } catch (IOException | IOException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                    }
//                System.out.println("\n====================file created============================\n");
//                // 流媒体输出地址，分辨率（长，高），是否录制音频（0:不录制/1:录制）
//                recorder = new FFmpegFrameRecorder(filePath, 1080, 1440, 1);
//                recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);// 直播流格式
////                recorder.setInterleaved(true);
////                // 该参数用于降低延迟
////                 recorder.setVideoOption("tune", "zerolatency");
////                // ultrafast(终极快)提供最少的压缩（低编码器CPU）和最大的视频流大小；
////                // 参考以下命令: ffmpeg -i '' -crf 30 -preset ultrafast
////                recorder.setVideoOption("preset", "ultrafast");
//                recorder.setFormat("flv");// 录制的视频格式
//                recorder.setFrameRate(25);// 帧数
//                //百度翻译的比特率，默认400000，但是我400000贼模糊，调成800000比较合适
//                recorder.setVideoBitrate(800000);
//                recorder.start();
//                while ((isRecord&&frame != null)) {
//                    recorder.record(frame);// 录制
//                    frame = grabber.grabFrame();// 获取下一帧
//                }
//                System.out.println("\n====================stop successful============================\n");
//                recorder.record(frame);
//                // 停止录制
//                recorder.stop();
//                grabber.stop();
//            }
//        } catch (FrameGrabber.Exception e) {
//            e.printStackTrace();
//        } catch (FrameRecorder.Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (null != grabber) {
//                try {
//                    grabber.stop();
//                } catch (FrameGrabber.Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            if (recorder != null) {
//                try {
//                    recorder.stop();
//                } catch (FrameRecorder.Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }
    public static void recode(String rtmpaddr, String location, String name) {
        RecordVideoThread recordVideoThread = null;
        HelloWorldController.recordThreads.add( recordVideoThread = new RecordVideoThread(rtmpaddr,location, name));
        recordVideoThread.start();
    }
}







//public class FFmpegFrameRecorderTest {
//
//    private static FFmpegFrameRecorder setRecorder(String rtmpUrl, int imageWidth, int height) {
//        // 流媒体输出地址，分辨率（长，高），是否录制音频（0:不录制/1:录制）
//        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(rtmpUrl, imageWidth, height, 0);
//        recorder.setInterleaved(true);
//        // 该参数用于降低延迟
//        // recorder.setVideoOption("tune", "zerolatency");
//        // ultrafast(终极快)提供最少的压缩（低编码器CPU）和最大的视频流大小；
//        // 参考以下命令: ffmpeg -i '' -crf 30 -preset ultrafast
//        recorder.setVideoOption("preset", "ultrafast");
//        recorder.setVideoOption("crf", "30");
//        // 视频编码器输出的比特率2000kbps/s
//        recorder.setVideoBitrate(2000000);
//        // H.264编码格式
//        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
//        // 提供输出流封装格式(rtmp协议只支持flv封装格式)
//        recorder.setFormat("flv");
//        // 视频帧率
//        recorder.setFrameRate(30);
//        // 关键帧间隔，一般与帧率相同或者是视频帧率的两倍
//        recorder.setGopSize(60);
//        // 不可变(固定)音频比特率
//        recorder.setAudioOption("crf", "0");
//        // Highest quality
//        recorder.setAudioQuality(0);
//        // 音频比特率 192 Kbps
//        recorder.setAudioBitrate(192000);
//        // 频采样率
//        recorder.setSampleRate(44100);
//        // 双通道(立体声)
//        recorder.setAudioChannels(2);
//        // 音频编/解码器
//        recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
//        return recorder;
//    }
//
//    public static void generatorOutputfile(String inputFile, String outputFile) {
//        {
//            System.out.println("视频解析开始");
//            FFmpegFrameRecorder recorder = null;
//            FFmpegFrameGrabber frameGrabber = null;
//            try {
//
//                // 获取视频并解析视频流
//                frameGrabber = new FFmpegFrameGrabber(inputFile);
////                frameGrabber.setFormat("mp4");
//                frameGrabber.start();
//                // 流媒体输出地址，分辨率（长，高），是否录制音频（0:不录制/1:录制）
//                recorder = setRecorder(outputFile, frameGrabber.getImageWidth(), frameGrabber.getImageHeight());
//                System.out.println("流媒体输出地址:" + outputFile);
//                recorder.start();
//                long startTime = 0, videoTS = 0;
//                Frame frame = null;
//                while ((frame = frameGrabber.grabFrame()) != null) {
//                    if (startTime == 0) {
//                        startTime = System.currentTimeMillis();
//                    }
//                    videoTS = 1000 * (System.currentTimeMillis() - startTime);
//                    recorder.setTimestamp(videoTS);
//                    recorder.record(frame);
//                }
//                recorder.stop();
//                frameGrabber.stop();
//                frameGrabber.release();
//                System.out.println("流媒体输出结束");
//            } catch (Exception e) {
//                System.out.println("parse 解析过程失败" + e);
//                e.printStackTrace();
//            } finally {
//                // 播放结束或server端主动断开时，需要清空内存
//
//                if (frameGrabber != null) {
//                    try {
//                        frameGrabber.stop();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }
//
//
//
//
//    public static void main(String[] args) throws FrameGrabber.Exception {
//
//        generatorOutputfile("E://tmp/1.flv", "E://tmp//123456.flv");
//    }
//
//}


//
//
//    /**
//     * 按帧录制视频
//     *
//     * @param inputFile-该地址可以是网络直播/录播地址，也可以是远程/本地文件路径
//     * @param outputFile
//     *            -该地址只能是文件地址，如果使用该方法推送流媒体服务器会报错，原因是没有设置编码格式
//     * @throws FrameGrabber.Exception
//     * @throws FrameRecorder.Exception
//     * @throws org.bytedeco.javacv.FrameRecorder.Exception
//     */
//    public static void frameRecord(String inputFile, String outputFile, int audioChannel)
//            throws Exception, org.bytedeco.javacv.FrameRecorder.Exception {
//
//        boolean isStart=true;//该变量建议设置为全局控制变量，用于控制录制结束
//        // 获取视频源
//        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
//        // 流媒体输出地址，分辨率（长，高），是否录制音频（0:不录制/1:录制）
//        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, 1280, 720, audioChannel);
//        // 开始取视频源
//        recordByFrame(grabber, recorder, isStart);
//    }
//
//    private static void recordByFrame(FFmpegFrameGrabber grabber, FFmpegFrameRecorder recorder, Boolean status)
//            throws Exception, org.bytedeco.javacv.FrameRecorder.Exception {
//        try {//建议在线程中使用该方法
//            grabber.start();
//            recorder.start();
//            Frame frame = null;
//            while (status&& (frame = grabber.grabFrame()) != null) {
//                recorder.record(frame);
//            }
//            recorder.stop();
//            grabber.stop();
//        } finally {
//            if (grabber != null) {
//                grabber.stop();
//            }
//        }
//    }
//
//    public static void main(String[] args)
//            throws FrameRecorder.Exception, FrameGrabber.Exception, InterruptedException {
//
//        String inputFile = "rtsp://admin:admin@192.168.2.236:37779/cam/realmonitor?channel=1&subtype=0";
//        // Decodes-encodes
//        String outputFile = "recorde.mp4";
//        frameRecord(inputFile, outputFile,1);
//    }