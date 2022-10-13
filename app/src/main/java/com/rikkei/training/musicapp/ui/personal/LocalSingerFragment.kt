package com.rikkei.training.musicapp.ui.personal

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.adapter.NewSingerAdapter
import com.rikkei.training.musicapp.databinding.FragmentLocalMusicBinding
import com.rikkei.training.musicapp.viewmodel.LocalViewModel

class LocalSingerFragment : Fragment() {
    private var _binding: FragmentLocalMusicBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LocalViewModel by viewModels()

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

    private val singerAdapter = NewSingerAdapter()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getSingerList().observe(viewLifecycleOwner){
            it.sortBy { singer ->
                singer.name
            }
            binding.titleNumSong.text = "${it.size} ca sĩ"
            singerAdapter.dataset = it
        }
        binding.musicRecyclerList.apply {
            adapter = singerAdapter
            layoutManager = LinearLayoutManager(context)
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.personalFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}