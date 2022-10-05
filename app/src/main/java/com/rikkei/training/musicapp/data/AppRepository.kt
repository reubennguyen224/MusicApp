package com.rikkei.training.musicapp.data

import android.app.Application
import com.rikkei.training.musicapp.HomeFragment

class AppRepository(app: Application) {

    suspend fun getNewAlbumFromAPI() = HomeFragment.loginAPI.getNewAlbums()


}