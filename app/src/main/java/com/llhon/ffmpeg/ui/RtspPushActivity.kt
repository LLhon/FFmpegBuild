package com.llhon.ffmpeg.ui

import com.llhon.ffmpeg.FFmpegCmd
import com.llhon.ffmpeg.databinding.ActivityRtspPushBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * author  : LLhon
 * date    : 2024/4/7 15:41.
 * des     :
 */
class RtspPushActivity : BaseActivity<ActivityRtspPushBinding>() {

    private val RTSP_URL = "rtsp://admin:password@192.168.2.215:554/cam/realmonitor?channel=1&subtype=0&unicast=true&proto=Onvif" //能推

    private val RTMP_PUSH_URL = "rtmp://qn-push.xxx.com/baby-live/test1"
    private var mExecutorService: ExecutorService? = null

    override fun initView() {
        mBind.btnVersion.setOnClickListener {
            mBind.tvInfo.text = FFmpegCmd.getFFmpegVersion()
        }
        mBind.btnAvcodec.setOnClickListener {
            mBind.tvInfo.text = FFmpegCmd.getConfiguration()
        }
        mBind.btnPush.setOnClickListener {
            startPushChannel(RTSP_URL, RTMP_PUSH_URL)
        }
    }

    override fun initData() {
        mExecutorService = Executors.newFixedThreadPool(1)
    }

    private fun startPushChannel(rtspUrl: String, rtmpUrl: String) {
        val cmd = arrayOf<String>(
            "ffmpeg",
            "-i", rtspUrl,  // 输入文件，这里使用 input1.mp4 作为示例
            "-c:v", "copy",
            "-c:a", "copy",
            "-f", "flv",
            rtmpUrl
        )
        // 启动新的线程执行 FFmpeg 命令
        mExecutorService?.submit {
            FFmpegCmd.runCmd(cmd)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mExecutorService?.shutdown()
    }
}