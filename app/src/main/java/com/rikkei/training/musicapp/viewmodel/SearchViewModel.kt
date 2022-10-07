package com.rikkei.training.musicapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class SearchViewModel: ViewModel() {
    private var _localSongList = MutableLiveData<ArrayList<Song>>()
    private var _internetSongList = MutableLiveData<ArrayList<Song>>()

    fun getLocalList(query: String): LiveData<ArrayList<Song>>{
        return getLocalSongList(query)
    }

    fun getInternetList(query: String): LiveData<ArrayList<Song>>{
        return getInternetSongList(query)
    }

    private fun getLocalSongList(query: String): MutableLiveData<ArrayList<Song>>{
        val localSong = MutableLiveData<ArrayList<Song>>()
        val songList = ArrayList<Song>()
        for (song in PersonalFragment.songlist){
            if (song.thisTile.lowercase().contains(query))
                songList.add(song)
        }
        localSong.postValue(songList)
        return localSong
    }

    private fun getInternetSongList(query: String): MutableLiveData<ArrayList<Song>>{
        val internetSong = MutableLiveData<ArrayList<Song>>()
        viewModelScope.launch {
            val song = ArrayList<Song>()
            withContext(Dispatchers.IO){
                HomeFragment.loginAPI.getSearchRequest(query).enqueue(object : Callback<MusicAPI> {
                    override fun onResponse(call: Call<MusicAPI>, response: Response<MusicAPI>) {
                        val musicList = response.body()
                        for (music in musicList!!){
                            song.add(Song(
                                thisId = music.id.toLong(),
                                thisTile = music.title,
                                thisArtist = music.artist,
                                thisAlbum = "",
                                dateModifier = "",
                                favourite = false,
                                imageUri = music.coverURI,
                                songUri = music.songURI))

                        }
                    }

                    override fun onFailure(call: Call<MusicAPI>, t: Throwable) {
                        Log.e("API Failed", "Can't get data from API")
                    }
                })
            }
            _internetSongList.postValue(song)
            internetSong.postValue(song)
        }
        return internetSong
    }
}