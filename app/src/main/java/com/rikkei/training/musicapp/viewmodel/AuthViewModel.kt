package com.rikkei.training.musicapp.viewmodel

import android.app.Application
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rikkei.training.musicapp.model.DataAPIX
import com.rikkei.training.musicapp.model.ListMessage
import com.rikkei.training.musicapp.model.Message
import com.rikkei.training.musicapp.model.UserAPI
import com.rikkei.training.musicapp.ui.HomeFragment
import com.rikkei.training.musicapp.utils.MusicApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private var _loginRespone = MutableLiveData<ArrayList<DataAPIX>>()
    var avatarUri: String = ""

    private fun getRealPathFromURI(contentURI: Uri): String? {
        val result: String?
        val cursor: Cursor =
            getApplication<MusicApplication>().contentResolver.query(contentURI,
                null,
                null,
                null,
                null)!!
        cursor.moveToFirst()
        val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        result = cursor.getString(idx)
        cursor.close()
        return result
    }

    fun uploadFile(uri: Uri?, fileName: String) {
        var link = "https://hoang2204.000webhostapp.com/img/userAvatar/"
        if (uri == null) return
        val file = File(getRealPathFromURI(uri)!!)
        val requestBody =
            file.asRequestBody(getApplication<MusicApplication>().contentResolver.getType(uri)!!
                .toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part
            .createFormData("uploaded_file", fileName, requestBody)
        avatarUri = link + fileName
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                HomeFragment.loginAPI.uploadPhoto(multipartBody)
                    .enqueue(object : Callback<Message> {
                        override fun onResponse(call: Call<Message>, response: Response<Message>) =
                            Unit

                        override fun onFailure(call: Call<Message>, t: Throwable) = Unit
                    })
            }
        }

    }

    fun registerUser(
        username: String,
        password: String,
        dob: String,
        address: String,
        firstName: String,
        lastName: String,
    ) : LiveData<String>{
        val mes = MutableLiveData<String>()
        val userAvatar = "https://hoang2204.000webhostapp.com/img/userAvatar/default_avatar.jpg"
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                HomeFragment.loginAPI.register(username = username,
                    password = password,
                    dob = dob,
                    address = address,
                    firstname = firstName,
                    lastname = lastName,
                    avataruri = userAvatar).enqueue(object : Callback<ListMessage> {
                    override fun onResponse(
                        call: Call<ListMessage>,
                        response: Response<ListMessage>,
                    ) {
                        val body = response.body()!!
                        Toast.makeText(getApplication(), body[0].message, Toast.LENGTH_SHORT).show()
                        mes.postValue("success")
                    }

                    override fun onFailure(call: Call<ListMessage>, t: Throwable) {
                        Toast.makeText(getApplication(),
                            "Lỗi đăng ký, thử lại sau",
                            Toast.LENGTH_SHORT).show()
                        mes.postValue("failed")
                    }
                })
            }
        }
        return mes
    }

    fun uriNull() {
        avatarUri = HomeFragment.dataAPI[0].avataruri
    }

    fun callUpdateAPI(
        address: String,
        password: String,
        dob: String,
        firstName: String,
        lastName: String,
        userID: String,
    ): MutableLiveData<ArrayList<DataAPIX>> {
        val response = MutableLiveData<ArrayList<DataAPIX>>()
        viewModelScope.launch {
            val userData = ArrayList<DataAPIX>()
            withContext(Dispatchers.IO) {
                HomeFragment.loginAPI.updateUser(password = password,
                    firstname = firstName, lastname = lastName, address = address, dob = dob,
                    userID = userID, avataruri = avatarUri)
                    .enqueue(object : Callback<List<UserAPI>> {
                        override fun onResponse(
                            call: Call<List<UserAPI>>,
                            response: Response<List<UserAPI>>,
                        ) {
                            val body: ArrayList<UserAPI> = response.body() as ArrayList<UserAPI>
                            if (body.size > 0) {
                                if (body[0].status == 200) {
                                    userData.addAll(body[0].data)
                                    HomeFragment.dataAPI[0].avataruri = userData[0].avataruri
                                    HomeFragment.dataAPI[0].dob = userData[0].dob
                                    HomeFragment.dataAPI[0].firstname = userData[0].firstname
                                    HomeFragment.dataAPI[0].lastname = userData[0].lastname
                                    HomeFragment.dataAPI[0].password = userData[0].password
                                    HomeFragment.dataAPI[0].address = userData[0].address


                                    Toast.makeText(getApplication(),
                                        "Cập nhật thành công",
                                        Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(getApplication(),
                                        body[0].message,
                                        Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        override fun onFailure(call: Call<List<UserAPI>>, t: Throwable) {
                            Toast.makeText(getApplication(),
                                "Cập nhật thông tin thất bại!",
                                Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
                delay(1000)
                response.postValue(userData)
            }
        }
        return response
    }

    fun sendLogin(
        username: String,
        password: String,
    ): MutableLiveData<String> {
        val res = MutableLiveData<String>()
        viewModelScope.launch {
            val userData = ArrayList<DataAPIX>()
            withContext(Dispatchers.IO) {
                if (username.isNotEmpty() && password.length > 4) {
                    HomeFragment.loginAPI.login(username = username, password = password)
                        .enqueue(object : Callback<List<DataAPIX>> {
                            override fun onResponse(
                                call: Call<List<DataAPIX>>,
                                response: Response<List<DataAPIX>>,
                            ) {
                                val body = response.body()
                                //if (body.size > 0) {
                                   // if (body[0].status == 200) {
                                        userData.addAll(body!!)
                                        HomeFragment.dataAPI.add(
                                            DataAPIX(
                                                address = userData[0].address,
                                                avataruri = userData[0].avataruri,
                                                dob = userData[0].dob,
                                                firstname = userData[0].firstname,
                                                lastname = userData[0].lastname,
                                                password = userData[0].password,
                                                Id = userData[0].Id,
                                                username = userData[0].username
                                            )
                                        )
                                        val i: Int = Random(32).nextInt()
                                        val token = Integer.toHexString(i)
                                        HomeFragment.userToken = token

//                                    } else {
//                                        Toast.makeText(getApplication(),
//                                            body[0].message,
//                                            Toast.LENGTH_SHORT).show()
//                                    }
                                //}
                                res.postValue("success")
                            }

                            override fun onFailure(call: Call<List<DataAPIX>>, t: Throwable) {
                                Toast.makeText(getApplication(),
                                    "Đăng nhập thất bại!",
                                    Toast.LENGTH_SHORT).show()
                            }
                        })

                } else Toast.makeText(getApplication(),
                    "Vui lòng nhập mật khẩu/tên đăng nhập",
                    Toast.LENGTH_SHORT)
                    .show()
            }
        }
        return res
    }
}