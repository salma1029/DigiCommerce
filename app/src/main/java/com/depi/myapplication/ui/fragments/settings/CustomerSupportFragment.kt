package com.depi.myapplication.ui.fragments.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.depi.myapplication.data.models.User
import com.depi.myapplication.databinding.FragmentCustomerSupportBinding


class CustomerSupportFragment : Fragment() {
    private lateinit var binding: FragmentCustomerSupportBinding
    private val args by navArgs<CustomerSupportFragmentArgs>()
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = args.user
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCustomerSupportBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.welcomeUser.text = buildString {
            append("Welcome ${user.firstName}")
        }

        binding.imgPhone.setOnClickListener {
            phoneSupport()
        }

        binding.imgEmail.setOnClickListener {
            emailSupport()
        }

        binding.imgCloseHelp.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    private fun phoneSupport() {
        Intent(Intent.ACTION_DIAL, Uri.parse("tel:+201156717308")).also {
            startActivity(it)
        }
    }

    private fun emailSupport() {
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("zeyademad1z234@gmail.com")) // recipients
            putExtra(Intent.EXTRA_SUBJECT, "")
            putExtra(Intent.EXTRA_TEXT, "")
            putExtra(Intent.EXTRA_STREAM, Uri.parse("content://path/to/email/attachment"))
        }.also { startActivity(it) }
    }

}