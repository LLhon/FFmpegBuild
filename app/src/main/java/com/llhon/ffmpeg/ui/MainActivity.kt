package com.llhon.ffmpeg.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.FrameLayout
import com.llhon.ffmpeg.FFmpegCmd
import com.llhon.ffmpeg.databinding.ActivityMainBinding
import org.videolan.libvlc.util.VLCVideoLayout
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


/**
 * author  : LLhon
 * date    : 2024/3/20 16:57.
 * des     :
 */
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val TAG = "MainActivity"
    private val RTSP_URL = "rtsp://admin:password@192.168.2.215:554/cam/realmonitor?channel=1&subtype=0&unicast=true&proto=Onvif" //能推
    private val RTSP_URL2 = "rtsp://admin:password@192.168.6.89:554/11" //推不了
    private val RTSP_URL3 = "rtsp://admin:password@192.168.6.66:554/Streaming/Channels/1" //能推
    private val RTSP_URL4 = "rtsp://admin:password@192.168.6.2:554" //能推
    private val RTSP_URL5 = "rtsp://admin:password@192.168.6.13:554" //能推
    private val RTSP_URL6 = "rtsp://admin:password@192.168.6.223:554" //能推
    private val RTSP_URL7 = "rtsp://admin:password@192.168.6.172:554/11" //能推
    private val RTSP_URL8 = "rtsp://admin:password@192.168.6.14:554/live/0/MAIN" //能推

    private val mRtspUrlList = mutableListOf<String>()
    private val mRtspPlayerList = mutableListOf<RtspPlayer>()
//    private val mRtspPlayerList = mutableListOf<MediaPlayer>()
    private val mRtspViewList = mutableListOf<VLCVideoLayout>()
//    private val mRtspViewList = mutableListOf<SurfaceView>()

    override fun initView() {
        mBind.fab.setOnClickListener {
            startActivity(Intent(this, RtspPushActivity::class.java))
        }
    }

    override fun initData() {
        mRtspUrlList.add(RTSP_URL)
        mRtspUrlList.add(RTSP_URL2)
        mRtspUrlList.add(RTSP_URL3)
        mRtspUrlList.add(RTSP_URL4)
//        mRtspUrlList.add(RTSP_URL5)
//        mRtspUrlList.add(RTSP_URL6)
//        mRtspUrlList.add(RTSP_URL7)
//        mRtspUrlList.add(RTSP_URL8)
//        mRtspUrlList.add(RTSP_URL)
//        mRtspUrlList.add(RTSP_URL2)
//        mRtspUrlList.add(RTSP_URL3)
//        mRtspUrlList.add(RTSP_URL4)
//        mRtspUrlList.add(RTSP_URL5)
//        mRtspUrlList.add(RTSP_URL6)
//        mRtspUrlList.add(RTSP_URL7)
//        mRtspUrlList.add(RTSP_URL8)

        mRtspViewList.add(mBind.previewView1)
        mRtspViewList.add(mBind.previewView2)
        mRtspViewList.add(mBind.previewView3)
        mRtspViewList.add(mBind.previewView4)
//        mRtspViewList.add(mBind.previewView5)
//        mRtspViewList.add(mBind.previewView6)
//        mRtspViewList.add(mBind.previewView7)
//        mRtspViewList.add(mBind.previewView8)
//        mRtspViewList.add(mBind.previewView9)
//        mRtspViewList.add(mBind.previewView10)
//        mRtspViewList.add(mBind.previewView11)
//        mRtspViewList.add(mBind.previewView12)
//        mRtspViewList.add(mBind.previewView13)
//        mRtspViewList.add(mBind.previewView14)
//        mRtspViewList.add(mBind.previewView15)
//        mRtspViewList.add(mBind.previewView16)


        initVlcPlayer()
//        initNodePlayer()
    }

    private fun initMediaPlayer() {
//        for (i in 0 until mRtspUrlList.size) {
//            val player = MediaPlayer.create(this, Uri.parse(mRtspUrlList[i]))
//            mRtspPlayerList.add(player)
//            mRtspViewList[i].holder.addCallback(object: SurfaceHolder.Callback {
//                override fun surfaceCreated(p0: SurfaceHolder) {
//                    mRtspPlayerList[i].setDisplay(p0)
//                    mRtspPlayerList[i].start()
//                }
//
//                override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
//
//                }
//
//                override fun surfaceDestroyed(p0: SurfaceHolder) {
//                    mRtspPlayerList[i].stop()
//                }
//            })
//        }
    }

    /**
     * vlc播放器：
     *      播8路rtsp流 cpu占用率：50% 内存使用率：341MB
     *      播16路rtsp流 闪退
     */
    private fun initVlcPlayer() {
        for (i in 0 until mRtspUrlList.size) {
            val player = RtspPlayer(mRtspUrlList[i], mRtspViewList[i], this)
            mRtspPlayerList.add(player)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        mRtspPlayerList.forEach {
            it.stopPlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}