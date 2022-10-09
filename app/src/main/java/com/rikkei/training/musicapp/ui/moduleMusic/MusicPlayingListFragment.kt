package com.rikkei.training.musicapp.ui.moduleMusic

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.adapter.MusicAdapter
import com.rikkei.training.musicapp.databinding.FragmentMusicPlayingListBinding
import com.rikkei.training.musicapp.utils.ItemMoveCallback
import com.rikkei.training.musicapp.viewmodel.MusicModuleViewModel

@RequiresApi(Build.VERSION_CODES.O)
class MusicPlayingListFragment : Fragment() {

    private var _binding: FragmentMusicPlayingListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MusicModuleViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentMusicPlayingListBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    private val songAdapter = MusicAdapter()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getMusicList().observe(viewLifecycleOwner){
            binding.songListTitle.text = "Danh sách phát (${it.size})"
            songAdapter.dataset = it
        }

        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }

        val callback = ItemMoveCallback(songAdapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.musicPlayingList)
        binding.musicPlayingList.apply {
            adapter = songAdapter
            layoutManager = LinearLayoutManager(context)
        }

        songAdapter.setOnItemClickListener(object : MusicAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                PlayMusicFragment.songPosition = position
                PlayMusicFragment.musicPlayService!!.createMediaPlayer()
                PlayMusicFragment.binding.nameSong.text = MusicModuleViewModel.listOfSongs[PlayMusicFragment.songPosition].thisTile
                PlayMusicFragment.binding.singerName.text = MusicModuleViewModel.listOfSongs[PlayMusicFragment.songPosition].thisArtist
                Glide.with(requireContext())
                    .load(MusicModuleViewModel.listOfSongs[PlayMusicFragment.songPosition].imageUri)
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