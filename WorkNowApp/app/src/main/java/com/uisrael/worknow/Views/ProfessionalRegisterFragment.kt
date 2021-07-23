package com.uisrael.worknow.Views

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessaging
import com.uisrael.worknow.Model.Data.CategoriasData
import com.uisrael.worknow.R
import com.uisrael.worknow.ViewModel.ProfessionalViewModel
import com.uisrael.worknow.Views.Dialogs.IResponseMapFragment
import com.uisrael.worknow.Views.Dialogs.MapCityFragment
import com.uisrael.worknow.Views.Dialogs.RegisterCompleteFragment
import com.uisrael.worknow.Views.Utilities.Utilitity
import kotlinx.android.synthetic.main.professional_fragment.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

class ProfessionalRegisterFragment(private val userGoogle: FirebaseUser?,
                                   val completeFragment: RegisterCompleteFragment?,
                                   val registerFragment: RegisterFragment?) : Fragment(), IResponseMapFragment {

    private var isNombreTypedProf : Boolean = false
    private var isApellidoTypedProf : Boolean = false
    private var isCiudadTypedProf : Boolean = false
    private var isTelefonoTypedProf : Boolean = false
    private var isCategoriaTypedProf : Boolean = false
    private var isDescripcionTypedProf : Boolean = false
    private var isCorreoTypedProf : Boolean = false
    private var isPaswordTypedProf : Boolean = false

    private var categoriasViewRepository: MutableList<CategoriasData> = ArrayList()
    private var categoriasRepository: Array<String> = emptyArray()
    private lateinit var selectecCategoria: BooleanArray
    private var categoriasList = arrayListOf<Int>()

    companion object {
        fun newInstance(user: FirebaseUser?,
                        completeFragment: RegisterCompleteFragment?,
                        registerFragment: RegisterFragment?) = ProfessionalRegisterFragment(user, completeFragment, registerFragment)
    }

    private lateinit var viewModel: ProfessionalViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.professional_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfessionalViewModel::class.java)
        if (userGoogle != null) {
            viewModel.userGoogleView = userGoogle
        }
        inicializarListeners()
        collectorFlow()
    }

    private fun collectorFlow() {
        lifecycleScope.launch {
            viewModel.isFormProfSucess.collect { value ->
                btnRegisterProf.isEnabled = value
            }
        }

        lifecycleScope.launch {
            viewModel.isNombreProfOk.collect { value ->
                if (isNombreTypedProf) {
                    when (value.respuesta) {
                        0 -> {
                            errorNombreProf.isVisible = false
                            errorNombreProf.text = value.mensaje
                            rltNombreProf.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields) }
                        }
                        else -> {
                            errorNombreProf.isVisible = true
                            errorNombreProf.text = value.mensaje
                            rltNombreProf.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields_error) }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isApellidoProfOk.collect { value ->
                if (isApellidoTypedProf) {
                    when (value.respuesta) {
                        0 -> {
                            errorApellidoProf.isVisible = false
                            errorApellidoProf.text = value.mensaje
                            rltApellidoProf.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields) }
                        }
                        else -> {
                            errorApellidoProf.isVisible = true
                            errorApellidoProf.text = value.mensaje
                            rltApellidoProf.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields_error) }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isCiudadProfOk.collect { value ->
                if (isCiudadTypedProf) {
                    when (value.respuesta) {
                        0 -> {
                            errorCiudadProf.isVisible = false
                            errorCiudadProf.text = value.mensaje
                            rltCiudadProf.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields) }
                        }
                        else -> {
                            errorCiudadProf.isVisible = true
                            errorCiudadProf.text = value.mensaje
                            rltCiudadProf.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields_error) }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isTelefonoProfOk.collect { value ->
                if (isTelefonoTypedProf) {
                    when (value.respuesta) {
                        0 -> {
                            errorTelefonoProf.isVisible = false
                            errorTelefonoProf.text = value.mensaje
                            rltTelefonoProf.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields) }
                        }
                        else -> {
                            errorTelefonoProf.isVisible = true
                            errorTelefonoProf.text = value.mensaje
                            rltTelefonoProf.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields_error) }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isCategoriaProfOk.collect { value ->
                if (isCategoriaTypedProf){
                    when (value.respuesta) {
                        0 -> {
                            errorCategoriaProf.isVisible = false
                            errorCategoriaProf.text = value.mensaje
                            rltCategoriasProf.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields) }
                        }
                        else -> {
                            errorCategoriaProf.isVisible = true
                            errorCategoriaProf.text = value.mensaje
                            rltCategoriasProf.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields_error) }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isDescripcionProfOk.collect { value ->
                if (isDescripcionTypedProf){
                    when (value.respuesta) {
                        0 -> {
                            errorDescripcionProf.isVisible = false
                            errorDescripcionProf.text = value.mensaje
                            rltDescripcionProf.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields) }
                        }
                        else -> {
                            errorDescripcionProf.isVisible = true
                            errorDescripcionProf.text = value.mensaje
                            rltDescripcionProf.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields_error) }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isCorreoProfOK.collect { value ->
                if (isCorreoTypedProf) {
                    when (value.respuesta) {
                        0 -> {
                            errorCorreoProf.isVisible = false
                            errorCorreoProf.text = value.mensaje
                            rltCorreoProf.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields) }
                        }
                        else -> {
                            errorCorreoProf.isVisible = true
                            errorCorreoProf.text = value.mensaje
                            rltCorreoProf.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields_error) }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isPasswordProfOK.collect { value ->
                if (isPaswordTypedProf){
                    when (value.respuesta){
                        0 -> {
                            errorPasswordProf.isVisible = false
                            errorPasswordProf.text = value.mensaje
                            rltPasswordProf.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields) }
                        }
                        else -> {
                            errorPasswordProf.isVisible = true
                            errorPasswordProf.text = value.mensaje
                            rltPasswordProf.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields_error) }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.getViewCategorias().collect{ value ->
                categoriasViewRepository = value as MutableList<CategoriasData>
                val categoriasAux = arrayListOf<String>()

                value.forEachIndexed { _ , b ->
                    if(b.estado)
                        categoriasAux.add(b.nombre)
                }

                categoriasRepository = categoriasAux.toTypedArray()
                selectecCategoria =  BooleanArray(categoriasRepository.size)
            }
            // cancelar el realtime
            this.cancel()
        }
    }

    @SuppressLint("ShowToast")
    private fun inicializarListeners() {
        nombreTxtProf.addTextChangedListener{
            if (!isNombreTypedProf){
                if (it.toString().isNotEmpty()){
                    isNombreTypedProf = true
                    viewModel.viewModelScope.launch {
                        viewModel.setNombreProf(it.toString().trim())
                    }
                }
            } else {
                viewModel.viewModelScope.launch {
                    viewModel.setNombreProf(it.toString().trim())
                }
            }
        }

        apellidoTxtProf.addTextChangedListener{
            if (!isApellidoTypedProf){
                if (it.toString().isNotEmpty()){
                    isApellidoTypedProf = true
                    viewModel.viewModelScope.launch {
                        viewModel.setApellidoProf(it.toString().trim())
                    }
                }
            } else {
                viewModel.viewModelScope.launch {
                    viewModel.setApellidoProf(it.toString().trim())
                }
            }
        }

        ciudadTxtProf.addTextChangedListener{
            if (!isCiudadTypedProf){
                if (it.toString().isNotEmpty()){
                    isCiudadTypedProf = true
                    viewModel.viewModelScope.launch {
                        viewModel.setCiudadProf(it.toString().trim())
                    }
                }
            } else {
                viewModel.viewModelScope.launch {
                    viewModel.setCiudadProf(it.toString().trim())
                }
            }
        }

        telefonoTxtProf.addTextChangedListener{
            if (!isTelefonoTypedProf){
                if (it.toString().isNotEmpty()){
                    isTelefonoTypedProf = true
                    viewModel.viewModelScope.launch {
                        viewModel.setTelefonoProf(it.toString().trim())
                    }
                }
            } else {
                viewModel.viewModelScope.launch {
                    viewModel.setTelefonoProf(it.toString().trim())
                }
            }
        }


        spinnerCategoriasProf.setOnClickListener{
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Escoja las categorias a las que desea aplicar")
            builder.setCancelable(false)

            builder.setMultiChoiceItems(categoriasRepository,selectecCategoria) { _: DialogInterface, i: Int, b: Boolean ->
                if (b) {
                    categoriasList.add(i)
                    categoriasList.sort()
                } else {
                    categoriasList.remove(i)
                }
            }

            builder.setPositiveButton("Aceptar") { _, _ ->
                val categoriasSelected = arrayListOf<String>()
                val categoriasUis = arrayListOf<String>()

                categoriasList.map { i ->
                    categoriasSelected.add(categoriasRepository[i])
                    categoriasUis.add(categoriasViewRepository[i].uid)
                }

                spinnerCategoriasProf.text = categoriasSelected.joinToString()

                if(!isCategoriaTypedProf){
                    isCategoriaTypedProf = true
                    if(spinnerCategoriasProf.text.toString().isNotEmpty()){
                        viewModel.viewModelScope.launch { viewModel.setCategoriasProf(categoriasUis.joinToString()) }
                    }else{
                        viewModel.viewModelScope.launch { viewModel.setCategoriasProf("A") }
                    }
                } else {
                    if(spinnerCategoriasProf.text.toString().isNotEmpty()){
                        viewModel.viewModelScope.launch { viewModel.setCategoriasProf(categoriasUis.joinToString()) }
                    }else{
                        viewModel.viewModelScope.launch { viewModel.setCategoriasProf("A") }
                    }
                }


            }

            builder.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()

                val categoriasUis = arrayListOf<String>()

                categoriasList.map { i ->
                    categoriasUis.add(categoriasViewRepository[i].uid)
                }

                if(!isCategoriaTypedProf){
                    isCategoriaTypedProf = true
                    if(spinnerCategoriasProf.text.toString().isNotEmpty()){
                        viewModel.viewModelScope.launch { viewModel.setCategoriasProf(categoriasUis.joinToString()) }
                    }else{
                        viewModel.viewModelScope.launch { viewModel.setCategoriasProf("C") }
                    }
                }else{
                    if(spinnerCategoriasProf.text.toString().isNotEmpty()){
                        viewModel.viewModelScope.launch { viewModel.setCategoriasProf(categoriasUis.joinToString()) }
                    }else{
                        viewModel.viewModelScope.launch { viewModel.setCategoriasProf("C") }
                    }
                }
            }

            builder.setNeutralButton("Limpiar") { _, _ ->
                selectecCategoria.forEachIndexed { index, _ ->
                    selectecCategoria[index] = false
                }

                categoriasList.clear()

                spinnerCategoriasProf.text = "Escoja su categoría"

                if(!isCategoriaTypedProf){
                    isCategoriaTypedProf = true
                    if(spinnerCategoriasProf.text.toString().isNotEmpty()){
                        viewModel.viewModelScope.launch { viewModel.setCategoriasProf(spinnerCategoriasProf.text.toString()) }
                    }else{
                        viewModel.viewModelScope.launch { viewModel.setCategoriasProf("N") }
                    }
                }else{
                    if(spinnerCategoriasProf.text.toString().isNotEmpty()){
                        viewModel.viewModelScope.launch { viewModel.setCategoriasProf(spinnerCategoriasProf.text.toString()) }
                    }else{
                        viewModel.viewModelScope.launch { viewModel.setCategoriasProf("N") }
                    }
                }
            }
            builder.show()
        }

        descripcionTxtProf.addTextChangedListener{
            if(!isDescripcionTypedProf){
                if (it.toString().isNotEmpty()){
                    isDescripcionTypedProf = true
                    viewModel.viewModelScope.launch { viewModel.setDescripcionProf(it.toString().trim()) }
                }
            } else {
                viewModel.viewModelScope.launch { viewModel.setDescripcionProf(it.toString().trim()) }
            }
        }

        correoTxtProf.addTextChangedListener{
            if (!isCorreoTypedProf){
                if (it.toString().isNotEmpty()){
                    isCorreoTypedProf = true
                    viewModel.viewModelScope.launch { viewModel.setCorreoProf(it.toString().trim()) }
                }
            } else {
                viewModel.viewModelScope.launch { viewModel.setCorreoProf(it.toString().trim()) }
            }

        }

        passwordTxtProf.addTextChangedListener{
            if (!isPaswordTypedProf){
                if (it.toString().isNotEmpty()){
                    isPaswordTypedProf = true
                    viewModel.viewModelScope.launch { viewModel.setPasswordProf(it.toString().trim()) }
                }
            } else {
                viewModel.viewModelScope.launch { viewModel.setPasswordProf(it.toString().trim()) }
            }
        }

        ciudadTxtProf.setOnClickListener {
            val internetConnection: Boolean? = activity?.let { it1 -> Utilitity.isNetworkAvailable(it1) }
            if(internetConnection == true){
                context?.let { it1 -> MapCityFragment(it1, this,false,null,null) }
                    ?.show(childFragmentManager, "mapcityfragment")
            } else {
                Snackbar
                    .make(rltContentDatosPersonalesProf, "Sin conexión a internet, revise los ajustes de conexión para continuar.", Snackbar.LENGTH_SHORT)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                    .setAnchorView(view)
                    .setBackgroundTint(resources.getColor(R.color.purple_700))
                    .show()
            }
        }

        btnRegisterProf.setOnClickListener{
            val internetConnection: Boolean? = activity?.let { it1 -> Utilitity.isNetworkAvailable(it1) }
            if(internetConnection == true){
                FirebaseMessaging.getInstance().token.addOnCompleteListener {
                    if (!it.isSuccessful) {
                        Snackbar
                            .make(
                                rltContentDatosPersonalesProf,
                                "Dispositivo no compatible con notificaciones.",
                                Snackbar.LENGTH_SHORT
                            )
                            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                            .setBackgroundTint(resources.getColor(R.color.black))
                            .show()
                        return@addOnCompleteListener
                    }

                    // Get new FCM registration token
                    val token = it.result
                    registerProf(userGoogle, token)
                }
            } else {
                Snackbar
                    .make(rltContentDatosPersonalesProf, "Sin conexión a internet, revise los ajustes de conexión para continuar.", Snackbar.LENGTH_SHORT)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                    .setAnchorView(view)
                    .setBackgroundTint(resources.getColor(R.color.purple_700))
                    .show()
            }
        }

        if(userGoogle != null){
            val nameSplit = userGoogle.displayName.split(" ")
            if (nameSplit.size >=2) {
                nombreTxtProf.text = Editable.Factory.getInstance().newEditable(nameSplit[0])
                apellidoTxtProf.text = Editable.Factory.getInstance().newEditable(nameSplit[1])
            }else{
                nombreTxtProf.text = Editable.Factory.getInstance().newEditable(userGoogle.displayName)
            }
            correoTxtProf.text = Editable.Factory.getInstance().newEditable(userGoogle.email)
            correoTxtProf.isEnabled = false
            rltPasswordProf.isVisible = false

            viewModel.setFotoProf(userGoogle.photoUrl.toString())
        }
    }

    @SuppressLint("ShowToast")
    private fun registerProf(userGoogle: FirebaseUser?, tokenFire: String) {
        viewModel.viewModelScope.launch {
            if (userGoogle != null) {
                val datosProf = viewModel.registeViewProfDataUsuario(userGoogle.uid)
                if (datosProf != null) {
                    val credencialesProf =
                        viewModel.registeViewProfCredenciales(userGoogle.uid, true)
                    if (credencialesProf != null) {
                        val tokenCli = viewModel.registerViewProfToken(userGoogle.uid, tokenFire)
                        if(tokenCli != null){
                            Snackbar
                                .make(
                                    rltContentDatosPersonalesProf,
                                    "Usuario ${userGoogle.displayName} registrado exitosamente.",
                                    Snackbar.LENGTH_SHORT
                                )
                                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                .setBackgroundTint(resources.getColor(R.color.black))
                                .setActionTextColor(resources.getColor(R.color.purple_500))
                                .show()
                            completeFragment?.dismiss()
                            goToMenuPrincipal(Utilitity.ROL_PROFESIONAL, userGoogle.uid)
                        } else{
                            Snackbar
                                .make(
                                    rltContentDatosPersonalesProf,
                                    getString(R.string.msgerrorusuario),
                                    Snackbar.LENGTH_SHORT
                                )
                                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                .setBackgroundTint(resources.getColor(R.color.black))
                                .show()
                        }
                    } else {
                        Snackbar
                            .make(
                                rltContentDatosPersonalesProf,
                                getString(R.string.msgerrorusuario),
                                Snackbar.LENGTH_SHORT
                            )
                            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                            .setBackgroundTint(resources.getColor(R.color.black))
                            .show()
                    }
                } else {
                    Snackbar
                        .make(
                            rltContentDatosPersonalesProf,
                            getString(R.string.msgerrorusuario),
                            Snackbar.LENGTH_SHORT
                        )
                        .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                        .setBackgroundTint(resources.getColor(R.color.black))
                        .show()
                }
            } else {
                viewModel.getViewCredentialEmailUser().collect { credUser ->
                    if (credUser == null){
                        val user = viewModel.registerViewProf()
                        if (user != null) {
                            val datosProf = viewModel.registeViewProfDataUsuario(user.uid)
                            if (datosProf != null) {
                                val credencialesProf = viewModel.registeViewProfCredenciales(
                                    user.uid,
                                    false
                                )
                                if (credencialesProf != null) {
                                    val tokenCli = viewModel.registerViewProfToken(user.uid, tokenFire)
                                    if(tokenCli != null){
                                        Snackbar
                                            .make(
                                                rltContentDatosPersonalesProf,
                                                "Usuario registrado exitosamente. Por favor inicie sesión.",
                                                Snackbar.LENGTH_SHORT
                                            )
                                            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                            .setBackgroundTint(resources.getColor(R.color.black))
                                            .setActionTextColor(resources.getColor(R.color.purple_500))
                                            .show()
                                        registerFragment?.returnToLogin()
                                    } else {
                                        Snackbar
                                            .make(
                                                rltContentDatosPersonalesProf,
                                                getString(R.string.msgerrorusuario),
                                                Snackbar.LENGTH_SHORT
                                            )
                                            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                            .setBackgroundTint(resources.getColor(R.color.black))
                                            .show()
                                    }
                                } else {
                                    Snackbar
                                        .make(
                                            rltContentDatosPersonalesProf,
                                            getString(R.string.msgerrorusuario),
                                            Snackbar.LENGTH_SHORT
                                        )
                                        .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                        .setBackgroundTint(resources.getColor(R.color.black))
                                        .show()
                                }
                            } else {
                                Snackbar
                                    .make(
                                        rltContentDatosPersonalesProf,
                                        getString(R.string.msgerrorusuario),
                                        Snackbar.LENGTH_SHORT
                                    )
                                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                    .setBackgroundTint(resources.getColor(R.color.black))
                                    .show()
                            }
                        } else {
                            Snackbar
                                .make(
                                    rltContentDatosPersonalesProf,
                                    "Error al crear el usuario",
                                    Snackbar.LENGTH_SHORT
                                )
                                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                .setBackgroundTint(resources.getColor(R.color.black))
                                .show()
                        }
                    }else{
                        Snackbar
                            .make(
                                rltContentDatosPersonalesProf,
                                "El email ingresado ya existe",
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

    private fun goToMenuPrincipal(rol: String, uid: String){
        val intent = Intent(context, TabUsersActivity::class.java).apply {
            putExtra("rolUser", rol)
            putExtra("uid", uid)
        }
        startActivity(intent)
    }

    override fun responseMap(geocoder: String) {
        ciudadTxtProf.text = Editable.Factory.getInstance().newEditable(geocoder)
    }

}