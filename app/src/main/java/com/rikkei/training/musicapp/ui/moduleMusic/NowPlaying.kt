package com.rikkei.training.musicapp.ui.moduleMusic

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.databinding.FragmentNowPlayingBinding
import com.rikkei.training.musicapp.model.setSongPosition

class NowPlaying : Fragment() {

    companion object{
        @SuppressLint("StaticFieldLeak")
        private var _binding: FragmentNowPlayingBinding? = null
        val binding get() = _binding!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentNowPlayingBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.visibility = View.GONE
        binding.playPauseSongBar.setOnClickListener {
            if (PlayMusicFragment.isPlaying) pauseMusic() else playMusic()
        }
        binding.nextSongBar.setOnClickListener {
            setSongPosition(increment = true)
            PlayMusicFragment.musicPlayService!!.createMediaPlayer()
            PlayMusicFragment.binding.nameSong.text = PlayMusicFragment.song[PlayMusicFragment.songPosition].thisTile
            PlayMusicFragment.binding.singerName.text = PlayMusicFragment.song[PlayMusicFragment.songPosition].thisArtist
            onResume()
            Glide.with(requireContext())
                .load(PlayMusicFragment.song[PlayMusicFragment.songPosition].imageUri)
                .centerCrop()
                .into(PlayMusicFragment.binding.songImg)
            playMusic()
        }

        binding.root.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("songPosition", PlayMusicFragment.songPosition)
            bundle.putString("album", "NowPlaying")
            if (findNavController().currentDestination?.id == R.id.musicLocalFragment)
                findNavController().navigate(R.id.playMusicFragment, bundle)
            else findNavController().navigate(R.id.playMusicFragment2, bundle)
        }

    }

    override fun onResume() {
        super.onResume()
        if (PlayMusicFragment.musicPlayService != null){
            binding.root.visibility = View.VISIBLE
            binding.nameSong.text = PlayMusicFragment.song[PlayMusicFragment.songPosition].thisTile
            binding.nameSinger.text = PlayMusicFragment.song[PlayMusicFragment.songPosition].thisArtist
            Glide.with(requireContext())
                .load(PlayMusicFragment.song[PlayMusicFragment.songPosition].imageUri)
                .centerCrop()
                .into(binding.imageMusic)
            if(PlayMusicFragment.isPlaying) binding.playPauseSongBar.setImageResource(R.drawable.ic_pause_bar)
            else binding.playPauseSongBar.setImageResource(R.drawable.ic_play)
        }
    }

    private fun playMusic(){
        PlayMusicFragment.musicPlayService!!.songPlayer!!.start()
        binding.playPauseSongBar.setImageResource(R.drawable.ic_pause_bar)
        PlayMusicFragment.musicPlayService!!.sendNotification(R.drawable.ic_pause_bar)
        PlayMusicFragment.binding.playMusic.setImageResource(R.drawable.ic_pause_bar)
        PlayMusicFragment.isPlaying = true
    }

    private fun pauseMusic(){
        PlayMusicFragment.musicPlayService!!.songPlayer!!.pause()
        binding.playPauseSongBar.setImageResource(R.drawable.ic_play)
        PlayMusicFragment.musicPlayService!!.sendNotification(R.drawable.ic_play)
        PlayMusicFragment.binding.playMusic.setImageResource(R.drawable.ic_play)
        PlayMusicFragment.isPlaying = false
    }
}