package com.rikkei.training.musicapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rikkei.training.musicapp.model.Album
import com.rikkei.training.musicapp.model.Artist
import com.rikkei.training.musicapp.model.Song

class LocalViewModel : ViewModel() {
    private var _albumList = MutableLiveData<Album>()

    fun getAlbumList(): LiveData<Album> {
        val albums = MutableLiveData<Album>()
        albums.postValue(PersonalViewModel.albumArrayList)
        return albums
    }

    fun getSongList(): LiveData<ArrayList<Song>> {
        val songs = MutableLiveData<ArrayList<Song>>()
        songs.postValue(PersonalViewModel.songArraylist)
        return songs
    }

    fun getSingerList(): LiveData<ArrayList<Artist>> {
        val singers = MutableLiveData<ArrayList<Artist>>()
        singers.postValue(PersonalViewModel.singerArrayList)
        return singers
    }

    fun getDownloadList(): LiveData<ArrayList<Song>> {
        val songs = MutableLiveData<ArrayList<Song>>()
        songs.postValue(PersonalViewModel.listMusicFile)
        return songs
    }

}