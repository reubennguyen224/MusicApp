package com.rikkei.training.musicapp.ui.header

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.adapter.MusicAdapter
import com.rikkei.training.musicapp.adapter.SearchListAdapter
import com.rikkei.training.musicapp.databinding.FragmentSearchBinding
import com.rikkei.training.musicapp.viewmodel.SearchViewModel
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    val viewModel: SearchViewModel by activityViewModels()
    companion object{
        var searchQuery = ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    private val musicLocalListener = object : MusicAdapter.OnItemClickListener {
        override fun onItemClick(position: Int) {
            val bundle = Bundle()
            bundle.putInt("songPosition", position)
            bundle.putString("album", "MusicSearchAdapter")
            findNavController().navigate(R.id.playMusicFragment, bundle)
        }
    }

    private val musicInternetListener = object : MusicAdapter.OnItemClickListener {
        override fun onItemClick(position: Int) {
            val bundle = Bundle()
            bundle.putInt("songPosition", position)
            bundle.putString("album", "MusicSearchInternetAdapter")
            findNavController().navigate(R.id.playMusicFragment, bundle)
        }
    }

    private val songAdapter = SearchListAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBackSearch.setOnClickListener {
            findNavController().popBackStack()
        }

        lifecycleScope.launch{
            viewModel.getList().observe(viewLifecycleOwner) {
                songAdapter.dataset = it
                songAdapter.update(0, musicLocalListener)
                songAdapter.update(1, musicInternetListener)
                songAdapter.notifyDataSetChanged()
            }
        }

        binding.searchResultList.apply {
            adapter = songAdapter
            layoutManager = LinearLayoutManager(context)
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                //viewModel.setContent(newText)
                searchQuery = newText!!
                //lifecycleScope.launch{
                    viewModel.getLocalSongList(query = searchQuery).observe(viewLifecycleOwner){
                        songAdapter.update(0, SearchViewModel.musicListSearch)
                    }
                    viewModel.getInternetSongList(query = searchQuery).observe(viewLifecycleOwner){
                        songAdapter.update(1, it)
                    }
                    binding.searchResultList.adapter = songAdapter
                //}
                return true
            }
        })
    }

}