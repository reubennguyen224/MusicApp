package com.rikkei.training.musicapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.rikkei.training.musicapp.databinding.FragmentLoginBinding
import com.rikkei.training.musicapp.model.DataAPIX
import com.rikkei.training.musicapp.model.UserAPI
import com.rikkei.training.musicapp.utils.LoginAPI
import com.rikkei.training.musicapp.utils.LoginClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var token: String
    private val userData = ArrayList<DataAPIX>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    private fun sendLogin(username: String, password: String){
        val loginAPI = LoginClient.getInstance().create(LoginAPI::class.java)
        if (username.isNotEmpty() && password.length > 4){
            loginAPI.login(username = username, password = password).enqueue(object : Callback<List<UserAPI>> {
                override fun onResponse(
                    call: Call<List<UserAPI>>,
                    response: Response<List<UserAPI>>,
                ) {
                    val body: ArrayList<UserAPI> = response.body() as ArrayList<UserAPI>
                    if (body.size > 0){
                        if (body[0].status == 200){
                            userData.addAll(body[0].data)
                            HomeFragment.dataAPI.add(DataAPIX(
                                address = userData[0].address,
                                avataruri = userData[0].avataruri,
                                dob = userData[0].dob,
                                firstname = userData[0].firstname,
                                lastname = userData[0].lastname,
                                password = userData[0].password,
                                Id = userData[0].Id,
                                username = userData[0].username
                            ))
                            val i: Int = Random(32).nextInt()
                            val token = Integer.toHexString(i)
                            HomeFragment.userToken = token
                            findNavController().popBackStack()
                            Toast.makeText(context, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()

                        } else{
                            Toast.makeText(context, body[0].message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<List<UserAPI>>, t: Throwable) {
                    Toast.makeText(context, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show()
                }
            })
        } else Toast.makeText(context, "Vui lòng nhập mật khẩu/tên đăng nhập", Toast.LENGTH_SHORT).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener{
            val username = binding.txtUsername.editText?.text.toString().trim()
            val password = binding.txtPassword.editText?.text.toString().trim()
            sendLogin(username = username, password = password)
        }
    }

}