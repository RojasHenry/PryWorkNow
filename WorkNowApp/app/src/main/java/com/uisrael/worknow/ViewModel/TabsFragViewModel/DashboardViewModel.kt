package com.uisrael.worknow.ViewModel.TabsFragViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.uisrael.worknow.Model.Data.PublicationsData
import com.uisrael.worknow.Model.FirebaseAuthRepository
import com.uisrael.worknow.Model.FirebaseModelsRepository
import kotlinx.coroutines.flow.Flow

class DashboardViewModel : ViewModel() {
    private var authFirebaseRepository: FirebaseAuthRepository = FirebaseAuthRepository()
    private var modelFirebaseRepository: FirebaseModelsRepository = FirebaseModelsRepository()

    fun getUserViewLogged(): MutableLiveData<FirebaseUser> {
        return authFirebaseRepository.getUserLogged()
    }

    fun getOffersViewAcceptAndPublic(uidUser:String): Flow<MutableList<PublicationsData>> {
        return modelFirebaseRepository.getOffersAcceptAndPublic(uidUser)
    }
    fun getOffersViewNoCalifCli(uidUser:String, estado:String): Flow<MutableList<PublicationsData>> {
        return modelFirebaseRepository.getOffersNoCalifCli(uidUser,estado)
    }

    fun getOfferViewAccepted(uidProf: String): Flow<MutableList<PublicationsData>> {
        return modelFirebaseRepository.getOfferViewAccepted(uidProf)
    }
}