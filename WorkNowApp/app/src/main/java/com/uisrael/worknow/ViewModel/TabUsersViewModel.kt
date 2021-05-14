package com.uisrael.worknow.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.uisrael.worknow.Model.Data.UsuariosData
import com.uisrael.worknow.Model.FirebaseAuthRepository
import com.uisrael.worknow.Model.FirebaseModelsRepository
import kotlinx.coroutines.flow.Flow

class TabUsersViewModel : ViewModel() {
    private var authFirebaseRepository: FirebaseAuthRepository = FirebaseAuthRepository()
    private var modelFirebaseRepository: FirebaseModelsRepository = FirebaseModelsRepository()

    fun logOutUsuario(){
        return authFirebaseRepository.loginOutUser()
    }

    fun getCurrentUser(uid:String ): Flow<UsuariosData?> {
        return modelFirebaseRepository.getCurrentUser(uid,true)
    }

    fun getUserLogged(): MutableLiveData<FirebaseUser> {
        return authFirebaseRepository.getUserLogged()
    }
}