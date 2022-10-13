package com.rikkei.training.musicapp.ui

import android.os.Build
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.databinding.FragmentHomeBinding
import com.rikkei.training.musicapp.model.DataAPIX
import com.rikkei.training.musicapp.model.Song
import com.rikkei.training.musicapp.model.User
import com.rikkei.training.musicapp.ui.header.LoginFragment
import com.rikkei.training.musicapp.ui.header.ProfileFragment
import com.rikkei.training.musicapp.ui.header.RegisterFragment
import com.rikkei.training.musicapp.ui.moduleMusic.MusicPlayingListFragment
import com.rikkei.training.musicapp.ui.moduleMusic.PlayMusicFragment
import com.rikkei.training.musicapp.utils.*
import com.rikkei.training.musicapp.viewmodel.HomeViewModel
import com.rikkei.training.musicapp.viewmodel.PersonalViewModel


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navHostFragment: NavHostFragment

    private val viewModel: HomeViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        navHostFragment =
            childFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.personalFragment,
                R.id.discoveryFragment,
                R.id.chartFragment,
                R.id.newFeedFragment
            )
        )
        binding.bottomNavigate.setupWithNavController(navController) //attention: id menu bottom must match id graph
        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.registerFragmentLifecycleCallbacks(object :
            FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentViewCreated(
                fm: FragmentManager,
                f: Fragment,
                v: View,
                savedInstanceState: Bundle?
            ) {
                TransitionManager.beginDelayedTransition(
                    binding.root, Slide(Gravity.BOTTOM).excludeTarget(
                        androidx.navigation.fragment.R.id.nav_host_fragment_container, true
                    )
                )
                when (f) {
                    is PlayMusicFragment -> binding.bottomNavigate.visibility = View.GONE
                    is MusicPlayingListFragment -> binding.bottomNavigate.visibility = View.GONE
                    is LoginFragment -> binding.bottomNavigate.visibility = View.GONE
                    is ProfileFragment -> binding.bottomNavigate.visibility = View.GONE
                    is RegisterFragment -> binding.bottomNavigate.visibility = View.GONE
                    else -> binding.bottomNavigate.visibility = View.VISIBLE
                }
            }

        }, true)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
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

        viewModel.getLocalSingerList().observe(viewLifecycleOwner){
            PersonalViewModel.singerArrayList = it
        }
        viewModel.getLocalAlbumList().observe(viewLifecycleOwner){
            PersonalViewModel.albumArrayList = it
        }
        viewModel.getLocalSongList().observe(viewLifecycleOwner){
            PersonalViewModel.songArraylist = it
        }
        viewModel.getMusicDownloadList().observe(viewLifecycleOwner){
            PersonalViewModel.listMusicFile = it
        }

        if (musicPlayService == null) musicPlayService = PlayMusicFragment.musicPlayService
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
//        val editor = MainActivity().getSharedPreferences("Favourite", MODE_PRIVATE).edit()
//        val jsonString = GsonBuilder().create().toJson(LocalFavouriteViewModel.favouriteList)
//        editor.putString("FavouriteSong", jsonString)
//        editor.apply()
    }

    companion object {
        val user: User = User()
        val dataAPI = ArrayList<DataAPIX>()
        var userToken: String = ""
        val deezerAPI: DeezerAPI = ChartClient.getInstance().create(DeezerAPI::class.java)
        val loginAPI = LoginClient.getInstance().create(LoginAPI::class.java)
        val allSongLocal = ArrayList<Song>()
        var musicPlayService: MusicPlayService? = null
    }
}