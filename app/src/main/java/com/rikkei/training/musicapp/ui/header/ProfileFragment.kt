package com.rikkei.training.musicapp.ui.header

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.Images
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.rikkei.training.musicapp.ui.HomeFragment
import com.rikkei.training.musicapp.databinding.FragmentProfileBinding
import com.rikkei.training.musicapp.model.DataAPIX
import com.rikkei.training.musicapp.model.Message
import com.rikkei.training.musicapp.model.UserAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileNotFoundException


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val userData = ArrayList<DataAPIX>()
    private var uri: Uri? = null
    var avatarUri: String? = ""

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
        binding.txtUpdatePassword.editText?.setText(HomeFragment.dataAPI[0].password)
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
                avatarUri = HomeFragment.dataAPI[0].avataruri

            callUpdateAPI(address, password, dob, firstName, lastName, HomeFragment.dataAPI[0].Id, avatarUri!!)
        }
    }

    private fun uploadFile(uri: Uri?, fileName: String) {
        var link = "https://hoang2204.000webhostapp.com/img/userAvatar/"
        if (uri == null) return
        val file = File(getRealPathFromURI(uri)!!)
        val requestBody =
            file.asRequestBody(requireActivity().contentResolver.getType(uri)!!.toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part
            .createFormData("uploaded_file", fileName, requestBody)
        avatarUri = link + fileName
        lifecycleScope.launch(Dispatchers.IO){
            HomeFragment.loginAPI.uploadPhoto(multipartBody).enqueue(object : Callback<Message>{
                override fun onResponse(call: Call<Message>, response: Response<Message>) = Unit
                override fun onFailure(call: Call<Message>, t: Throwable) = Unit
            })
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
                uploadFile(uri, HomeFragment.dataAPI[0].firstname + HomeFragment.dataAPI[0].Id + ".jpg")

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

    private fun callUpdateAPI(address: String, password: String, dob: String, firstName: String, lastName: String, userID: String, avatarUri: String){
        lifecycleScope.launch(Dispatchers.IO){
            HomeFragment.loginAPI.updateUser(password = password,
                firstname = firstName, lastname = lastName, address = address, dob = dob,
                userID = userID, avataruri = avatarUri).enqueue(object : Callback<List<UserAPI>>{
                override fun onResponse(call: Call<List<UserAPI>>, response: Response<List<UserAPI>>) {
                    val body: ArrayList<UserAPI> = response.body() as ArrayList<UserAPI>
                    if (body.size > 0){
                        if (body[0].status == 200){
                            userData.addAll(body[0].data)
                            HomeFragment.dataAPI[0].avataruri = userData[0].avataruri
                            HomeFragment.dataAPI[0].dob = userData[0].dob
                            HomeFragment.dataAPI[0].firstname = userData[0].firstname
                            HomeFragment.dataAPI[0].lastname= userData[0].lastname
                            HomeFragment.dataAPI[0].password = userData[0].password
                            HomeFragment.dataAPI[0].address = userData[0].address

                            setData()
                            Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                        } else{
                            Toast.makeText(context, body[0].message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<List<UserAPI>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Cập nhật thông tin thất bại!", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }

    }
    private fun getRealPathFromURI(contentURI: Uri): String? {
        val result: String?
        val cursor: Cursor =
            requireActivity().contentResolver.query(contentURI, null, null, null, null)!!
        cursor.moveToFirst()
        val idx: Int = cursor.getColumnIndex(Images.ImageColumns.DATA)
        result = cursor.getString(idx)
        cursor.close()
        return result
    }
}