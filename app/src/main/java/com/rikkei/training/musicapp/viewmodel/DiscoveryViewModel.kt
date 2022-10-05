package com.rikkei.training.musicapp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.rikkei.training.musicapp.data.AppRepository
import com.rikkei.training.musicapp.utils.Resource
import kotlinx.coroutines.Dispatchers

class DiscoveryViewModel(application: Application): ViewModel() {

    private val appRepository: AppRepository = AppRepository(application)

    fun getNewAlbumAPI() = liveData(Dispatchers.IO){
        try {
            emit(Resource.success(appRepository.getNewAlbumFromAPI()))
        } catch (ex: Exception){
            emit(Resource.failed(null, ex.message ?: "Lỗi chưa xác định"))
        }
    }

    class AppViewModelFactory(private val application: Application): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DiscoveryViewModel::class.java)){

                return DiscoveryViewModel(application = application) as T
            }
            throw IllegalArgumentException("Unable construct ViewModel")
        }
    }
}