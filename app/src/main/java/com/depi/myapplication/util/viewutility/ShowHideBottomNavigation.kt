package com.depi.myapplication.util.viewutility

import androidx.fragment.app.Fragment
import com.depi.myapplication.ui.activities.ShoppingActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.hideBottomNavigationView(){
    val bottomNavigationView =
        (activity as ShoppingActivity).findViewById<BottomNavigationView>(
            com.depi.myapplication.R.id.bottomNavigation
        )
    bottomNavigationView.visibility = android.view.View.GONE
}

fun Fragment.showBottomNavigationView(){
    val bottomNavigationView =
        (activity as ShoppingActivity).findViewById<BottomNavigationView>(
            com.depi.myapplication.R.id.bottomNavigation
        )
    bottomNavigationView.visibility = android.view.View.VISIBLE
}