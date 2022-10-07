package com.rikkei.training.musicapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rikkei.training.musicapp.model.Album
import com.rikkei.training.musicapp.model.Artist
import com.rikkei.training.musicapp.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewReleaseLocalViewModel : ViewModel() {
    private var _newAlbumLocalList = MutableLiveData<Album>()
    private var _newSongLocalList = MutableLiveData<ArrayList<Song>>()
    private var _newSingerLocalList = MutableLiveData<ArrayList<Artist>>()

    fun getNewAlbumLocal(): LiveData<Album> {
        return _newAlbumLocalList
    }

    fun getNewSongLocal(): LiveData<ArrayList<Song>> {
        return _newSongLocalList
    }

    fun getNewSingerLocal(): LiveData<ArrayList<Artist>> {
        return _newSingerLocalList
    }

    companion object {
        val songs = ArrayList<Song>()
        val albums = Album()
        val singers = ArrayList<Artist>()
    }

    init {
        _newAlbumLocalList = getNewAlbumList()
        _newSingerLocalList = getNewSingerList()
        _newSongLocalList = getNewSongList()
    }

    private fun getNewSongList(): MutableLiveData<ArrayList<Song>> {
        val newSong = MutableLiveData<ArrayList<Song>>()
        viewModelScope.launch {
            val allSongs = PersonalViewModel.songArraylist
            songs.clear()
            allSongs.sortBy {
                it.dateModifier
            }
            for (song in allSongs) {
                if (songs.size <= 10) {
                    songs.add(song)
                }
            }
            newSong.postValue(songs)
        }
        return newSong
    }

    private fun getNewAlbumList(): MutableLiveData<Album> {
        val newAlbum = MutableLiveData<Album>()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                albums.clear()
                for (album in PersonalViewModel.albumArrayList) {
                    if (albums.size <= 10) {
                        albums.add(album)
                    }
                }
                newAlbum.postValue(albums)
            }
        }
        return newAlbum
    }

    private fun getNewSingerList(): MutableLiveData<ArrayList<Artist>> {
        val newSinger = MutableLiveData<ArrayList<Artist>>()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                singers.clear()
                for (singer in PersonalViewModel.singerArrayList) {
                    if (singers.size <= 10) {
                        singers.add(singer)
                    }
                }
                newSinger.postValue(singers)
            }
        }
        return newSinger
    }
}