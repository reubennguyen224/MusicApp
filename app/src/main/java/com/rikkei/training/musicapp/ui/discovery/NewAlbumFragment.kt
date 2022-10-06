package com.rikkei.training.musicapp.ui.discovery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.adapter.MusicAdapter
import com.rikkei.training.musicapp.databinding.FragmentNewAlbum2Binding
import com.rikkei.training.musicapp.model.Album
import com.rikkei.training.musicapp.model.AlbumItem
import com.rikkei.training.musicapp.model.MusicAPI
import com.rikkei.training.musicapp.model.Song
import com.rikkei.training.musicapp.ui.HomeFragment
import com.rikkei.training.musicapp.ui.personal.PersonalFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NewAlbumFragment : Fragment() {
    private var _binding: FragmentNewAlbum2Binding? = null
    private val binding get() = _binding!!

    companion object {
        val albumItem = ArrayList<Song>()
        val albumList = Album()
        lateinit var album: AlbumItem
    }

    private var albumPosition: Int = 0
    var position = ""
    var isLocal = false


    private fun initial(bundle: Bundle) {
        albumList.clear()
        albumPosition = bundle.getInt("album_position", 0)
        when (bundle.getString("local")) {
            "local" -> {
                isLocal = true
                albumList.addAll(PersonalFragment.albumList)
                album = albumList[albumPosition]
                getAlbumLocalItem()
            }
            "internet" -> {
                isLocal = false
                albumList.addAll(DiscoveryFragment.newAlbum)
                album = albumList[albumPosition]
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

    private lateinit var adapter: MusicAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        Glide.with(requireContext())
            .load(albumList[albumPosition].image)
            .centerCrop()
            .into(binding.albumArt)

        binding.txtAlbumTitle.text = albumList[albumPosition].name
        binding.txtAlbumArtist.text = albumList[albumPosition].singer_name

        adapter = MusicAdapter()
        adapter.dataset = albumItem
        setSongView()

        binding.btnPlayAlbum.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("songPosition", 0)
            bundle.putString("album", "AlbumSufferFragment")
            findNavController().navigate(R.id.playMusicFragment2, bundle)
        }
    }

    private fun setSongView() {
        binding.albumItemList.adapter = adapter
        binding.albumItemList.layoutManager = LinearLayoutManager(context)
        adapter.setOnItemClickListener(object : MusicAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val bundle = Bundle()
                bundle.putInt("songPosition", position)
                bundle.putString("album", "AlbumFragment")
                findNavController().navigate(R.id.playMusicFragment2, bundle)
            }

        })
    }

    private fun getAlbumLocalItem() {
        albumItem.clear()
        val songList = PersonalFragment.songlist
        for (song in songList) {
            if (song.thisAlbum == album.name)
                albumItem.add(song)
        }
    }

    private fun getAlbumOnlItem() {
        albumItem.clear()
        lifecycleScope.launch(Dispatchers.IO) {
            HomeFragment.loginAPI.getAlbumItem(albumList[albumPosition].id.toInt())
                .enqueue(object : Callback<MusicAPI> {
                    override fun onResponse(call: Call<MusicAPI>, response: Response<MusicAPI>) {
                        val musicList = response.body()
                        for (music in musicList!!) {
                            albumItem.add(
                                Song(
                                    thisId = music.id.toLong(),
                                    thisTile = music.title,
                                    thisArtist = music.artist,
                                    thisAlbum = "",
                                    dateModifier = "",
                                    favourite = false,
                                    imageUri = music.coverURI,
                                    songUri = music.songURI
                                )
                            )
                        }
                        setSongView()
                    }

                    override fun onFailure(call: Call<MusicAPI>, t: Throwable) {
                        Toast.makeText(context, "Failed to get album", Toast.LENGTH_SHORT).show()
                    }
                })
            withContext(Dispatchers.Main) {
                adapter.notifyDataSetChanged()
            }
        }

    }
}