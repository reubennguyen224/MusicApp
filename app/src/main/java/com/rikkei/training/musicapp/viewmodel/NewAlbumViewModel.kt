package com.rikkei.training.musicapp.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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

    private var _albumLocalItemList = MutableLiveData<ArrayList<Song>>()
    private var _albumItemList = MutableLiveData<ArrayList<Song>>()

    fun getAlbumLocalItemList(name: String): LiveData<ArrayList<Song>> {
        return getAlbumLocalItem(name)
    }

    fun getAlbumItemList(id: Int): LiveData<ArrayList<Song>> {
        return getAlbumItem(id)
    }

    companion object {
        val album = ArrayList<Song>()
    }

    private fun getAlbumLocalItem(albumName: String): MutableLiveData<ArrayList<Song>> {
        album.clear()
        val albumItem = MutableLiveData<ArrayList<Song>>()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val songList = PersonalViewModel.songArraylist
                for (song in songList) {
                    if (song.thisAlbum == albumName) album.add(song)
                }
            }
            albumItem.postValue(album)
        }
        return albumItem
    }

    private fun getAlbumItem(albumId: Int): MutableLiveData<ArrayList<Song>> {
        album.clear()
        val albumItem = MutableLiveData<ArrayList<Song>>()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                HomeFragment.loginAPI.getAlbumItem(albumId)
                    .enqueue(object : Callback<MusicAPI> {
                        override fun onResponse(
                            call: Call<MusicAPI>,
                            response: Response<MusicAPI>
                        ) {
                            val musicList = response.body()
                            for (music in musicList!!) {
                                album.add(
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
                            Log.e("API Failed", "Can't get data through API")
                            Toast.makeText(getApplication(), "", Toast.LENGTH_SHORT).show()
                        }
                    })
                albumItem.postValue(album)
            }
        }
        return albumItem
    }
}