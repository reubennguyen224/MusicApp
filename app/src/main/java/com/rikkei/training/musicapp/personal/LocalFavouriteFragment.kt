package com.rikkei.training.musicapp.personal

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.adapter.MusicAdapter
import com.rikkei.training.musicapp.databinding.FragmentLocalMusicBinding
import com.rikkei.training.musicapp.model.Song

class LocalFavouriteFragment:Fragment() {
    private var _binding: FragmentLocalMusicBinding? = null
    private val binding get() = _binding!!
    companion object{
        val favouriteList = ArrayList<Song>()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLocalMusicBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.titleFragment.text = getString(R.string.favourite)

        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.titleNumSong.text = "${favouriteList.size} bài hát"

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        val adapter = MusicAdapter(favouriteList)
        binding.musicRecyclerList.adapter = adapter
        binding.musicRecyclerList.layoutManager = LinearLayoutManager(context)

        adapter.setOnItemClickListener(object : MusicAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                val bundle = Bundle()
                bundle.putInt("songPosition", position)
                bundle.putString("album", "Favourite")
                findNavController().navigate(R.id.playMusicFragment, bundle)
            }
        })

        binding.btnPlaySuffle.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("songPosition", 0)
            bundle.putString("album", "FavouriteSuffer")
            findNavController().navigate(R.id.playMusicFragment, bundle)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}