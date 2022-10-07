package com.rikkei.training.musicapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rikkei.training.musicapp.model.Song
import kotlinx.coroutines.launch

class LocalFavouriteViewModel:ViewModel() {
    private var _favouriteSongList = MutableLiveData<ArrayList<Song>>()
    fun getFavouriteSongList(): LiveData<ArrayList<Song>>{
        return _favouriteSongList
    }

    companion object {
        val favouriteList = ArrayList<Song>()
    }

    init {
        _favouriteSongList = getFavouriteList()
    }

    private fun getFavouriteList():MutableLiveData<ArrayList<Song>>{
        val favour = MutableLiveData<ArrayList<Song>>()
        viewModelScope.launch {
            favour.postValue(favouriteList)
        }
        return favour
    }
}