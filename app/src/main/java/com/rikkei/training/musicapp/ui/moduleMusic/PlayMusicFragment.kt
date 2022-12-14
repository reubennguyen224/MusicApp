package com.rikkei.training.musicapp.ui.moduleMusic

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.databinding.FragmentPlayMusicBinding
import com.rikkei.training.musicapp.model.favouriteChecker
import com.rikkei.training.musicapp.model.getImgArt
import com.rikkei.training.musicapp.model.getThumb
import com.rikkei.training.musicapp.model.setSongPosition
import com.rikkei.training.musicapp.ui.HomeFragment
import com.rikkei.training.musicapp.utils.MusicPlayService
import com.rikkei.training.musicapp.viewmodel.LocalFavouriteViewModel
import com.rikkei.training.musicapp.viewmodel.MusicModuleViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class PlayMusicFragment : Fragment(), ServiceConnection, MediaPlayer.OnCompletionListener {

    val viewModel: MusicModuleViewModel by activityViewModels()

    companion object {
        var songPosition: Int = 0
        var isPlaying: Boolean = false
        var musicPlayService: MusicPlayService? = null

        @SuppressLint("StaticFieldLeak")
        var _binding: FragmentPlayMusicBinding? = null
        val binding get() = _binding!!

        @SuppressLint("StaticFieldLeak")
        lateinit var thumbView: View
        var nowPlayingId: String = ""
        var isFavourite: Boolean = false
        var fIndex: Int = -1
    }

    private lateinit var animation: Animation
    private var local: String = "local"

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            initializeLayout(it)
        }
        thumbView =
            LayoutInflater.from(requireContext()).inflate(R.layout.seekbar_thumb, null, false)
    }

    var nowPlaying = false

    private fun initializeLayout(bundle: Bundle) {
        songPosition = bundle.getInt("songPosition", 0)

        when (bundle.getString("album")) {
            "MusicSearchAdapter" -> {
                val intent = Intent(requireContext(), MusicPlayService::class.java)
                requireContext().bindService(intent, this, Context.BIND_AUTO_CREATE)
                requireContext().startService(intent)
                viewModel.setListFromSearch(true)
            }
            "MusicSearchInternetAdapter" -> {
                val intent = Intent(requireContext(), MusicPlayService::class.java)
                requireContext().bindService(intent, this, Context.BIND_AUTO_CREATE)
                requireContext().startService(intent)
                viewModel.setListFromSearch(false)
                local = "discovery"
            }
            "MusicShuffleAdapter" -> {
                val intent = Intent(requireContext(), MusicPlayService::class.java)
                requireContext().bindService(intent, this, Context.BIND_AUTO_CREATE)
                requireContext().startService(intent)
                viewModel.getListFromLocal(isShuffle = true)
            }
            "NowPlaying" -> {
                nowPlaying = true
                binding.timestampSong.progress = musicPlayService!!.songPlayer!!.currentPosition
                binding.timestampSong.max = musicPlayService!!.songPlayer!!.duration
                runtimeTimestamp()
                if (isPlaying)
                    binding.playMusic.setImageResource(R.drawable.ic_pause)
                else
                    binding.playMusic.setImageResource(R.drawable.ic_play_music)
            }
            "MusicAdapter" -> {
                val intent = Intent(requireContext(), MusicPlayService::class.java)
                requireContext().bindService(intent, this, Context.BIND_AUTO_CREATE)
                requireContext().startService(intent)
                viewModel.getListFromLocal(isShuffle = false)
            }
            "AlbumFragment" -> {
                val intent = Intent(requireContext(), MusicPlayService::class.java)
                requireContext().bindService(intent, this, Context.BIND_AUTO_CREATE)
                requireContext().startService(intent)
                viewModel.getListFromAlbum(isShuffle = false)
                songPosition = bundle.getInt("position", 0)
            }
            "AlbumSufferFragment" -> {
                val intent = Intent(requireContext(), MusicPlayService::class.java)
                requireContext().bindService(intent, this, Context.BIND_AUTO_CREATE)
                requireContext().startService(intent)
                viewModel.getListFromAlbum(isShuffle = true)
                songPosition = bundle.getInt("position", 0)
            }
            "SingerShuffleFragment" -> {
                val intent = Intent(requireContext(), MusicPlayService::class.java)
                requireContext().bindService(intent, this, Context.BIND_AUTO_CREATE)
                requireContext().startService(intent)
                viewModel.getListFromSinger(isShuffle = true)
                songPosition = bundle.getInt("position", 0)
                local = "discovery"
            }
            "SingerFragment" -> {
                val intent = Intent(requireContext(), MusicPlayService::class.java)
                requireContext().bindService(intent, this, Context.BIND_AUTO_CREATE)
                requireContext().startService(intent)
                viewModel.getListFromSinger(isShuffle = false)
                songPosition = bundle.getInt("position", 0)
                local = "discovery"
            }
            "DiscoveryFragment" -> {
                val intent = Intent(requireContext(), MusicPlayService::class.java)
                requireContext().bindService(intent, this, Context.BIND_IMPORTANT)
                requireContext().startService(intent)
                viewModel.getListFromDiscovery(isSS = false)
                songPosition = bundle.getInt("position", 0)
                local = "discovery"
            }
            "DiscoverySSFragment" -> {
                val intent = Intent(requireContext(), MusicPlayService::class.java)
                requireContext().bindService(intent, this, Context.BIND_AUTO_CREATE)
                requireContext().startService(intent)
                viewModel.getListFromDiscovery(isSS = true)
                songPosition = bundle.getInt("position", 0)
                local = "discovery"
            }
            "Favourite" -> {
                val intent = Intent(requireContext(), MusicPlayService::class.java)
                requireContext().bindService(intent, this, Context.BIND_AUTO_CREATE)
                requireContext().startService(intent)
                viewModel.getListFromFavourite(isShuffle = false)
            }
            "FavouriteSuffer" -> {
                val intent = Intent(requireContext(), MusicPlayService::class.java)
                requireContext().bindService(intent, this, Context.BIND_AUTO_CREATE)
                requireContext().startService(intent)
                viewModel.getListFromFavourite(isShuffle = true)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (nowPlaying) {
            binding.playMusic.setImageResource(R.drawable.ic_pause)
            binding.timestampSong.thumb =
                getThumb(musicPlayService!!.songPlayer!!.currentPosition, requireContext())
            binding.timestampSong.progress = musicPlayService!!.songPlayer!!.currentPosition
            binding.timestampSong.max = musicPlayService!!.songPlayer!!.duration
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentPlayMusicBinding.inflate(inflater, container, false)
        val view = binding.root

        initialSongInformation()
        if (nowPlaying || isPlaying) {
            nowPlaying = false

            binding.timestampSong.thumb =
                getThumb(musicPlayService!!.songPlayer!!.currentPosition, requireContext())
            binding.timestampSong.progress = musicPlayService!!.songPlayer!!.currentPosition
            binding.timestampSong.max = musicPlayService!!.songPlayer!!.duration
        }
        isPlaying = true
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.collapsePlayMusic.setOnClickListener {
            findNavController().navigate(findNavController().previousBackStackEntry?.destination!!.id)
        }

        animation = AnimationUtils.loadAnimation(requireContext(), R.anim.rotation)

        binding.songImg.startAnimation(animation)

        binding.playMusic.setOnClickListener {
            if (isPlaying) {
                pauseMusic()
            } else {
                playMusic()
            }
        }
        binding.timestampSong.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) musicPlayService!!.songPlayer!!.seekTo(progress)
                binding.timestampSong.progress = progress
                binding.timestampSong.thumb = context?.let { getThumb(progress, it) }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })

        binding.playlist.setOnClickListener(playlistListener)
        binding.btnPlaylist.setOnClickListener(playlistListener)

        binding.nextSong.setOnClickListener {
            prevNextSong(increment = true)
        }

        binding.previousSong.setOnClickListener {
            prevNextSong(increment = false)
        }
        binding.btnFavour.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.playMusicFragment) {
                if (HomeFragment.userToken == "") {
                    val uri = Uri.parse("android-app://com.rikkei.training.musicapp/login")
                    findNavController().navigate(uri)
                } else btnFavorView(isFavourite)
            } else {
                Toast.makeText(requireContext(), "Ch???c n??ng ch??a ???????c h??? tr??? khi nghe nh???c tr???c tuy???n", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val playlistListener = View.OnClickListener {
        if (findNavController().currentDestination?.id == R.id.playMusicFragment) findNavController().navigate(R.id.musicPlayingListFragment)
        else findNavController().navigate(R.id.musicPlayingListFragment2)
    }

    private fun btnFavorView(status: Boolean) {
        if (status) {
            isFavourite = false
            binding.btnFavour.setImageResource(R.drawable.ic_favorite_border_24)
            LocalFavouriteViewModel.favouriteList.removeAt(fIndex)
        } else {
            isFavourite = true
            binding.btnFavour.setImageResource(R.drawable.ic_favorite)
            LocalFavouriteViewModel.favouriteList.add(MusicModuleViewModel.listOfSongs[songPosition])
        }
    }


    private fun playMusic() {
        binding.playMusic.setImageResource(R.drawable.ic_pause)
        musicPlayService!!.sendNotification(R.drawable.ic_pause_bar)
        isPlaying = true
        musicPlayService!!.songPlayer!!.start()
        binding.songImg.startAnimation(animation)
        runtimeTimestamp()
    }

    private fun pauseMusic() {
        binding.playMusic.setImageResource(R.drawable.ic_play_music)
        musicPlayService!!.sendNotification(R.drawable.ic_play)
        isPlaying = false
        binding.songImg.clearAnimation()
        musicPlayService!!.songPlayer!!.pause()
    }

    private fun prevNextSong(increment: Boolean) {
        if (increment) {
            setSongPosition(increment = true)
            initialSongInformation()
            createMediaPlayer()
        } else {
            setSongPosition(increment = false)
            initialSongInformation()
            createMediaPlayer()
        }
        musicPlayService!!.sendNotification(R.drawable.ic_pause_bar)
    }

    private fun initialSongInformation() {
        viewModel.getMusicList().observe(viewLifecycleOwner) {
            binding.nameSong.text = it[songPosition].thisTile
            NowPlaying.binding.nameSong.text = it[songPosition].thisTile
            binding.singerName.text = it[songPosition].thisArtist
            NowPlaying.binding.nameSinger.text = it[songPosition].thisArtist
            Glide.with(requireContext())
                .load(it[songPosition].imageUri)
                .centerCrop()
                .into(binding.songImg)
            fIndex = favouriteChecker(it[songPosition].thisId)
        }
        if (isFavourite) binding.btnFavour.setImageResource(R.drawable.ic_favorite)
        else binding.btnFavour.setImageResource(R.drawable.ic_favorite_border_24)

    }

    private fun createMediaPlayer() {
        try {
            if (musicPlayService!!.songPlayer == null) musicPlayService!!.songPlayer = MediaPlayer()
            musicPlayService!!.songPlayer!!.reset()
            musicPlayService!!.songPlayer!!.setDataSource(MusicModuleViewModel.listOfSongs[songPosition].songUri)
            musicPlayService!!.songPlayer!!.prepare()
            musicPlayService!!.songPlayer!!.start()
            isPlaying = true
            binding.playMusic.setImageResource(R.drawable.ic_pause)
            binding.timestampSong.max = musicPlayService!!.songPlayer!!.duration
            binding.timestampSong.thumb =
                getThumb(musicPlayService!!.songPlayer!!.currentPosition, requireContext())
            musicPlayService!!.songPlayer!!.setOnCompletionListener(this) //chuyen bai khi bai hat hien t???i ket thuc
            nowPlayingId = MusicModuleViewModel.listOfSongs[songPosition].thisId.toString()
        } catch (e: Exception) {
            return
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicPlayService.MyBinder

        musicPlayService = binder.currentService()
        createMediaPlayer()
        musicPlayService!!.sendNotification(R.drawable.ic_pause_bar)

        runtimeTimestamp()
    }

    private fun runtimeTimestamp() {
        lifecycleScope.launch {
            while (isPlaying) {
                binding.playMusic.setImageResource(R.drawable.ic_pause)
                binding.timestampSong.progress = musicPlayService!!.songPlayer!!.currentPosition
                binding.timestampSong.thumb =
                    getThumb(musicPlayService!!.songPlayer!!.currentPosition, requireContext())
                delay(600)
            }
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicPlayService = null
    }

    override fun onCompletion(mp: MediaPlayer?) {
        if (local == "discovery") {
            viewModel.updateStream()
        }
        setSongPosition(increment = true)
        createMediaPlayer()
        musicPlayService!!.sendNotification(R.drawable.ic_pause_bar)
        val imgArt = getImgArt(MusicModuleViewModel.listOfSongs[songPosition].songUri)
        val image = imgArt?.let {
            it.size.let { it1 ->
                BitmapFactory.decodeByteArray(
                    imgArt, 0,
                    it1
                )
            }
        }
        NowPlaying.binding.imageMusic.setImageBitmap(image)
        try {
            initialSongInformation()
        } catch (e: Exception) {
            return
        }
    }
}