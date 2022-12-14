package com.rikkei.training.musicapp.viewmodel

import android.app.Application
import android.os.Bundle
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

class SingerDetailViewModel(application: Application) : AndroidViewModel(application) {

    private var _artistDetail = MutableLiveData<ArrayList<SingerDetail>>()

    companion object {
        var songList = ArrayList<Song>()
        val album = ArrayList<Song>()
        var position = 0
        var item =  DiscoveryViewModel.newSingerList[position]
    }

    fun getArtistDetail(): LiveData<ArrayList<SingerDetail>>{
        return _artistDetail
    }
    private var singerList = Singer()

    init {
        songList = getInitItem()
        _artistDetail = pageAdapter()
    }

    fun setCompanionObject(bundle: Bundle) {
        position = bundle.getInt("position")
        when (bundle.getString("fromWhere")) {
            "discovery" -> {
                singerList = DiscoveryViewModel.newSingerList

            }
        }
        item = singerList[position]
    }

    private fun pageAdapter(): MutableLiveData<ArrayList<SingerDetail>> {
        val page = MutableLiveData<ArrayList<SingerDetail>>()
        viewModelScope.launch {
            val singerDetail = ArrayList<SingerDetail>()
            withContext(Dispatchers.IO) {
                singerDetail.add(SingerDetail("Bài hát", songList))
                singerDetail.add(SingerDetail("Album", album))
            }
            page.postValue(singerDetail)
        }
        return page
    }

    fun getSingerDetail(): LiveData<Artist> {
        val singer = MutableLiveData<Artist>()
        singer.postValue(item)
        return singer
    }

    fun getMusicListOfSinger(): LiveData<ArrayList<Song>> {
        return getSingerItem()
    }

    private fun getSingerItem(): MutableLiveData<ArrayList<Song>> {
        val musicList = MutableLiveData<ArrayList<Song>>()
        viewModelScope.launch {
            val singerItem = ArrayList<Song>()
            withContext(Dispatchers.IO) {
                HomeFragment.loginAPI.getArtistItem(item.id.toInt())
                    .enqueue(object : Callback<MusicAPI> {
                        override fun onResponse(
                            call: Call<MusicAPI>,
                            response: Response<MusicAPI>
                        ) {
                            //songList.clear()
                            val songs = response.body()
                            for (music in songs!!) {
                                singerItem.add(
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
                            songList = singerItem
                            musicList.postValue(singerItem)
                        }

                        override fun onFailure(call: Call<MusicAPI>, t: Throwable) {
                            Toast.makeText(
                                getApplication(),
                                "Failed to get music!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }
            withContext(Dispatchers.Main){

            }
        }
        return musicList
    }

    private fun getInitItem(): ArrayList<Song> {
        val musicList = ArrayList<Song>()
        viewModelScope.launch {
            val singerItem = ArrayList<Song>()
            withContext(Dispatchers.IO) {
                HomeFragment.loginAPI.getArtistItem(item.id.toInt())
                    .enqueue(object : Callback<MusicAPI> {
                        override fun onResponse(
                            call: Call<MusicAPI>,
                            response: Response<MusicAPI>
                        ) {
                            val songs = response.body()
                            for (music in songs!!) {
                                singerItem.add(
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
                            Toast.makeText(
                                getApplication(),
                                "Failed to get music!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }
        }
        return musicList
    }
}