package com.uisrael.worknow.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.uisrael.worknow.Model.Data.PublicationsData
import com.uisrael.worknow.Model.FirebaseAuthRepository
import com.uisrael.worknow.Model.FirebaseModelsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

class HistoryOffersViewModel : ViewModel() {
    private var authFirebaseRepository: FirebaseAuthRepository = FirebaseAuthRepository()
    private var modelFirebaseRepository: FirebaseModelsRepository = FirebaseModelsRepository()

    fun getUserViewLogged(): MutableLiveData<FirebaseUser> {
        return authFirebaseRepository.getUserLogged()
    }

    fun getOffersViewPorStatusAndCli(uidCli:String, estado:String): Flow<MutableList<PublicationsData>> {
        return modelFirebaseRepository.getOffersPorStatusAndCli(uidCli,estado)
    }

    fun registerViewAgainOffer(publicationsAgainData: PublicationsData): Any? {
        val newPublicationsData = PublicationsData()
        newPublicationsData.fechaIni = publicationsAgainData.fechaIni
        newPublicationsData.fechaFin = publicationsAgainData.fechaFin
        newPublicationsData.descripcion = publicationsAgainData.descripcion
        newPublicationsData.idCategoria = publicationsAgainData.idCategoria
        newPublicationsData.idUsuarioCli = publicationsAgainData.idUsuarioCli
        newPublicationsData.inmediato = publicationsAgainData.inmediato
        newPublicationsData.soloUnaPersona = publicationsAgainData.soloUnaPersona
        newPublicationsData.telefono = publicationsAgainData.telefono
        newPublicationsData.tieneFotos = publicationsAgainData.tieneFotos
        newPublicationsData.ubicacion = publicationsAgainData.ubicacion
        return modelFirebaseRepository.registerOffer(newPublicationsData)
    }

    suspend fun registerViewAgainOfferPictures(uidPub:String, newUidPub:String) {
        val fotosPublications = modelFirebaseRepository.getOffersRespFoto(uidPub)
        fotosPublications.collect {
            if (it != null) {
                modelFirebaseRepository.registerPicturesOffer(it,newUidPub)
            }
        }
    }
}