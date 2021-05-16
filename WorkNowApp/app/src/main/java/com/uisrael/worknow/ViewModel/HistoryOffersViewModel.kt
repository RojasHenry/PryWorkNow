package com.uisrael.worknow.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.uisrael.worknow.Model.Data.PublicationsData
import com.uisrael.worknow.Model.FirebaseAuthRepository
import com.uisrael.worknow.Model.FirebaseModelsRepository
import kotlinx.coroutines.flow.Flow

class HistoryOffersViewModel : ViewModel() {
    private var authFirebaseRepository: FirebaseAuthRepository = FirebaseAuthRepository()
    private var modelFirebaseRepository: FirebaseModelsRepository = FirebaseModelsRepository()

    fun getUserViewLogged(): MutableLiveData<FirebaseUser> {
        return authFirebaseRepository.getUserLogged()
    }

    fun getOffersViewPorStatusAndCli(uidCli:String, estado:String): Flow<MutableList<PublicationsData>> {
        return modelFirebaseRepository.getOffersPorStatusAndCli(uidCli,estado)
    }
}