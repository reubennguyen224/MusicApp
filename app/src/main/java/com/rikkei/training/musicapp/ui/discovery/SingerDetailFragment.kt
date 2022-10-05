package com.rikkei.training.musicapp.ui.discovery

import android.annotation.SuppressLint
import android.net.Uri
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
import com.rikkei.training.musicapp.ui.HomeFragment
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.adapter.SingerDetailAdapter
import com.rikkei.training.musicapp.databinding.FragmentSingerDetailBinding
import com.rikkei.training.musicapp.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.abs

class SingerDetailFragment : Fragment() {
    private var _binding: FragmentSingerDetailBinding? = null
    private val binding get() = _binding!!

    private var singerList = Singer()
    private lateinit var singer: Artist

    companion object {
        val songList = ArrayList<Song>()
        var position = 0

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSingerDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        arguments?.let {
            position = it.getInt("position")
            when (it.getString("fromWhere")) {
                "discovery" -> {
                    singerList = DiscoveryFragment.newSinger
                    singer = singerList[position]
                }
            }
        }
        callingAPI(singerList[position].id.toInt())
        return view
    }

    val album = ArrayList<Song>()
    val list = arrayListOf(
        SingerDetail("Bài hát", songList),
        SingerDetail("Album", album)
    )
    private val adapter = SingerDetailAdapter(list)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.listSong.adapter = adapter
        binding.listSong.layoutManager = LinearLayoutManager(context)

        Glide.with(requireContext())
            .load(singer.avatarID)
            .centerCrop()
            .into(binding.imgAvatarArtist)

        binding.appbar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (abs(verticalOffset) - appBarLayout!!.totalScrollRange == 0) { // collapsing toolbar
                collapsingToolbar()
            } else { //expand toolbar
                expandToolbar()
            }
        }

        binding.btnPlayShuffle.setOnClickListener {
            val string = "SingerShuffleFragment"
            val uri = Uri.parse("android-app://com.rikkei.training.musicapp/play/${string}/${0}")
            findNavController().navigate(uri)
        }

    }

    private fun collapsingToolbar() {
        binding.btnPlayShuffle.visibility = View.GONE
        binding.btnBack.setImageResource(R.drawable.ic_back_button)
        binding.titleSinger.visibility = View.GONE
        binding.numSong.visibility = View.GONE
        binding.titleFragment.visibility = View.VISIBLE
        binding.titleFragment.text = singer.name
        binding.titleSinger.visibility = View.GONE
        binding.numSong.visibility = View.GONE
    }

    @SuppressLint("SetTextI18n")
    private fun expandToolbar() {
        binding.btnPlayShuffle.visibility = View.VISIBLE
        binding.titleSinger.visibility = View.VISIBLE
        binding.numSong.visibility = View.VISIBLE
        binding.titleSinger.text = singer.name
        binding.titleFragment.visibility = View.GONE
        binding.numSong.text = "${songList.size} bài hát"
    }

    private fun callingAPI(position: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            HomeFragment.loginAPI.getArtistItem(position).enqueue(object : Callback<MusicAPI> {
                override fun onResponse(call: Call<MusicAPI>, response: Response<MusicAPI>) {
                    songList.clear()
                    val musicList = response.body()
                    for (music in musicList!!) {
                        songList.add(
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
                    adapter.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<MusicAPI>, t: Throwable) {
                    Toast.makeText(context, "Failed to get music!", Toast.LENGTH_SHORT).show()
                }
            })
//            withContext(Dispatchers.Main){
//                adapter.notifyDataSetChanged()
//            }
        }

    }
}