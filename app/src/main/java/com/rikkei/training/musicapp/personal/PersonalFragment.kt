package com.rikkei.training.musicapp.personal

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.rikkei.training.musicapp.PlayMusicFragment
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.adapter.ArtistAdapter
import com.rikkei.training.musicapp.adapter.LibraryAdapter
import com.rikkei.training.musicapp.adapter.TabAdapter
import com.rikkei.training.musicapp.databinding.FragmentMusicLocalBinding
import com.rikkei.training.musicapp.model.Album
import com.rikkei.training.musicapp.model.Artist
import com.rikkei.training.musicapp.model.LibraryCard
import com.rikkei.training.musicapp.model.Song
import com.rikkei.training.musicapp.utils.MusicPlayService
import com.rikkei.training.musicapp.viewmodel.MusicLocalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class PersonalFragment : Fragment() {

    private var _binding: FragmentMusicLocalBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MusicLocalViewModel by viewModels()
//    by activityViewModels {
//        MusicLocalFactory((activity?.application as MusicApplication).database.itemDao())
//    }

    companion object{
         val songlist = ArrayList<Song>()
         val albumList = Album()
         val singerList = ArrayList<Artist>()
         val listMusicFile= ArrayList<Song>()
         var musicPlayService: MusicPlayService? = null
    }

    private val artistAdapter = ArtistAdapter(singerList)
    private val favouriteList = ArrayList<Song>()

    private val list = arrayListOf(
        LibraryCard(R.drawable.ic_music_local, "Bài hát", songlist.size),
        LibraryCard(R.drawable.ic_album, "Album", albumList.size),
        LibraryCard(R.drawable.ic_download, "Tải xuống", listMusicFile.size),
        LibraryCard(R.drawable.ic_singer, "Ca sĩ", singerList.size),
        LibraryCard(R.drawable.ic_heart, "Yêu thích", favouriteList.size),
    )
    val adapter = LibraryAdapter(list)
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicLocalBinding.inflate(inflater, container, false)
        val view = binding.root

        findFile() //get music file in folder download
//        getSongList() //get music file in sdcard

        binding.libBlock.adapter = adapter
        adapter.setOnItemClickListener(object : LibraryAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                when(position){
                    0 -> findNavController().navigate(PersonalFragmentDirections.actionMusicLocalFragmentToLocalMusicFragment())
                    1 -> findNavController().navigate(PersonalFragmentDirections.actionMusicLocalFragmentToLocalAlbumFragment())
                    2 -> findNavController().navigate(PersonalFragmentDirections.actionMusicLocalFragmentToLocalDownloadFragment())
                    3 -> findNavController().navigate(PersonalFragmentDirections.actionMusicLocalFragmentToLocalSingerFragment())
                    4 -> findNavController().navigate(PersonalFragmentDirections.actionMusicLocalFragmentToLocalFavouriteFragment())
                }
            }
        })

        binding.viewPagerLib.adapter = TabAdapter(childFragmentManager, lifecycle)
        val tab = binding.tabLayout
        tab.addTab(tab.newTab().setText("Nhạc mới cập nhật"))
        tab.addTab(tab.newTab().setText("Nghệ sĩ mới"))
        tab.addTab(tab.newTab().setText("Album mới"))

        tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let { binding.viewPagerLib.currentItem = it.position }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?)  = Unit
            override fun onTabReselected(tab: TabLayout.Tab?) = Unit
        })

        binding.viewPagerLib.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tab.selectTab(tab.getTabAt(position))
            }
        })

//        findAlbum()
//        findSinger()
        getFavouriteList() //get favourite song

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fixData()

        val gridLayoutManager = GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
        binding.libBlock.layoutManager = gridLayoutManager

        singerList.sortBy {
            it.name
        }

        binding.artistBlock.adapter = artistAdapter
        val artistLayout = GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
        binding.artistBlock.layoutManager = artistLayout
        
        artistAdapter.setOnArtistClickListener(object : ArtistAdapter.OnItemClickListener{
            override fun onArtistClickListener(position: Int) {
                Toast.makeText(context, "Thông tin nghệ sĩ không khả dụng", Toast.LENGTH_SHORT).show()
            }
        })


        if (musicPlayService == null) musicPlayService = PlayMusicFragment.musicPlayService

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("Number Local", songlist.size)
        outState.putInt("Number Album", albumList.size)
        outState.putInt("Number Music File", listMusicFile.size)
        outState.putInt("Number Singer", singerList.size)
        outState.putInt("Number Favourite", favouriteList.size)

    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        list[0].numberItems = savedInstanceState?.getInt("Number Local")
        list[1].numberItems = savedInstanceState?.getInt("Number Album")
        list[2].numberItems = savedInstanceState?.getInt("Number Music File")
        list[3].numberItems = savedInstanceState?.getInt("Number Singer")
        list[4].numberItems = savedInstanceState?.getInt("Number Favourite")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    @SuppressLint("Range")
//    private fun getSongList(){
//        lifecycleScope.launch(Dispatchers.IO){
//            val selection = MediaStore.Audio.Media.IS_MUSIC
//            songlist.clear()
//            singerList.clear()
//            albumList.clear()
//
//            val  res: ContentResolver = activity?.contentResolver!!
//            val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
//            val projection = arrayOf(MediaStore.MediaColumns.TITLE,
//                MediaStore.Audio.Media._ID, MediaStore.Audio.AudioColumns.ARTIST,
//                MediaStore.Audio.AudioColumns.ALBUM, MediaStore.MediaColumns.DATE_MODIFIED,
//                MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID,
//                MediaStore.Audio.Artists.ARTIST, MediaStore.Audio.Artists._ID)
//            val cursor = res.query(musicUri, projection, selection, null, MediaStore.Audio.Media.DATE_ADDED + " DESC", null)
//            if (cursor != null && cursor.moveToFirst()){
//                do {
//                    val thisTitle = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
//                    val thisId: Long = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
//                    val thisArtist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST))
//                    val thisAlbum = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM))
//                    val thisDate = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED))
//                    val thisUri = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
//                    val albumId = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
//                    val uri = Uri.parse("content://media/external/audio/albumart")
//                    val thisImage = Uri.withAppendedPath(uri, albumId).toString()
//
//                    val song = Song(
//                        thisId = thisId,
//                        thisTile = thisTitle,
//                        thisArtist = thisArtist,
//                        thisAlbum = thisAlbum,
//                        dateModifier = thisDate,
//                        favourite = false,
//                        imageUri = thisImage,
//                        songUri = thisUri
//                    )
//                    val file = File(song.songUri!!)
//                    if (file.exists())
//                        songlist.add(song)
//
//                    val singerId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Artists._ID))
//                    val artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST))
//                    val singer = Artist(id = singerId, name = artistName, avatarID = null, description = null)
//                    var has = false
//                    for (tmp in singerList){
//                        if (tmp.name == singer.name) has = true
//                    }
//                    if (!has) singerList.add(singer)
//
//                    val album = AlbumItem(id = albumId.toLong(), image = thisImage, name = thisAlbum, singer_name = artistName)
//                    has = false
//
//                    if (albumList.size == 0)
//                        albumList.add(album)
//                    else{
//                        for (tmp in albumList){
//                            if (tmp.name == album.name)
//                                has = true
//                        }
//                        if (!has) albumList.add(album)
//                    }
//                } while (cursor.moveToNext())
//                cursor.close()
//            }
//            singerList.add(Artist(singerList.size.toLong(),"Various Artist",  null, ""))
//            singerList.add(Artist(singerList.size.toLong(),"Unknown Artist", null, ""))
//            songlist.sortBy {
//                it.dateModifier
//            }
//            withContext(Dispatchers.Main){
//                fixData()
//                adapter.notifyDataSetChanged()
//                artistAdapter.notifyDataSetChanged()
//            }
//        }
//
//    }

    private fun findFile() {
        lifecycleScope.launch(Dispatchers.IO){
            listMusicFile.clear()
            val res: ContentResolver = activity?.contentResolver!!
            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(MediaStore.MediaColumns.TITLE,
                MediaStore.Audio.Media._ID, MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.ALBUM, MediaStore.MediaColumns.DATE_MODIFIED,
                MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID)
            val folder = arrayOf("%Download%")
            val cursor = res.query(uri, projection, MediaStore.Audio.Media.DATA + " like ? ", folder, null)

            if (cursor != null && cursor.moveToFirst()){
                do{
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
                        listMusicFile.add(song)
                } while (cursor.moveToNext())
                cursor.close()
            }
            withContext(Dispatchers.Main){
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun fixData(){
        list[0].numberItems = songlist.size
        list[1].numberItems = albumList.size
        list[2].numberItems = listMusicFile.size
        list[3].numberItems = singerList.size
        list[4].numberItems = favouriteList.size
    }

    @SuppressLint("Range")
    private fun findAlbum(){
//        albumList.clear()
//        val selection = MediaStore.Audio.Media.IS_MUSIC
//        val  res: ContentResolver = activity?.contentResolver!!
//        val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
//        val projection = arrayOf(
//            MediaStore.Audio.Media._ID, MediaStore.Audio.AudioColumns.ARTIST,
//            MediaStore.Audio.AudioColumns.ALBUM,
//            MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID)
//        val cursor = res.query(musicUri, projection, selection, null, MediaStore.Audio.Media.DATE_ADDED + " DESC", null)
//        if (cursor != null && cursor.moveToFirst()){
//            do {
//                val thisId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
//                val thisArtist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
//                val thisAlbum = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
//                val uri = Uri.parse("content://media/external/audio/albumart")
//                val thisImage = Uri.withAppendedPath(uri, thisId.toString()).toString()
//
//                val album = AlbumItem(id = thisId, image = thisImage, name = thisAlbum, singer_name = thisArtist)
//                var has = false
//
//                if (albumList.size == 0)
//                    albumList.add(album)
//                else{
//                    for (tmp in albumList){
//                        if (tmp.name == album.name)
//                            has = true
//                    }
//                    if (!has) albumList.add(album)
//                }
//
//            } while (cursor.moveToNext())
//            cursor.close()
//        }
    }

    @SuppressLint("Range")
    private fun findSinger(){
//        singerList.clear()
//        val selection = MediaStore.Audio.Media.IS_MUSIC
//        val  res: ContentResolver = activity?.contentResolver!!
//        val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
//        val projection = arrayOf(
//            MediaStore.Audio.Artists.ARTIST, MediaStore.Audio.Artists._ID)
//        val cursor = res.query(musicUri, projection, selection, null, MediaStore.Audio.Media.DATE_ADDED + " DESC", null)
//        if (cursor != null && cursor.moveToFirst()){
//            do {
//                val thisId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Artists._ID))
//                val thisArtist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST))
//
//                val singer = Artist(id = thisId, name = thisArtist, avatarID = null, description = null)
//                var has = false
//                for (tmp in singerList){
//                    if (tmp.name == singer.name) has = true
//                }
//                if (!has) singerList.add(singer)
//
//            } while (cursor.moveToNext())
//            cursor.close()
//        }
    }

    private fun getFavouriteList(){
        favouriteList.clear()
        favouriteList.addAll(LocalFavouriteFragment.favouriteList)
    }

}


