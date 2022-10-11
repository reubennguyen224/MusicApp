package com.rikkei.training.musicapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
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

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private var _localSongList = MutableLiveData<ArrayList<Song>>()
    private var _internetSongList = MutableLiveData<ArrayList<Song>>()

    companion object {
        var musicListSearch = ArrayList<Song>()
        var musicListInternetSearch = ArrayList<Song>()
        var list = ArrayList<SongDetail>()
    }

    init {
//        getLocalSongList(SearchFragment.searchQuery)
//        getInternetSongList(SearchFragment.searchQuery)
//        musicListInternetSearch = getInterSongList(SearchFragment.searchQuery)
//        musicListSearch = getLocSongList(SearchFragment.searchQuery)
    }

    fun getList(): MutableLiveData<ArrayList<SongDetail>> {
        val songDetail = MutableLiveData<ArrayList<SongDetail>>()
        val tmpList = ArrayList<SongDetail>()
        viewModelScope.launch {
            tmpList.add(SongDetail("Nhạc trên thiết bị", musicListSearch, null))
            tmpList.add(SongDetail("Nhạc trực tuyến", musicListInternetSearch, null))
        }
        songDetail.postValue(tmpList)
        list.addAll(tmpList)
        return songDetail
    }

    fun setContent(query: String?) {
        //viewModelScope.launch {
            musicListSearch.clear()
           // musicListInternetSearch.clear()
            //withContext(Dispatchers.IO) {
                if (query != null) {
                    val userInput = query.lowercase()
                    musicListSearch.addAll( getLocSongList(query = userInput))
                    musicListInternetSearch= getInterSongList(query = userInput)
                    if (userInput == "") {
                        musicListSearch.clear()
                        musicListInternetSearch.clear()
                    }
                //}
            }

        //}
    }

    fun getLocalSongList(query: String): MutableLiveData<ArrayList<Song>> {
        val songs = MutableLiveData<ArrayList<Song>>()
        viewModelScope.launch {
            val songList = ArrayList<Song>()
            withContext(Dispatchers.IO){
                for (song in PersonalViewModel.songArraylist) {
                    if (song.thisTile.lowercase().contains(query))
                        songList.add(song)
                }
                songs.postValue(songList)
                musicListSearch = (songList)
            }
        }
        return songs
    }

    fun getLocSongList(query: String):ArrayList<Song> {
        val songList = ArrayList<Song>()
        for (song in PersonalViewModel.songArraylist) {
            if (song.thisTile.lowercase().contains(query))
                songList.add(song)
        }
        return songList
    }

    fun getInternetSongList(query: String): MutableLiveData<ArrayList<Song>> {
        val songlist = MutableLiveData<ArrayList<Song>>()
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
                        songlist.postValue(song)
                        musicListInternetSearch = (song)
                    }

                    override fun onFailure(call: Call<MusicAPI>, t: Throwable) {
                        Log.e("API Failed", "Can't get data from API")
                    }
                })
            }

        }
        return songlist
    }

    fun getInterSongList(query: String): ArrayList<Song> {
        val song = ArrayList<Song>()
        viewModelScope.launch {
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
                    }

                    override fun onFailure(call: Call<MusicAPI>, t: Throwable) {
                        Log.e("API Failed", "Can't get data from API")
                    }
                })
            }
        }
        return song
    }
}