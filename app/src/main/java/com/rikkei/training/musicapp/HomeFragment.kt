package com.rikkei.training.musicapp

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context.MODE_PRIVATE
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.gson.GsonBuilder
import com.rikkei.training.musicapp.databinding.FragmentHomeBinding
import com.rikkei.training.musicapp.header.ProfileFragment
import com.rikkei.training.musicapp.model.*
import com.rikkei.training.musicapp.personal.LocalFavouriteFragment
import com.rikkei.training.musicapp.personal.PersonalFragment
import com.rikkei.training.musicapp.utils.ChartClient
import com.rikkei.training.musicapp.utils.DeezerAPI
import com.rikkei.training.musicapp.utils.LoginAPI
import com.rikkei.training.musicapp.utils.LoginClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController:NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        navHostFragment = childFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(setOf(R.id.musicLocalFragment, R.id.discoveryFragment, R.id.chartFragment, R.id.newFeedFragment))
        binding.bottomNavigate.setupWithNavController(navController) //attention: id menu bottom must match id graph
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks(){
            override fun onFragmentViewCreated(
                fm: FragmentManager,
                f: Fragment,
                v: View,
                savedInstanceState: Bundle?
            ) {
                TransitionManager.beginDelayedTransition(binding.root, Slide(Gravity.BOTTOM).excludeTarget(
                    androidx.navigation.fragment.R.id.nav_host_fragment_container, true))
                when(f){
                    is PlayMusicFragment -> binding.bottomNavigate.visibility = View.GONE
                    is MusicPlayingListFragment -> binding.bottomNavigate.visibility = View.GONE
                    is LoginFragment -> binding.bottomNavigate.visibility = View.GONE
                    is ProfileFragment -> binding.bottomNavigate.visibility = View.GONE
                    else -> binding.bottomNavigate.visibility = View.VISIBLE
                }
            }
        }, true)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

//        val editor = MainActivity().getSharedPreferences("Favourite", MODE_PRIVATE)
//        val jsonString =  editor.getString("FavouriteSong", null)
//        val typeToken = object : TypeToken<ArrayList<Song>>(){}.type
//        if (jsonString != null){
//            val data: ArrayList<Song> = GsonBuilder().create().fromJson(jsonString, typeToken)
//            LocalFavouriteFragment.favouriteList.addAll(data)
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSongList()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        val editor = MainActivity().getSharedPreferences("Favourite", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(LocalFavouriteFragment.favouriteList)
        editor.putString("FavouriteSong", jsonString)
        editor.apply()
    }

    companion object{
        val user: User = User()
        val dataAPI = ArrayList<DataAPIX>()
        var userToken: String = ""
        val deezerAPI: DeezerAPI = ChartClient.getInstance().create(DeezerAPI::class.java)
        val loginAPI = LoginClient.getInstance().create(LoginAPI::class.java)!!
        val allSongLocal = ArrayList<Song>()
    }

    @SuppressLint("Range")
    private fun getSongList(){
        lifecycleScope.launch(Dispatchers.IO){
            val selection = MediaStore.Audio.Media.IS_MUSIC
            PersonalFragment.songlist.clear()
            PersonalFragment.singerList.clear()
            PersonalFragment.albumList.clear()

            val  res: ContentResolver = activity?.contentResolver!!
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
            PersonalFragment.singerList.add(Artist(PersonalFragment.singerList.size.toLong(),"Various Artist",  null, ""))
            PersonalFragment.singerList.add(Artist(PersonalFragment.singerList.size.toLong(),"Unknown Artist", null, ""))
            PersonalFragment.songlist.sortBy {
                it.dateModifier
            }

        }

    }
}