package com.rikkei.training.musicapp.ui.discovery

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.adapter.AlbumAdapter
import com.rikkei.training.musicapp.adapter.ArtistAdapter
import com.rikkei.training.musicapp.adapter.MusicAdapter
import com.rikkei.training.musicapp.databinding.FragmentDiscoveryBinding
import com.rikkei.training.musicapp.model.Album
import com.rikkei.training.musicapp.model.Singer
import com.rikkei.training.musicapp.model.Song
import com.rikkei.training.musicapp.viewmodel.DiscoveryViewModel

class DiscoveryFragment : Fragment() {
    private var _binding: FragmentDiscoveryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DiscoveryViewModel by viewModels()

    companion object {
        val newMusic = ArrayList<Song>()
        val newAlbum = Album()
        val newSinger = Singer()
        val songSuggest = ArrayList<Song>()
    }

    private val songAdapter = MusicAdapter()
    private val albumAdapter = AlbumAdapter()
    private val singerAdapter = ArtistAdapter()
    private val songSSAdapter = MusicAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiscoveryBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnSong.background =
            ResourcesCompat.getDrawable(resources, R.drawable.button_little_background_2, null)
        binding.btnSong.setTextColor(
            ContextCompat.getColor(
                requireActivity().applicationContext,
                R.color.white
            )
        )

        binding.btnAlbum.setOnClickListener {
            binding.btnAlbum.background =
                ResourcesCompat.getDrawable(resources, R.drawable.button_little_background_2, null)
            binding.btnAlbum.setTextColor(
                ContextCompat.getColor(
                    requireActivity().applicationContext,
                    R.color.white
                )
            )
            binding.btnSong.background =
                ResourcesCompat.getDrawable(resources, R.drawable.button_little_background, null)
            binding.btnSong.setTextColor(
                ContextCompat.getColor(
                    requireActivity().applicationContext,
                    R.color.purple_500
                )
            )
            binding.listNew.adapter = albumAdapter
        }

        binding.btnSong.setOnClickListener {
            binding.btnSong.background =
                ResourcesCompat.getDrawable(resources, R.drawable.button_little_background_2, null)
            binding.btnSong.setTextColor(
                ContextCompat.getColor(
                    requireActivity().applicationContext,
                    R.color.white
                )
            )
            binding.btnAlbum.background =
                ResourcesCompat.getDrawable(resources, R.drawable.button_little_background, null)
            binding.btnAlbum.setTextColor(
                ContextCompat.getColor(
                    requireActivity().applicationContext,
                    R.color.purple_500
                )
            )
            binding.listNew.adapter = songAdapter
        }

        albumAdapter.setOnAlbumItemClickListener(albumListener)

        binding.suggestList.apply {
            adapter = songSSAdapter
            layoutManager = LinearLayoutManager(context)
        }

        songSSAdapter.setOnItemClickListener(songSSListener)
        binding.newSingerOnlineList.apply {
            adapter = singerAdapter
            layoutManager =
                GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
        }

        singerAdapter.setOnArtistClickListener(singerListener)

        binding.listNew.apply {
            adapter = songAdapter
            layoutManager =
                GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
        }

        songAdapter.setOnItemClickListener(newSongListener)

        return view
    }

    private val newSongListener = object : MusicAdapter.OnItemClickListener {
        override fun onItemClick(position: Int) {
            val bundle = Bundle()
            bundle.putInt("position", position)
            bundle.putString("album", "DiscoveryFragment")
            findNavController().navigate(R.id.playMusicFragment2, bundle)
        }
    }

    private val singerListener = object : ArtistAdapter.OnItemClickListener {
        override fun onArtistClickListener(position: Int) {
            val string = "discovery"
            val bundle = Bundle()
            bundle.putInt("position", position)
            bundle.putString("fromWhere", string)
            findNavController().navigate(R.id.singerDetailFragment, bundle)
        }
    }

    private val songSSListener = object : MusicAdapter.OnItemClickListener {
        override fun onItemClick(position: Int) {
            val bundle = Bundle()
            bundle.putInt("position", position)
            bundle.putString("album", "DiscoverySSFragment")
            findNavController().navigate(R.id.playMusicFragment2, bundle)
        }
    }

    private val albumListener = object : AlbumAdapter.OnClickListener {
        override fun onAlbumItemClickListener(position: Int) {
            val bundle = Bundle()
            bundle.putInt("album_position", position)
            bundle.putString("local", "internet")
            findNavController().navigate(R.id.newAlbumFragment, bundle)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshData()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshData() {
        viewModel.getNewAlbum().observe(viewLifecycleOwner) {
            albumAdapter.dataset = it
            newAlbum.clear()
            newAlbum.addAll(it)
            albumAdapter.notifyDataSetChanged()
        }
        viewModel.getNewSinger().observe(viewLifecycleOwner) {
            singerAdapter.artistList = it
            newSinger.clear()
            newSinger.addAll(it)
            singerAdapter.notifyDataSetChanged()
        }
        viewModel.getNewSong().observe(viewLifecycleOwner) {
            songAdapter.dataset = it
            newMusic.clear()
            newMusic.addAll(it)
            songAdapter.notifyDataSetChanged()
        }
        viewModel.getSongSuggest().observe(viewLifecycleOwner) {
            songSSAdapter.dataset = it
            songSuggest.clear()
            songSuggest.addAll(it)
            songSSAdapter.notifyDataSetChanged()
        }
    }


}