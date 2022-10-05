package com.rikkei.training.musicapp.utils

sealed class Result<out T: Any>{
    data class Success<out T: Any>(val data: T): Result<T>()
    data class Failed(val exception: Exception): Result<Nothing>()
}

//open class BaseRepository{
//    suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>, errorMessage: String): T?{
//        val result: Result<T> = safeApiResult(call, errorMessage)
//        var data: T? = null
//
//        when(result){
//            is Result.Success -> data = result.data
//            is Result.Failed -> Log.d("Data Repository", "$errorMessage & Exception: ${result.exception}")
//        }
//        return data
//    }
//
//    private suspend fun <T: Any> safeApiResult(call: suspend () -> Response<T>, errorMessage:String): Result<T>{
//        val response = call.invoke()
//        if (response.isSuccessful) return Result.Success(response.body()!!)
//        return Result.Failed(IOException("Error Occurred during getting safe Api result - $errorMessage"))
//    }
//}
//class DiscoveryRepository(private val api: DiscoveryAPI): BaseRepository(){
//    suspend fun getNewSinger(): MutableList<SingerItem> {
//        val singerResponse = safeApiCall(
//            call = {
//                api.getNewSingersAsync()
//            },
//            errorMessage = "Error Fetching data"
//        )
//        return singerResponse?.results!!.toMutableList()
//    }
//}