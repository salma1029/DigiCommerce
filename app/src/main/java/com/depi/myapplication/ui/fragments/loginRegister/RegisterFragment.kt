package com.depi.myapplication.ui.fragments.loginRegister

import android.os.Bundle
import android.util.Log
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
import com.depi.myapplication.data.models.User
import com.depi.myapplication.databinding.FragmentRegisterBinding
import com.depi.myapplication.ui.state.Resource
import com.depi.myapplication.ui.viewmodel.loginregister.RegisterViewModel
import com.depi.myapplication.util.registerutility.RegisterValidation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private val TAG = "Register Fragment"

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: Inflating the layout for RegisterFragment")
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigate to login when the "Have an account?" text is clicked
        binding.tvHavaAccount.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_login)
        }
        Log.d(TAG, "onViewCreated: View created, ready to set up listeners")

        // Set up the register button click listener
        binding.apply {
            buttonRegisterRegister.setOnClickListener {
                Log.d(TAG, "onClick: Register button clicked")

                // Create a User object with input data
                val user = User(
                    edFirstNameRegister.text.toString().trim(),
                    edLastNameRegister.text.toString().trim(),
                    edEmailRegister.text.toString().trim()
                )
                val password = edPasswordRegister.text.toString()
                val confirmPassword = edConfirmPasswordRegister.text.toString()

                // Check if passwords match
                if (password == confirmPassword) {
                    // Call ViewModel to create an account
                    viewModel.createAccountWithEmailAndPassword(user, password, confirmPassword)
                    Log.d(TAG, "onClick: CreateAccountWithEmailAndPassword called")
                } else {
                    // Set error message if passwords do not match
                    binding.edConfirmPasswordRegister.error = "Passwords do not match"
                    binding.edConfirmPasswordRegister.requestFocus()
                }
            }
        }

        // Launch a coroutine to observe registration status
        lifecycleScope.launch {
            Log.d(TAG, "lifecycleScope: Launching coroutine to observe register LiveData")
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                Log.d(TAG, "repeatOnLifecycle: Collecting register flow from ViewModel")
                viewModel.register.collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            Log.d(TAG, "collect: Register process is Loading")
                            binding.buttonRegisterRegister.startAnimation()
                        }
                        is Resource.Success -> {
                            Log.d(TAG, "collect: Register Success - ${result.data}")
                            binding.buttonRegisterRegister.revertAnimation()
                        }
                        is Resource.Error -> {
                            Log.e(TAG, "collect: Register Error - ${result.message}")
                            binding.buttonRegisterRegister.revertAnimation()
                        }
                        else -> {
                            Log.d(TAG, "collect: Unknown result")
                        }
                    }
                }
            }
        }

        // Launch a coroutine to observe validation results
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.validation.collect { validation ->
                    // Handle email validation result
                    if (validation.email is RegisterValidation.Failed) {
                        binding.edEmailRegister.apply {
                            error = validation.email.message
                            requestFocus()
                        }
                    }

                    // Handle password validation result
                    if (validation.password is RegisterValidation.Failed) {
                        binding.edPasswordRegister.apply {
                            error = validation.password.message
                            requestFocus()
                        }
                        binding.edConfirmPasswordRegister.apply {
                            error = validation.password.message // Use the same error for confirm password
                            requestFocus()
                        }
                    }
                }
            }
        }
    }
}