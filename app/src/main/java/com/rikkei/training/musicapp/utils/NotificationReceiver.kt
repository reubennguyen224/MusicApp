package com.rikkei.training.musicapp.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.rikkei.training.musicapp.NowPlaying
import com.rikkei.training.musicapp.PlayMusicFragment
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.model.favouriteChecker
import com.rikkei.training.musicapp.model.setSongPosition
import kotlin.system.exitProcess

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            MusicApplication.PREVIOUS ->{
                previousMusic(increment = false, context = context!!)
            } //previous song
            MusicApplication.PLAY -> {
                if (PlayMusicFragment.isPlaying)
                    pauseMusic()
                else
                    playMusic()
            }
            MusicApplication.NEXT -> {
                previousMusic(increment = true, context = context!!)
            }
            MusicApplication.EXIT -> {
                PlayMusicFragment.musicPlayService!!.stopForeground(true) //stop foreground services
                PlayMusicFragment.musicPlayService = null //delete all service
                exitProcess(1) //  end process of app
            }
        }
    }

    private fun previousMusic(increment: Boolean, context: Context){
        setSongPosition(increment = increment)
        PlayMusicFragment.musicPlayService!!.createMediaPlayer()
        PlayMusicFragment.binding.nameSong.text = PlayMusicFragment.song[PlayMusicFragment.songPosition].thisTile
        PlayMusicFragment.binding.singerName.text = PlayMusicFragment.song[PlayMusicFragment.songPosition].thisArtist
        Glide.with(context)
            .load(PlayMusicFragment.song[PlayMusicFragment.songPosition].imageUri)
            .centerCrop()
            .into(PlayMusicFragment.binding.songImg)
        playMusic()
        PlayMusicFragment.fIndex = favouriteChecker(PlayMusicFragment.song[PlayMusicFragment.songPosition].thisId)
        if (PlayMusicFragment.isFavourite){
            PlayMusicFragment.binding.btnFavour.setImageResource(R.drawable.ic_favorite)
        } else PlayMusicFragment.binding.btnFavour.setImageResource(R.drawable.ic_favorite_border_24)
    }

    private fun playMusic(){
        PlayMusicFragment.isPlaying= true
        PlayMusicFragment.musicPlayService!!.songPlayer!!.start()
        PlayMusicFragment.musicPlayService!!.sendNotification(R.drawable.ic_pause_bar)
        PlayMusicFragment.binding.playMusic.setImageResource(R.drawable.ic_pause)
        NowPlaying.binding.playPauseSongBar.setImageResource(R.drawable.ic_pause_bar)
    }

    private fun pauseMusic(){
        PlayMusicFragment.isPlaying= false
        PlayMusicFragment.musicPlayService!!.songPlayer!!.pause()
        PlayMusicFragment.musicPlayService!!.sendNotification(R.drawable.ic_play)
        PlayMusicFragment.binding.playMusic.setImageResource(R.drawable.ic_play_music)
        NowPlaying.binding.playPauseSongBar.setImageResource(R.drawable.ic_play)
    }
}