package com.depi.myapplication.util.registerutility

data class RegisterFailedState(
    val email: RegisterValidation,
    val password: RegisterValidation
) {

}
