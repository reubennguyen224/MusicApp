package com.rikkei.training.musicapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rikkei.training.musicapp.HomeFragment
import com.rikkei.training.musicapp.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiscoveryViewModel : ViewModel() {

    private var _newAlbumList = MutableLiveData<Album>()
    private var _newSongList = MutableLiveData<ArrayList<Song>>()
    private var _newSingerList = MutableLiveData<Singer>()
    private var _songSuggestList = MutableLiveData<ArrayList<Song>>()

    fun getNewAlbum(): LiveData<Album> {
        return _newAlbumList
    }

    fun getNewSinger():LiveData<Singer>{
        return _newSingerList
    }

    fun getNewSong():LiveData<ArrayList<Song>>{
        return _newSongList
    }

    fun getSongSuggest():LiveData<ArrayList<Song>>{
        return _songSuggestList
    }

    init {
        _newAlbumList = getNewAlbumAPI()
        _newSingerList = getNewSingerAPI()
        _newSongList = getNewSongList()
        _songSuggestList = getSongSuggestList()
    }

    private fun getSongSuggestList(): MutableLiveData<ArrayList<Song>>{
        val songSuggestListLiveData = MutableLiveData<ArrayList<Song>>()
        viewModelScope.launch {
            val song = ArrayList<Song>()
            withContext(Dispatchers.IO){
                HomeFragment.loginAPI.getSongSS().enqueue(object : Callback<MusicAPI> {
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
                        songSuggestListLiveData.postValue(song)
                    }
                    override fun onFailure(call: Call<MusicAPI>, t: Throwable) = Unit
                })
            }
        }
        return songSuggestListLiveData
    }

    private fun getNewSongList(): MutableLiveData<ArrayList<Song>>{
        val songListLiveData = MutableLiveData<ArrayList<Song>>()
        viewModelScope.launch {
            val song = ArrayList<Song>()
            withContext(Dispatchers.IO){
                HomeFragment.loginAPI.getNewSongs().enqueue(object : Callback<MusicAPI> {
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
                        songListLiveData.postValue(song)
                    }

                    override fun onFailure(call: Call<MusicAPI>, t: Throwable) {
                        Log.e("API Failed", "Can't get data from API")
                    }
                })
            }
        }
        return songListLiveData
    }

    private fun getNewAlbumAPI(): MutableLiveData<Album> {
        val albumListLiveData = MutableLiveData<Album>()

        viewModelScope.launch {
            val album = Album()
            withContext(Dispatchers.IO) {
                HomeFragment.loginAPI.getNewAlbums().enqueue(object : Callback<AlbumAPI> {
                    override fun onResponse(call: Call<AlbumAPI>, response: Response<AlbumAPI>) {
                        val albumList = response.body()!!
                        for (item in albumList) {
                            album.add(
                                AlbumItem(
                                    id = item.id.toLong(),
                                    name = item.name,
                                    singer_name = item.artist,
                                    image = item.cover
                                )
                            )
                        }
                        albumListLiveData.postValue(album)
                    }

                    override fun onFailure(call: Call<AlbumAPI>, t: Throwable) {
                        Log.e("API Failed", "Can't get data from API")
                    }
                })
            }
        }
        return albumListLiveData
    }

    private fun getNewSingerAPI(): MutableLiveData<Singer>{
        val singerListLiveData = MutableLiveData<Singer>()

        viewModelScope.launch {
            val singer = Singer()
            withContext(Dispatchers.IO){
                HomeFragment.loginAPI.getNewSingers().enqueue(object : Callback<SingerAPI> {
                    override fun onResponse(call: Call<SingerAPI>, response: Response<SingerAPI>) {
                        val singerList = response.body()
                        for (item in singerList!!) {
                            singer .add(
                                Artist(
                                    id = item.Id.toLong(),
                                    name = item.name,
                                    avatarID = item.avatarURI,
                                    description = null
                                )
                            )
                        }
                        singerListLiveData.postValue(singer)
                    }

                    override fun onFailure(call: Call<SingerAPI>, t: Throwable) {
                        Log.e("API Failed", "Can't get data from API")
                    }
                })
            }
        }
        return singerListLiveData
    }
}