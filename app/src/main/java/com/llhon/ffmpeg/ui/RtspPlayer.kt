package com.llhon.ffmpeg.ui

import android.content.Context
import android.net.Uri
import android.util.Log
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout

/**
 * author  : LLhon date    : 2024/4/7 16:04. des     :
 */
class RtspPlayer(url: String, playerView: VLCVideoLayout?, context: Context?) {

    //ExoPlayer player;
    var media: Media
    var mediaPlayer: MediaPlayer
    var libVlc: LibVLC

    init {
        Log.d("URL", url)
        libVlc = LibVLC(context)
        mediaPlayer = MediaPlayer(libVlc)
        mediaPlayer.attachViews(playerView!!, null, false, false)
        media = Media(libVlc, Uri.parse(url))
        media.setHWDecoderEnabled(true, false)
        //media.addOption(":network-caching=600");
        media.addOption(":no-audio")
        media.addOption(":quiet")
        mediaPlayer.media = media
        media.release()
        mediaPlayer.play()

//        MediaSource mediaSource =
//                new RtspMediaSource.Factory()
//                        .createMediaSource(MediaItem.fromUri(url));
//        player = new ExoPlayer.Builder(context)
//                .setMediaSourceFactory(new RtspMediaSource.Factory().setDebugLoggingEnabled(true))
//                .build();
//        playerView.setPlayer(player);
//        player.setMediaSource(mediaSource);
//        player.prepare();
//        player.play();


//        Thread t = new Thread() {
//            public void run() {
//
//            }
//        };
//        t.start();
    }

    fun stopPlayer() {
        mediaPlayer.stop()
        mediaPlayer.vlcVout.detachViews()
        mediaPlayer.release()
        libVlc.release()
        Log.i("INFO", "Play stopped")
    }
}
