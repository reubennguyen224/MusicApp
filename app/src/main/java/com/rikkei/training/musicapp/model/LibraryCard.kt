package com.rikkei.training.musicapp.model

import com.rikkei.training.musicapp.adapter.MusicAdapter

data class LibraryCard (
    val iconId: Int,
    val nameCard: String,
    var numberItems: Int? ){
}

data class SingerDetail(
    val title: String,
    val listSong: ArrayList<Song>
)

data class SongDetail(
    val title: String,
    var listSong: ArrayList<Song>,
    var listener: MusicAdapter.OnItemClickListener?
)