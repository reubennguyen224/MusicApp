package com.rikkei.training.musicapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "album")
data class AlbumItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "imageUrl")
    val image: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "singerName")
    val singer_name: String
)

