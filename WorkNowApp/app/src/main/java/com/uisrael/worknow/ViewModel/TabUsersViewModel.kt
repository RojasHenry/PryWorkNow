package com.uisrael.worknow.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.uisrael.worknow.Model.Data.UsuariosData
import com.uisrael.worknow.Model.FirebaseAuthRepository
import com.uisrael.worknow.Model.FirebaseModelsRepository
import kotlinx.coroutines.flow.Flow

class TabUsersViewModel : ViewModel() {

    lateinit var userCurrent: UsuariosData;
    private var authFirebaseRepository: FirebaseAuthRepository = FirebaseAuthRepository()
    private var modelFirebaseRepository: FirebaseModelsRepository = FirebaseModelsRepository()

    fun logOutUsuario(){
        return authFirebaseRepository.loginOutUser()
    }

    fun getCurrentUser(uid: String, finish: Boolean): Flow<UsuariosData?> {
        return modelFirebaseRepository.getCurrentUser(uid,finish)
    }

    fun getUserLogged(): MutableLiveData<FirebaseUser> {
        return authFirebaseRepository.getUserLogged()
    }
}