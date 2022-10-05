package com.rikkei.training.musicapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "song")
data class Song(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    val thisId: Long,
    @ColumnInfo(name = "title")
    val thisTile: String,
    @ColumnInfo(name = "artist")
    val thisArtist: String,
    @ColumnInfo(name = "album")
    val thisAlbum: String,
    @ColumnInfo(name = "dateModifier")
    val dateModifier: String,
    @ColumnInfo(name = "favourite")
    var favourite: Boolean,
    @ColumnInfo(name = "imageUrl")
    val imageUri: String? = null,
    @ColumnInfo(name = "songUrl")
    var songUri: String? = null
): Serializable