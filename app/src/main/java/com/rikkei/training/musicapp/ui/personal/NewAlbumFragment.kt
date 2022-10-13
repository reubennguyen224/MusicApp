package com.rikkei.training.musicapp.ui.personal

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
import com.rikkei.training.musicapp.databinding.FragmentNewAlbumBinding
import com.rikkei.training.musicapp.viewmodel.NewReleaseLocalViewModel


class NewAlbumFragment : Fragment() {

    private var _binding: FragmentNewAlbumBinding? = null
    private val binding get() = _binding!!

    private val albumAdapter = AlbumAdapter()
    private val viewModel: NewReleaseLocalViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentNewAlbumBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getNewAlbumLocal().observe(viewLifecycleOwner){
            albumAdapter.dataset = it
        }
        binding.newAlbumList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = albumAdapter
        }

        albumAdapter.setOnAlbumItemClickListener(object : AlbumAdapter.OnClickListener{
            override fun onAlbumItemClickListener(position: Int) {
                val bundle = Bundle()
                bundle.putInt("album_position", position)
                bundle.putString("local", "local")
                Log.e("test", findNavController().currentDestination!!.navigatorName)
                findNavController().navigate(R.id.newAlbumFragment2, bundle)
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}