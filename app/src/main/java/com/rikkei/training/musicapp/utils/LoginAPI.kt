package com.rikkei.training.musicapp.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rikkei.training.musicapp.model.*
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface LoginAPI {

    @FormUrlEncoded
    @POST("login.php")
    fun login(
        @Field("username") username: String,
        @Field("password") password: String,
    ): Call<List<UserAPI>>

    @FormUrlEncoded
    @POST("register.php")
    fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("firstname") firstname: String,
        @Field("lastname") lastname: String,
        @Field("dob") dob: String,
        @Field("address") address: String,
        @Field("avataruri") avataruri: String,
    ): Call<ListMessage>

    @FormUrlEncoded
    @POST("updateUser.php")
    fun updateUser(
        @Field("password") password: String,
        @Field("firstname") firstname: String,
        @Field("lastname") lastname: String,
        @Field("address") address: String,
        @Field("avatarURI") avataruri: String,
        @Field("dob") dob: String,
        @Field("userID") userID: String
    ): Call<List<UserAPI>>

    @Multipart
    @POST("uploadImage.php")
    fun uploadPhoto(
        @Part file: MultipartBody.Part
    ): Call<Message>

    @GET("getAlbum.php")
    fun getNewAlbums(): Call<AlbumAPI>

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

class LoginClient {
    companion object {
        private const val BASE_URL = "https://hoang2204.000webhostapp.com/server/"
        lateinit var gson: Gson
        var retrofit: Retrofit? = null
        private val builder = OkHttpClient.Builder()
            .readTimeout(9000, TimeUnit.MILLISECONDS) //thoi gian doc du lieu
            .writeTimeout(9000, TimeUnit.MILLISECONDS) // thoi gian ghi du lieu
            .connectTimeout(15000, TimeUnit.MILLISECONDS) //thoi gian cho server phan hoi
            .retryOnConnectionFailure(true)
        private val authInterceptor = Interceptor { chain ->
            val newUrl = chain.request().url
                .newBuilder()
                .build()
            val newRequest = chain.request()
                .newBuilder()
                .url(newUrl)
                .build()
            chain.proceed(newRequest)
        }
        private val apiClient = OkHttpClient().newBuilder().addInterceptor(authInterceptor).build()

        fun getInstance(): Retrofit {
            if (retrofit == null) {
                gson = GsonBuilder()
                    .setLenient()
                    .create()

                retrofit = Retrofit.Builder()
                    .client(apiClient)
                    .baseUrl(BASE_URL)
                    .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            }
            return retrofit!!
        }
    }
}

