package com.rikkei.training.musicapp.utils

class Resource<out T>(val status: Int, val message: String?, val data: T?) {
    companion object{
        fun <T> success(data: T): Resource<T> = Resource(status = 200, message = null, data = data)

        fun <T> failed(data: T?, message: String): Resource<T> = Resource(status = 404, message = message, data = data)
    }
}