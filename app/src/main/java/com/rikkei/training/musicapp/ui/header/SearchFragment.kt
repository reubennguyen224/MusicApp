package com.rikkei.training.musicapp.ui.header

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.adapter.MusicAdapter
import com.rikkei.training.musicapp.adapter.MusicListAdapter
import com.rikkei.training.musicapp.databinding.FragmentSearchBinding
import com.rikkei.training.musicapp.viewmodel.SearchViewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    val viewModel: SearchViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    val musicLocalListener = object : MusicAdapter.OnItemClickListener {
        override fun onItemClick(position: Int) {
            val bundle = Bundle()
            bundle.putInt("songPosition", position)
            bundle.putString("album", "MusicSearchAdapter")
            findNavController().navigate(R.id.playMusicFragment, bundle)
        }
    }
    val musicInternetListener = object : MusicAdapter.OnItemClickListener {
        override fun onItemClick(position: Int) {
            val bundle = Bundle()
            bundle.putInt("songPosition", position)
            bundle.putString("album", "MusicSearchInternetAdapter")
            findNavController().navigate(R.id.playMusicFragment, bundle)
        }
    }

    val adapter = MusicListAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBackSearch.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.getList().observe(viewLifecycleOwner) {
            adapter.dataset = it
            adapter.update(0, SearchViewModel.musicListSearch, musicLocalListener)
            adapter.update(1, SearchViewModel.musicListInternetSearch, musicInternetListener)
            adapter.notifyDataSetChanged()
        }

        binding.searchResultList.adapter = adapter
        binding.searchResultList.layoutManager = LinearLayoutManager(context)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setContent(newText)
                adapter.update(0, SearchViewModel.musicListSearch, musicLocalListener)
                adapter.update(1, SearchViewModel.musicListInternetSearch, musicInternetListener)
                adapter.notifyDataSetChanged()
                return true
            }
        })
    }


}