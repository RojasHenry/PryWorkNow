package com.uisrael.worknow.ViewModel

import androidx.lifecycle.ViewModel
import com.uisrael.worknow.Model.Data.CategoriasData
import com.uisrael.worknow.Model.Data.FotosPublicacionData
import com.uisrael.worknow.Model.Data.PublicationsData
import com.uisrael.worknow.Model.Data.UsuariosData
import com.uisrael.worknow.Model.FirebaseModelsRepository
import kotlinx.coroutines.flow.Flow

class OfferBottomSheetViewModel : ViewModel() {
    private var modelFirebaseRepository: FirebaseModelsRepository = FirebaseModelsRepository()

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
}