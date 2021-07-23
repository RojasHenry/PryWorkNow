package com.uisrael.worknow.ViewModel

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.uisrael.worknow.Model.Data.CredencialesData
import com.uisrael.worknow.Model.Data.TokenData
import com.uisrael.worknow.Model.Data.UsuariosData
import com.uisrael.worknow.Model.FirebaseAuthRepository
import com.uisrael.worknow.Model.FirebaseModelsRepository
import com.uisrael.worknow.ViewModel.ValidatorRespuestas.Respuesta
import com.uisrael.worknow.Views.Utilities.Utilitity
import com.wajahatkarim3.easyvalidation.core.Validator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class ClientViewModel : ViewModel() {

    private var modelsFirebaseRepository: FirebaseModelsRepository = FirebaseModelsRepository()
    private var authFirebaseRepository: FirebaseAuthRepository = FirebaseAuthRepository()

    var userGoogleView: FirebaseUser? = null

    private val _nombreCli = MutableStateFlow("")
    private val _apellidoCli = MutableStateFlow("")
    private val _ciudadCli = MutableStateFlow("")
    private val _telefonoCli = MutableStateFlow("")
    private var _correoCli = MutableStateFlow("")
    private val _passwordCli = MutableStateFlow("")
    private val _usuarioDatos = MutableStateFlow(UsuariosData())
    private val _usuarioCredentials = MutableStateFlow(CredencialesData())
    private val _usuarioDatosOK = MutableStateFlow(false)
    private val _usuarioCredencialesOK = MutableStateFlow(false)

    suspend fun registerViewCli(): FirebaseUser? {
        return authFirebaseRepository.registerUser(_usuarioCredentials.value.correo,_passwordCli.value)?.user
    }

    fun registerViewCliDataUsuario(uid: String): Any? {
        _usuarioDatos.value.rol = Utilitity.ROL_CLIENTE
        return modelsFirebaseRepository.registerUser(_usuarioDatos.value, uid)
    }

    fun registerViewCliCredenciales(uid: String, isFromSocNet: Boolean): Any? {
        _usuarioCredentials.value.fromSocNet = isFromSocNet
        return modelsFirebaseRepository.registerCredenciales(_usuarioCredentials.value, uid)
    }

    fun registerViewCliToken(uid: String, token:String): Any? {
        return modelsFirebaseRepository.registerViewUserToken(uid,TokenData(token = token))
    }

    fun getViewCredentialEmailUser(): Flow<CredencialesData?> {
        return modelsFirebaseRepository.getCredentialEmailUser(_usuarioCredentials.value.correo)
    }

    fun setFotoCli(fotoB64: String){
        _usuarioDatos.value.foto = fotoB64
    }

    fun setNombreCli (nombre: String){
        _nombreCli.value = nombre
        _usuarioDatos.value.nombre = nombre
        _usuarioDatosOK.value = true
    }

    fun setApellidoCli (apellido: String){
        _apellidoCli.value = apellido
        _usuarioDatos.value.apellido = apellido
        _usuarioDatosOK.value = true
    }

    fun setCiudadCli (ciudad: String){
        _ciudadCli.value = ciudad
        _usuarioDatos.value.ciudad = ciudad
        _usuarioDatosOK.value = true
    }

    fun setTelefonoCli (telefono: String){
        _telefonoCli.value = telefono
        _usuarioDatos.value.telefono = telefono
        _usuarioDatosOK.value = true
    }

    fun setCorreoCli (correo: String){
        _correoCli.value = correo
        _usuarioCredentials.value.correo = correo
        _usuarioCredencialesOK.value = true
    }

    fun setPasswordCli (password: String){
        _passwordCli.value = password
        _usuarioCredencialesOK.value = true
    }

    val isFormCliSucess: Flow<Boolean> = combine(_usuarioDatosOK,_usuarioCredencialesOK) { _, _ ->
        val validatorNombre = Validator(_usuarioDatos.value.nombre)
        val validatorApellido = Validator(_usuarioDatos.value.apellido)
        val validatorCiudad = Validator(_usuarioDatos.value.ciudad)
        val validatorTelefono = Validator(_usuarioDatos.value.telefono)
        val validatorCorreo= Validator(_usuarioCredentials.value.correo)
        val validatorPassword = Validator(_passwordCli.value)
        val isNombreValid = validatorNombre.nonEmpty().check()
        val isApellidoValid = validatorApellido.nonEmpty().check()
        val isCiudadValid = validatorCiudad.nonEmpty().check()
        val isTelefonoValid = (validatorTelefono.nonEmpty().check() and Patterns.PHONE.matcher(validatorTelefono.text).matches() and validatorTelefono.minLength(7).check())
        val isCorreoValid = validatorCorreo.nonEmpty().validEmail().check()
        val isPasswordValid = if (userGoogleView == null) validatorPassword.nonEmpty().check() else true
        Log.i("Cliente",_usuarioDatos.value.toString())
        if(isNombreValid and isApellidoValid and isCiudadValid and isTelefonoValid and isCorreoValid and isPasswordValid){
            return@combine true
        }else{
            _usuarioDatosOK.value = false
            _usuarioCredencialesOK.value = false
            return@combine false
        }

    }

    val isNombreCliOk: Flow<Respuesta> = combine(_nombreCli) { nombre ->
        val respuesta = Respuesta()
        val validatorNombre = Validator(nombre[0])
        if(!validatorNombre.nonEmpty().check()){
            respuesta.respuesta = 1
            respuesta.mensaje = "Complete el campo de nombre"
        }

        if (respuesta.respuesta != 0){
            _usuarioDatosOK.value = false
            _usuarioCredencialesOK.value = false
        }

        return@combine respuesta
    }

    val isApellidoCliOk: Flow<Respuesta> = combine(_apellidoCli) { apellido ->
        val respuesta = Respuesta()
        val validatorApellido = Validator(apellido[0])
        if(!validatorApellido.nonEmpty().check()){
            respuesta.respuesta = 1
            respuesta.mensaje = "Complete el campo de apellido"
        }

        if (respuesta.respuesta != 0){
            _usuarioDatosOK.value = false
            _usuarioCredencialesOK.value = false
        }

        return@combine respuesta
    }

    val isCiudadCliOk: Flow<Respuesta> = combine(_ciudadCli) { ciudad ->
        val respuesta = Respuesta()
        val validatorCiudad = Validator(ciudad[0])
        if(!validatorCiudad.nonEmpty().check()){
            respuesta.respuesta = 1
            respuesta.mensaje = "Complete el campo de ciudad"
        }

        if (respuesta.respuesta != 0){
            _usuarioDatosOK.value = false
            _usuarioCredencialesOK.value = false
        }

        return@combine respuesta
    }

    val isTelefonoCliOk: Flow<Respuesta> = combine(_telefonoCli) { telefono ->
        val respuesta = Respuesta()
        val validatorTelefono = Validator(telefono[0])
        if(validatorTelefono.nonEmpty().check()){
            if(validatorTelefono.validNumber().check() and validatorTelefono.minLength(7).check()){
                if(!Patterns.PHONE.matcher(validatorTelefono.text).matches()){
                    respuesta.respuesta = 1
                    respuesta.mensaje = "Teléfono inválido"
                }
            }else{
                respuesta.respuesta = 2
                respuesta.mensaje = "Teléfono incorrecto"
            }
        }else{
            respuesta.respuesta = 3
            respuesta.mensaje = "Complete el campo de Teléfono"
        }

        if (respuesta.respuesta != 0){
            _usuarioDatosOK.value = false
            _usuarioCredencialesOK.value = false
        }

        return@combine respuesta
    }

    val isCorreoCliOK: Flow<Respuesta> = combine(_correoCli) { correo ->
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

        if (respuesta.respuesta != 0){
            _usuarioDatosOK.value = false
            _usuarioCredencialesOK.value = false
        }

        return@combine respuesta
    }

    val isPasswordCliOK: Flow<Respuesta> = combine(_passwordCli) { password ->
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

        if (respuesta.respuesta != 0){
            _usuarioDatosOK.value = false
            _usuarioCredencialesOK.value = false
        }

        return@combine respuesta
    }

}