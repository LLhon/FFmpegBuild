package com.llhon.ffmpeg;

/**
 * author  : LLhon date    : 2024/3/27 16:37. des     :
 */
public class FFmpegCmd {

    static {
        //这几个包已经合并成了一个单独的 ffmpeg-org.so 包
//        System.loadLibrary("avcodec");
//        System.loadLibrary("avdevice");
//        System.loadLibrary("avfilter");
//        System.loadLibrary("avformat");
//        System.loadLibrary("avutil");
//        System.loadLibrary("swresample");
//        System.loadLibrary("swscale");

        System.loadLibrary("ffmpeg-org");
        System.loadLibrary("ffmpeg-cmd");
    }

    public static int runCmd(String[] cmd){
        return run(cmd.length,cmd);
    }

    public static String getFFmpegVersion(){
        return ffmpegVersion();
    }

    public static String getConfiguration(){
        return configuration();
    }

    private static native int run(int cmdLen, String[] cmd);

    private static native String ffmpegVersion();

    private static native String configuration();
}
