package com.rikkei.training.musicapp.ui.header

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.rikkei.training.musicapp.databinding.FragmentLoginBinding
import com.rikkei.training.musicapp.viewmodel.AuthViewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var token: String
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val username = binding.txtUsername.editText?.text.toString().trim()
            val password = binding.txtPassword.editText?.text.toString().trim()
            viewModel.sendLogin(username = username, password = password)
                .observe(viewLifecycleOwner) {
                    //if (it.size > 0) {
                        Toast.makeText(context,
                            "Đăng nhập thành công",
                            Toast.LENGTH_SHORT)
                            .show()
                        findNavController().popBackStack()
                    //} else {
//                        Toast.makeText(context,
//                            "Đăng nhập thất bại!",
//                            Toast.LENGTH_SHORT).show()
                    //}

                }
        }

        binding.btnRegister.setOnClickListener {
            val uri = Uri.parse("android-app://com.rikkei.training.musicapp/register")
            findNavController().navigate(uri)
        }
    }

}