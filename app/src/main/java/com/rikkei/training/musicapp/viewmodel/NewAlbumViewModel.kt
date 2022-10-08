package com.rikkei.training.musicapp.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rikkei.training.musicapp.model.Album
import com.rikkei.training.musicapp.model.AlbumItem
import com.rikkei.training.musicapp.model.MusicAPI
import com.rikkei.training.musicapp.model.Song
import com.rikkei.training.musicapp.ui.HomeFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewAlbumViewModel(application: Application) : AndroidViewModel(application) {

    fun getAlbumLocalItemList(): LiveData<ArrayList<Song>> {
        return getAlbumLocalItem()
    }

    fun getAlbumItemList(): LiveData<ArrayList<Song>> {
        return getAlbumItem()
    }

    fun getAlbumDetail(): LiveData<AlbumItem>{
        val album = MutableLiveData<AlbumItem>()
        album.postValue(item)
        return album
    }

    companion object {
        val album = ArrayList<Song>()
        val albumList = Album()
        lateinit var item: AlbumItem
    }

    fun setCompanionObjectData(from: String, position: Int){
        albumList.clear()
        when(from){
            "local" -> {
                albumList.addAll(PersonalViewModel.albumArrayList)
                item = albumList[position]
            }
            "internet" -> {
                albumList.addAll(DiscoveryViewModel.newAlbumList)
                item = albumList[position]
            }
        }
    }

    private fun getAlbumLocalItem(): MutableLiveData<ArrayList<Song>> {
        album.clear()
        val albumItem = MutableLiveData<ArrayList<Song>>()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val songList = PersonalViewModel.songArraylist
                for (song in songList) {
                    if (song.thisAlbum == item.name) album.add(song)
                }
            }
            albumItem.postValue(album)
        }
        return albumItem
    }

    private fun getAlbumItem(): MutableLiveData<ArrayList<Song>> {
        album.clear()
        val albumItem = MutableLiveData<ArrayList<Song>>()
        viewModelScope.launch {
            val songs = ArrayList<Song>()
            withContext(Dispatchers.IO) {
                HomeFragment.loginAPI.getAlbumItem(item.id.toInt())
                    .enqueue(object : Callback<MusicAPI> {
                        override fun onResponse(
                            call: Call<MusicAPI>,
                            response: Response<MusicAPI>
                        ) {
                            val musicList = response.body()
                            for (music in musicList!!) {
                                songs.add(
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
                            album.addAll(songs)
                            albumItem.postValue(songs)
                        }

                        override fun onFailure(call: Call<MusicAPI>, t: Throwable) {
                            Log.e("API Failed", "Can't get data through API")
                            Toast.makeText(getApplication(), "", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        }
        return albumItem
    }
}