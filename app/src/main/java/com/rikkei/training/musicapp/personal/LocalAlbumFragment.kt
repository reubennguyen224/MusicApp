package com.rikkei.training.musicapp.personal

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.adapter.AlbumAdapter
import com.rikkei.training.musicapp.databinding.FragmentLocalMusicBinding
import com.rikkei.training.musicapp.model.Album
import com.rikkei.training.musicapp.model.Song
import java.io.File

class LocalAlbumFragment: Fragment() {

    private var _binding: FragmentLocalMusicBinding? = null
    private val binding get() = _binding!!

    private val songlist = ArrayList<Song>()
    private val albumList = Album()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLocalMusicBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSongList()
        findAlbum()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.titleFragment.text = "Album"
        binding.titleNumSong.text = "${albumList.size} album"
        val adapter = AlbumAdapter(albumList)
        binding.musicRecyclerList.adapter = adapter
        binding.musicRecyclerList.layoutManager  = LinearLayoutManager(context)

        adapter.setOnAlbumItemClickListener(object : AlbumAdapter.OnClickListener{
            override fun onAlbumItemClickListener(position: Int) {
                val bundle = Bundle()
                bundle.putInt("album_position", position)
                bundle.putString("local", "local")
                findNavController().navigate(R.id.newAlbumFragment2, bundle)
            }

        })

        binding.btnPlaySuffle.visibility = View.GONE
    }

    private fun getSongList(){
        val selection = MediaStore.Audio.Media.IS_MUSIC
        songlist.clear()
        val  res: ContentResolver = activity?.contentResolver!!
        val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.MediaColumns.TITLE,
            MediaStore.Audio.Media._ID, MediaStore.Audio.AudioColumns.ARTIST,
            MediaStore.Audio.AudioColumns.ALBUM, MediaStore.MediaColumns.DATE_MODIFIED,
            MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID)
        val cursor = res.query(musicUri, projection, selection, null, MediaStore.Audio.Media.DATE_ADDED + " DESC", null)
        if (cursor != null && cursor.moveToFirst()){
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
                    songlist.add(song)
            } while (cursor.moveToNext())
        }
        songlist.sortBy { it.dateModifier  }
    }

    private fun findAlbum(){
        albumList.clear()
        albumList.addAll(PersonalFragment.albumList)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}