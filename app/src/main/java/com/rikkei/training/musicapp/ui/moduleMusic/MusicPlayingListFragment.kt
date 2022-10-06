package com.rikkei.training.musicapp.ui.moduleMusic

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.adapter.MusicAdapter
import com.rikkei.training.musicapp.databinding.FragmentMusicPlayingListBinding
import com.rikkei.training.musicapp.model.Song
import com.rikkei.training.musicapp.utils.ItemMoveCallback


class MusicPlayingListFragment : Fragment() {

    private var _binding: FragmentMusicPlayingListBinding? = null
    private val binding get() = _binding!!

    private val musicList = ArrayList<Song>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentMusicPlayingListBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        musicList.addAll(PlayMusicFragment.song)

        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.songListTitle.text = "Danh sách phát(${musicList.size})"

        val adapter = MusicAdapter()
        adapter.dataset = musicList
        val callback = ItemMoveCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.musicPlayingList)
        binding.musicPlayingList.adapter = adapter
        binding.musicPlayingList.layoutManager = LinearLayoutManager(context)

        adapter.setOnItemClickListener(object : MusicAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                PlayMusicFragment.songPosition = position
                PlayMusicFragment.musicPlayService!!.createMediaPlayer()
                PlayMusicFragment.binding.nameSong.text = PlayMusicFragment.song[PlayMusicFragment.songPosition].thisTile
                PlayMusicFragment.binding.singerName.text = PlayMusicFragment.song[PlayMusicFragment.songPosition].thisArtist
                Glide.with(requireContext())
                    .load(PlayMusicFragment.song[PlayMusicFragment.songPosition].imageUri)
                    .centerCrop()
                    .into(PlayMusicFragment.binding.songImg)
                PlayMusicFragment.isPlaying= true
                PlayMusicFragment.musicPlayService!!.initialSongInformation()
                PlayMusicFragment.musicPlayService!!.songPlayer!!.start()
                PlayMusicFragment.musicPlayService!!.sendNotification(R.drawable.ic_pause_bar)
                NowPlaying.binding.playPauseSongBar.setImageResource(R.drawable.ic_pause_bar)
            }

        })
    }

}