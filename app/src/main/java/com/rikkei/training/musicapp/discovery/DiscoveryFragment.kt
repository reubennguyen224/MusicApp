package com.rikkei.training.musicapp.discovery

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.rikkei.training.musicapp.HomeFragment
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.adapter.AlbumAdapter
import com.rikkei.training.musicapp.adapter.ArtistAdapter
import com.rikkei.training.musicapp.adapter.MusicAdapter
import com.rikkei.training.musicapp.databinding.FragmentDiscoveryBinding
import com.rikkei.training.musicapp.model.*
import com.rikkei.training.musicapp.viewmodel.DiscoveryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DiscoveryFragment : Fragment() {
    private var _binding: FragmentDiscoveryBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DiscoveryViewModel

    companion object{
        val newMusic = ArrayList<Song>()
        val newAlbum = Album()
        val newSinger = Singer()
        val songSuggest = ArrayList<Song>()
    }
    private val songAdapter: MusicAdapter = MusicAdapter(newMusic)
    private val albumAdapter: AlbumAdapter = AlbumAdapter(newAlbum)
    private val singerAdapter: ArtistAdapter = ArtistAdapter(newSinger)
    private val songSSAdapter: MusicAdapter = MusicAdapter(songSuggest)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiscoveryBinding.inflate(inflater, container, false)
        val view = binding.root
        setNewSongList()
        setSingerList()
        setSongSS()
        binding.btnSong.background = ResourcesCompat.getDrawable(resources, R.drawable.button_little_background_2, null)
        binding.btnSong.setTextColor(ContextCompat.getColor(requireActivity().applicationContext, R.color.white))

        binding.btnAlbum.setOnClickListener {
            binding.btnAlbum.background = ResourcesCompat.getDrawable(resources, R.drawable.button_little_background_2, null)
            binding.btnAlbum.setTextColor(ContextCompat.getColor(requireActivity().applicationContext, R.color.white))
            binding.btnSong.background = ResourcesCompat.getDrawable(resources, R.drawable.button_little_background, null)
            binding.btnSong.setTextColor(ContextCompat.getColor(requireActivity().applicationContext, R.color.purple_500))
            binding.listNew.adapter = albumAdapter
        }

        binding.btnSong.setOnClickListener {
            binding.btnSong.background = ResourcesCompat.getDrawable(resources, R.drawable.button_little_background_2, null)
            binding.btnSong.setTextColor(ContextCompat.getColor(requireActivity().applicationContext, R.color.white))
            binding.btnAlbum.background = ResourcesCompat.getDrawable(resources, R.drawable.button_little_background, null)
            binding.btnAlbum.setTextColor(ContextCompat.getColor(requireActivity().applicationContext, R.color.purple_500))
            binding.listNew.adapter = songAdapter
        }

        return view
    }

    private fun setNewSongList(){
        binding.listNew.adapter = songAdapter
        binding.listNew.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
        songAdapter.setOnItemClickListener(newSongListener)
    }

    private val newSongListener = object : MusicAdapter.OnItemClickListener{
        override fun onItemClick(position: Int) {
            val bundle = Bundle()
            bundle.putInt("position", position)
            bundle.putString("album", "DiscoveryFragment")
            findNavController().navigate(R.id.playMusicFragment2, bundle)
        }
    }

    private fun setSingerList(){
        binding.newSingerOnlineList.adapter = singerAdapter
        binding.newSingerOnlineList.layoutManager = GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)

        singerAdapter.setOnArtistClickListener(singerListener)
    }
    private val singerListener = object : ArtistAdapter.OnItemClickListener{
        override fun onArtistClickListener(position: Int) {
            val string = "discovery"
            val bundle = Bundle()
            bundle.putInt("position", position)
            bundle.putString("fromWhere", string)
            findNavController().navigate(R.id.singerDetailFragment, bundle)
        }
    }

    private fun setSongSS(){
        binding.suggestList.adapter = songSSAdapter
        binding.suggestList.layoutManager = LinearLayoutManager(context)

        songSSAdapter.setOnItemClickListener(songSSListener)
    }

    private val songSSListener = object : MusicAdapter.OnItemClickListener{
        override fun onItemClick(position: Int) {
            val bundle = Bundle()
            bundle.putInt("position", position)
            bundle.putString("album", "DiscoverySSFragment")
            findNavController().navigate(R.id.playMusicFragment2, bundle)
        }
    }

    private val albumListener = object : AlbumAdapter.OnClickListener{
        override fun onAlbumItemClickListener(position: Int) {
            val bundle = Bundle()
            bundle.putInt("album_position", position)
            bundle.putString("local", "internet")
            findNavController().navigate(R.id.newAlbumFragment, bundle)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        callAPIForData()

        albumAdapter.setOnAlbumItemClickListener(albumListener)

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun callAPIForData(){
        lifecycleScope.launch(Dispatchers.IO){
            HomeFragment.loginAPI.getSongSS().enqueue(object : Callback<MusicAPI> {
                override fun onResponse(call: Call<MusicAPI>, response: Response<MusicAPI>) {
                    songSuggest.clear()
                    val musicList = response.body()
                    for (music in musicList!!){
                        songSuggest.add(Song(
                            thisId = music.id.toLong(),
                            thisTile = music.title,
                            thisArtist = music.artist,
                            thisAlbum = "",
                            dateModifier = "",
                            favourite = false,
                            imageUri = music.coverURI,
                            songUri = music.songURI))
                    }
                    songSSAdapter.notifyDataSetChanged()
                }
                override fun onFailure(call: Call<MusicAPI>, t: Throwable) = Unit
            })

            HomeFragment.loginAPI.getNewSingers().enqueue(object : Callback<SingerAPI>{
                override fun onResponse(call: Call<SingerAPI>, response: Response<SingerAPI>) {
                    newSinger.clear()
                    val singerList = response.body()
                    for (singer in singerList!!){
                        newSinger.add(Artist(
                            id = singer.Id.toLong(),
                            name = singer.name,
                            avatarID = singer.avatarURI,
                            description = null
                        ))
                    }
                    setSingerList()
                }
                override fun onFailure(call: Call<SingerAPI>, t: Throwable) {
                    Toast.makeText(context, "Failed to get data!", Toast.LENGTH_SHORT).show()
                }
            })

            HomeFragment.loginAPI.getNewSongs().enqueue(object : Callback<MusicAPI> {
                override fun onResponse(call: Call<MusicAPI>, response: Response<MusicAPI>) {
                    newMusic.clear()
                    val musicList = response.body()
                    for (music in musicList!!){
                            newMusic.add(Song(
                                thisId = music.id.toLong(),
                                thisTile = music.title,
                                thisArtist = music.artist,
                                thisAlbum = "",
                                dateModifier = "",
                                favourite = false,
                                imageUri = music.coverURI,
                                songUri = music.songURI))
                    }
                    songAdapter.notifyDataSetChanged()
                }
                override fun onFailure(call: Call<MusicAPI>, t: Throwable) {
                    Toast.makeText(context, "Failed to get data!", Toast.LENGTH_SHORT).show()
                }
            })

            HomeFragment.loginAPI.getNewAlbums().enqueue(object : Callback<AlbumAPI> {
                override fun onResponse(call: Call<AlbumAPI>, response: Response<AlbumAPI>) {
                    newAlbum.clear()
                    val albumList = response.body()
                    for (album in albumList!!){
                        newAlbum.add(
                            AlbumItem(
                            id = album.id.toLong(),
                            name = album.name,
                            singer_name = album.artist,
                            image = album.cover
                            )
                        )
                    }
                }
                override fun onFailure(call: Call<AlbumAPI>, t: Throwable) {
                    Toast.makeText(context, "Failed to get data!", Toast.LENGTH_SHORT).show()
                }
            })

            withContext(Dispatchers.Main){
                songAdapter.notifyDataSetChanged()
                albumAdapter.notifyDataSetChanged()
                singerAdapter.notifyDataSetChanged()
                songSSAdapter.notifyDataSetChanged()
            }
        }


    }

}