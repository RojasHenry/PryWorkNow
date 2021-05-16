package com.uisrael.worknow.ViewModel.TabsFragViewModel

import androidx.lifecycle.LiveData
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

    fun getOffersViewCliAceptedOnProgress (uidCli:String): Flow<MutableList<PublicationsData>> {
        return modelFirebaseRepository.getOffersCliAceptedOnProgress(uidCli)
    }

    fun getOffersViewProfAceptedOnProgress (uidProf:String): Flow<MutableList<PublicationsData>> {
        return modelFirebaseRepository.getOffersProfAceptedOnProgress(uidProf)
    }
}