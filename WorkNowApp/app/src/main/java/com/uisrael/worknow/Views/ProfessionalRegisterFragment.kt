package com.uisrael.worknow.Views

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.uisrael.worknow.R
import com.uisrael.worknow.ViewModel.ProfessionalViewModel
import kotlinx.android.synthetic.main.professional_fragment.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProfessionalRegisterFragment : Fragment() {

    var isNombreTypedProf : Boolean = false
    var isApellidoTypedProf : Boolean = false
    var isCiudadTypedProf : Boolean = false
    var isTelefonoTypedProf : Boolean = false
    var isCategoriaTypedProf : Boolean = false
    var isDescripcionTypedProf : Boolean = false
    var isCorreoTypedProf : Boolean = false
    var isPaswordTypedProf : Boolean = false

    var categoriasRepository = arrayOf("Plomería", "Electricista", "Carpinteria")

    var selectecCategoria: BooleanArray =  BooleanArray(categoriasRepository.size)

    var categoriasList = arrayListOf<Int>()

    companion object {
        fun newInstance() = ProfessionalRegisterFragment()
    }

    private lateinit var viewModel: ProfessionalViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.professional_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfessionalViewModel::class.java)
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
                            rltNombreProf.background = context?.let { ContextCompat.getDrawable(it,R.drawable.fieldsbackground) }
                        }
                        1 -> {
                            errorNombreProf.isVisible = true
                            errorNombreProf.text = value.mensaje
                            rltNombreProf.background = context?.let { ContextCompat.getDrawable(it,R.drawable.fieldsbackgrounderror) }
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
                            rltApellidoProf.background = context?.let { ContextCompat.getDrawable(it,R.drawable.fieldsbackground) }
                        }
                        1 -> {
                            errorApellidoProf.isVisible = true
                            errorApellidoProf.text = value.mensaje
                            rltApellidoProf.background = context?.let { ContextCompat.getDrawable(it,R.drawable.fieldsbackgrounderror) }
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
                            rltCiudadProf.background = context?.let { ContextCompat.getDrawable(it,R.drawable.fieldsbackground) }
                        }
                        1 -> {
                            errorCiudadProf.isVisible = true
                            errorCiudadProf.text = value.mensaje
                            rltCiudadProf.background = context?.let { ContextCompat.getDrawable(it,R.drawable.fieldsbackgrounderror) }
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
                            rltTelefonoProf.background = context?.let { ContextCompat.getDrawable(it,R.drawable.fieldsbackground) }
                        }
                        1, 2, 3, -> {
                            errorTelefonoProf.isVisible = true
                            errorTelefonoProf.text = value.mensaje
                            rltTelefonoProf.background = context?.let { ContextCompat.getDrawable(it,R.drawable.fieldsbackgrounderror) }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isCategoriaProOk.collect { value ->
                if (isCategoriaTypedProf){
                    when (value.respuesta) {
                        0 -> {
                            errorCategoriaProf.isVisible = false
                            errorCategoriaProf.text = value.mensaje
                            rltCategoriasProf.background = context?.let { ContextCompat.getDrawable(it,R.drawable.fieldsbackground) }
                        }
                        1,2 -> {
                            errorCategoriaProf.isVisible = true
                            errorCategoriaProf.text = value.mensaje
                            rltCategoriasProf.background = context?.let { ContextCompat.getDrawable(it,R.drawable.fieldsbackgrounderror) }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isDescripcionProOk.collect { value ->
                if (isDescripcionTypedProf){
                    when (value.respuesta) {
                        0 -> {
                            errorDescripcionProf.isVisible = false
                            errorDescripcionProf.text = value.mensaje
                            rltDescripcionProf.background = context?.let { ContextCompat.getDrawable(it,R.drawable.fieldsbackground) }
                        }
                        1,2 -> {
                            errorDescripcionProf.isVisible = true
                            errorDescripcionProf.text = value.mensaje
                            rltDescripcionProf.background = context?.let { ContextCompat.getDrawable(it,R.drawable.fieldsbackgrounderror) }
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
                            rltCorreoProf.background = context?.let { ContextCompat.getDrawable(it,R.drawable.fieldsbackground) }
                        }
                        1, 2 -> {
                            errorCorreoProf.isVisible = true
                            errorCorreoProf.text = value.mensaje
                            rltCorreoProf.background = context?.let { ContextCompat.getDrawable(it,R.drawable.fieldsbackgrounderror) }
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
                            rltPasswordProf.background = context?.let { ContextCompat.getDrawable(it,R.drawable.fieldsbackground) }
                        }
                        1 -> {
                            errorPasswordProf.isVisible = true
                            errorPasswordProf.text = value.mensaje
                            rltPasswordProf.background = context?.let { ContextCompat.getDrawable(it,R.drawable.fieldsbackgrounderror) }
                        }
                    }
                }
            }
        }
    }

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

            builder.setMultiChoiceItems(categoriasRepository,selectecCategoria) { dialogInterface: DialogInterface, i: Int, b: Boolean ->
                if (b) {
                    categoriasList.add(i)
                    categoriasList.sort()
                } else {
                    categoriasList.remove(i)
                }
            }
            builder.setPositiveButton("Aceptar") { dialog, which ->
                var categoriasSelected = arrayListOf<String>()

                categoriasList.map { i ->
                    categoriasSelected.add(categoriasRepository[i])
                }

                spinnerCategoriasProf.text = categoriasSelected.joinToString()

                if(!isCategoriaTypedProf){
                    isCategoriaTypedProf = true
                    if(spinnerCategoriasProf.text.toString().isNotEmpty()){
                        viewModel.viewModelScope.launch { viewModel.setCategoriasProf(categoriasSelected.joinToString()) }
                    }else{
                        viewModel.viewModelScope.launch { viewModel.setCategoriasProf("A") }
                    }
                } else {
                    if(spinnerCategoriasProf.text.toString().isNotEmpty()){
                        viewModel.viewModelScope.launch { viewModel.setCategoriasProf(categoriasSelected.joinToString()) }
                    }else{
                        viewModel.viewModelScope.launch { viewModel.setCategoriasProf("A") }
                    }
                }


            }

            builder.setNegativeButton("Cancelar") { dialog, which ->
                dialog.dismiss()

                if(!isCategoriaTypedProf){
                    isCategoriaTypedProf = true
                    if(spinnerCategoriasProf.text.toString().isNotEmpty()){
                        viewModel.viewModelScope.launch { viewModel.setCategoriasProf(spinnerCategoriasProf.text.toString()) }
                    }else{
                        viewModel.viewModelScope.launch { viewModel.setCategoriasProf("C") }
                    }
                }else{
                    if(spinnerCategoriasProf.text.toString().isNotEmpty()){
                        viewModel.viewModelScope.launch { viewModel.setCategoriasProf(spinnerCategoriasProf.text.toString()) }
                    }else{
                        viewModel.viewModelScope.launch { viewModel.setCategoriasProf("C") }
                    }
                }
            }

            builder.setNeutralButton("Limpiar") { dialog, which ->
                selectecCategoria.forEachIndexed { index, b ->
                    selectecCategoria[index] = false
                }

                categoriasList.clear()

                spinnerCategoriasProf.text = "Escoja su categoria"

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
    }

}