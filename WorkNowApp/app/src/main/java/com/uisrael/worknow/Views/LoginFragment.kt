package com.uisrael.worknow.Views

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.uisrael.worknow.R
import com.uisrael.worknow.ViewModel.LoginViewModel
import com.uisrael.worknow.ViewModel.ValidatorRespuestas.Respuesta
import kotlinx.android.synthetic.main.login_fragment.*
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class LoginFragment : Fragment() {

    private val REQUEST_PERMISOS = 1

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
        validatePermissions ()
    }

    @InternalCoroutinesApi
    fun validatePermissions () {
        val permisoCamera = context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.CAMERA) }
        val permisoEscritura = context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.WRITE_EXTERNAL_STORAGE) }
        val permisoLectura = context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.READ_EXTERNAL_STORAGE) }
        if (permisoCamera == PackageManager.PERMISSION_GRANTED &&
                permisoEscritura == PackageManager.PERMISSION_GRANTED &&
                permisoLectura == PackageManager.PERMISSION_GRANTED ) {
            inicializarListeners()
            collectorFlow()
        } else {
            activity?.let {
                ActivityCompat.requestPermissions(
                        it,
                        arrayOf(Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISOS
                )
            }
        }
    }

    @InternalCoroutinesApi
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode){
            REQUEST_PERMISOS -> {
                val perms: MutableMap<String, Int> = HashMap()
                perms[Manifest.permission.CAMERA] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.READ_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED

                if (grantResults.isNotEmpty()) {
                    for (i in permissions.indices) perms[permissions[i]] = grantResults[i]
                    // Check for both permissions
                    if (perms[Manifest.permission.CAMERA] === PackageManager.PERMISSION_GRANTED
                            && perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] === PackageManager.PERMISSION_GRANTED
                            && perms[Manifest.permission.READ_EXTERNAL_STORAGE] === PackageManager.PERMISSION_GRANTED) {
                        inicializarListeners()
                        collectorFlow()
                    } else {
                        if (activity?.let { ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.CAMERA) } == true ||
                                activity?.let { ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.WRITE_EXTERNAL_STORAGE) } == true ||
                                activity?.let { ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.READ_EXTERNAL_STORAGE) } == true) {
                            showDialogOK("Esta aplicación requiere el uso de cámara y el almacenamiento del dispositivo.",
                                    DialogInterface.OnClickListener { dialog, which ->
                                        when (which) {
                                            DialogInterface.BUTTON_POSITIVE -> validatePermissions ()
                                            DialogInterface.BUTTON_NEGATIVE -> {
                                            }
                                        }
                                    })
                        } else {
                            Toast.makeText(context, "Habilite los permisos solicitados en configuraciones", Toast.LENGTH_LONG)
                                    .show()
                        }
                    }
                }
            }
        }
    }

    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show()
    }

    @InternalCoroutinesApi
    private fun collectorFlow() {

        lifecycleScope.launch {
            viewModel.validateViewUserLogged().observe(viewLifecycleOwner, {
                if (it) {
                    viewModel.getViewUserLogged().observe(viewLifecycleOwner, Observer {userFire ->
                        lifecycleScope.launch {
                            val uid = userFire.uid
                            viewModel.getCurrentUser(uid).collect {
                                if (it != null) {
                                    goToMenuPrincipal(it.rol, uid)
                                }
                            }
                        }
                    })
                }
            })
        }

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
                            rltCorreo.background = context?.let { ContextCompat.getDrawable(it, R.drawable.background_fields) }
                        }
                        else -> {
                            errorCorreo.isVisible = true
                            errorCorreo.text = value.mensaje
                            rltCorreo.background = context?.let { ContextCompat.getDrawable(it, R.drawable.background_fields_error) }
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
                            rltPassword.background = context?.let { ContextCompat.getDrawable(it, R.drawable.background_fields) }
                        }
                        else -> {
                            errorPassword.isVisible = true
                            errorPassword.text = value.mensaje
                            rltPassword.background = context?.let { ContextCompat.getDrawable(it, R.drawable.background_fields_error) }
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
                    val user = viewModel.loginViewUser(
                            correoTxt.text.toString(),
                            passwordTxt.text.toString()
                    )
                    if (user != null) {
                        viewModel.getCurrentUser(user.uid).collect {
                            if (it != null) {
                                goToMenuPrincipal(it.rol,user.uid)
                            }
                        }

                    }
                }
            } else {
                Toast.makeText(activity, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
            }
        }

        linkRegistro.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun goToMenuPrincipal(rol: String, uid: String){
        val intent = Intent(context, TabUsersActivity::class.java).apply {
            putExtra("rolUser", rol)
            putExtra("uid", uid)
        }
        startActivity(intent)
    }

}

