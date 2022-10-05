package com.rikkei.training.musicapp.splash

import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.rikkei.training.musicapp.MainActivity
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.databinding.FragmentSplashBinding
import kotlinx.coroutines.delay
import java.util.jar.Manifest

@Suppress("DEPRECATION")
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        askRequest()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun askRequest() {
        Dexter.withContext(context)
            .withPermissions(android.Manifest.permission.INTERNET,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                android.Manifest.permission.FOREGROUND_SERVICE) .withListener(object :
                MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    lifecycleScope.launchWhenCreated {
                        goToHomeFragment()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?,
                ) {
                    p1?.continuePermissionRequest()
                }

            }).check()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private suspend fun goToHomeFragment(){
        delay(3000)
        //askRequest() //ask permissions to use app
        findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToHomeFragment())
    }
}