package com.uisrael.worknow.Views

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.uisrael.worknow.Model.Data.CategoriasData
import com.uisrael.worknow.Model.Data.UsuariosData
import com.uisrael.worknow.R
import com.uisrael.worknow.ViewModel.ProfileUserViewModel
import com.uisrael.worknow.Views.Dialogs.PicturePickerFragment
import com.uisrael.worknow.Views.Utilities.Utilitity
import kotlinx.android.synthetic.main.profile_user_fragment.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProfileUserFragment (var uidUser: String, private val currentUser: Any) : DialogFragment() {

    var categoriasViewRepository: MutableList<CategoriasData> = ArrayList()
    var categoriasRepository: Array<String> = emptyArray()
    lateinit var selectecCategoria: BooleanArray
    var categoriasList = arrayListOf<Int>()
    private lateinit var picturePickerFragment: PicturePickerFragment

    private lateinit var viewModel: ProfileUserViewModel
    private var currentPhotoPath: String = ""

    private val REQUESTCAMERA = 200
    private val REQUESTGALLERY = 201

    private val currentUserInfo = currentUser.toString()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.profile_user_fragment, container, false)
    }

    override fun getTheme(): Int {
        return R.style.FullScreenDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileUserViewModel::class.java)
        viewModel.currentUser = currentUser as UsuariosData
        viewModel.uidUserCurrent = uidUser
        inicializarListeners()
        collectorFlow()
    }

    @SuppressLint("ShowToast")
    private fun inicializarListeners() {
        nombreTxtUserProfile.addTextChangedListener{
            viewModel.viewModelScope.launch { viewModel.setNombreUser(it.toString().trim()) }
        }

        apellidoTxtUserProfile.addTextChangedListener{
            viewModel.viewModelScope.launch { viewModel.setApellidoUser(it.toString().trim()) }
        }

        ciudadTxtUserProfile.addTextChangedListener{
            viewModel.viewModelScope.launch { viewModel.setCiudadUser(it.toString().trim()) }
        }

        telefonoTxtUserProfile.addTextChangedListener{
            viewModel.viewModelScope.launch { viewModel.setTelefonoUser(it.toString().trim()) }
        }

        spinnerCategoriasUserProfile.setOnClickListener{
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Escoja las categorias a las que desea aplicar")
            builder.setCancelable(false)

            builder.setMultiChoiceItems(categoriasRepository,selectecCategoria) { dialogInterface: DialogInterface, i: Int, b: Boolean ->
                if (b) {
                    categoriasList.add(i)
                    categoriasList.sort()
                } else {
                    categoriasList.remove(i)
                }
            }

            builder.setPositiveButton("Aceptar") { dialog, which ->
                val categoriasSelected = arrayListOf<String>()
                val categoriasUis = arrayListOf<String>()

                categoriasList.map { i ->
                    categoriasSelected.add(categoriasRepository[i])
                    categoriasUis.add(categoriasViewRepository[i].uid)
                }

                spinnerCategoriasUserProfile.text = categoriasSelected.joinToString()

                if(spinnerCategoriasUserProfile.text.toString().isNotEmpty()){
                    viewModel.viewModelScope.launch { viewModel.setCategoriasUser(categoriasUis.joinToString()) }
                }else{
                    viewModel.viewModelScope.launch { viewModel.setCategoriasUser("A") }
                }
            }

            builder.setNegativeButton("Cancelar") { dialog, which ->
                dialog.dismiss()

                val categoriasUis = arrayListOf<String>()

                categoriasList.map { i ->
                    categoriasUis.add(categoriasViewRepository[i].uid)
                }

                if(spinnerCategoriasUserProfile.text.toString().isNotEmpty()){
                    viewModel.viewModelScope.launch { viewModel.setCategoriasUser(categoriasUis.joinToString()) }
                }else{
                    viewModel.viewModelScope.launch { viewModel.setCategoriasUser("C") }
                }
            }

            builder.setNeutralButton("Limpiar") { dialog, which ->
                selectecCategoria.forEachIndexed { index, b ->
                    selectecCategoria[index] = false
                }

                categoriasList.clear()

                spinnerCategoriasUserProfile.text = "Escoja su categoria"

                if(spinnerCategoriasUserProfile.text.toString().isNotEmpty()){
                    viewModel.viewModelScope.launch { viewModel.setCategoriasUser(spinnerCategoriasUserProfile.text.toString()) }
                }else{
                    viewModel.viewModelScope.launch { viewModel.setCategoriasUser("N") }
                }
            }
            builder.show()
        }

        descripcionTxtUserProfile.addTextChangedListener{
            viewModel.viewModelScope.launch { viewModel.setDescripcionUser(it.toString().trim()) }
        }

        correoTxtUserProfile.addTextChangedListener{
            viewModel.viewModelScope.launch { viewModel.setCorreoUser(it.toString().trim()) }
        }

        passwordTxtUserProfile.addTextChangedListener{
            viewModel.viewModelScope.launch { viewModel.setPasswordUser(it.toString().trim()) }
        }

        btnEditarUserProfile.setOnClickListener{
            if (viewModel.setDataUserViewUpdateUserProfile(currentUserInfo)) {
                context?.let { it1 ->
                    Utilitity().showDialog(it1,"Aviso", "Esta seguro que desea actualizar su perfil?",R.drawable.ic_warning_24)
                        ?.setPositiveButton("Aceptar") { dialog, _ ->
                            viewModel.viewModelScope.launch {
                                val response = viewModel.setUpdateViewUserProfile(uidUser)
                                if (response != null) {
                                    Snackbar
                                        .make(
                                            scrollDatosPersonalesUserProfile,
                                            "Datos actualizados exitosamente.",
                                            Snackbar.LENGTH_SHORT
                                        )
                                        .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                        .setBackgroundTint(resources.getColor(R.color.black))
                                        .show()
                                } else {
                                    Snackbar
                                        .make(
                                            scrollDatosPersonalesUserProfile,
                                            "Error al actualizar los datos del usuario.",
                                            Snackbar.LENGTH_SHORT
                                        )
                                        .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                        .setBackgroundTint(resources.getColor(R.color.black))
                                        .show()
                                }
                                dialog.dismiss()
                            }
                        }
                        ?.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                        ?.show()
                }
            } else {
                Snackbar
                    .make(
                        scrollDatosPersonalesUserProfile,
                        "Se deben realizar cambios para actualizar los datos.",
                        Snackbar.LENGTH_INDEFINITE
                    )
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                    .setBackgroundTint(resources.getColor(R.color.black))
                    .setActionTextColor(resources.getColor(R.color.purple_500))
                    .setAction("OK") {}
                    .show()
            }
        }

        btnToolbarUserProfile.setOnClickListener {
            dismiss()
        }

        nombreTxtUserProfile.text = Editable.Factory.getInstance().newEditable(viewModel.currentUser.nombre)
        apellidoTxtUserProfile.text = Editable.Factory.getInstance().newEditable(viewModel.currentUser.apellido)
        ciudadTxtUserProfile.text = Editable.Factory.getInstance().newEditable(viewModel.currentUser.ciudad)
        telefonoTxtUserProfile.text = Editable.Factory.getInstance().newEditable(viewModel.currentUser.telefono)

        viewModel.viewModelScope.launch {
            viewModel.getCredencialesViewUser().collect {
                if (it != null) {
                    correoTxtUserProfile.text = Editable.Factory.getInstance().newEditable(it.correo)
                    passwordTxtUserProfile.text = Editable.Factory.getInstance().newEditable(it.password)
                    correoTxtUserProfile.isEnabled = false
                    passwordTxtUserProfile.isEnabled = false
                    if(it.fromSocNet){
                        rltPasswordUserProfile.isVisible = false
                        errorPasswordUserProfile.isVisible = false
                        viewModel.isFromSocNet = true
                    }
                }
            }
        }

        if(viewModel.currentUser.rol == Utilitity.ROL_PROFESIONAL){
            titleDatosProfesionalUserProfile.isVisible = true
            rltDatosProfesionalUserProfile.isVisible = true
            descripcionTxtUserProfile.text = Editable.Factory.getInstance().newEditable(viewModel.currentUser.datosProf.descripcion)
        }

        if (viewModel.currentUser.foto.isNotBlank()){
            if(Utilitity().isValidUrl(viewModel.currentUser.foto)){
                Picasso
                    .get()
                    .load(viewModel.currentUser.foto)
                    .resize(250, 250)
                    .centerCrop()
                    .into(imageUsuarioUserProfile)
            }else{
                val imageBytes = Base64.decode(viewModel.currentUser.foto, Base64.DEFAULT)
                val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                imageUsuarioUserProfile.setImageBitmap(Utilitity().getRoundedCornerBitmap(decodedImage))
                iconUsuarioUserProfile.isVisible = false
            }
            iconUsuarioUserProfile.isVisible = false
        }else{
            iconUsuarioUserProfile.text = "${viewModel.currentUser.nombre[0]}${viewModel.currentUser.apellido[0]}"
        }

        imageContenedorUserProfile.setOnClickListener {
            if(context != null){
                picturePickerFragment = PicturePickerFragment(requireContext(),this,currentPhotoPath)
                picturePickerFragment.show(childFragmentManager,"frg_dialog_picker")
            }
        }
    }

    private fun collectorFlow() {
        lifecycleScope.launch {
            viewModel.isFormUserSucess.collect { value ->
                btnEditarUserProfile.isEnabled = value
            }
        }

        lifecycleScope.launch {
            viewModel.isNombreUserOk.collect { value ->
                when (value.respuesta) {
                    0 -> {
                        errorNombreUserProfile.isVisible = false
                        errorNombreUserProfile.text = value.mensaje
                        rltNombreUserProfile.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields) }
                    }
                    else -> {
                        errorNombreUserProfile.isVisible = true
                        errorNombreUserProfile.text = value.mensaje
                        rltNombreUserProfile.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields_error) }
                    }
                }

            }
        }

        lifecycleScope.launch {
            viewModel.isApellidoUserOk.collect { value ->
                when (value.respuesta) {
                    0 -> {
                        errorApellidoUserProfile.isVisible = false
                        errorApellidoUserProfile.text = value.mensaje
                        rltApellidoUserProfile.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields) }
                    }
                    else -> {
                        errorApellidoUserProfile.isVisible = true
                        errorApellidoUserProfile.text = value.mensaje
                        rltApellidoUserProfile.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields_error) }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isCiudadUserOk.collect { value ->
                when (value.respuesta) {
                    0 -> {
                        errorCiudadUserProfile.isVisible = false
                        errorCiudadUserProfile.text = value.mensaje
                        rltCiudadUserProfile.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields) }
                    }
                    else -> {
                        errorCiudadUserProfile.isVisible = true
                        errorCiudadUserProfile.text = value.mensaje
                        rltCiudadUserProfile.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields_error) }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isTelefonoUserOk.collect { value ->
                when (value.respuesta) {
                    0 -> {
                        errorTelefonoUserProfile.isVisible = false
                        errorTelefonoUserProfile.text = value.mensaje
                        rltTelefonoUserProfile.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields) }
                    }
                    else -> {
                        errorTelefonoUserProfile.isVisible = true
                        errorTelefonoUserProfile.text = value.mensaje
                        rltTelefonoUserProfile.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields_error) }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isCategoriaUserOk.collect { value ->
                when (value.respuesta) {
                    0 -> {
                        errorCategoriaUserProfile.isVisible = false
                        errorCategoriaUserProfile.text = value.mensaje
                        rltCategoriasUserProfile.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields) }
                    }
                    else -> {
                        errorCategoriaUserProfile.isVisible = true
                        errorCategoriaUserProfile.text = value.mensaje
                        rltCategoriasUserProfile.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields_error) }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isDescripcionUserOk.collect { value ->
                when (value.respuesta) {
                    0 -> {
                        errorDescripcionUserProfile.isVisible = false
                        errorDescripcionUserProfile.text = value.mensaje
                        rltDescripcionUserProfile.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields) }
                    }
                    else -> {
                        errorDescripcionUserProfile.isVisible = true
                        errorDescripcionUserProfile.text = value.mensaje
                        rltDescripcionUserProfile.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields_error) }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isCorreoUserOK.collect { value ->
                when (value.respuesta) {
                    0 -> {
                        errorCorreoUserProfile.isVisible = false
                        errorCorreoUserProfile.text = value.mensaje
                        rltCorreoUserProfile.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields) }
                    }
                    else -> {
                        errorCorreoUserProfile.isVisible = true
                        errorCorreoUserProfile.text = value.mensaje
                        rltCorreoUserProfile.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields_error) }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isPasswordUserOK.collect { value ->
                when (value.respuesta){
                    0 -> {
                        errorPasswordUserProfile.isVisible = false
                        errorPasswordUserProfile.text = value.mensaje
                        rltPasswordUserProfile.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields) }
                    }
                    else -> {
                        errorPasswordUserProfile.isVisible = true
                        errorPasswordUserProfile.text = value.mensaje
                        rltPasswordUserProfile.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_fields_error) }
                    }
                }
            }
        }

        if(viewModel.currentUser.rol == Utilitity.ROL_PROFESIONAL){
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

                    val categoriasSelected = arrayListOf<String>()
                    val categoriasUis = arrayListOf<String>()

                    val listCategoriaProf = viewModel.currentUser.datosProf.categorias
                    listCategoriaProf.mapIndexed { index, idCategoria ->
                        categoriasViewRepository.mapIndexed { index1, categoria->
                            if(categoria.estado){
                                if(idCategoria == categoria.uid){
                                    categoriasSelected.add(categoria.nombre)
                                    categoriasUis.add(idCategoria)
                                    categoriasList.add(index1)
                                    categoriasList.sort()
                                }
                            }
                        }
                    }

                    categoriasList.forEach { pos ->
                        selectecCategoria[pos] = true
                    }

                    spinnerCategoriasUserProfile.text = categoriasSelected.joinToString()
                    if(spinnerCategoriasUserProfile.text.toString().isNotEmpty())
                        viewModel.viewModelScope.launch { viewModel.setCategoriasUser(categoriasUis.joinToString()) }
                }
                this.cancel()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUESTCAMERA){
            val picture64 = context?.let { Utilitity().compressImage(picturePickerFragment.currentPhotoPath, it) }
            val imageBytes = Base64.decode(picture64, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            if (viewModel.currentUser.foto.isNotBlank()){
                imageUsuarioUserProfile.setImageBitmap(Utilitity().getRoundedCornerBitmap(decodedImage))
            }else{
                imageUsuarioUserProfile.setImageBitmap(Utilitity().getRoundedCornerBitmap(decodedImage))
                iconUsuarioUserProfile.isVisible = false
            }
            if (picture64 != null) {
                viewModel.currentUser.foto = picture64
            }
        }

        if (resultCode == Activity.RESULT_OK && requestCode == REQUESTGALLERY){
            currentPhotoPath = data?.data?.let { getRealPathFromURI(it) }.toString()
            val picture64 = context?.let { Utilitity().compressImage(currentPhotoPath, it) }
            val imageBytes = Base64.decode(picture64, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            if (viewModel.currentUser.foto.isNotBlank()){
                imageUsuarioUserProfile.setImageBitmap(Utilitity().getRoundedCornerBitmap(decodedImage))
            }else{
                imageUsuarioUserProfile.setImageBitmap(Utilitity().getRoundedCornerBitmap(decodedImage))
                iconUsuarioUserProfile.isVisible = false
            }
            if (picture64 != null) {
                viewModel.currentUser.foto = picture64
            }
        }
    }

    private fun getRealPathFromURI(contentURI: Uri): String {
        val result: String
        val cursor: Cursor? = activity?.getContentResolver()
            ?.query(contentURI, null, null, null, null)
        if (cursor == null) {
            result = contentURI.getPath().toString()
        } else {
            cursor.moveToFirst()
            val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }

}