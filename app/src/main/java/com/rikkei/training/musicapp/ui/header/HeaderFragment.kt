package com.rikkei.training.musicapp.ui.header

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.databinding.FragmentHeaderBinding
import com.rikkei.training.musicapp.ui.HomeFragment

class HeaderFragment : Fragment() {
    private var _binding: FragmentHeaderBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentHeaderBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imgAvatarUser.setOnClickListener {
            var uri = Uri.parse("android-app://com.rikkei.training.musicapp/login")
            if (HomeFragment.userToken == "")
                findNavController().navigate(uri)
            else {
                uri = Uri.parse("android-app://com.rikkei.training.musicapp/update")
                findNavController().navigate(uri)
            }
        }

        binding.searchViewBar.setOnClickListener {
            val uri = Uri.parse("android-app://com.rikkei.training.musicapp/search")
            findNavController().navigate(uri)
        }

        if (HomeFragment.dataAPI.isNotEmpty()) {
            if (HomeFragment.dataAPI[0].avataruri != "")
                Glide.with(requireContext())
                    .load(HomeFragment.dataAPI[0].avataruri)
                    .fitCenter()
                    .into(binding.imgAvatarUser)
            else
                Glide.with(requireContext())
                    .load(R.drawable.splash_logo)
                    .centerCrop()
                    .into(binding.imgAvatarUser)
        }
    }
}