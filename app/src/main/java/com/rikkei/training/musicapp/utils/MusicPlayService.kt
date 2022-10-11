package com.rikkei.training.musicapp.utils

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.model.getImgArt
import com.rikkei.training.musicapp.model.getThumb
import com.rikkei.training.musicapp.ui.moduleMusic.NowPlaying
import com.rikkei.training.musicapp.ui.moduleMusic.PlayMusicFragment
import com.rikkei.training.musicapp.utils.MusicApplication.Companion.CHANNEL_ID
import com.rikkei.training.musicapp.viewmodel.MusicModuleViewModel

@RequiresApi(Build.VERSION_CODES.O)
class MusicPlayService : Service() {

    var myBinder = MyBinder()
    var songPlayer: MediaPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat

    override fun onBind(intent: Intent?): IBinder? {
        mediaSession = MediaSessionCompat(baseContext, "Music Application")
        return myBinder
    }

    inner class MyBinder : Binder() {
        fun currentService(): MusicPlayService {
            return this@MusicPlayService
        }
    }

    @SuppressLint("RemoteViewLayout", "UnspecifiedImmutableFlag")
    fun sendNotification(playPauseBtn: Int) {

        val previousIntent = Intent(
            baseContext,
            NotificationReceiver::class.java
        ).setAction(MusicApplication.PREVIOUS)
        val previousPendingIntent =
            PendingIntent.getBroadcast(baseContext, 0, previousIntent, PendingIntent.FLAG_IMMUTABLE)

        val playIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(MusicApplication.PLAY)
        val playPendingIntent =
            PendingIntent.getBroadcast(baseContext, 0, playIntent, PendingIntent.FLAG_IMMUTABLE)

        val nextIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(MusicApplication.NEXT)
        val nextPendingIntent =
            PendingIntent.getBroadcast(baseContext, 0, nextIntent, PendingIntent.FLAG_IMMUTABLE)

        val exitIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(MusicApplication.EXIT)
        val exitPendingIntent =
            PendingIntent.getBroadcast(baseContext, 0, exitIntent, PendingIntent.FLAG_IMMUTABLE)

        val imgArt = getImgArt(MusicModuleViewModel.listOfSongs[PlayMusicFragment.songPosition].songUri)
        val image = if (imgArt != null) {
            BitmapFactory.decodeByteArray(imgArt, 0, imgArt.size)
        } else {
            BitmapFactory.decodeResource(resources, R.drawable.splash_logo)
        }

        val notification = NotificationCompat.Builder(baseContext, CHANNEL_ID)
            .setContentTitle(MusicModuleViewModel.listOfSongs[PlayMusicFragment.songPosition].thisTile)
            .setContentText(MusicModuleViewModel.listOfSongs[PlayMusicFragment.songPosition].thisArtist)
            .setSmallIcon(R.drawable.ic_library)
            .setLargeIcon(image) //icon of large notification
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.ic_previous, "Previous", previousPendingIntent)
            .addAction(playPauseBtn, "Play", playPendingIntent)
            .addAction(R.drawable.ic_next, "Next", nextPendingIntent)
            .addAction(R.drawable.ic_clear, "exit", exitPendingIntent)
            .build()

        startForeground(2, notification)
    }

    fun createMediaPlayer() {
        try {
            if (PlayMusicFragment.musicPlayService!!.songPlayer == null) PlayMusicFragment.musicPlayService!!.songPlayer =
                MediaPlayer()
            PlayMusicFragment.musicPlayService!!.songPlayer!!.reset()
            PlayMusicFragment.musicPlayService!!.songPlayer!!.setDataSource(MusicModuleViewModel.listOfSongs[PlayMusicFragment.songPosition].songUri)
            PlayMusicFragment.musicPlayService!!.songPlayer!!.prepare()
            PlayMusicFragment.binding.playMusic.setImageResource(R.drawable.ic_pause)
            PlayMusicFragment.binding.timestampSong.progress = 0
            PlayMusicFragment.binding.timestampSong.max = songPlayer!!.duration
            PlayMusicFragment.binding.timestampSong.thumb =
                getThumb(songPlayer!!.currentPosition, baseContext)
            PlayMusicFragment.nowPlayingId =
                MusicModuleViewModel.listOfSongs[PlayMusicFragment.songPosition].thisId.toString()
            Glide.with(baseContext)
                .load(MusicModuleViewModel.listOfSongs[PlayMusicFragment.songPosition].imageUri)
                .centerCrop()
                .into(NowPlaying.binding.imageMusic)
        } catch (e: Exception) {
            return
        }
    }

    fun initialSongInformation() {
        PlayMusicFragment.binding.nameSong.text =
            MusicModuleViewModel.listOfSongs[PlayMusicFragment.songPosition].thisTile
        NowPlaying.binding.nameSong.text =
            MusicModuleViewModel.listOfSongs[PlayMusicFragment.songPosition].thisTile
        PlayMusicFragment.binding.singerName.text =
            MusicModuleViewModel.listOfSongs[PlayMusicFragment.songPosition].thisArtist
        NowPlaying.binding.nameSinger.text =
            MusicModuleViewModel.listOfSongs[PlayMusicFragment.songPosition].thisArtist
        Glide.with(baseContext)
            .load(MusicModuleViewModel.listOfSongs[PlayMusicFragment.songPosition].imageUri)
            .centerCrop()
            .into(PlayMusicFragment.binding.songImg)

    }
}