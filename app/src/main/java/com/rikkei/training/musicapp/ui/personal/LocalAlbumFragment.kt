package com.rikkei.training.musicapp.ui.personal

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.adapter.AlbumAdapter
import com.rikkei.training.musicapp.databinding.FragmentLocalMusicBinding
import com.rikkei.training.musicapp.viewmodel.LocalViewModel

class LocalAlbumFragment : Fragment() {

    private var _binding: FragmentLocalMusicBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LocalViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLocalMusicBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    private val albumAdapter = AlbumAdapter()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.personalFragment)
        }

        viewModel.getAlbumList().observe(viewLifecycleOwner) {
            binding.titleNumSong.text = "${it.size} album"
            albumAdapter.dataset = it

        }
        binding.titleFragment.text = "Album"

        binding.musicRecyclerList.apply {
            adapter = albumAdapter
            layoutManager = LinearLayoutManager(context)
        }

        albumAdapter.setOnAlbumItemClickListener(object : AlbumAdapter.OnClickListener {
            override fun onAlbumItemClickListener(position: Int) {
                val bundle = Bundle()
                bundle.putInt("album_position", position)
                bundle.putString("local", "local")
                Log.e("test", findNavController().currentDestination!!.displayName)
                findNavController().navigate(R.id.newAlbumFragment2, bundle)
            }

        })

        binding.btnPlaySuffle.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}