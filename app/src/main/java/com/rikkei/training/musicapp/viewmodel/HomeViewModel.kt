package com.rikkei.training.musicapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rikkei.training.musicapp.model.Album
import com.rikkei.training.musicapp.model.Artist
import com.rikkei.training.musicapp.model.Song

class HomeViewModel: ViewModel() {

    private var songlist = MutableLiveData<ArrayList<Song>>()
    private var albumList = MutableLiveData<Album>()
    private var singerList = MutableLiveData<ArrayList<Artist>>()

    fun getLocalSongList(): LiveData<ArrayList<Song>>{
        return songlist
    }

    fun getLocalAlbumList(): LiveData<Album>{
        return albumList
    }

    fun getLocalSingerList(): LiveData<ArrayList<Artist>>{
        return singerList
    }
}