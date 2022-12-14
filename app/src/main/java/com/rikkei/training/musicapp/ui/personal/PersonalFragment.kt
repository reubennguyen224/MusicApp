package com.rikkei.training.musicapp.ui.personal

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.rikkei.training.musicapp.adapter.ArtistAdapter
import com.rikkei.training.musicapp.adapter.LibraryAdapter
import com.rikkei.training.musicapp.adapter.TabAdapter
import com.rikkei.training.musicapp.databinding.FragmentPersonalBinding
import com.rikkei.training.musicapp.viewmodel.PersonalViewModel


class PersonalFragment : Fragment() {

    private var _binding: FragmentPersonalBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PersonalViewModel by activityViewModels()

    private val artistAdapter = ArtistAdapter()
    private val libAdapter = LibraryAdapter()

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel.getLibraryCard().observe(viewLifecycleOwner){
            libAdapter.dataSet = it
            libAdapter.notifyDataSetChanged()
        }
        viewModel.getArtistList().observe(viewLifecycleOwner){
            artistAdapter.artistList = it
        }

        libAdapter.setOnItemClickListener(object : LibraryAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                when (position) {
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
        tab.addTab(tab.newTab().setText("Nh???c m???i c???p nh???t"))
        tab.addTab(tab.newTab().setText("Ngh??? s?? m???i"))
        tab.addTab(tab.newTab().setText("Album m???i"))

        tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let { binding.viewPagerLib.currentItem = it.position }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) = Unit
            override fun onTabReselected(tab: TabLayout.Tab?) = Unit
        })

        binding.viewPagerLib.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tab.selectTab(tab.getTabAt(position))
            }
        })

        binding.libBlock.apply {
            val gridLayoutManager = GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
            layoutManager = gridLayoutManager
            adapter = libAdapter
        }

        binding.artistBlock.apply {
            adapter = artistAdapter
            val artistLayout = GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
            layoutManager = artistLayout
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        artistAdapter.setOnArtistClickListener(object : ArtistAdapter.OnItemClickListener {
            override fun onArtistClickListener(position: Int) {
                Toast.makeText(context, "Th??ng tin ngh??? s?? kh??ng kh??? d???ng", Toast.LENGTH_SHORT)
                    .show()
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}


