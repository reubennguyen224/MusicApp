package com.rikkei.training.musicapp.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rikkei.training.musicapp.model.MusicAPI
import com.rikkei.training.musicapp.model.Song
import com.rikkei.training.musicapp.model.SongDetail
import com.rikkei.training.musicapp.ui.HomeFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchViewModel : ViewModel() {
    private var _localSongList = MutableLiveData<ArrayList<Song>>()
    private var _internetSongList = MutableLiveData<ArrayList<Song>>()

    companion object {
        val musicListSearch = ArrayList<Song>()
        val musicListInternetSearch = ArrayList<Song>()
        val list = ArrayList<SongDetail>()
    }

    fun getList(): MutableLiveData<ArrayList<SongDetail>> {
        val songDetail = MutableLiveData<ArrayList<SongDetail>>()
        viewModelScope.launch {
            list.clear()
            val tmpList = ArrayList<SongDetail>()
            tmpList.add(SongDetail("Nhạc trên thiết bị", musicListSearch, null))
            tmpList.add(SongDetail("Nhạc trực tuyến", musicListInternetSearch, null))
            list.addAll(tmpList)
            songDetail.postValue(tmpList)
        }
        return songDetail
    }

    fun setContent(query: String?) {
        viewModelScope.launch {
            musicListSearch.clear()
            musicListInternetSearch.clear()
            withContext(Dispatchers.IO) {
                if (query != null) {
                    val userInput = query.lowercase()
                    getLocalSongList(userInput)

                    getInternetSongList(query = userInput)
                    if (userInput == "") {
                        musicListSearch.clear()
                        musicListInternetSearch.clear()
                    }
                }
            }

        }
    }

    private fun getLocalSongList(query: String) {
        musicListSearch.clear()
        val songList = ArrayList<Song>()
        for (song in PersonalViewModel.songArraylist) {
            if (song.thisTile.lowercase().contains(query))
                songList.add(song)

        }
        musicListSearch.addAll(songList)
    }

    private fun getInternetSongList(query: String) {
        viewModelScope.launch {
            val song = ArrayList<Song>()
            withContext(Dispatchers.IO) {
                HomeFragment.loginAPI.getSearchRequest(query).enqueue(object : Callback<MusicAPI> {
                    override fun onResponse(call: Call<MusicAPI>, response: Response<MusicAPI>) {
                        val musicList = response.body()
                        for (music in musicList!!) {
                            song.add(
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
                        musicListInternetSearch.addAll(song)
                    }

                    override fun onFailure(call: Call<MusicAPI>, t: Throwable) {
                        Log.e("API Failed", "Can't get data from API")
                    }
                })
            }
        }
    }
}