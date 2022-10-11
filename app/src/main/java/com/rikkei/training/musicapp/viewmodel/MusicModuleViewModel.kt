package com.rikkei.training.musicapp.viewmodel

import android.app.Application
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rikkei.training.musicapp.model.ListMessage
import com.rikkei.training.musicapp.model.Song
import com.rikkei.training.musicapp.ui.HomeFragment
import com.rikkei.training.musicapp.ui.moduleMusic.PlayMusicFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MusicModuleViewModel(application: Application) : AndroidViewModel(application) {
    private val musicList = MutableLiveData<ArrayList<Song>>()

    companion object{
        val listOfSongs = ArrayList<Song>()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateStream(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                HomeFragment.loginAPI.updateNumberOfStream(listOfSongs[PlayMusicFragment.songPosition - 1].thisId.toInt())
                    .enqueue(object :
                        Callback<ListMessage> {
                        override fun onResponse(
                            call: Call<ListMessage>,
                            response: Response<ListMessage>
                        ) {
                            val mesList = response.body()
                            for (tmp in mesList!!)
                                Log.d("Update Done!", tmp.message)
                        }

                        override fun onFailure(call: Call<ListMessage>, t: Throwable) {
                            Toast.makeText(
                                getApplication(),
                                "Something were wrong!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }
        }
    }

    fun getMusicList():LiveData<ArrayList<Song>>{
        val list = MutableLiveData<ArrayList<Song>>()
        viewModelScope.launch {
            val songs = ArrayList<Song>()
            withContext(Dispatchers.IO){
                songs.addAll(listOfSongs)
                list.postValue(songs)
            }
        }
        return list
    }

    fun getListFromLocal(isShuffle: Boolean){
        viewModelScope.launch {
            if (isShuffle){
                listOfSongs.clear()
                listOfSongs.addAll(PersonalViewModel.songArraylist)
            }else{
                listOfSongs.clear()
                listOfSongs.addAll(PersonalViewModel.songArraylist)
            }
        }
    }

    fun getListFromFavourite(isShuffle: Boolean){
        viewModelScope.launch {
            if (isShuffle){
                listOfSongs.clear()
                listOfSongs.addAll(LocalFavouriteViewModel.favouriteList)
                listOfSongs.shuffle()
            }else{
                listOfSongs.clear()
                listOfSongs.addAll(LocalFavouriteViewModel.favouriteList)
            }
        }

    }

    fun getListFromDiscovery(isSS: Boolean){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                if (isSS){
                    listOfSongs.clear()
                    listOfSongs.addAll(DiscoveryViewModel.songSuggestList)
                }else{
                    listOfSongs.clear()
                    listOfSongs.addAll(DiscoveryViewModel.newMusicList)
                }
            }

        }
    }

    fun getListFromAlbum(isShuffle: Boolean){
        viewModelScope.launch {
            if (!isShuffle){
                listOfSongs.clear()
                listOfSongs.addAll(NewAlbumViewModel.album)
            }else{
                listOfSongs.clear()
                listOfSongs.addAll(NewAlbumViewModel.album)
                listOfSongs.shuffle()
            }
        }

    }

    fun getListFromSinger(isShuffle: Boolean){
        viewModelScope.launch {
            if (!isShuffle){
                listOfSongs.clear()
                listOfSongs.addAll(SingerDetailViewModel.album)
            }else{
                listOfSongs.clear()
                listOfSongs.addAll(SingerDetailViewModel.album)
                listOfSongs.shuffle()
            }
        }

    }

    fun setListFromSearch(isLocal: Boolean){
        viewModelScope.launch {
            if (isLocal){
                listOfSongs.clear()
                listOfSongs.addAll(SearchViewModel.musicListSearch)
            }else{
                listOfSongs.clear()
                listOfSongs.addAll(SearchViewModel.musicListInternetSearch)
            }
        }

    }
}