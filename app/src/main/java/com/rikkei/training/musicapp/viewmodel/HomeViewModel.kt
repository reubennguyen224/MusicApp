package com.rikkei.training.musicapp.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rikkei.training.musicapp.model.Album
import com.rikkei.training.musicapp.model.AlbumItem
import com.rikkei.training.musicapp.model.Artist
import com.rikkei.training.musicapp.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private var songlist = MutableLiveData<ArrayList<Song>>()
    private var albumlist = MutableLiveData<Album>()
    private var singerlist = MutableLiveData<ArrayList<Artist>>()

    fun getLocalSongList(): LiveData<ArrayList<Song>> {
        return songlist
    }

    fun getLocalAlbumList(): LiveData<Album> {
        return albumlist
    }

    fun getLocalSingerList(): LiveData<ArrayList<Artist>> {
        return singerlist
    }

    init {
        songlist = getMusicFile()
        singerlist = getSingerList()
        albumlist = getAlbumList()
    }

    @SuppressLint("Range")
    fun getSingerList(): MutableLiveData<ArrayList<Artist>> {
        val singerList = MutableLiveData<ArrayList<Artist>>()
        viewModelScope.launch {
            val selection = MediaStore.Audio.Media.IS_MUSIC
            val res: ContentResolver = getApplication<Application>().contentResolver
            val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Audio.Artists.ARTIST, MediaStore.Audio.Artists._ID
            )
            withContext(Dispatchers.IO) {
                val cursor = res.query(
                    musicUri,
                    projection,
                    selection,
                    null,
                    MediaStore.Audio.Media.DATE_ADDED + " DESC",
                    null
                )
                val artistList = ArrayList<Artist>()
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        val singerId =
                            cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Artists._ID))
                        val artistName =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST))
                        val singer = Artist(
                            id = singerId,
                            name = artistName,
                            avatarID = null,
                            description = null
                        )
                        var has = false
                        for (tmp in artistList) {
                            if (tmp.name == singer.name) has = true
                        }
                        if (!has) artistList.add(singer)

                    } while (cursor.moveToNext())
                    cursor.close()
                }
                artistList.add(
                    Artist(
                        artistList.size.toLong(),
                        "Various Artist",
                        null,
                        ""
                    )
                )
                artistList.add(
                    Artist(
                        artistList.size.toLong(),
                        "Unknown Artist",
                        null,
                        ""
                    )
                )
                singerList.postValue(artistList)
            }
        }
        return singerList
    }

    @SuppressLint("Range")
    fun getAlbumList(): MutableLiveData<Album> {
        val albumList = MutableLiveData<Album>()
        viewModelScope.launch {
            val selection = MediaStore.Audio.Media.IS_MUSIC
            val res: ContentResolver = getApplication<Application>().contentResolver
            val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Artists.ARTIST
            )
            withContext(Dispatchers.IO) {
                val cursor = res.query(
                    musicUri,
                    projection,
                    selection,
                    null,
                    MediaStore.Audio.Media.DATE_ADDED + " DESC",
                    null
                )
                val albumArray = Album()
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        val thisAlbum =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM))
                        val albumId =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                        val uri = Uri.parse("content://media/external/audio/albumart")
                        val thisImage = Uri.withAppendedPath(uri, albumId).toString()
                        val artistName =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST))

                        var has = false

                        val album = AlbumItem(
                            id = albumId.toLong(),
                            image = thisImage,
                            name = thisAlbum,
                            singer_name = artistName
                        )

                        if (albumArray.size == 0)
                            albumArray.add(album)
                        else {
                            for (tmp in albumArray) {
                                if (tmp.name == album.name)
                                    has = true
                            }
                            if (!has) albumArray.add(album)
                        }
                    } while (cursor.moveToNext())
                    cursor.close()
                }
                albumList.postValue(albumArray)
            }
        }
        return albumList
    }

    @SuppressLint("Range")
    fun getMusicFile():MutableLiveData<ArrayList<Song>> {

        val songList = MutableLiveData<ArrayList<Song>>()
        viewModelScope.launch {
            val selection = MediaStore.Audio.Media.IS_MUSIC
            val res: ContentResolver = getApplication<Application>().contentResolver
            val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.MediaColumns.TITLE,
                MediaStore.Audio.Media._ID, MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.ALBUM, MediaStore.MediaColumns.DATE_MODIFIED,
                MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Artists.ARTIST, MediaStore.Audio.Artists._ID
            )
            withContext(Dispatchers.IO) {

                val cursor = res.query(
                    musicUri,
                    projection,
                    selection,
                    null,
                    MediaStore.Audio.Media.DATE_ADDED + " DESC",
                    null
                )
                val musicList = ArrayList<Song>()
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        val thisTitle =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                        val thisId: Long =
                            cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                        val thisArtist =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST))
                        val thisAlbum =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM))
                        val thisDate =
                            cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED))
                        val thisUri =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                        val albumId =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
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
                            musicList.add(song)

                    } while (cursor.moveToNext())
                    cursor.close()
                }
                musicList.sortBy {
                    it.dateModifier
                }
                songList.postValue(musicList)
            }

        }
        return songList
    }
}