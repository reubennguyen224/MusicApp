package com.rikkei.training.musicapp.ui.personal

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.adapter.MusicAdapter
import com.rikkei.training.musicapp.databinding.FragmentLocalMusicBinding
import com.rikkei.training.musicapp.viewmodel.LocalViewModel

class LocalMusicFragment : Fragment() {

    private var _binding: FragmentLocalMusicBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LocalViewModel by viewModels()

    private val songAdapter = MusicAdapter()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLocalMusicBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel.getSongList().observe(viewLifecycleOwner) {
            songAdapter.dataset = it
            binding.titleNumSong.text = "${it.size} bài hát"
        }

        binding.musicRecyclerList.apply {
            adapter = songAdapter
            layoutManager = LinearLayoutManager(context)

        }

        binding.btnBack.setOnClickListener {
            findNavController().navigate(findNavController().previousBackStackEntry?.destination!!.id)
        }

        songAdapter.setOnItemClickListener(object : MusicAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val bundle = Bundle()
                bundle.putInt("songPosition", position)
                bundle.putString("album", "MusicAdapter")
                findNavController().navigate(R.id.playMusicFragment, bundle)
            }
        })

        binding.titleFragment.text = "Bài hát"

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPlaySuffle.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("songPosition", 0)
            bundle.putString("album", "MusicShuffleAdapter")
            findNavController().navigate(R.id.playMusicFragment, bundle)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}