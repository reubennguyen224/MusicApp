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
import com.rikkei.training.musicapp.viewmodel.LocalFavouriteViewModel

class LocalFavouriteFragment : Fragment() {
    private var _binding: FragmentLocalMusicBinding? = null
    private val binding get() = _binding!!

    private val viewModel : LocalFavouriteViewModel by viewModels()

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
    private val favouriteAdapter = MusicAdapter()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.personalFragment)
        }

        viewModel.getFavouriteSongList().observe(viewLifecycleOwner){
            binding.titleNumSong.text = "${it.size} bài hát"
            favouriteAdapter.dataset = it
        }

        binding.musicRecyclerList.apply {
            adapter = favouriteAdapter
            layoutManager = LinearLayoutManager(context)
        }

        favouriteAdapter.setOnItemClickListener(object : MusicAdapter.OnItemClickListener {
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