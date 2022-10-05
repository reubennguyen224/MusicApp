package com.rikkei.training.musicapp.personal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.rikkei.training.musicapp.adapter.NewSingerAdapter
import com.rikkei.training.musicapp.databinding.FragmentNewSingerBinding
import com.rikkei.training.musicapp.model.Artist
import com.rikkei.training.musicapp.model.Song

class NewSingerFragment : Fragment() {

    private var _binding: FragmentNewSingerBinding? = null
    private val binding get() = _binding!!

    private val songlist = ArrayList<Song>()
    private val singerList = ArrayList<Artist>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentNewSingerBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findSinger()
        binding.newSingerList.adapter = NewSingerAdapter(singerList)
        binding.newSingerList.layoutManager = LinearLayoutManager(context)
    }


    private fun findSinger() {
        singerList.clear()
        var x = 0
        if (PersonalFragment.singerList.size < 10) {
            singerList.addAll(PersonalFragment.singerList)
        } else {
            while (x++ <= 10) {
                singerList.add(PersonalFragment.singerList[x])
            }
        }
    }

}