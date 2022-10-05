package com.rikkei.training.musicapp.model

data class Music(
    val id: String,
    val coverURI: String,
    val title: String,
    val artist: String,
    val genre: String,
    val songURI: String,
    val numberOfStream: String? = null
)
class MusicAPI : ArrayList<Music>()

data class MusicResponse(
    val status: String,
    val message: String,
    val data: List<Music>
)
class MusicList: ArrayList<MusicResponse>()
data class Message(
    val status: String,
    val message: String
)
class ListMessage: ArrayList<Message>()