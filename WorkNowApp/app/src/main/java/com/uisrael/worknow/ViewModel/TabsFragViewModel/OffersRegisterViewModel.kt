package com.uisrael.worknow.ViewModel.TabsFragViewModel

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.uisrael.worknow.Model.Data.CategoriasData
import com.uisrael.worknow.Model.Data.FotosPublicacionData
import com.uisrael.worknow.Model.Data.PublicationsData
import com.uisrael.worknow.Model.FirebaseAuthRepository
import com.uisrael.worknow.Model.FirebaseModelsRepository
import com.uisrael.worknow.ViewModel.ValidatorRespuestas.Respuesta
import com.uisrael.worknow.Views.Utilities.Utilitity
import com.wajahatkarim3.easyvalidation.core.Validator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import java.text.SimpleDateFormat

class OffersRegisterViewModel : ViewModel() {
    private var modelsFirebaseRepository: FirebaseModelsRepository = FirebaseModelsRepository()
    private var authFirebaseRepository: FirebaseAuthRepository = FirebaseAuthRepository()

    private var _fechaIniOffer = MutableStateFlow("")
    private var _fechaFinOffer = MutableStateFlow("")
    private var _inmediatoOffer = MutableStateFlow(false)
    private var _descripcionOffer = MutableStateFlow("")
    private var _soloUnaPersonaOffer = MutableStateFlow(false)
    private var _cantidadOffer = MutableStateFlow("")
    private var _ubicacionOffer = MutableStateFlow("")
    private var _fotosOffer = MutableStateFlow("")
    private var _telefonoOffer = MutableStateFlow("")
    private var _idCategoriaOffer = MutableStateFlow("")

    private var _publicacionCli = MutableStateFlow(PublicationsData())
    private var _publicacionCliOK = MutableStateFlow(false)

    var fechaIniFin:Boolean = false
    var masPersonas:Boolean = false
    var detalleFoto:Boolean = false

    val caracteres = 20
    val numLengthTelefono = 7


    fun registerViewOffer(): Any? {
        val uidCliente = authFirebaseRepository.getUserUid()
        if (uidCliente != null) {
            _publicacionCli.value.idUsuarioCli = uidCliente
        }
        return modelsFirebaseRepository.registerOffer(_publicacionCli.value)
    }

    fun registerViewOfferPictures(uidOffer:String, ctx:Context): Any? {
        val listPictures = arrayListOf<String>()

        _fotosOffer.value.split(",").map {
            if(it.trim().isNotEmpty()){
                val picture64 = Utilitity().compressImage(it.trim())
                if (picture64 != null) {
                    listPictures.add(picture64)
                }
            }
        }

        val fotosPublicacionData = FotosPublicacionData()
        fotosPublicacionData.fotos = listPictures

        return modelsFirebaseRepository.registerPicturesOffer(fotosPublicacionData,uidOffer)
    }

    fun getViewCategorias (): Flow<List<CategoriasData>> {
        return modelsFirebaseRepository.getCategorias
    }

    fun setFechaIniOffer(fechaIni: String) {
        _fechaIniOffer.value = fechaIni
        _publicacionCli.value.fechaIni = fechaIni
        _publicacionCliOK.value = true
    }

    fun setFechasErrorOffer (isFechaIni: Boolean){
        if(isFechaIni){
            _publicacionCli.value.fechaIni = ""
        }else{
            _publicacionCli.value.fechaFin = ""
        }
        _publicacionCliOK.value = true
        _publicacionCliOK.value = false
    }

    fun setFechaFinOffer(fechaFin: String) {
        _fechaFinOffer.value = fechaFin
        _publicacionCli.value.fechaFin = fechaFin
        _publicacionCliOK.value = true
    }

    fun setInmediatoOffer(inmediato: Boolean) {
        _inmediatoOffer.value = inmediato
        _publicacionCli.value.inmediato = inmediato
        _publicacionCliOK.value = true
    }

    fun setDescripcionOffer(descripcion: String) {
        _descripcionOffer.value = descripcion
        _publicacionCli.value.descripcion = descripcion
        _publicacionCliOK.value = true
    }

    fun setSoloUnaPersonaOffer(soloUnaPersona: Boolean) {
        _soloUnaPersonaOffer.value = soloUnaPersona
        _publicacionCli.value.soloUnaPersona = soloUnaPersona
        _publicacionCliOK.value = true
    }

    fun setCantidadOffer(cantidad: String) {
        _cantidadOffer.value = cantidad
        _publicacionCli.value.cantidad = if(cantidad.isNotEmpty()) cantidad.toInt() else 0
        _publicacionCliOK.value = true
    }

    fun setUbicacionOffer(ubicacion: String) {
        _ubicacionOffer.value = ubicacion
        _publicacionCli.value.ubicacion = ubicacion
        _publicacionCliOK.value = true
    }

    fun setFotosOffer(fotos: String) {
        _fotosOffer.value = if(fotos.replace(",","").trim().isNotEmpty()) fotos else ""
        _publicacionCli.value.tieneFotos = fotos.replace(",","").trim().isNotEmpty()
        _publicacionCliOK.value = true
    }

    fun setTelefonoOffer(telefono: String) {
        _telefonoOffer.value = telefono
        _publicacionCli.value.telefono = telefono
        _publicacionCliOK.value = true
    }

    fun setCategoriaOffer(categoria: String) {
        _idCategoriaOffer.value = categoria
        _publicacionCli.value.idCategoria = categoria
        _publicacionCliOK.value = true
    }

    val isFormOfferSucess: Flow<Boolean> = combine(_publicacionCliOK){

        val validatorFechaIni = Validator(_publicacionCli.value.fechaIni)
        val validatorFechaFin = Validator(_publicacionCli.value.fechaFin)
        val validatorInmediato = Validator(_publicacionCli.value.inmediato.toString())
        val validatorDescripcion = Validator(_publicacionCli.value.descripcion)
        val validatorSoloUnaPersona = Validator(_publicacionCli.value.soloUnaPersona.toString())
        val validatorCantidad = Validator(_publicacionCli.value.cantidad.toString())
        val validatorFotosResp = Validator(_fotosOffer.value)
        val validatorUbicacion = Validator(_publicacionCli.value.ubicacion)
        val validatorCategoria = Validator(_publicacionCli.value.idCategoria)

        val isFechaIniValid = validatorFechaIni.nonEmpty().check()
        val isFechaFinValid = if (fechaIniFin) validatorFechaFin.nonEmpty().check() else true
        val isInmediatoValid = validatorInmediato.nonEmpty().check()
        val isDescripcionValid = validatorDescripcion.nonEmpty().minLength(caracteres).check()
        val isSoloUnaPersonaValid = validatorSoloUnaPersona.nonEmpty().check()
        val isCantidadValid = if (masPersonas) validatorCantidad.nonEmpty().check() else true
        val isFotosRespValid = if (detalleFoto) validatorFotosResp.nonEmpty().check() else true
        val isUbicacionValid = validatorUbicacion.nonEmpty().check()
        val isCategoriaValid = validatorCategoria.nonEmpty().check()
        var isTelefonoValid = true
        if(validatorCategoria.nonEmpty().check())
            isTelefonoValid = validatorCategoria.nonEmpty().minLength(numLengthTelefono).check()

        if(isFechaIniValid and isFechaFinValid and isInmediatoValid and isDescripcionValid and
                isSoloUnaPersonaValid and isCantidadValid and isUbicacionValid and isFotosRespValid and isCategoriaValid and isTelefonoValid){
            return@combine true
        }else{
            _publicacionCliOK.value = false
            return@combine false
        }
    }

    val isFechaInOfferOK:Flow<Respuesta> = combine(_fechaIniOffer){ fechaIni ->
        val respuesta = Respuesta()
        val dfDate = SimpleDateFormat("yyyy-MM-dd")
        val validatorFechaFin = Validator(_fechaFinOffer.value)
        if(fechaIniFin){
            if(validatorFechaFin.nonEmpty().check()){
                if(dfDate.parse(fechaIni[0]).after(dfDate.parse(_fechaFinOffer.value)))
                {
                    respuesta.respuesta = 1
                    respuesta.mensaje = "Fecha Inicial mayor a Fecha Fin"
                }
            }
        }

        _publicacionCliOK.value = false

        return@combine respuesta
    }

    val isFechaFinOfferOK:Flow<Respuesta> = combine(_fechaFinOffer) { fechaFin ->
        val respuesta = Respuesta()
        val dfDate = SimpleDateFormat("yyyy-MM-dd")
        val validatorFechaIni = Validator(_fechaIniOffer.value)
        if(fechaIniFin){
            if(validatorFechaIni.nonEmpty().check()){
                if(dfDate.parse(fechaFin[0]).before(dfDate.parse(_fechaIniOffer.value)))
                {
                    respuesta.respuesta = 1
                    respuesta.mensaje = "Fecha Fin menor a Fecha Inicial"
                }
            }
        }

        _publicacionCliOK.value = false

        return@combine respuesta
    }

    val isDescripcionOfferOK:Flow<Respuesta> = combine(_descripcionOffer) {
        val respuesta = Respuesta()
        val validatorDescripcion = Validator(it[0])
        if(validatorDescripcion.nonEmpty().check()){
            if(!validatorDescripcion.minLength(caracteres).check()){
                respuesta.respuesta = 2
                respuesta.mensaje = "Ingrese por lo menos $caracteres caracteres de descripción"
            }
        }else{
            respuesta.respuesta = 1
            respuesta.mensaje = "Complete el campo de descripción de la solicitud"
        }

        _publicacionCliOK.value = false

        return@combine respuesta
    }

    val isCantidadOfferOK:Flow<Respuesta> = combine(_cantidadOffer) {
        val limite = 50
        val respuesta = Respuesta()
        val validatorCantidad = Validator(it[0])

        if(masPersonas){
            if(validatorCantidad.nonEmpty().check()){
                val cantidad = it[0].toInt()
                if(cantidad > limite){
                    respuesta.respuesta = 3
                    respuesta.mensaje = "No se puede ingresar más de $limite"
                }else if(cantidad <= 0){
                    respuesta.respuesta = 2
                    respuesta.mensaje = "Ingrese un número mayor a $cantidad y menor a $limite"
                }
            } else{
                respuesta.respuesta = 1
                respuesta.mensaje = "Complete el campo de cantidad"
            }
        }

        _publicacionCliOK.value = false

        return@combine respuesta
    }

    val isUbicacionOfferOK:Flow<Respuesta> = combine(_ubicacionOffer) {
        val respuesta = Respuesta()
        val validatorUbicacion = Validator(it[0])

        if(!validatorUbicacion.nonEmpty().check()){
            respuesta.respuesta = 1
            respuesta.mensaje = "Complete el campo de ubicación"
        }

        _publicacionCliOK.value = false

        return@combine respuesta
    }

    val isFotosOfferOK:Flow<Respuesta> = combine(_fotosOffer) {
        val respuesta = Respuesta()

        if(detalleFoto){
            if(it[0].isEmpty()){
                respuesta.respuesta = 1
                respuesta.mensaje = "Realice el adjunto de fotografías (máximo 5)"
            }
        }
        _publicacionCliOK.value = false

        return@combine respuesta
    }

    val isTelefonoOfferOK:Flow<Respuesta> = combine(_telefonoOffer) {
        val respuesta = Respuesta()
        val validatorTelefono = Validator(it[0])

        if(validatorTelefono.nonEmpty().check()){
            if(validatorTelefono.validNumber().check() and validatorTelefono.minLength(numLengthTelefono).check()){
                if(!Patterns.PHONE.matcher(validatorTelefono.text).matches()){
                    respuesta.respuesta = 2
                    respuesta.mensaje = "Teléfono inválido"
                }
            }else{
                respuesta.respuesta = 1
                respuesta.mensaje = "Teléfono incorrecto"
            }
        }

        _publicacionCliOK.value = false

        return@combine respuesta
    }

    val isCatetoriasOfferOK:Flow<Respuesta> = combine(_idCategoriaOffer) {
        val respuesta = Respuesta()

        if(it[0].isEmpty()){
            respuesta.respuesta = 1
            respuesta.mensaje = "Escoja una categoria para su solicitud"
        }

        _publicacionCliOK.value = false

        return@combine respuesta
    }

    fun clearFormViewModel(uidCategoria: String){

        fechaIniFin = false
        masPersonas = false
        detalleFoto = false

        _publicacionCli.value = PublicationsData()

        _publicacionCliOK.value = true

        setFechaFinOffer("")
        setFechaFinOffer("")
        setInmediatoOffer(false)
        setDescripcionOffer("")
        setSoloUnaPersonaOffer(false)
        setCantidadOffer("")
        setUbicacionOffer("")
        setFotosOffer("")
        setTelefonoOffer("")
        setCategoriaOffer(uidCategoria)

        _publicacionCliOK.value = false

    }



}