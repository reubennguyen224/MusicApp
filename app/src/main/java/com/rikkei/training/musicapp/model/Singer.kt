package com.rikkei.training.musicapp.model

data class SingerItem(
    val Id: String,
    val avatarURI: String,
    val name: String
)
class Singer : ArrayList<Artist>()
data class SingerResponse (
    val status: String,
    val message: String,
    val data: ArrayList<SingerItem>
)
//data class SingerAPI(val results: List<SingerItem>)
class SingerAPI : ArrayList<SingerItem>()