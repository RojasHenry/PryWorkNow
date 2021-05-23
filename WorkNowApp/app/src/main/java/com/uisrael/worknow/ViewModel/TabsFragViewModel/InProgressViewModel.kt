package com.uisrael.worknow.ViewModel.TabsFragViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.uisrael.worknow.Model.Data.PublicationsData
import com.uisrael.worknow.Model.FirebaseAuthRepository
import com.uisrael.worknow.Model.FirebaseModelsRepository
import kotlinx.coroutines.flow.Flow

class InProgressViewModel : ViewModel() {
    private var authFirebaseRepository: FirebaseAuthRepository = FirebaseAuthRepository()
    private var modelFirebaseRepository: FirebaseModelsRepository = FirebaseModelsRepository()

    fun getUserViewLogged(): MutableLiveData<FirebaseUser> {
        return authFirebaseRepository.getUserLogged()
    }

    fun getOffersViewCliAcceptedOnProgress (uidCli:String): Flow<MutableList<PublicationsData>> {
        return modelFirebaseRepository.getOffersCliAcceptedOnProgress(uidCli)
    }

    fun getOffersViewProfAceptedOnProgress (uidProf:String): Flow<MutableList<PublicationsData>> {
        return modelFirebaseRepository.getOffersProfAceptedOnProgress(uidProf)
    }

    fun setOfferViewProfUpdateEstado(uidPub:String, status:String): Any? {
        return modelFirebaseRepository.setOfferProfUpdateEstado(uidPub, status)
    }
}