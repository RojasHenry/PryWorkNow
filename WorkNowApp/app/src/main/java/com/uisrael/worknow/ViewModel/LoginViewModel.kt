package com.uisrael.worknow.ViewModel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.uisrael.worknow.Model.FirebaseAuthRepository

class LoginViewModel : ViewModel() {
    private var authFirebaseRepository: FirebaseAuthRepository = FirebaseAuthRepository()

    suspend fun registerViewUser(email: String, password: String): FirebaseUser?{
        return authFirebaseRepository.registerUser(email, password)?.user
    }

}