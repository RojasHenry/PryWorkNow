package com.uisrael.worknow.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.uisrael.worknow.Model.Data.UsuariosData
import com.uisrael.worknow.Model.FirebaseAuthRepository
import com.uisrael.worknow.Model.FirebaseModelsRepository
import com.uisrael.worknow.ViewModel.ValidatorRespuestas.Respuesta
import com.wajahatkarim3.easyvalidation.core.Validator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class LoginViewModel : ViewModel() {

    private val _correo = MutableStateFlow("")
    private val _password = MutableStateFlow("")

    private var authFirebaseRepository: FirebaseAuthRepository = FirebaseAuthRepository()
    private var modelFirebaseRepository: FirebaseModelsRepository = FirebaseModelsRepository()

    suspend fun loginViewUser(email: String, password: String): FirebaseUser?{
        return authFirebaseRepository.loginUser(email, password)?.user
    }

    fun loginViewWithGoogle(idToken: String): Flow<FirebaseUser?> {
        return authFirebaseRepository.loginViewWithGoogle(idToken)
    }

    fun validateViewUserLogged(): MutableLiveData<Boolean> {
        return authFirebaseRepository.validateUserLogged()
    }

    fun getViewUserLogged(): MutableLiveData<FirebaseUser> {
        return authFirebaseRepository.getUserLogged()
    }

    fun getCurrentUser(uid:String ): Flow<UsuariosData?> {
        return modelFirebaseRepository.getCurrentUser(uid, true)
    }

    fun setCorreo (correo: String){
        _correo.value = correo
    }

    fun setPassword (password: String){
        _password.value = password
    }

    val isFormSucess: Flow<Boolean> = combine(_correo, _password) { correo, password ->
        val validatorCorreo = Validator(correo)
        val validatorPassword = Validator(password)
        val isCorreoValid = validatorCorreo.nonEmpty().validEmail().check()
        val isPasswordValid = validatorPassword.nonEmpty().check()
        return@combine isCorreoValid and isPasswordValid
    }

    val isCorreoOK: Flow<Respuesta> = combine(_correo) { correo ->
        val respuesta = Respuesta()
        val validatorCorreo = Validator(correo[0])
        if(validatorCorreo.nonEmpty().check()){
            if(!validatorCorreo.validEmail().check()){
                respuesta.respuesta = 2
                respuesta.mensaje = "Correo ingresado no válido"
            }
        }else{
            respuesta.respuesta = 1
            respuesta.mensaje = "Complete el campo de correo"
        }
        return@combine respuesta
    }

    val isPasswordOK: Flow<Respuesta> = combine(_password) { password ->
        val respuesta = Respuesta()
        val validatorPassword = Validator(password[0])
        if(validatorPassword.nonEmpty().check()){
            if(!validatorPassword.minLength(6).check()){
                respuesta.respuesta = 2
                respuesta.mensaje = "La contraseña debe ser de 6 o más caracteres."
            }
        }else{
            respuesta.respuesta = 1
            respuesta.mensaje = "Complete el campo de contraseña"
        }
        return@combine respuesta
    }
}