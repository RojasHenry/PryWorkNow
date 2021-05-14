package com.uisrael.worknow.ViewModel.TabsFragViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.uisrael.worknow.Model.Data.PublicationsData
import com.uisrael.worknow.Model.Data.UsuariosData
import com.uisrael.worknow.Model.FirebaseAuthRepository
import com.uisrael.worknow.Model.FirebaseModelsRepository
import kotlinx.coroutines.flow.Flow

class PublicationsViewModel : ViewModel() {

    private var authFirebaseRepository: FirebaseAuthRepository = FirebaseAuthRepository()
    private var modelsFirebaseRepository: FirebaseModelsRepository = FirebaseModelsRepository()

    fun getOffersViewPorCategoria(categorias:List<String>): Flow<MutableList<PublicationsData>> {
        return modelsFirebaseRepository.getOffersPorCategoria(categorias)
    }

    fun getCategoriasProfesional(uid:String): Flow<UsuariosData?> {
        return modelsFirebaseRepository.getCurrentUser(uid, false)
    }

    fun getUidProfesional(): MutableLiveData<FirebaseUser> {
        return authFirebaseRepository.getUserLogged()
    }
}