package com.uisrael.worknow.Views

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessaging
import com.uisrael.worknow.R
import com.uisrael.worknow.ViewModel.ClientViewModel
import kotlinx.android.synthetic.main.client_fragment.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class ClientRegisterFragment(private val userGoogle: FirebaseUser?) : Fragment() {

    var isNombreTypedCli : Boolean = false
    var isApellidoTypedCli : Boolean = false
    var isCiudadTypedCli : Boolean = false
    var isTelefonoTypedCli : Boolean = false
    var isCorreoTypedCli : Boolean = false
    var isPaswordTypedCli : Boolean = false

    companion object {
        fun newInstance(user: FirebaseUser?) = ClientRegisterFragment(user)
    }

    private lateinit var viewModel: ClientViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.client_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ClientViewModel::class.java)
        if (userGoogle != null) {
            viewModel.userGoogleView = userGoogle
        }
        inicializarListeners()
        collectorFlow()
    }

    private fun collectorFlow() {
        lifecycleScope.launch {
            viewModel.isFormCliSucess.collect { value ->
                btnRegisterCli.isEnabled = value
            }
        }

        lifecycleScope.launch {
            viewModel.isNombreCliOk.collect { value ->
                if (isNombreTypedCli) {
                    when (value.respuesta) {
                        0 -> {
                            errorNombreCli.isVisible = false
                            errorNombreCli.text = value.mensaje
                            rltNombreCli.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields) }
                        }
                        else -> {
                            errorNombreCli.isVisible = true
                            errorNombreCli.text = value.mensaje
                            rltNombreCli.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields_error) }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isApellidoCliOk.collect { value ->
                if (isApellidoTypedCli) {
                    when (value.respuesta) {
                        0 -> {
                            errorApellidoCli.isVisible = false
                            errorApellidoCli.text = value.mensaje
                            rltApellidoCli.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields) }
                        }
                        else -> {
                            errorApellidoCli.isVisible = true
                            errorApellidoCli.text = value.mensaje
                            rltApellidoCli.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields_error) }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isCiudadCliOk.collect { value ->
                if (isCiudadTypedCli) {
                    when (value.respuesta) {
                        0 -> {
                            errorCiudadCli.isVisible = false
                            errorCiudadCli.text = value.mensaje
                            rltCiudadCli.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields) }
                        }
                        else -> {
                            errorCiudadCli.isVisible = true
                            errorCiudadCli.text = value.mensaje
                            rltCiudadCli.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields_error) }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isTelefonoCliOk.collect { value ->
                if (isTelefonoTypedCli) {
                    when (value.respuesta) {
                        0 -> {
                            errorTelefonoCli.isVisible = false
                            errorTelefonoCli.text = value.mensaje
                            rltTelefonoCli.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields) }
                        }
                        else -> {
                            errorTelefonoCli.isVisible = true
                            errorTelefonoCli.text = value.mensaje
                            rltTelefonoCli.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields_error) }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isCorreoCliOK.collect { value ->
                if (isCorreoTypedCli) {
                    when (value.respuesta) {
                        0 -> {
                            errorCorreoCli.isVisible = false
                            errorCorreoCli.text = value.mensaje
                            rltCorreoCli.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields) }
                        }
                        else -> {
                            errorCorreoCli.isVisible = true
                            errorCorreoCli.text = value.mensaje
                            rltCorreoCli.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields_error) }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isPasswordCliOK.collect { value ->
                if (isPaswordTypedCli){
                    when (value.respuesta){
                        0 -> {
                            errorPasswordCli.isVisible = false
                            errorPasswordCli.text = value.mensaje
                            rltPasswordCli.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields) }
                        }
                        else -> {
                            errorPasswordCli.isVisible = true
                            errorPasswordCli.text = value.mensaje
                            rltPasswordCli.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields_error) }
                        }
                    }
                }
            }
        }

    }

    @SuppressLint("ShowToast")
    private fun inicializarListeners() {
        nombreTxtCli.addTextChangedListener{
            if (!isNombreTypedCli){
                if (it.toString().isNotEmpty()){
                    isNombreTypedCli = true
                    viewModel.viewModelScope.launch {
                        viewModel.setNombreCli(it.toString().trim())
                    }
                }
            } else {
                viewModel.viewModelScope.launch {
                    viewModel.setNombreCli(it.toString().trim())
                }
            }
        }

        apellidoTxtCli.addTextChangedListener{
            if (!isApellidoTypedCli){
                if (it.toString().isNotEmpty()){
                    isApellidoTypedCli = true
                    viewModel.viewModelScope.launch {
                        viewModel.setApellidoCli(it.toString().trim())
                    }
                }
            } else {
                    viewModel.viewModelScope.launch {
                        viewModel.setApellidoCli(it.toString().trim())
                    }
            }
        }

        ciudadTxtCli.addTextChangedListener{
            if (!isCiudadTypedCli){
                if (it.toString().isNotEmpty()){
                    isCiudadTypedCli = true
                    viewModel.viewModelScope.launch {
                        viewModel.setCiudadCli(it.toString().trim())
                    }
                }
            } else {
                viewModel.viewModelScope.launch {
                    viewModel.setCiudadCli(it.toString().trim())
                }
            }
        }

        telefonoTxtCli.addTextChangedListener{
            if (!isTelefonoTypedCli){
                if (it.toString().isNotEmpty()){
                    isTelefonoTypedCli = true
                    viewModel.viewModelScope.launch {
                        viewModel.setTelefonoCli(it.toString().trim())
                    }
                }
            } else {
                viewModel.viewModelScope.launch {
                    viewModel.setTelefonoCli(it.toString().trim())
                }
            }
        }

        correoTxtCli.addTextChangedListener{
            if (!isCorreoTypedCli){
                if (it.toString().isNotEmpty()){
                    isCorreoTypedCli = true
                    viewModel.viewModelScope.launch { viewModel.setCorreoCli(it.toString().trim()) }
                }
            } else {
                viewModel.viewModelScope.launch { viewModel.setCorreoCli(it.toString().trim()) }
            }

        }

        passwordTxtCli.addTextChangedListener{
            if (!isPaswordTypedCli){
                if (it.toString().isNotEmpty()){
                    isPaswordTypedCli = true
                    viewModel.viewModelScope.launch { viewModel.setPasswordCli(it.toString().trim()) }
                }
            } else {
                viewModel.viewModelScope.launch { viewModel.setPasswordCli(it.toString().trim()) }
            }
        }

        /*rltCiudadCli.setOnClickListener {
            context?.let { it1 -> MapCityFragment(it1) }
                ?.show(childFragmentManager, "mapcityfragment")
        }*/

        btnRegisterCli.setOnClickListener{
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if(!it.isSuccessful){
                    Snackbar
                        .make(rltContentDatosPersonalesCli, "Dispositivo no compatible con notificaciones.", Snackbar.LENGTH_SHORT)
                        .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                        .setBackgroundTint(resources.getColor(R.color.black))
                        .show()
                    return@addOnCompleteListener
                }

                // Get new FCM registration token
                val token = it.result
                registerCli(userGoogle, token)
            }

        }

        if(userGoogle != null){
            val nameSplit = userGoogle.displayName.split(" ")
            if (nameSplit.size >=2) {
                nombreTxtCli.text = Editable.Factory.getInstance().newEditable(nameSplit[0])
                apellidoTxtCli.text = Editable.Factory.getInstance().newEditable(nameSplit[1])
            }else{
                nombreTxtCli.text = Editable.Factory.getInstance().newEditable(userGoogle.displayName)
            }
            correoTxtCli.text = Editable.Factory.getInstance().newEditable(userGoogle.email)
            correoTxtCli.isEnabled = false
            rltPasswordCli.isVisible = false

            viewModel.setFotoCli(userGoogle.photoUrl.toString())

        }
    }

    @SuppressLint("ShowToast")
    private fun registerCli(userGoogle: FirebaseUser?, tokenFire: String) {
        viewModel.viewModelScope.launch {
            if (userGoogle != null) {
                val datosProf = viewModel.registerViewCliDataUsuario(userGoogle.uid)
                if (datosProf != null) {
                    val credencialesProf =
                        viewModel.registerViewCliCredenciales(userGoogle.uid, true)
                    if (credencialesProf != null) {
                        val tokenCli = viewModel.registerViewCliToken(userGoogle.uid, tokenFire)
                        if(tokenCli != null){
                            Snackbar
                                .make(
                                    rltContentDatosPersonalesCli,
                                    "Usuario ${userGoogle.displayName} registrado exitosamente.",
                                    Snackbar.LENGTH_INDEFINITE
                                )
                                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                .setBackgroundTint(resources.getColor(R.color.black))
                                .setActionTextColor(resources.getColor(R.color.purple_500))
                                .setAction("OK") {}
                                .show()
                        } else {
                            Snackbar
                                .make(
                                    rltContentDatosPersonalesCli,
                                    "Error al crear el usuario.",
                                    Snackbar.LENGTH_SHORT
                                )
                                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                .setBackgroundTint(resources.getColor(R.color.black))
                                .show()
                        }
                    } else {
                        Snackbar
                            .make(
                                rltContentDatosPersonalesCli,
                                "Error al crear el usuario.",
                                Snackbar.LENGTH_SHORT
                            )
                            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                            .setBackgroundTint(resources.getColor(R.color.black))
                            .show()
                    }
                } else {
                    Snackbar
                        .make(
                            rltContentDatosPersonalesCli,
                            "Error al crear el usuario.",
                            Snackbar.LENGTH_SHORT
                        )
                        .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                        .setBackgroundTint(resources.getColor(R.color.black))
                        .show()
                }
            } else {
                val user = viewModel.registerViewCli()
                if (user != null) {
                    val datosProf = viewModel.registerViewCliDataUsuario(user.uid)
                    if (datosProf != null) {
                        val credencialesProf = viewModel.registerViewCliCredenciales(
                            user.uid,
                            false
                        )
                        if (credencialesProf != null) {
                            val tokenCli = viewModel.registerViewCliToken(user.uid, tokenFire)
                            if(tokenCli != null){
                                Snackbar
                                    .make(
                                        rltContentDatosPersonalesCli,
                                        "Usuario ${user.uid} registrado exitosamente.",
                                        Snackbar.LENGTH_INDEFINITE
                                    )
                                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                    .setBackgroundTint(resources.getColor(R.color.black))
                                    .setActionTextColor(resources.getColor(R.color.purple_500))
                                    .setAction("OK") {}
                                    .show()
                            }else{
                                Snackbar
                                    .make(
                                        rltContentDatosPersonalesCli,
                                        "Error al crear el usuario.",
                                        Snackbar.LENGTH_SHORT
                                    )
                                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                    .setBackgroundTint(resources.getColor(R.color.black))
                                    .show()
                            }
                        } else {
                            Snackbar
                                .make(
                                    rltContentDatosPersonalesCli,
                                    "Error al crear el usuario.",
                                    Snackbar.LENGTH_SHORT
                                )
                                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                .setBackgroundTint(resources.getColor(R.color.black))
                                .show()
                        }
                    } else {
                        Snackbar
                            .make(
                                rltContentDatosPersonalesCli,
                                "Error al crear el usuario.",
                                Snackbar.LENGTH_SHORT
                            )
                            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                            .setBackgroundTint(resources.getColor(R.color.black))
                            .show()
                    }
                } else {
                    Snackbar
                        .make(
                            rltContentDatosPersonalesCli,
                            "Error al crear el usuario.",
                            Snackbar.LENGTH_SHORT
                        )
                        .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                        .setBackgroundTint(resources.getColor(R.color.black))
                        .show()
                }
            }
        }
    }

}