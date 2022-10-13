package com.rikkei.training.musicapp.ui.discovery

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.adapter.SingerDetailAdapter
import com.rikkei.training.musicapp.databinding.FragmentSingerDetailBinding
import com.rikkei.training.musicapp.viewmodel.SingerDetailViewModel
import kotlin.math.abs

class SingerDetailFragment : Fragment() {
    private var _binding: FragmentSingerDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SingerDetailViewModel by activityViewModels()

    private val songAdapter = SingerDetailAdapter()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSingerDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        arguments?.let {
            viewModel.setCompanionObject(it)
        }

        viewModel.getArtistDetail().observe(viewLifecycleOwner){
            songAdapter.dataset = it
            songAdapter.notifyDataSetChanged()
        }

        viewModel.getMusicListOfSinger().observe(viewLifecycleOwner){
            binding.numSong.text = "${it.size} bài hát"
            songAdapter.update(0, it)
        }

        binding.listSong.apply {
            adapter = songAdapter
            layoutManager = LinearLayoutManager(context)
        }
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
//            if (findNavController().previousBackStackEntry?.destination?.id == R.id.playMusicFragment2){
                findNavController().navigate(R.id.discoveryFragment)
//            } else
//            findNavController().popBackStack()
        }

        viewModel.getSingerDetail().observe(viewLifecycleOwner){
            Glide.with(requireContext())
                .load(it.avatarID)
                .centerCrop()
                .into(binding.imgAvatarArtist)
            binding.titleSinger.text = it.name
            binding.titleFragment.text = it.name
        }

        binding.appbar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (abs(verticalOffset) - appBarLayout!!.totalScrollRange == 0) { // collapsing toolbar
                collapsingToolbar()
            } else { //expand toolbar
                expandToolbar()
            }
        }

        binding.btnPlayShuffle.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("songPosition", 0)
            bundle.putString("album", "SingerShuffleFragment")
            findNavController().navigate(R.id.playMusicFragment2, bundle)
        }

    }

    private fun collapsingToolbar() {
        binding.btnPlayShuffle.visibility = View.GONE
        binding.btnBack.setImageResource(R.drawable.ic_back_button)
        binding.titleSinger.visibility = View.GONE
        binding.numSong.visibility = View.GONE
        binding.titleFragment.visibility = View.VISIBLE
        binding.titleSinger.visibility = View.GONE
        binding.numSong.visibility = View.GONE
    }

    private fun expandToolbar() {
        binding.btnPlayShuffle.visibility = View.VISIBLE
        binding.titleSinger.visibility = View.VISIBLE
        binding.numSong.visibility = View.VISIBLE
        binding.titleFragment.visibility = View.GONE
    }

}