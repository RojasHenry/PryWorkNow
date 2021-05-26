package com.uisrael.worknow.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.uisrael.worknow.Model.Data.ComentariosData
import com.uisrael.worknow.Model.Data.UsuariosData
import com.uisrael.worknow.Model.FirebaseAuthRepository
import com.uisrael.worknow.Model.FirebaseModelsRepository
import com.uisrael.worknow.ViewModel.ValidatorRespuestas.Respuesta
import com.uisrael.worknow.Views.Utilities.Utilitity
import com.wajahatkarim3.easyvalidation.core.Validator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import java.util.*
import kotlin.collections.ArrayList

class CommentsViewModel : ViewModel() {

    private var modelFirebaseRepository: FirebaseModelsRepository = FirebaseModelsRepository()
    private var authFirebaseRepository: FirebaseAuthRepository = FirebaseAuthRepository()

    lateinit var usuarioCurrentData:UsuariosData
    lateinit var usuarioUidData: String

    private val _mensaje = MutableStateFlow("")
    private val _usuarioComentario = MutableStateFlow(ComentariosData())
    private val _usuarioComentarioOK = MutableStateFlow(false)

    fun getViewUserLogged(): MutableLiveData<FirebaseUser> {
        return authFirebaseRepository.getUserLogged()
    }

    fun getCurrentUser(uid: String, finish: Boolean): Flow<UsuariosData?> {
        return modelFirebaseRepository.getCurrentUser(uid,finish)
    }

    fun sendViewCommentOffer(uidPub:String, uidEmisor: String, uidReceptor: String): Any? {
        _usuarioComentario.value.estado = Utilitity.COMMENT_ENVIADO
        _usuarioComentario.value.idEmisor = uidEmisor
        _usuarioComentario.value.idReceptor = uidReceptor
        _usuarioComentario.value.timespan = getTimespan()
        return modelFirebaseRepository.sendCommentOffer(uidPub, _usuarioComentario.value)
    }

    private fun getTimespan(): String {
        val calendar = Calendar.getInstance()
        return calendar.timeInMillis.toString()
    }

    fun getCommentsOffer(uidPub:String): Flow<MutableList<ComentariosData>> {
        return modelFirebaseRepository.getCommentsOffer(uidPub)
    }

    fun setComentarioMensaje (mensaje: String){
        _mensaje.value = mensaje
        _usuarioComentario.value.mensaje = mensaje
        _usuarioComentarioOK.value = true
    }

    fun setUpdateComment(
        uidPub: String,
        usuarioUidData: String,
        comentarios: ArrayList<ComentariosData>
    ) {
        comentarios.filter { v -> v.estado == Utilitity.COMMENT_ENVIADO }.map { comentariosData ->
            if(comentariosData.idReceptor == usuarioUidData){
                modelFirebaseRepository.setUpdateComment(uidPub,comentariosData.uid)
            }
        }
    }

    val isComentarioMensajeOk: Flow<Respuesta> = combine(_mensaje) { mensaje ->
        val respuesta = Respuesta()
        val validatorNombre = Validator(mensaje[0])
        if(!validatorNombre.nonEmpty().check()){
            respuesta.respuesta = 1
            respuesta.mensaje = "Ingrese un comentario"
        }

        if (respuesta.respuesta != 0){
            _usuarioComentarioOK.value = false
        }

        return@combine respuesta
    }
}