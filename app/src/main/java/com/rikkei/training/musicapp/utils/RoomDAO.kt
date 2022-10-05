package com.rikkei.training.musicapp.utils

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rikkei.training.musicapp.model.AlbumItem
import com.rikkei.training.musicapp.model.Artist
import com.rikkei.training.musicapp.model.Song

@Dao
interface RoomDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSongInfo(vararg song: Song)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAlbumInfo(album: AlbumItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSingerInfo(singer: Artist)

    @Query(value = "SELECT * FROM song")
    fun getAllSongs() : List<Song>

    @Query(value = "SELECT * FROM album")
    fun getAllAlbums() : List<AlbumItem>

    @Query(value = "SELECT * FROM artist")
    fun getAllArtists() : List<Artist>
}