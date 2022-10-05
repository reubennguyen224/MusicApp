package com.rikkei.training.musicapp.personal

import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.adapter.MusicAdapter
import com.rikkei.training.musicapp.databinding.FragmentNewSongBinding
import com.rikkei.training.musicapp.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class NewSongFragment : Fragment() {

    private var _binding: FragmentNewSongBinding? = null
    private val binding get() = _binding!!

    private val songlist = ArrayList<Song>()
    val adapter = MusicAdapter(songlist)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentNewSongBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newSongsList.adapter = adapter

        adapter.setOnItemClickListener(object : MusicAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val bundle = Bundle()
                bundle.putInt("songPosition", position)
                bundle.putString("album", "MusicAdapter")
                findNavController().navigate(R.id.playMusicFragment, bundle)
            }
        })
        binding.newSongsList.layoutManager = LinearLayoutManager(context)
        getSongList()
    }

    private fun getSongList() {
        lifecycleScope.launch(Dispatchers.IO) {
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
                } while (cursor.moveToNext() && songlist.size < 10)
                cursor.close()
            }
            songlist.sortBy { it.dateModifier }
            withContext(Dispatchers.Main) {
                adapter.notifyDataSetChanged()
            }
        }

    }

}