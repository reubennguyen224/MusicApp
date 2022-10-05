package com.rikkei.training.musicapp.ui.header

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rikkei.training.musicapp.ui.HomeFragment
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.adapter.MusicAdapter
import com.rikkei.training.musicapp.adapter.MusicListAdapter
import com.rikkei.training.musicapp.databinding.FragmentSearchBinding
import com.rikkei.training.musicapp.model.MusicAPI
import com.rikkei.training.musicapp.model.Song
import com.rikkei.training.musicapp.model.SongDetail
import com.rikkei.training.musicapp.ui.personal.PersonalFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    companion object{
        val musicListSearch= ArrayList<Song>()
        val musicListInternetSearch= ArrayList<Song>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val view = binding.root
        musicListSearch.clear()
        musicListInternetSearch.clear()
        return view
    }
    val musicLocalListener = object : MusicAdapter.OnItemClickListener{
        override fun onItemClick(position: Int) {
            val bundle = Bundle()
            bundle.putInt("songPosition", position)
            bundle.putString("album", "MusicSearchAdapter")
            findNavController().navigate(R.id.playMusicFragment, bundle)
        }
    }
    val musicInternetListener = object : MusicAdapter.OnItemClickListener{
        override fun onItemClick(position: Int) {
            val bundle = Bundle()
            bundle.putInt("songPosition", position)
            bundle.putString("album", "MusicSearchInternetAdapter")
            findNavController().navigate(R.id.playMusicFragment, bundle)
        }
    }

    val list = arrayListOf(
        SongDetail("Nhạc trên thiết bị", musicListSearch, musicLocalListener),
        SongDetail("Nhạc trực tuyến", musicListInternetSearch, musicInternetListener)
    )
    val adapter = MusicListAdapter(list)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBackSearch.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.searchResultList.adapter = adapter
        binding.searchResultList.layoutManager = LinearLayoutManager(context)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                musicListSearch.clear()
                if (newText != null){
                    val userInput = newText.lowercase()
                    for (song in PersonalFragment.songlist){
                        if (song.thisTile.lowercase().contains(userInput) && musicListSearch.size < 6)
                            musicListSearch.add(song)
                    }
                    adapter.notifyDataSetChanged()
                    lifecycleScope.launch{
                        if (userInput != "")
                            callAPI(query = userInput)
                        adapter.notifyDataSetChanged()
                    }
                    if (userInput == ""){
                        musicListSearch.clear()
                        musicListInternetSearch.clear()
                    }
                }
                return true
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    fun callAPI(query: String){
        lifecycleScope.launch(Dispatchers.IO){
            HomeFragment.loginAPI.getSearchRequest(query).enqueue(object : Callback<MusicAPI>{
                override fun onResponse(call: Call<MusicAPI>, response: Response<MusicAPI>) {
                    musicListInternetSearch.clear()
                    val musicList = response.body()
                    for (music in musicList!!){
                        //for (music in tmp.data){
                            musicListInternetSearch.add(Song(
                                thisId = music.id.toLong(),
                                thisTile = music.title,
                                thisArtist = music.artist,
                                thisAlbum = "",
                                dateModifier = "",
                                favourite = false,
                                imageUri = music.coverURI,
                                songUri = music.songURI))
                        //}
                    }
                    adapter.notifyDataSetChanged()
                }
                override fun onFailure(call: Call<MusicAPI>, t: Throwable) = Unit
            })
            withContext(Dispatchers.Main){
                adapter.notifyDataSetChanged()
            }
        }

    }

}