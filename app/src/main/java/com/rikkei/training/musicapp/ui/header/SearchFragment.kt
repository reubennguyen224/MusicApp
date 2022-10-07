package com.rikkei.training.musicapp.ui.header

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.adapter.MusicAdapter
import com.rikkei.training.musicapp.adapter.MusicListAdapter
import com.rikkei.training.musicapp.databinding.FragmentSearchBinding
import com.rikkei.training.musicapp.model.Song
import com.rikkei.training.musicapp.model.SongDetail
import com.rikkei.training.musicapp.viewmodel.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    val viewModel: SearchViewModel by viewModels()

    companion object{
        val musicListSearch= ArrayList<Song>()
        val musicListInternetSearch= ArrayList<Song>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val view = binding.root
        musicListSearch.clear()
        musicListInternetSearch.clear()
        return view
    }
    val musicLocalListener = object : MusicAdapter.OnItemClickListener{
        override fun onItemClick(position: Int) {
            val bundle = Bundle()
            bundle.putInt("songPosition", position)
            bundle.putString("album", "MusicSearchAdapter")
            findNavController().navigate(R.id.playMusicFragment, bundle)
        }
    }
    val musicInternetListener = object : MusicAdapter.OnItemClickListener{
        override fun onItemClick(position: Int) {
            val bundle = Bundle()
            bundle.putInt("songPosition", position)
            bundle.putString("album", "MusicSearchInternetAdapter")
            findNavController().navigate(R.id.playMusicFragment, bundle)
        }
    }

    val list = arrayListOf(
        SongDetail("Nhạc trên thiết bị", musicListSearch, musicLocalListener),
        SongDetail("Nhạc trực tuyến", musicListInternetSearch, musicInternetListener)
    )
    val adapter = MusicListAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBackSearch.setOnClickListener {
            findNavController().popBackStack()
        }
        adapter.dataset = list
        binding.searchResultList.adapter = adapter
        binding.searchResultList.layoutManager = LinearLayoutManager(context)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                musicListSearch.clear()
                if (newText != null){
                    val userInput = newText.lowercase()
                    viewModel.getLocalList(query = userInput).observe(viewLifecycleOwner){
                        musicListSearch.addAll(it)
                    }
                    adapter.notifyDataSetChanged()
                    lifecycleScope.launch{
                        //if (userInput != "")
                            callAPI(query = userInput)
                        withContext(Dispatchers.Main){
                            adapter.notifyDataSetChanged()
                        }
                    }
                    if (userInput == ""){
                        musicListSearch.clear()
                        musicListInternetSearch.clear()
                    }
                }
                return true
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    fun callAPI(query: String){
        viewModel.getInternetList(query).observe(viewLifecycleOwner){
            musicListInternetSearch.addAll(it)
        }

    }

}