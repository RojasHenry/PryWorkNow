package com.uisrael.worknow.ViewModel

import androidx.lifecycle.ViewModel
import com.uisrael.worknow.Model.FirebaseAuthRepository

class TabUsersViewModel : ViewModel() {
    private var authFirebaseRepository: FirebaseAuthRepository = FirebaseAuthRepository()

    fun logOutUsuario(){
        return authFirebaseRepository.loginOutUser()
    }
}