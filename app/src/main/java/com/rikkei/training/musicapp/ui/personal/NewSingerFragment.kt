package com.rikkei.training.musicapp.ui.personal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rikkei.training.musicapp.adapter.NewSingerAdapter
import com.rikkei.training.musicapp.databinding.FragmentNewSingerBinding
import com.rikkei.training.musicapp.viewmodel.NewReleaseLocalViewModel

class NewSingerFragment : Fragment() {

    private var _binding: FragmentNewSingerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewReleaseLocalViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentNewSingerBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    private val singerAdapter = NewSingerAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getNewSingerLocal().observe(viewLifecycleOwner){
            singerAdapter.dataset  = it
        }

        binding.newSingerList.apply {
            adapter = singerAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }

    }

}