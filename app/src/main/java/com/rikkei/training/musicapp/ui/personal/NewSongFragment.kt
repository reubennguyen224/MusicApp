package com.rikkei.training.musicapp.ui.personal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.adapter.MusicAdapter
import com.rikkei.training.musicapp.databinding.FragmentNewSongBinding
import com.rikkei.training.musicapp.viewmodel.NewReleaseLocalViewModel


class NewSongFragment : Fragment() {

    private var _binding: FragmentNewSongBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewReleaseLocalViewModel by viewModels()
    private val songAdapter = MusicAdapter()

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

        viewModel.getNewSongLocal().observe(viewLifecycleOwner){
            songAdapter.dataset = it
        }
        binding.newSongsList.apply {
            adapter = songAdapter
            layoutManager = LinearLayoutManager(context)
        }

        songAdapter.setOnItemClickListener(object : MusicAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val bundle = Bundle()
                bundle.putInt("songPosition", position)
                bundle.putString("album", "MusicAdapter")
                findNavController().navigate(R.id.playMusicFragment, bundle)
            }
        })
    }

}