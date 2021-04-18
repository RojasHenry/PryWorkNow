package com.uisrael.worknow.Views

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
import androidx.navigation.findNavController
import com.uisrael.worknow.R
import com.uisrael.worknow.ViewModel.LoginViewModel
import com.uisrael.worknow.ViewModel.ValidatorRespuestas.Respuesta
import kotlinx.android.synthetic.main.login_fragment.*
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    var isCorreoTyped : Boolean = false
    var isPaswordTyped : Boolean = false

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    @InternalCoroutinesApi
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        inicializarListeners()
        collectorFlow()
    }

    @InternalCoroutinesApi
    private fun collectorFlow() {
        lifecycleScope.launch {
            viewModel.isFormSucess.collect { value: Boolean ->
                btnLogin.isEnabled = value
            }
        }

        lifecycleScope.launch {
            viewModel.isCorreoOK.collect { value: Respuesta ->
                if (isCorreoTyped){
                    when (value.respuesta){
                        0 -> {
                            errorCorreo.isVisible = false
                            errorCorreo.text = value.mensaje
                            rltCorreo.background = context?.let { ContextCompat.getDrawable(it,R.drawable.fieldsbackground) }
                        }
                        1,2 -> {
                            errorCorreo.isVisible = true
                            errorCorreo.text = value.mensaje
                            rltCorreo.background = context?.let { ContextCompat.getDrawable(it,R.drawable.fieldsbackgrounderror) }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isPasswordOK.collect { value: Respuesta ->
                if (isPaswordTyped){
                    when (value.respuesta){
                        0 -> {
                            errorPassword.isVisible = false
                            errorPassword.text = value.mensaje
                            rltPassword.background = context?.let { ContextCompat.getDrawable(it,R.drawable.fieldsbackground) }
                        }
                        1 -> {
                            errorPassword.isVisible = true
                            errorPassword.text = value.mensaje
                            rltPassword.background = context?.let { ContextCompat.getDrawable(it,R.drawable.fieldsbackgrounderror) }
                        }
                    }
                }
            }
        }
    }

    private fun inicializarListeners(){
        correoTxt.addTextChangedListener {
            if(!isCorreoTyped){
                if (it.toString().isNotEmpty()){
                    isCorreoTyped = true
                    viewModel.setCorreo(it.toString().trim())
                }
            }else{
                viewModel.setCorreo(it.toString().trim())
            }
        }

        passwordTxt.addTextChangedListener {
            if(!isPaswordTyped){
                if (it.toString().isNotEmpty()){
                    isPaswordTyped = true
                    viewModel.setPassword(it.toString().trim())
                }
            }else{
                viewModel.setPassword(it.toString().trim())
            }
        }

        btnLogin.setOnClickListener {
            if (correoTxt.length() > 0 && passwordTxt.length() > 0) {
                viewModel.viewModelScope.launch {
                    val user = viewModel.registerViewUser(
                        correoTxt.text.toString(),
                        passwordTxt.text.toString()
                    )
                    Toast.makeText(activity, "Usuario actual $user", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
            }
        }

        linkRegistro.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

}

