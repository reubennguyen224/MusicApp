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
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class PersonalViewModel(application: Application) : AndroidViewModel(application) {
    private var _libraryCard = MutableLiveData<ArrayList<LibraryCard>>()
    private var _singerList = MutableLiveData<ArrayList<Artist>>()
    private var _songlist = MutableLiveData<ArrayList<Song>>()
    private var _albumlist = MutableLiveData<Album>()
    private var _downloadlist = MutableLiveData<ArrayList<Song>>()

    fun getLibraryCard(): LiveData<ArrayList<LibraryCard>> {
        return _libraryCard
    }

    fun getArtistList(): LiveData<ArrayList<Artist>> {
        return _singerList
    }

    companion object {
        var songArraylist = ArrayList<Song>()
        var albumArrayList = Album()
        var singerArrayList = ArrayList<Artist>()
        var favouriteList = ArrayList<Song>()
        var listMusicFile = ArrayList<Song>()
    }

    init {
        _singerList = getSingerList()
        _songlist = getMusicFile()
        _albumlist = getAlbumList()
        _downloadlist = getDownloadList()
        _libraryCard = libraryCard()
    }

    private fun libraryCard(): MutableLiveData<ArrayList<LibraryCard>> {
        val library = MutableLiveData<ArrayList<LibraryCard>>()
        viewModelScope.launch {
            val card = ArrayList<LibraryCard>()
            withContext(Dispatchers.IO) {
                card.add(LibraryCard(R.drawable.ic_music_local, "Bài hát", songArraylist.size))
                card.add(LibraryCard(R.drawable.ic_album, "Album", albumArrayList.size))
                card.add(LibraryCard(R.drawable.ic_download, "Tải xuống", listMusicFile.size))
                card.add(LibraryCard(R.drawable.ic_singer, "Ca sĩ", singerArrayList.size))
                card.add(LibraryCard(R.drawable.ic_heart, "Yêu thích", favouriteList.size))
            }
            library.postValue(card)
        }
        return library
    }

    private fun getDownloadList(): MutableLiveData<ArrayList<Song>> {
        val downloadSong = MutableLiveData<ArrayList<Song>>()
        viewModelScope.launch {
            val res: ContentResolver = getApplication<Application>().contentResolver!!
            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.MediaColumns.TITLE,
                MediaStore.Audio.Media._ID, MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.ALBUM, MediaStore.MediaColumns.DATE_MODIFIED,
                MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID
            )
            val folder = arrayOf("%Download%")
            val downloadFile = ArrayList<Song>()
            withContext(Dispatchers.IO) {
                val cursor =
                    res.query(
                        uri,
                        projection,
                        MediaStore.Audio.Media.DATA + " like ? ",
                        folder,
                        null
                    )
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        val thisTitle = cursor.getString(0)
                        val thisId: Long = cursor.getLong(1)
                        val thisArtist = cursor.getString(2)
                        val thisAlbum = cursor.getString(3)
                        val thisDate = cursor.getString(4)
                        val thisUri = cursor.getString(5)
                        val albumId = cursor.getString(6)
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
                            downloadFile.add(song)
                    } while (cursor.moveToNext())
                    cursor.close()
                }
                downloadSong.postValue(downloadFile)
            }
        }
        return downloadSong
    }

    @SuppressLint("Range")
    private fun getSingerList(): MutableLiveData<ArrayList<Artist>> {
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
                artistList.sortBy {
                    it.name
                }
                singerList.postValue(artistList)
            }
        }
        return singerList
    }

    @SuppressLint("Range")
    private fun getAlbumList(): MutableLiveData<Album> {
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
    fun getMusicFile(): MutableLiveData<ArrayList<Song>> {
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
