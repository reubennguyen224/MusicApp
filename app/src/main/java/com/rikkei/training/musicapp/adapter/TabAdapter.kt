package com.rikkei.training.musicapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rikkei.training.musicapp.ui.personal.NewAlbumFragment
import com.rikkei.training.musicapp.ui.personal.NewSingerFragment
import com.rikkei.training.musicapp.ui.personal.NewSongFragment

class TabAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(
        fragmentManager,
        lifecycle
    ) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        if (position == 0)
            return NewSongFragment()
        else if (position == 1)
            return NewSingerFragment()
        return NewAlbumFragment()
    }

}