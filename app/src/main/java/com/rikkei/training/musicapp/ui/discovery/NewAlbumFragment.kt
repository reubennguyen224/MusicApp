package com.rikkei.training.musicapp.ui.discovery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.adapter.MusicAdapter
import com.rikkei.training.musicapp.databinding.FragmentNewAlbum2Binding
import com.rikkei.training.musicapp.viewmodel.NewAlbumViewModel


class NewAlbumFragment : Fragment() {
    private var _binding: FragmentNewAlbum2Binding? = null
    private val binding get() = _binding!!
    private val viewModel: NewAlbumViewModel by viewModels()

    private var albumPosition: Int = 0
    var position = ""
    var isLocal = false


    private fun initial(bundle: Bundle) {
        albumPosition = bundle.getInt("album_position", 0)
        when (bundle.getString("local")) {
            "local" -> {
                isLocal = true
                viewModel.setCompanionObjectData("local", albumPosition)
                getAlbumLocalItem()
            }
            "internet" -> {
                isLocal = false
                viewModel.setCompanionObjectData("internet", albumPosition)
                getAlbumOnlItem()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentNewAlbum2Binding.inflate(inflater, container, false)
        val view = binding.root
        arguments?.let {
            initial(it)
        }
        return view
    }

    private val musicAdapter= MusicAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.getAlbumDetail().observe(viewLifecycleOwner){
            Glide.with(requireContext()).load(it.image).centerCrop()
                .into(binding.albumArt)
            binding.txtAlbumTitle.text = it.name
            binding.txtAlbumArtist.text = it.singer_name
        }

        setSongView()

        binding.btnPlayAlbum.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("songPosition", 0)
            bundle.putString("album", "AlbumSufferFragment")
            findNavController().navigate(R.id.playMusicFragment2, bundle)
        }
    }

    private fun setSongView() {
        binding.albumItemList.apply {
            adapter = musicAdapter
            layoutManager = LinearLayoutManager(context)
        }
        musicAdapter.setOnItemClickListener(object : MusicAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val bundle = Bundle()
                bundle.putInt("songPosition", position)
                bundle.putString("album", "AlbumFragment")
                findNavController().navigate(R.id.playMusicFragment2, bundle)
            }

        })
    }

    private fun getAlbumLocalItem() {
        viewModel.getAlbumLocalItemList().observe(viewLifecycleOwner){
            musicAdapter.dataset = it
        }
    }

    fun failed() {
        Toast.makeText(context, "Failed to get album", Toast.LENGTH_SHORT).show()
    }

    private fun getAlbumOnlItem() {
        viewModel.getAlbumItemList().observe(viewLifecycleOwner){
            musicAdapter.dataset = it
        }

    }
}