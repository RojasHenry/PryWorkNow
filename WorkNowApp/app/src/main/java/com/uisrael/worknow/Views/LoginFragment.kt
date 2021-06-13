package com.uisrael.worknow.Views

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.uisrael.worknow.R
import com.uisrael.worknow.ViewModel.LoginViewModel
import com.uisrael.worknow.ViewModel.ValidatorRespuestas.Respuesta
import com.uisrael.worknow.Views.Dialogs.RegisterCompleteFragment
import com.uisrael.worknow.Views.Utilities.Utilitity
import kotlinx.android.synthetic.main.login_fragment.*
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class LoginFragment : Fragment() {

    private val RC_SIGN_IN = 2
    private val REQUEST_PERMISOS = 1

    var isCorreoTyped : Boolean = false
    var isPaswordTyped : Boolean = false
    private lateinit var googleSignInClient:GoogleSignInClient

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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        validatePermissions()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = activity?.let { it1 -> GoogleSignIn.getClient(it1, gso) }!!
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

    @SuppressLint("ShowToast")
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
                            showDialogOK("Esta aplicación requiere el uso de cámara y el almacenamiento del dispositivo.") { _, which ->
                                when (which) {
                                    DialogInterface.BUTTON_POSITIVE -> validatePermissions()
                                    DialogInterface.BUTTON_NEGATIVE -> {
                                    }
                                }
                            }
                        } else {
                            Snackbar
                                .make(rltContenedorViewLogin, "Habilite los permisos solicitados en configuraciones.", Snackbar.LENGTH_INDEFINITE)
                                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                .setBackgroundTint(resources.getColor(R.color.black))
                                .setActionTextColor(resources.getColor(R.color.purple_500))
                                .setAction("OK"){}
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
                val internetConnection: Boolean? = activity?.let { it1 -> Utilitity.isNetworkAvailable(it1) }
                if (internetConnection == true){
                    if (it) {
                        viewModel.getViewUserLogged().observe(viewLifecycleOwner, { userFire ->
                            lifecycleScope.launch {
                                val uid = userFire.uid
                                viewModel.getCurrentUser(uid).collect { user->
                                    if (user != null) {
                                        viewModel.getViewCredentialUser(uid).collect { cred ->
                                            if(cred?.estado == true){
                                                context?.let { it1 -> Utilitity.showLoading(it1,"Iniciando sesión, por favor espere...",childFragmentManager) }
                                                goToMenuPrincipal(user.rol, uid)
                                            }
                                        }
                                    }/*else{
                                        Utilitity.dissMissLoading(0)
                                        val dialog = RegisterCompleteFragment(userFire)
                                        dialog.show(childFragmentManager,"registerComplete")
                                    }*/
                                }
                            }
                        })
                    }
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

    @SuppressLint("ShowToast")
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
            val internetConnection: Boolean? = activity?.let { it1 -> Utilitity.isNetworkAvailable(it1) }
            if (internetConnection == true){
                if (correoTxt.length() > 0 && passwordTxt.length() > 0) {
                    context?.let { it1 -> Utilitity.showLoading(it1,"Iniciando sesión, por favor espere...",childFragmentManager) }
                    viewModel.viewModelScope.launch {
                        val user = viewModel.loginViewUser(
                            correoTxt.text.toString(),
                            passwordTxt.text.toString()
                        )
                        if (user != null) {
                            viewModel.getViewCredentialUser(user.uid).collect { cred ->
                                if(cred?.estado == true){
                                    viewModel.getCurrentUser(user.uid).collect {
                                        if (it != null) {
                                            goToMenuPrincipal(it.rol,user.uid)
                                        }else{
                                            Snackbar
                                                .make(rltContenedorViewLogin, "Error al iniciar sesión.", Snackbar.LENGTH_SHORT)
                                                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                                .setBackgroundTint(resources.getColor(R.color.black))
                                                .show()
                                            Utilitity.dissMissLoading(0)
                                        }
                                    }
                                }else{
                                    Snackbar
                                        .make(rltContenedorViewLogin, "Su usuario se encuentra bloqueado.", Snackbar.LENGTH_SHORT)
                                        .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                        .setBackgroundTint(resources.getColor(R.color.black))
                                        .show()
                                    Utilitity.dissMissLoading(0)
                                }
                            }

                        }else{
                            Snackbar
                                .make(rltContenedorViewLogin, "Error al iniciar sesión.", Snackbar.LENGTH_SHORT)
                                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                .setBackgroundTint(resources.getColor(R.color.black))
                                .show()
                            Utilitity.dissMissLoading(0)
                        }
                    }
                } else {
                    Snackbar
                        .make(rltContenedorViewLogin, "Error al iniciar sesión.", Snackbar.LENGTH_SHORT)
                        .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                        .setBackgroundTint(resources.getColor(R.color.black))
                        .show()
                }
            }else{
                Snackbar
                    .make(rltContenedorViewLogin, "Sin conexión a internet, revise los ajustes de conexión para continuar.", Snackbar.LENGTH_SHORT)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                    .setAnchorView(view)
                    .setBackgroundTint(resources.getColor(R.color.purple_700))
                    .show()
            }
        }

        linkRegistro.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_loginFragment_to_registerFragment)
        }

        btnLoginGoogle.setOnClickListener {
            val internetConnection: Boolean? = activity?.let { it1 -> Utilitity.isNetworkAvailable(it1) }
            if (internetConnection == true){
                context?.let { it1 -> Utilitity.showLoading(it1,"Iniciando sesión, por favor espere...",childFragmentManager) }
                val signInIntent: Intent? = googleSignInClient?.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }else{
                Snackbar
                    .make(rltContenedorViewLogin, "Sin conexión a internet, revise los ajustes de conexión para continuar.", Snackbar.LENGTH_SHORT)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                    .setAnchorView(view)
                    .setBackgroundTint(resources.getColor(R.color.purple_700))
                    .show()
            }
        }
    }

    private fun goToMenuPrincipal(rol: String, uid: String){
        val intent = Intent(context, TabUsersActivity::class.java).apply {
            putExtra("rolUser", rol)
            putExtra("uid", uid)
        }
        startActivity(intent)
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        Utilitity.dissMissLoading(0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                viewModel.viewModelScope.launch {
                    viewModel.loginViewWithGoogle(account.idToken!!).collect { user ->
                        if (user != null) {
                            viewModel.getCurrentUser(user.uid).collect {
                                if (it != null){
                                    viewModel.getViewCredentialUser(user.uid).collect { cred ->
                                        if (cred?.estado == true) {
                                            goToMenuPrincipal(it.rol, user.uid)
                                        }else{
                                            Utilitity.dissMissLoading(0)
                                            Snackbar
                                                .make(rltContenedorViewLogin, "Su usuario se encuentra bloqueado.", Snackbar.LENGTH_SHORT)
                                                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                                .setBackgroundTint(resources.getColor(R.color.black))
                                                .show()
                                        }
                                    }
                                }else{
                                    val dialog = RegisterCompleteFragment(user)
                                    dialog.show(childFragmentManager,"registerComplete")
                                }
                            }
                        }
                    }
                }
            } catch (e: ApiException) {
                Utilitity.dissMissLoading(0)
                Snackbar
                    .make(rltContenedorViewLogin, "Error al iniciar sesión con Google.", Snackbar.LENGTH_SHORT)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                    .setBackgroundTint(resources.getColor(R.color.black))
                    .show()
            }

        }

    }
}

