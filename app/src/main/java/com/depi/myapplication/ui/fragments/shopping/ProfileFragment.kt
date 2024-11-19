package com.depi.myapplication.ui.fragments.shopping

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.depi.myapplication.MainActivity
import com.depi.myapplication.R
import com.depi.myapplication.data.models.User
import com.depi.myapplication.databinding.FragmentProfileBinding
import com.depi.myapplication.ui.state.Resource
import com.depi.myapplication.ui.viewmodel.settings.ProfileViewModel
import com.depi.myapplication.util.viewutility.showBottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var user: User
    val viewModel by viewModels<ProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        goPassDataToCustomer()

        goToUserAccount()

        goToOrdersFragment()

        goToBillingFragment()

        goToLanguage()

        logout()

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.user.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            binding.progressbarSettings.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {
                            binding.progressbarSettings.visibility = View.GONE
                            Glide.with(requireView()).load(it.data!!.imagePath).error(
                                ColorDrawable(
                                    Color.BLACK
                                )
                            ).into(binding.imgUser)
                            binding.tvUserName.text = "${it.data.firstName} ${it.data.lastName}"
                            user = it.data

                        }

                        is Resource.Error -> {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                            binding.progressbarSettings.visibility = View.GONE
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun goToLanguage() {
      binding.linearLanguage.setOnClickListener {
          findNavController().navigate(
              R.id.action_profileFragment_to_languageFragment
          )
      }
    }

    private fun logout() {
        binding.linearOut.setOnClickListener {
            viewModel.logout()
            val intent = Intent(requireActivity(), MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or  Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun goToBillingFragment() {
        binding.linearBilling.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToBillingFragment(
                0f,
                emptyArray(),
                false
            )
            findNavController().navigate(action)
        }
    }

    private fun goToUserAccount() {
        binding.constraintProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_userAccountFragment)
        }
    }

    private fun goPassDataToCustomer() {
        binding.linearHelp.setOnClickListener {
            val action =
                ProfileFragmentDirections.actionProfileFragmentToCustomerSupportFragment(user)
            findNavController().navigate(action)
        }
    }

    private fun goToOrdersFragment() {
        binding.linearOrders.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_ordersFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}











