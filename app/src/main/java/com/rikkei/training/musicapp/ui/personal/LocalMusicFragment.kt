package com.rikkei.training.musicapp.ui.personal

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.adapter.MusicAdapter
import com.rikkei.training.musicapp.databinding.FragmentLocalMusicBinding
import com.rikkei.training.musicapp.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocalMusicFragment : Fragment() {

    private var _binding: FragmentLocalMusicBinding? = null
    private val binding get() = _binding!!

    companion object {
        val songlist = ArrayList<Song>()
    }

    val adapter = MusicAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLocalMusicBinding.inflate(inflater, container, false)
        val view = binding.root
        adapter.dataset = songlist
        binding.musicRecyclerList.adapter = adapter

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        adapter.setOnItemClickListener(object : MusicAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val bundle = Bundle()
                bundle.putInt("songPosition", position)
                bundle.putString("album", "MusicAdapter")
                findNavController().navigate(R.id.playMusicFragment, bundle)
            }
        })

        binding.musicRecyclerList.layoutManager = LinearLayoutManager(context)
        binding.titleFragment.text = "Bài hát"
        getSongList()

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

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun getSongList() {
        lifecycleScope.launch(Dispatchers.IO) {
            songlist.addAll(PersonalFragment.songlist)
            withContext(Dispatchers.Main) {
                binding.titleNumSong.text = "${songlist.size} bài hát"
                adapter.notifyDataSetChanged()
            }
        }
    }
}