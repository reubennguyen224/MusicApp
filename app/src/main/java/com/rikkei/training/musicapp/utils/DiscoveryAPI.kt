package com.rikkei.training.musicapp.utils

import com.rikkei.training.musicapp.model.AlbumAPI
import com.rikkei.training.musicapp.model.ListMessage
import com.rikkei.training.musicapp.model.MusicAPI
import com.rikkei.training.musicapp.model.SingerAPI
import retrofit2.Call
import retrofit2.http.*

interface DiscoveryAPI {
    @GET("getAlbum.php")
    fun getNewAlbums():Call<AlbumAPI>

    @GET("getArtist.php")
    fun getNewSingers(): Call<SingerAPI>

    @GET("getNewSong.php")
    fun getNewSongs(): Call<MusicAPI>

    @GET("getAlbumItem.php")
    fun getAlbumItem(@Query("id") id: Int): Call<MusicAPI>

    @GET("getSongSS.php")
    fun getSongSS(): Call<MusicAPI>

    @GET("searchSong.php")
    fun getSearchRequest(@Query("key") name: String): Call<MusicAPI>

    @GET("getArtistItem.php")
    fun getArtistItem(@Query("id") id: Int): Call<MusicAPI>

    @FormUrlEncoded
    @POST("updateStream.php")
    fun updateNumberOfStream(@Field("Id") id: Int): Call<ListMessage>
}