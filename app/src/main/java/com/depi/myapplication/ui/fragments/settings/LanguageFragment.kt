package com.depi.myapplication.ui.fragments.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.depi.myapplication.R
import com.depi.myapplication.databinding.FragmentLanguageBinding
import com.depi.myapplication.ui.activities.ShoppingActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale

class LanguageFragment : Fragment() {
    private lateinit var binding: FragmentLanguageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLanguageBinding.inflate(inflater)
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.visibility = View.GONE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentLanguage = Locale.getDefault().language

        binding.imgCloseLanguage.setOnClickListener {
            findNavController().navigateUp()
        }

        when (currentLanguage) {
            "en" -> {
                changeToEnglish()
            }

            "ar" -> {
                changeToArabic()
            }
        }

        binding.linearArabic.setOnClickListener {
            changeLanguage("ar")
        }

        binding.linearEnglish.setOnClickListener {
            changeLanguage("en")
        }

    }

    private fun changeToArabic() {
        binding.apply {
            imgArabic.visibility = View.VISIBLE
            imgEnglish.visibility = View.INVISIBLE
        }
    }

    private fun changeToEnglish() {
        binding.apply {
            imgArabic.visibility = View.INVISIBLE
            imgEnglish.visibility = View.VISIBLE
        }
    }

    private fun changeLanguage(code: String) {
        val intent = Intent(requireActivity(), ShoppingActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

        val sharedPref = activity?.getSharedPreferences("Language", Context.MODE_PRIVATE)
        sharedPref?.edit()?.putString("language", "en")?.apply()
        if (code == "en") {
            setLocal(requireActivity(),"en")
            changeToEnglish()
            sharedPref?.edit()?.putString("language", "en")?.apply()
            startActivity(intent)
        } else if (code == "ar") {
            setLocal(requireActivity(),"ar")
            changeToArabic()
            sharedPref?.edit()?.putString("language", "ar")?.apply()
            startActivity(intent)
        }
    }

    private fun setLocal(activity: Activity, langCode: String) {
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val resources = context?.resources
        val config = resources?.configuration
        config?.locale = locale
        resources?.updateConfiguration(config,resources.displayMetrics)
    }
}