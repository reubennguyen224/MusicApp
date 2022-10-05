package com.rikkei.training.musicapp.model

class Album : ArrayList<AlbumItem>()
data class AlbumItemAPI(
    val id: String,
    val cover: String,
    val name: String,
    val artist: String,
    val type: String
)
//data class AlbumAPI(
//    val status: String,
//    val message: String,
//    val data: ArrayList<AlbumItemAPI>
//)
class AlbumAPI: ArrayList<AlbumItemAPI>()