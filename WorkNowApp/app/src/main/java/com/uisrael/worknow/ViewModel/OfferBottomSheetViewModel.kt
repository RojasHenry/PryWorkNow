package com.uisrael.worknow.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.uisrael.worknow.Model.Data.CategoriasData
import com.uisrael.worknow.Model.Data.FotosPublicacionData
import com.uisrael.worknow.Model.Data.PublicationsData
import com.uisrael.worknow.Model.Data.UsuariosData
import com.uisrael.worknow.Model.FirebaseAuthRepository
import com.uisrael.worknow.Model.FirebaseModelsRepository
import kotlinx.coroutines.flow.Flow

class OfferBottomSheetViewModel : ViewModel() {
    private var modelFirebaseRepository: FirebaseModelsRepository = FirebaseModelsRepository()
    private var authFirebaseRepository: FirebaseAuthRepository = FirebaseAuthRepository()

    fun getCurrentViewUser(uid:String ): Flow<UsuariosData?> {
        return modelFirebaseRepository.getCurrentUser(uid,false)
    }

    fun getOffersViewRespFoto(uidPub: String): Flow<FotosPublicacionData?> {
        return modelFirebaseRepository.getOffersRespFoto(uidPub)
    }

    fun getCategoriaViewOfferName(uidCat: String): Flow<CategoriasData?> {
        return modelFirebaseRepository.getCategoriaOfferName(uidCat)
    }

    fun getEstadoViewCurrentOffer(uidPub: String): Flow<PublicationsData?> {
        return modelFirebaseRepository.getEstadoCurrentOffer(uidPub)
    }

    fun getUidProfesional(): MutableLiveData<FirebaseUser> {
        return authFirebaseRepository.getUserLogged()
    }

    fun setOfferViewAcceptProf (uidProf:String, uidPub: String, status:String): Any? {
        return modelFirebaseRepository.setOfferAcceptProf(uidProf,uidPub,status)
    }

    fun setOfferViewUpdateEstado(uidPub:String, status:String): Any? {
        return modelFirebaseRepository.setOfferProfUpdateEstado(uidPub, status)
    }
}