package com.uisrael.worknow.ViewModel

import androidx.lifecycle.ViewModel
import com.uisrael.worknow.Model.Data.CalificacionData
import com.uisrael.worknow.Model.Data.UsuariosData
import com.uisrael.worknow.Model.FirebaseModelsRepository
import kotlinx.coroutines.flow.Flow

class QualificationViewModel : ViewModel() {

    lateinit var currentProf:UsuariosData
    private var modelFirebaseRepository: FirebaseModelsRepository = FirebaseModelsRepository()

    fun getCurrentViewUser(uid:String ): Flow<UsuariosData?> {
        return modelFirebaseRepository.getCurrentUser(uid,true)
    }

    fun setQualificationViewOffer(uidPub: String, qualif: Double): Any? {
        return modelFirebaseRepository.setQualificationOffer(uidPub, qualif)
    }

    fun setQualificationViewProf(uidPub: String, uidProf: String, qualif: Double): Any? {
        val qualifProf = CalificacionData()
        qualifProf.calificacion = qualif
        qualifProf.uidPublicacion = uidPub
        currentProf.datosProf.calificaciones += qualifProf
        return modelFirebaseRepository.setQualificationProf(uidProf, currentProf.datosProf.calificaciones)
    }
}