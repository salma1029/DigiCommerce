package com.depi.myapplication.data.local

import android.content.SharedPreferences
import com.depi.myapplication.util.constants.Constants.SharedPreferencesConstants.IS_USER_LOGGED_IN
import javax.inject.Inject

class SharedPreferencesUtil @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    fun saveUserData() {
        sharedPreferences.edit()
            .putBoolean(IS_USER_LOGGED_IN, true)
            .apply()
    }

    fun clearUserState() {
        sharedPreferences.edit()
            .clear()
            .apply()
    }
    fun getUserStatus():Boolean{
        return sharedPreferences.getBoolean(IS_USER_LOGGED_IN , true)
    }
}