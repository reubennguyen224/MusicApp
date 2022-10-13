package com.rikkei.training.musicapp.ui.header

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.Images
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.rikkei.training.musicapp.databinding.FragmentProfileBinding
import com.rikkei.training.musicapp.ui.HomeFragment
import com.rikkei.training.musicapp.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import java.io.FileNotFoundException


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var uri: Uri? = null
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        setData()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MODE_CHANGED)
        return view
    }

    private fun setData() {
        binding.txtFirstName.editText?.setText(HomeFragment.dataAPI[0].firstname.trim())
        binding.txtLastName.editText?.setText(HomeFragment.dataAPI[0].lastname.trim())
        binding.txtUpdateAddress.editText?.setText(HomeFragment.dataAPI[0].address.trim())
        binding.txtUpdatePassword.editText?.setText("")
        binding.txtUpdateDob.editText?.setText(HomeFragment.dataAPI[0].dob)
        Glide.with(requireContext())
            .load(HomeFragment.dataAPI[0].avataruri)
            .centerCrop()
            .into(binding.imgAvatar)
    }

    @SuppressLint("IntentReset")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            binding.btnUpdateProfile.isEnabled = binding.txtUpdatePassword.editText?.text!!.isNotBlank()
        }
        binding.btnBack.setOnClickListener{
            findNavController().popBackStack()
        }

        binding.btnLogOut.setOnClickListener {
            HomeFragment.userToken = ""
            HomeFragment.dataAPI.clear()
            findNavController().popBackStack()
        }

        binding.btnChangeAvatar.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, 123)
        }

        binding.btnUpdateProfile.setOnClickListener {
            val firstName = binding.txtFirstName.editText?.text.toString()
            val lastName = binding.txtLastName.editText?.text.toString()
            val address = binding.txtUpdateAddress.editText?.text.toString()
            val password = binding.txtUpdatePassword.editText?.text.toString()
            val dob = binding.txtUpdateDob.editText?.text.toString()
            if (uri == null)
                viewModel.uriNull()

            if (password.equals("")){
                Toast.makeText(context, "Nhập lại mật khẩu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.callUpdateAPI(address, password, dob, firstName, lastName, HomeFragment.dataAPI[0].Id).observe(viewLifecycleOwner){
                setData()
                binding.txtFirstName.editText?.setText(it[0].firstname)
                binding.txtLastName.editText?.setText(it[0].lastname)
                binding.txtUpdateAddress.editText?.setText(it[0].address)
                binding.txtUpdateDob.editText?.setText(it[0].dob)
                binding.txtUpdatePassword.editText?.setText("")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 123 && resultCode == Activity.RESULT_OK){
            uri = data?.data
            try {
                Glide.with(requireContext())
                    .load(uri)
                    .centerCrop()
                    .into(binding.imgAvatar)
                viewModel.uploadFile(uri, HomeFragment.dataAPI[0].firstname + HomeFragment.dataAPI[0].Id + ".jpg")

            }catch (e: FileNotFoundException){
                e.printStackTrace()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }



}