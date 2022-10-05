package com.rikkei.training.musicapp.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rikkei.training.musicapp.model.Chart
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface DeezerAPI {
    @GET("chart/")
    fun getChart(): Call<Chart>
}
class ChartClient{
    companion object{
        val BASE_URL = "https://api.deezer.com/"
        lateinit var gson: Gson
        var retrofit: Retrofit? = null

        fun getInstance(): Retrofit {
            if (retrofit == null){
                gson = GsonBuilder()
                    .setLenient()
                    .create()

                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()

            }
            return retrofit!!
        }

    }
}