package com.uisrael.worknow.Views

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.uisrael.worknow.Model.Data.CredencialesData
import com.uisrael.worknow.Model.Data.UsuariosData
import com.uisrael.worknow.Model.FirebaseAuthRepository
import com.uisrael.worknow.Model.FirebaseModelsRepository
import com.uisrael.worknow.R
import com.uisrael.worknow.ViewModel.ClientViewModel
import kotlinx.android.synthetic.main.client_fragment.*
import kotlinx.android.synthetic.main.login_fragment.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ClientRegisterFragment : Fragment() {

    var isNombreTypedCli : Boolean = false
    var isApellidoTypedCli : Boolean = false
    var isCiudadTypedCli : Boolean = false
    var isTelefonoTypedCli : Boolean = false
    var isCorreoTypedCli : Boolean = false
    var isPaswordTypedCli : Boolean = false

    companion object {
        fun newInstance() = ClientRegisterFragment()
    }

    private lateinit var viewModel: ClientViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.client_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ClientViewModel::class.java)
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
                            rltNombreCli.background = context?.let { ContextCompat.getDrawable(it,R.drawable.fieldsbackground) }
                        }
                        else -> {
                            errorNombreCli.isVisible = true
                            errorNombreCli.text = value.mensaje
                            rltNombreCli.background = context?.let { ContextCompat.getDrawable(it,R.drawable.fieldsbackgrounderror) }
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
                            rltApellidoCli.background = context?.let { ContextCompat.getDrawable(it,R.drawable.fieldsbackground) }
                        }
                        else -> {
                            errorApellidoCli.isVisible = true
                            errorApellidoCli.text = value.mensaje
                            rltApellidoCli.background = context?.let { ContextCompat.getDrawable(it,R.drawable.fieldsbackgrounderror) }
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
                            rltCiudadCli.background = context?.let { ContextCompat.getDrawable(it,R.drawable.fieldsbackground) }
                        }
                        else -> {
                            errorCiudadCli.isVisible = true
                            errorCiudadCli.text = value.mensaje
                            rltCiudadCli.background = context?.let { ContextCompat.getDrawable(it,R.drawable.fieldsbackgrounderror) }
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
                            rltTelefonoCli.background = context?.let { ContextCompat.getDrawable(it,R.drawable.fieldsbackground) }
                        }
                        else -> {
                            errorTelefonoCli.isVisible = true
                            errorTelefonoCli.text = value.mensaje
                            rltTelefonoCli.background = context?.let { ContextCompat.getDrawable(it,R.drawable.fieldsbackgrounderror) }
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
                            rltCorreoCli.background = context?.let { ContextCompat.getDrawable(it,R.drawable.fieldsbackground) }
                        }
                        else -> {
                            errorCorreoCli.isVisible = true
                            errorCorreoCli.text = value.mensaje
                            rltCorreoCli.background = context?.let { ContextCompat.getDrawable(it,R.drawable.fieldsbackgrounderror) }
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
                            rltPasswordCli.background = context?.let { ContextCompat.getDrawable(it,R.drawable.fieldsbackground) }
                        }
                        else -> {
                            errorPasswordCli.isVisible = true
                            errorPasswordCli.text = value.mensaje
                            rltPasswordCli.background = context?.let { ContextCompat.getDrawable(it,R.drawable.fieldsbackgrounderror) }
                        }
                    }
                }
            }
        }

    }

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

        btnRegisterCli.setOnClickListener{
            viewModel.viewModelScope.launch {
                val user = viewModel.registerViewCli()
                if (user != null) {
                    val datosProf = viewModel.registeViewCliDataUsuario(user.uid)
                    if(datosProf != null){
                        val credencialesProf = viewModel.registeViewCliCredenciales(user.uid)
                        if(credencialesProf != null){
                            Toast.makeText(activity, "Usuario ${user.uid} Registrado exitosamente.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(activity, "Error al crear el usuario", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}