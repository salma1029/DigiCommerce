package com.depi.myapplication.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    val imagePath: String = " "   // It is used when user upload image profile
) : Parcelable {
    constructor() : this("", "", "", "")
}




