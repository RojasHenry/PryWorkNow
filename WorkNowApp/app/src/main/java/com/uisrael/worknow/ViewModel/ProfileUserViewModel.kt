package com.uisrael.worknow.ViewModel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.uisrael.worknow.Model.Data.CategoriasData
import com.uisrael.worknow.Model.Data.CredencialesData
import com.uisrael.worknow.Model.Data.ProfesionalData
import com.uisrael.worknow.Model.Data.UsuariosData
import com.uisrael.worknow.Model.FirebaseModelsRepository
import com.uisrael.worknow.ViewModel.ValidatorRespuestas.Respuesta
import com.uisrael.worknow.Views.Utilities.Utilitity
import com.wajahatkarim3.easyvalidation.core.Validator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class ProfileUserViewModel : ViewModel() {
    private var modelsFirebaseRepository: FirebaseModelsRepository = FirebaseModelsRepository()

    lateinit var currentUser:UsuariosData
    lateinit var uidUserCurrent: String
    var isFromSocNet = false

    private val _nombreUser = MutableStateFlow("")
    private val _apellidoUser = MutableStateFlow("")
    private val _ciudadUser = MutableStateFlow("")
    private val _telefonoUser = MutableStateFlow("")

    private val _categoriasUser = MutableStateFlow("")
    private val _descripcionUser = MutableStateFlow("")

    private var _correoUser = MutableStateFlow("")
    private val _passwordUser = MutableStateFlow("")

    private val _usuarioDatos = MutableStateFlow(UsuariosData())
    private val _usuarioCredentials = MutableStateFlow(CredencialesData())
    private val _usuarioProfesional = MutableStateFlow(ProfesionalData())

    private val _usuarioDatosOK = MutableStateFlow(false)
    private val _usuarioCredencialesOK = MutableStateFlow(false)
    private val _usuarioProfesionalOK = MutableStateFlow(false)

    val caracteres = 20
    val numLengthTelefono = 7

    fun setDataUserViewUpdateUserProfile (info:String):Boolean{
        var currentUserAux:UsuariosData = currentUser
        currentUserAux.nombre = _nombreUser.value
        currentUserAux.apellido = _apellidoUser.value
        currentUserAux.ciudad = _ciudadUser.value
        currentUserAux.telefono = _telefonoUser.value
        if (currentUserAux.rol == Utilitity.ROL_PROFESIONAL){
            currentUserAux.datosProf.categorias = _usuarioProfesional.value.categorias
            currentUserAux.datosProf.descripcion = _usuarioProfesional.value.descripcion
        }
        return info != currentUserAux.toString()
    }
    fun setUpdateViewUserProfile (uidUsuario:String): Any? {
        currentUser.nombre = _nombreUser.value
        currentUser.apellido = _apellidoUser.value
        currentUser.ciudad = _ciudadUser.value
        currentUser.telefono = _telefonoUser.value
        if (currentUser.rol == Utilitity.ROL_PROFESIONAL){
            currentUser.datosProf.categorias = _usuarioProfesional.value.categorias
            currentUser.datosProf.descripcion = _usuarioProfesional.value.descripcion
        }
        return modelsFirebaseRepository.setUpdateUserProfile(uidUsuario,currentUser)
    }

    fun getViewCategorias (): Flow<List<CategoriasData>> {
        return modelsFirebaseRepository.getCategorias
    }

    fun getCredencialesViewUser (): Flow<CredencialesData?> {
        return modelsFirebaseRepository.getCredencialesUser(uidUserCurrent)
    }

    fun setNombreUser (nombre: String){
        _nombreUser.value = nombre
        _usuarioDatos.value.nombre = nombre
        _usuarioDatosOK.value = true
    }

    fun setApellidoUser (apellido: String){
        _apellidoUser.value = apellido
        _usuarioDatos.value.apellido = apellido
        _usuarioDatosOK.value = true
    }

    fun setCiudadUser (ciudad: String){
        _ciudadUser.value = ciudad
        _usuarioDatos.value.ciudad = ciudad
        _usuarioDatosOK.value = true
    }

    fun setTelefonoUser (telefono: String){
        _telefonoUser.value = telefono
        _usuarioDatos.value.telefono = telefono
        _usuarioDatosOK.value = true
    }

    fun setCategoriasUser (categorias: String){
        _categoriasUser.value = categorias
        _usuarioProfesional.value.categorias = categorias.split(",").map { it.trim() }
        _usuarioProfesionalOK.value = true
    }

    fun setDescripcionUser (descripcion: String){
        _descripcionUser.value = descripcion
        _usuarioProfesional.value.descripcion = descripcion
        _usuarioProfesionalOK.value = true
    }

    fun setCorreoUser (correo: String){
        _correoUser.value = correo
        _usuarioCredentials.value.correo = correo
        _usuarioCredencialesOK.value = true
    }

    fun setPasswordUser (password: String){
        _passwordUser.value = password
        _usuarioCredentials.value.password = password
        _usuarioCredencialesOK.value = true
    }


    val isFormUserSucess: Flow<Boolean> = combine(_usuarioDatosOK,_usuarioCredencialesOK,_usuarioProfesionalOK) { usuarioDatosOK, usuarioCredentialsOK, usuarioProfesional->
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

        val isDescripcionValid = if (currentUser.rol == Utilitity.ROL_PROFESIONAL) validatorDescripcion.nonEmpty().minLength(caracteres).check() else true
        val isCategoriasValid = if (currentUser.rol == Utilitity.ROL_PROFESIONAL) validatorCategorias.nonEmpty().check() and (validatorCategorias.text != "A") and (validatorCategorias.text != "N") and (validatorCategorias.text != "C") and (validatorCategorias.text != "Escoja su categoria") else true

        val isCorreoValid = validatorCorreo.nonEmpty().validEmail().check()
        val isPasswordValid = if(isFromSocNet) true else validatorPassword.nonEmpty().check()
        if(isNombreValid and isApellidoValid and isCiudadValid and isTelefonoValid and isCorreoValid and isPasswordValid and isDescripcionValid and isCategoriasValid){
            return@combine true
        }else{
            _usuarioDatosOK.value = false
            _usuarioCredencialesOK.value = false
            _usuarioProfesionalOK.value = false
            return@combine false
        }

    }

    val isNombreUserOk: Flow<Respuesta> = combine(_nombreUser) { nombre ->
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

    val isApellidoUserOk: Flow<Respuesta> = combine(_apellidoUser) { apellido ->
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

    val isCiudadUserOk: Flow<Respuesta> = combine(_ciudadUser) { ciudad ->
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

    val isTelefonoUserOk: Flow<Respuesta> = combine(_telefonoUser) { telefono ->
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
            respuesta.mensaje = "Complete el campo de Telefono"
        }

        if (respuesta.respuesta != 0){
            _usuarioDatosOK.value = false
            _usuarioCredencialesOK.value = false
            _usuarioProfesionalOK.value = false
        }

        return@combine respuesta
    }

    val isCategoriaUserOk: Flow<Respuesta> = combine(_categoriasUser) { categorias ->
        val respuesta = Respuesta()
        if(categorias[0].isEmpty()){
            respuesta.respuesta = 2
            respuesta.mensaje = "Escoja por lo menos una categoria"
        }else{
            when(categorias[0]){
                "A","C","N", "Escoja su categoria" -> {
                    respuesta.respuesta = 1
                    respuesta.mensaje = "Escoja por lo menos una categoria"
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

    val isDescripcionUserOk: Flow<Respuesta> = combine(_descripcionUser) { descripcion ->
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

    val isCorreoUserOK: Flow<Respuesta> = combine(_correoUser) { correo ->
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

    val isPasswordUserOK: Flow<Respuesta> = combine(_passwordUser) { password ->
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