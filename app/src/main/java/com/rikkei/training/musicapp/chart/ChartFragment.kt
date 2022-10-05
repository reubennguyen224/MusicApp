package com.rikkei.training.musicapp.chart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.rikkei.training.musicapp.HomeFragment
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.adapter.ChartAdapter
import com.rikkei.training.musicapp.databinding.FragmentChartBinding
import com.rikkei.training.musicapp.model.Chart
import com.rikkei.training.musicapp.model.Song
import com.rikkei.training.musicapp.model.Tracks
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ChartFragment : Fragment() {

    private var _binding: FragmentChartBinding? = null
    private val binding get() = _binding!!

    private val songList = ArrayList<Song>()
    private lateinit var trackList : Tracks

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChartBinding.inflate(inflater, container, false)
        val view = binding.root
        callAPi()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setView()
    }

    fun setView(){
        val adapter = ChartAdapter(songList)
        binding.listChart.adapter = adapter
        binding.listChart.layoutManager = LinearLayoutManager(context)
    }

    private fun callAPi(){
        HomeFragment.deezerAPI.getChart().enqueue(object : Callback<Chart> {
            override fun onResponse(call: Call<Chart>?, response: Response<Chart>?) {
                songList.clear()
                val chart = response?.body()
                trackList = chart!!.tracks
                for (tmp in trackList.data){
                    val artist = tmp.artist.name
                    val album = tmp.album.title
                    val imgUri = tmp.album.cover
                    val id = tmp.position
                    songList.add(Song(
                        thisArtist = artist,
                        thisId = id.toLong(),
                        thisTile = tmp.title,
                        imageUri = imgUri,
                        songUri = tmp.link,
                        favourite = false,
                        thisAlbum = album,
                        dateModifier = "")
                    )
                }
                setView()
            }
            override fun onFailure(call: Call<Chart>?, t: Throwable?) {
                Toast.makeText(context, "Failed to get data!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}