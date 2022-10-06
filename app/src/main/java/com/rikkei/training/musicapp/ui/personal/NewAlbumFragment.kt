package com.rikkei.training.musicapp.ui.personal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.adapter.AlbumAdapter
import com.rikkei.training.musicapp.databinding.FragmentNewAlbumBinding
import com.rikkei.training.musicapp.model.Album


class NewAlbumFragment : Fragment() {

    private var _binding: FragmentNewAlbumBinding? = null
    private val binding get() = _binding!!

    private val albumList = Album()
    private val adapter = AlbumAdapter()

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
        findAlbum()

        adapter.dataset = albumList
        binding.newAlbumList.adapter = adapter
        binding.newAlbumList.layoutManager = LinearLayoutManager(context)

        adapter.setOnAlbumItemClickListener(object : AlbumAdapter.OnClickListener{
            override fun onAlbumItemClickListener(position: Int) {
                val bundle = Bundle()
                bundle.putInt("album_position", position)
                bundle.putString("local", "local")
                findNavController().navigate(R.id.newAlbumFragment2, bundle)
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun findAlbum(){
        albumList.clear()
        var x=10
        while (x++ <=10){
            if (PersonalFragment.albumList.size < 10) albumList.addAll(PersonalFragment.albumList)
            else albumList.add(PersonalFragment.albumList[x])
        }

    }

}