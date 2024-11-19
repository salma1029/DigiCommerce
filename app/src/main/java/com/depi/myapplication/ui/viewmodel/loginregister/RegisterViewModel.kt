package com.depi.myapplication.ui.viewmodel.loginregister


import android.util.Log
import androidx.lifecycle.ViewModel
import com.depi.myapplication.data.models.User
import com.depi.myapplication.data.remote.FirebaseUtility
import com.depi.myapplication.util.registerutility.RegisterFailedState
import com.depi.myapplication.util.registerutility.RegisterValidation
import com.depi.myapplication.util.registerutility.validateEmail
import com.depi.myapplication.util.registerutility.validatePassword
import com.depi.myapplication.ui.state.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseUtility: FirebaseUtility
) : ViewModel() {

    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register: Flow<Resource<User>> = _register

    private val _validation = Channel<RegisterFailedState>()
    val validation = _validation.receiveAsFlow()
    fun createAccountWithEmailAndPassword(user: User, password: String, confirmPassword: String) {

        if (checkValidation(user, password, confirmPassword)) {
            // أولاً، تحقق مما إذا كان البريد الإلكتروني موجودًا
            checkEmailExists(user.email) { emailExists ->
                if (emailExists) {
                    // إذا كان البريد الإلكتروني موجودًا، أرسل رسالة خطأ
                    runBlocking {
                        _validation.send(
                            RegisterFailedState(
                                email = RegisterValidation.Failed("This email is already in use."),
                                password = RegisterValidation.Success // أو أي قيمة مناسبة لحالة كلمة المرور
                            )
                        )
                    }
                } else {
                    // إذا لم يكن البريد الإلكتروني موجودًا، تابع إنشاء الحساب
                    runBlocking {
                        _register.emit(Resource.Loading())
                    }
                    firebaseUtility.createNewUser(user.email, password)
                        .addOnSuccessListener {
                            it.user?.let {
                                saveUserInfo(it.uid, user)
                                Log.d("Firestore", "User data added successfully.")
                            }
                        }.addOnFailureListener {
                            _register.value = Resource.Error(it.message.toString())
                            Log.w("Firestore", "Error adding user data", it)
                        }
                }
            }
        } else {
            val registerFailedState = RegisterFailedState(
                email = validateEmail(user.email),
                password = validatePassword(password, confirmPassword)
            )
            runBlocking {
                _validation.send(registerFailedState)
            }
        }
    }

    private fun checkEmailExists(email: String, callback: (Boolean) -> Unit) {
        firebaseUtility.checkUserExistence(email)
            .addOnSuccessListener { documents ->
                // إذا كانت هناك أي وثائق، فهذا يعني أن البريد الإلكتروني موجود
                callback(!documents.isEmpty)
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error checking email existence", exception)
                callback(false) // إذا حدث خطأ، اعتبر البريد الإلكتروني غير موجود
            }
    }

    private fun saveUserInfo(userUid: String, user: User) {
        firebaseUtility.saveUserData(userUid, user)
            .addOnSuccessListener {
                _register.value = Resource.Success(user)
            }
            .addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())
            }
    }

    private fun checkValidation(user: User, password: String , confirmPassword :String):Boolean {
        val emailvalidation = validateEmail(user.email)
        val passwordvalidation = validatePassword(password , confirmPassword)
        val shouldRegister = emailvalidation is RegisterValidation.Success
                && passwordvalidation is RegisterValidation.Success
        return shouldRegister
    }
}




