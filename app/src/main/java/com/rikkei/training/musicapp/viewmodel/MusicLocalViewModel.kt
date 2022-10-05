package com.rikkei.training.musicapp.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rikkei.training.musicapp.model.AlbumItem
import com.rikkei.training.musicapp.model.Artist
import com.rikkei.training.musicapp.model.Song
import com.rikkei.training.musicapp.ui.personal.PersonalFragment
import com.rikkei.training.musicapp.utils.MusicApplication
import kotlinx.coroutines.launch
import java.io.File

class MusicLocalViewModel(application: Application): AndroidViewModel(application) {

    fun insertSong(song: Array<Song>){
        viewModelScope.launch {
//            roomDao.insertSongInfo(song = song)
        }
    }

    fun insertAlbum(album: AlbumItem){
        viewModelScope.launch {
//            roomDao.insertAlbumInfo(album = album)
        }
    }

    fun insertSinger(artist: Artist){
        viewModelScope.launch {
//            roomDao.insertSingerInfo(singer = artist)
        }
    }

    @SuppressLint("Range")
    fun getListSong(): List<Song> {
        val tmp = ArrayList<Song>()
        viewModelScope.launch {
//            tmp.addAll(roomDao.getAllSongs())
            val selection = MediaStore.Audio.Media.IS_MUSIC
            PersonalFragment.songlist.clear()
            PersonalFragment.singerList.clear()
            PersonalFragment.albumList.clear()
            val res: ContentResolver = getApplication<MusicApplication>().contentResolver!!
            val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.MediaColumns.TITLE,
                MediaStore.Audio.Media._ID, MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.ALBUM, MediaStore.MediaColumns.DATE_MODIFIED,
                MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Artists.ARTIST, MediaStore.Audio.Artists._ID)
            val cursor = res.query(musicUri, projection, selection, null, MediaStore.Audio.Media.DATE_ADDED + " DESC", null)
            if (cursor != null && cursor.moveToFirst()){
                do {
                    val thisTitle = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val thisId: Long = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val thisArtist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST))
                    val thisAlbum = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM))
                    val thisDate = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED))
                    val thisUri = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val albumId = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val thisImage = Uri.withAppendedPath(uri, albumId).toString()

                    val song = Song(
                        thisId = thisId,
                        thisTile = thisTitle,
                        thisArtist = thisArtist,
                        thisAlbum = thisAlbum,
                        dateModifier = thisDate,
                        favourite = false,
                        imageUri = thisImage,
                        songUri = thisUri
                    )
                    val file = File(song.songUri!!)
                    if (file.exists())
                        PersonalFragment.songlist.add(song)

                    val singerId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Artists._ID))
                    val artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST))
                    val singer = Artist(id = singerId, name = artistName, avatarID = null, description = null)
                    var has = false
                    for (tmp in PersonalFragment.singerList){
                        if (tmp.name == singer.name) has = true
                    }
                    if (!has) PersonalFragment.singerList.add(singer)

                    val album = AlbumItem(id = albumId.toLong(), image = thisImage, name = thisAlbum, singer_name = artistName)
                    has = false

                    if (PersonalFragment.albumList.size == 0)
                        PersonalFragment.albumList.add(album)
                    else{
                        for (tmp in PersonalFragment.albumList){
                            if (tmp.name == album.name)
                                has = true
                        }
                        if (!has) PersonalFragment.albumList.add(album)
                    }
                } while (cursor.moveToNext())
                cursor.close()
            }
            PersonalFragment.songlist.sortBy {
                it.dateModifier
            }
        }
        return tmp
    }
}
