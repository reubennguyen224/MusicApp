package com.rikkei.training.musicapp.ui.header

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.rikkei.training.musicapp.databinding.FragmentRegisterBinding
import com.rikkei.training.musicapp.viewmodel.AuthViewModel

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener{
            findNavController().popBackStack()
        }

        binding.btnRegister.setOnClickListener {
            val txtUserName = binding.txtUsername.editText?.text.toString()
            val txtPassword = binding.txtPassword.editText?.text.toString()
            val txtFirstName = binding.txtFirstName.editText?.text.toString()
            val txtLastName = binding.txtLastName.editText?.text.toString()
            val txtAddress = binding.txtAddress.editText?.text.toString()
            val txtDob = binding.txtdob.editText?.text.toString()

            if (txtPassword.equals("") || txtUserName.equals("")) return@setOnClickListener
            else viewModel.registerUser(username = txtUserName, password = txtPassword, dob = txtDob, address = txtAddress, firstName = txtFirstName, lastName = txtLastName)
        }
    }
}