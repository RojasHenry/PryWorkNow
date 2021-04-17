package com.uisrael.worknow.ViewModel

import androidx.lifecycle.ViewModel
import com.uisrael.worknow.Model.Data.UsuariosData
import com.uisrael.worknow.Model.FirebaseModelsRepository

class RegisterViewModel : ViewModel() {
    private var modelFirebaseRepo: FirebaseModelsRepository = FirebaseModelsRepository()

    suspend fun registerViewUser(user:UsuariosData):Any?{
        return modelFirebaseRepo.registerUser(user)
    }

}