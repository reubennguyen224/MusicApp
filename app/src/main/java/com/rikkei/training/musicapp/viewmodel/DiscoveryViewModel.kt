package com.rikkei.training.musicapp.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rikkei.training.musicapp.model.*
import com.rikkei.training.musicapp.ui.HomeFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiscoveryViewModel(application: Application) : AndroidViewModel(application) {

    private var _newAlbumList = MutableLiveData<Album>()
    private var _newSongList = MutableLiveData<ArrayList<Song>>()
    private var _newSingerList = MutableLiveData<Singer>()
    private var _songSuggestList = MutableLiveData<ArrayList<Song>>()

    companion object {
        val newMusicList = ArrayList<Song>()
        val newAlbumList = Album()
        val newSingerList = Singer()
        val songSuggestList = ArrayList<Song>()
    }

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
        songSuggestList.clear()
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
                        songSuggestList.addAll(song)
                        songSuggestListLiveData.postValue(song)
                    }
                    override fun onFailure(call: Call<MusicAPI>, t: Throwable) = Unit
                })
            }
        }
        return songSuggestListLiveData
    }

    private fun getNewSongList(): MutableLiveData<ArrayList<Song>>{
        newMusicList.clear()
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
                        newMusicList.addAll(song)
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
        newAlbumList.clear()
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
                        newAlbumList.addAll(album)
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
        newSingerList.clear()
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
                        newSingerList.addAll(singer)
                        singerListLiveData.postValue(singer)
                    }

                    override fun onFailure(call: Call<SingerAPI>, t: Throwable) {
                        Log.e("API Failed", "Can't get data from API")
                        Toast.makeText(getApplication(), "Không thể làm mới dữ liệu", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
        return singerListLiveData
    }
}