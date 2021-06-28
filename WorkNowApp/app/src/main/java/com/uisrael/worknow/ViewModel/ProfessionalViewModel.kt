package com.uisrael.worknow.ViewModel

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.uisrael.worknow.Model.Data.*
import com.uisrael.worknow.Model.FirebaseAuthRepository
import com.uisrael.worknow.Model.FirebaseModelsRepository
import com.uisrael.worknow.ViewModel.ValidatorRespuestas.Respuesta
import com.uisrael.worknow.Views.Utilities.Utilitity
import com.wajahatkarim3.easyvalidation.core.Validator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class ProfessionalViewModel : ViewModel() {

    private var modelsFirebaseRepository: FirebaseModelsRepository = FirebaseModelsRepository()
    private var authFirebaseRepository: FirebaseAuthRepository = FirebaseAuthRepository()

    var userGoogleView: FirebaseUser? = null

    private val _nombreProf = MutableStateFlow("")
    private val _apellidoProf = MutableStateFlow("")
    private val _ciudadProf = MutableStateFlow("")
    private val _telefonoProf = MutableStateFlow("")

    private val _categoriasProf = MutableStateFlow("")
    private val _descripcionProf = MutableStateFlow("")

    private var _correoProf = MutableStateFlow("")
    private val _passwordProf = MutableStateFlow("")

    private val _usuarioDatos = MutableStateFlow(UsuariosData())
    private val _usuarioCredentials = MutableStateFlow(CredencialesData())
    private val _usuarioProfesional = MutableStateFlow(ProfesionalData())

    private val _usuarioDatosOK = MutableStateFlow(false)
    private val _usuarioCredencialesOK = MutableStateFlow(false)
    private val _usuarioProfesionalOK = MutableStateFlow(false)

    val caracteres = 20
    val numLengthTelefono = 7

    suspend fun registerViewProf(): FirebaseUser? {
        return authFirebaseRepository.registerUser(_usuarioCredentials.value.correo,_usuarioCredentials.value.password)?.user
    }

    fun getViewCredentialEmailUser(): Flow<CredencialesData?> {
        return modelsFirebaseRepository.getCredentialEmailUser(_usuarioCredentials.value.correo)
    }

    fun registeViewProfDataUsuario(uid: String): Any? {
        _usuarioDatos.value.rol = Utilitity.ROL_PROFESIONAL
        _usuarioDatos.value.datosProf = _usuarioProfesional.value
        return modelsFirebaseRepository.registerUser(_usuarioDatos.value, uid)
    }
    fun registeViewProfCredenciales(uid: String, fromSocialNet: Boolean): Any? {
        _usuarioCredentials.value.fromSocNet = fromSocialNet
        return modelsFirebaseRepository.registerCredenciales(_usuarioCredentials.value, uid)
    }

    fun registerViewProfToken(uid: String, token:String): Any? {
        return modelsFirebaseRepository.registerViewUserToken(uid, TokenData(token = token))
    }

    fun getViewCategorias (): Flow<List<CategoriasData>> {
        return modelsFirebaseRepository.getCategorias
    }

    fun setFotoProf(fotoB64: String){
        _usuarioDatos.value.foto = fotoB64
    }

    fun setNombreProf (nombre: String){
        _nombreProf.value = nombre
        _usuarioDatos.value.nombre = nombre
        _usuarioDatosOK.value = true
    }

    fun setApellidoProf (apellido: String){
        _apellidoProf.value = apellido
        _usuarioDatos.value.apellido = apellido
        _usuarioDatosOK.value = true
    }

    fun setCiudadProf (ciudad: String){
        _ciudadProf.value = ciudad
        _usuarioDatos.value.ciudad = ciudad
        _usuarioDatosOK.value = true
    }

    fun setTelefonoProf (telefono: String){
        _telefonoProf.value = telefono
        _usuarioDatos.value.telefono = telefono
        _usuarioDatosOK.value = true
    }

    fun setCategoriasProf (categorias: String){
        _categoriasProf.value = categorias
        _usuarioProfesional.value.categorias = categorias.split(",").map { it.trim() }
        _usuarioProfesionalOK.value = true
    }

    fun setDescripcionProf (descripcion: String){
        _descripcionProf.value = descripcion
        _usuarioProfesional.value.descripcion = descripcion
        _usuarioProfesionalOK.value = true
    }

    fun setCorreoProf (correo: String){
        _correoProf.value = correo
        _usuarioCredentials.value.correo = correo
        _usuarioCredencialesOK.value = true
    }

    fun setPasswordProf (password: String){
        _passwordProf.value = password
        _usuarioCredentials.value.password = password
        _usuarioCredencialesOK.value = true
    }


    val isFormProfSucess: Flow<Boolean> = combine(_usuarioDatosOK,_usuarioCredencialesOK,_usuarioProfesionalOK) { _, _, _->
        val validatorNombre = Validator(_usuarioDatos.value.nombre)
        val validatorApellido = Validator(_usuarioDatos.value.apellido)
        val validatorCiudad = Validator(_usuarioDatos.value.ciudad)
        val validatorTelefono = Validator(_usuarioDatos.value.telefono)
        val validatorDescripcion= Validator(_usuarioProfesional.value.descripcion)
        val validatorCategorias = Validator(_usuarioProfesional.value.categorias.joinToString())
        val validatorCorreo= Validator(_usuarioCredentials.value.correo)
        val validatorPassword = Validator(_usuarioCredentials.value.password)
        val isNombreValid = validatorNombre.nonEmpty().check()
        val isApellidoValid = validatorApellido.nonEmpty().check()
        val isCiudadValid = validatorCiudad.nonEmpty().check()
        val isTelefonoValid = (validatorTelefono.nonEmpty().check() and Patterns.PHONE.matcher(validatorTelefono.text).matches() and validatorTelefono.minLength(numLengthTelefono).check())
        val isDescripcionValid = validatorDescripcion.nonEmpty().minLength(caracteres).check()
        val isCategoriasValid = validatorCategorias.nonEmpty().check() and (validatorCategorias.text != "A") and (validatorCategorias.text != "N") and (validatorCategorias.text != "C") and (validatorCategorias.text != "Escoja su categoria")
        val isCorreoValid = validatorCorreo.nonEmpty().validEmail().check()
        val isPasswordValid = if (userGoogleView == null) validatorPassword.nonEmpty().check() else true
        Log.i("Profesional",_usuarioDatos.value.toString())
        if(isNombreValid and isApellidoValid and isCiudadValid and isTelefonoValid and isCorreoValid and isPasswordValid and isDescripcionValid and isCategoriasValid){
            return@combine true
        }else{
            _usuarioDatosOK.value = false
            _usuarioCredencialesOK.value = false
            _usuarioProfesionalOK.value = false
            return@combine false
        }

    }

    val isNombreProfOk: Flow<Respuesta> = combine(_nombreProf) { nombre ->
        val respuesta = Respuesta()
        val validatorNombre = Validator(nombre[0])
        if(!validatorNombre.nonEmpty().check()){
            respuesta.respuesta = 1
            respuesta.mensaje = "Complete el campo de nombre"
        }

        if (respuesta.respuesta != 0){
            _usuarioDatosOK.value = false
            _usuarioCredencialesOK.value = false
            _usuarioProfesionalOK.value = false
        }

        return@combine respuesta
    }

    val isApellidoProfOk: Flow<Respuesta> = combine(_apellidoProf) { apellido ->
        val respuesta = Respuesta()
        val validatorApellido = Validator(apellido[0])
        if(!validatorApellido.nonEmpty().check()){
            respuesta.respuesta = 1
            respuesta.mensaje = "Complete el campo de apellido"
        }

        if (respuesta.respuesta != 0){
            _usuarioDatosOK.value = false
            _usuarioCredencialesOK.value = false
            _usuarioProfesionalOK.value = false
        }

        return@combine respuesta
    }

    val isCiudadProfOk: Flow<Respuesta> = combine(_ciudadProf) { ciudad ->
        val respuesta = Respuesta()
        val validatorCiudad = Validator(ciudad[0])
        if(!validatorCiudad.nonEmpty().check()){
            respuesta.respuesta = 1
            respuesta.mensaje = "Complete el campo de ciudad"
        }

        if (respuesta.respuesta != 0){
            _usuarioDatosOK.value = false
            _usuarioCredencialesOK.value = false
            _usuarioProfesionalOK.value = false
        }

        return@combine respuesta
    }

    val isTelefonoProfOk: Flow<Respuesta> = combine(_telefonoProf) { telefono ->
        val respuesta = Respuesta()
        val validatorTelefono = Validator(telefono[0])
        if(validatorTelefono.nonEmpty().check()){
            if(validatorTelefono.validNumber().check() and validatorTelefono.minLength(numLengthTelefono).check()){
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
            _usuarioProfesionalOK.value = false
        }

        return@combine respuesta
    }

    val isCategoriaProfOk: Flow<Respuesta> = combine(_categoriasProf) { categorias ->
        val respuesta = Respuesta()
        if(categorias[0].isEmpty()){
            respuesta.respuesta = 2
            respuesta.mensaje = "Escoja por lo menos una categoría"
        }else{
            when(categorias[0]){
                "A","C","N", "Escoja su categoría" -> {
                    respuesta.respuesta = 1
                    respuesta.mensaje = "Escoja por lo menos una categoría"
                }
            }
        }

        if (respuesta.respuesta != 0){
            _usuarioDatosOK.value = false
            _usuarioCredencialesOK.value = false
            _usuarioProfesionalOK.value = false
        }

        return@combine respuesta
    }

    val isDescripcionProfOk: Flow<Respuesta> = combine(_descripcionProf) { descripcion ->
        val respuesta = Respuesta()
        val validatorDescripcion = Validator(descripcion[0])
        if(validatorDescripcion.nonEmpty().check()){
            if(!validatorDescripcion.minLength(caracteres).check()){
                respuesta.respuesta = 2
                respuesta.mensaje = "Ingrese por lo menos $caracteres caracteres de descripción"
            }
        }else{
            respuesta.respuesta = 1
            respuesta.mensaje = "Complete el campo de descripción"
        }

        if (respuesta.respuesta != 0){
            _usuarioDatosOK.value = false
            _usuarioCredencialesOK.value = false
            _usuarioProfesionalOK.value = false
        }

        return@combine respuesta
    }

    val isCorreoProfOK: Flow<Respuesta> = combine(_correoProf) { correo ->
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
            _usuarioProfesionalOK.value = false
        }

        return@combine respuesta
    }

    val isPasswordProfOK: Flow<Respuesta> = combine(_passwordProf) { password ->
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
            _usuarioProfesionalOK.value = false
        }

        return@combine respuesta
    }



}