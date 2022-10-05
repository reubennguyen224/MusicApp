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
import com.rikkei.training.musicapp.adapter.NewSingerAdapter
import com.rikkei.training.musicapp.databinding.FragmentLocalMusicBinding
import com.rikkei.training.musicapp.model.Artist
import com.rikkei.training.musicapp.model.Song
import java.io.File

class LocalSingerFragment : Fragment() {
    private var _binding: FragmentLocalMusicBinding? = null
    private val binding get() = _binding!!

    private val songlist = ArrayList<Song>()
    private val singerList = ArrayList<Artist>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLocalMusicBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.titleFragment.text = "Ca sĩ"
        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //getSongList()
        findSinger()
        binding.titleNumSong.text = "${singerList.size} ca sĩ"

        singerList.sortBy {
            it.name
        }
        binding.musicRecyclerList.adapter = NewSingerAdapter(singerList)
        binding.musicRecyclerList.layoutManager = LinearLayoutManager(context)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun getSongList() {
        val selection = MediaStore.Audio.Media.IS_MUSIC
        songlist.clear()
        val res: ContentResolver = activity?.contentResolver!!
        val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.MediaColumns.TITLE,
            MediaStore.Audio.Media._ID, MediaStore.Audio.AudioColumns.ARTIST,
            MediaStore.Audio.AudioColumns.ALBUM, MediaStore.MediaColumns.DATE_MODIFIED,
            MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID
        )
        val cursor = res.query(
            musicUri,
            projection,
            selection,
            null,
            MediaStore.Audio.Media.DATE_ADDED + " DESC",
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
                    songlist.add(song)
            } while (cursor.moveToNext())
            cursor.close()
        }
        songlist.sortBy { it.dateModifier }
    }

    private fun findSinger() {
        singerList.clear()
        singerList.addAll(PersonalFragment.singerList)
    }
}