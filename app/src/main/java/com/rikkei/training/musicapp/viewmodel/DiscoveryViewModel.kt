package com.rikkei.training.musicapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rikkei.training.musicapp.HomeFragment
import com.rikkei.training.musicapp.model.Album
import com.rikkei.training.musicapp.model.AlbumAPI
import com.rikkei.training.musicapp.model.AlbumItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiscoveryViewModel: ViewModel() {

    private var _newAlbumList =  MutableLiveData<Album>()
    fun getNewAlbum(): LiveData<Album>?{
        return _newAlbumList
    }
    init {
        _newAlbumList = getNewAlbumAPI()!!
    }

    private fun getNewAlbumAPI(): MutableLiveData<Album>?{
        val albumListLiveData: MutableLiveData<Album> = MutableLiveData<Album>()

        CoroutineScope(Dispatchers.Default).launch{
            val album = Album()
            launch(Dispatchers.IO) {
                HomeFragment.loginAPI.getNewAlbums().enqueue(object : Callback<AlbumAPI>{
                    override fun onResponse(call: Call<AlbumAPI>, response: Response<AlbumAPI>) {
                        val albumList = response.body()!!
                        for (item in albumList){
                            album.add(
                                AlbumItem(
                                    id = item.id.toLong(),
                                    name = item.name,
                                    singer_name = item.artist,
                                    image = item.cover
                            ))
                        }
                    }

                    override fun onFailure(call: Call<AlbumAPI>, t: Throwable) {
                        Log.e("API Failed", "Can't get data from API")
                    }
                })
            }
            withContext(Dispatchers.Default){
                albumListLiveData.postValue(album)
            }
        }
        return albumListLiveData
    }
}