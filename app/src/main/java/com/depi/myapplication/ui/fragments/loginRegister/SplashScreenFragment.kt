package com.depi.myapplication.ui.fragments.loginRegister

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.depi.myapplication.R
import com.depi.myapplication.ui.activities.ShoppingActivity
import com.depi.myapplication.databinding.FragmentSplashScreenBinding

import com.depi.myapplication.ui.viewmodel.loginregister.IntroductionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreenFragment : Fragment() {
    private lateinit var binding: FragmentSplashScreenBinding
    private val viewModel by viewModels<IntroductionViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashScreenBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.navigate.collect {
                    delay(900)
//                    checkUserStatus()
                    when (it) {
                        IntroductionViewModel.SHOPPING_ACTIVITY -> {
                            Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            }
                        }

                        IntroductionViewModel.ACCOUNT_OPTIONS_FRAGMENT -> {
                            viewModel.startButtonClick()
                            findNavController().navigate(R.id.action_splashScreenFragment_to_introductionFragment)
                        }

                        else -> Unit
                    }
                }


            }
        }
    }

    private fun checkUserStatus() {
        val isUserLoggedIn = viewModel.getUserStatus()
        if (isUserLoggedIn) {
            Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        } else {
            findNavController().navigate(R.id.action_splashScreenFragment_to_introductionFragment)
        }
    }


}