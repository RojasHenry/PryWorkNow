package com.uisrael.worknow.Views.TabsFragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.uisrael.worknow.Model.Data.CategoriasData
import com.uisrael.worknow.R
import com.uisrael.worknow.ViewModel.TabsFragViewModel.OffersRegisterViewModel
import com.uisrael.worknow.Views.Adapters.RespFotosGridAdapter
import com.uisrael.worknow.Views.Dialogs.DatePickerFragment
import com.uisrael.worknow.Views.Dialogs.IResponseMapFragment
import com.uisrael.worknow.Views.Dialogs.MapCityFragment
import com.uisrael.worknow.Views.Utilities.Utilitity
import kotlinx.android.synthetic.main.fragment_offersregister.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class OffersRegisterFragment : Fragment() ,IResponseMapFragment {

    var isCategoriaTypedOffer : Boolean = false
    var isFechaIniTypedOffer : Boolean = false
    var isFechaFinTypedOffer : Boolean = false
    var isDescripcionTypedOffer : Boolean = false
    var isCantidadTypedOffer : Boolean = false
    var isUbicacionTypedOffer : Boolean = false

    var categoriasViewRepository: MutableList<CategoriasData> = ArrayList()
    var categoriasRepository: Array<String> = emptyArray()
    lateinit var selectecCategoria: BooleanArray
    var categoriasList = arrayListOf<Int>()
    var currentCategoria: CategoriasData? = null

    var inCorrectFechaIni = false
    var inCorrectFechaFin = false

    var fotosList = ArrayList<String>()
    lateinit var adapter: RespFotosGridAdapter
    private val REQUESTCAMERA = 200
    private val REQUESTGALLERY = 201

    companion object {
        fun newInstance() = OffersRegisterFragment()
    }

    private lateinit var offersRegisterViewModel: OffersRegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_offersregister, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        offersRegisterViewModel = ViewModelProvider(this).get(OffersRegisterViewModel::class.java)
        inicializarListeners()
        collectorFlow()
    }

    @SuppressLint("ShowToast")
    private fun inicializarListeners() {

        spinnerCategoriasOffer.setOnClickListener{
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Escoja la categoria a la que pertenece:")
            builder.setCancelable(false)

            val indexSelected = if (categoriasList.isEmpty()) -1 else categoriasList[0]

            builder.setSingleChoiceItems(categoriasRepository, indexSelected) { _: DialogInterface, i: Int ->
                categoriasList.clear()
                categoriasList.add(i)
            }

            builder.setPositiveButton("Aceptar"){ _: DialogInterface, i: Int ->
                if(categoriasList.isNotEmpty()){
                    val categoriaUid = categoriasViewRepository[categoriasList[0]].uid
                    spinnerCategoriasOffer.text = categoriasRepository[categoriasList[0]]
                    if(!isCategoriaTypedOffer){
                        isCategoriaTypedOffer = true
                        if(spinnerCategoriasOffer.text.toString().isNotEmpty()){
                            updateFieldsInterface(categoriasViewRepository[categoriasList[0]],true)
                            offersRegisterViewModel.viewModelScope.launch { offersRegisterViewModel.setCategoriaOffer(
                                categoriaUid
                            )}

                        }else{
                            updateFieldsInterface(null,false)
                            offersRegisterViewModel.viewModelScope.launch { offersRegisterViewModel.setCategoriaOffer(
                                ""
                            ) }
                        }
                    } else {
                        updateFieldsInterface(categoriasViewRepository[categoriasList[0]],true)
                        if(spinnerCategoriasOffer.text.toString().isNotEmpty()){
                            offersRegisterViewModel.viewModelScope.launch { offersRegisterViewModel.setCategoriaOffer(
                                categoriaUid
                            )}
                        }else{
                            updateFieldsInterface(null,false)
                            offersRegisterViewModel.viewModelScope.launch { offersRegisterViewModel.setCategoriaOffer(
                                ""
                            ) }
                        }
                    }
                }else{
                    if(!isCategoriaTypedOffer) {
                        isCategoriaTypedOffer = true
                        updateFieldsInterface(null,false)
                        offersRegisterViewModel.viewModelScope.launch { offersRegisterViewModel.setCategoriaOffer(
                            ""
                        ) }
                    }else {
                        updateFieldsInterface(null,false)
                        offersRegisterViewModel.viewModelScope.launch { offersRegisterViewModel.setCategoriaOffer(
                            ""
                        ) }
                    }
                }
            }

            builder.setNegativeButton("Cancelar"){ dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
                if(categoriasList.isNotEmpty()){
                    val categoriaUid = categoriasViewRepository[categoriasList[0]].uid
                    if(!isCategoriaTypedOffer){
                        isCategoriaTypedOffer = true
                        if(spinnerCategoriasOffer.text.toString().isNotEmpty()){
                            updateFieldsInterface(categoriasViewRepository[categoriasList[0]],true)
                            offersRegisterViewModel.viewModelScope.launch { offersRegisterViewModel.setCategoriaOffer(
                                categoriaUid
                            )}
                        }else{
                            updateFieldsInterface(null,false)
                            offersRegisterViewModel.viewModelScope.launch { offersRegisterViewModel.setCategoriaOffer(
                                ""
                            ) }
                        }
                    }else {
                        if(spinnerCategoriasOffer.text.toString().isNotEmpty()){
                            updateFieldsInterface(categoriasViewRepository[categoriasList[0]],true)
                            offersRegisterViewModel.viewModelScope.launch { offersRegisterViewModel.setCategoriaOffer(
                                categoriaUid
                            )}
                        }else{
                            updateFieldsInterface(null,false)
                            offersRegisterViewModel.viewModelScope.launch { offersRegisterViewModel.setCategoriaOffer(
                                ""
                            ) }
                        }
                    }
                }else{
                    if(!isCategoriaTypedOffer) {
                        isCategoriaTypedOffer = true
                        updateFieldsInterface(null,false)
                        offersRegisterViewModel.viewModelScope.launch { offersRegisterViewModel.setCategoriaOffer(
                            ""
                        ) }
                    }else {
                        updateFieldsInterface(null,false)
                        offersRegisterViewModel.viewModelScope.launch { offersRegisterViewModel.setCategoriaOffer(
                            ""
                        ) }

                    }
                }

            }
            builder.setNeutralButton("Limpiar"){ _: DialogInterface, _: Int ->
                categoriasList.clear()

                spinnerCategoriasOffer.text = "Escoja una categoría"

                updateFieldsInterface(null,false)
                if(!isCategoriaTypedOffer){
                    isCategoriaTypedOffer = true
                    offersRegisterViewModel.viewModelScope.launch { offersRegisterViewModel.setCategoriaOffer(
                        ""
                    ) }

                } else {
                    offersRegisterViewModel.viewModelScope.launch { offersRegisterViewModel.setCategoriaOffer(
                        ""
                    ) }

                }
            }

            builder.show()
        }

        fechaInOffer.setOnClickListener{
            showDatePickerDialog(true)
        }

        fechaInOffer.addTextChangedListener{
            if (!isFechaIniTypedOffer){
                if (it.toString().isNotEmpty()){
                    isFechaIniTypedOffer = true
                    if(!inCorrectFechaIni){
                        offersRegisterViewModel.viewModelScope.launch {
                            offersRegisterViewModel.setFechaIniOffer(it.toString().trim())
                        }
                    }else{
                        inCorrectFechaIni = false
                        offersRegisterViewModel.viewModelScope.launch {
                            offersRegisterViewModel.setFechasErrorOffer(true)
                        }
                    }
                }
            } else {
                if(!inCorrectFechaIni){
                    offersRegisterViewModel.viewModelScope.launch {
                        offersRegisterViewModel.setFechaIniOffer(it.toString().trim())
                    }
                }else{
                    inCorrectFechaIni = false
                    offersRegisterViewModel.viewModelScope.launch {
                        offersRegisterViewModel.setFechasErrorOffer(true)
                    }
                }
            }
        }

        fechaFinOffer.setOnClickListener{
            showDatePickerDialog(false)
        }

        fechaFinOffer.addTextChangedListener {
            if (!isFechaFinTypedOffer){
                if (it.toString().isNotEmpty()){
                    isFechaFinTypedOffer = true
                    if(!inCorrectFechaFin){
                        offersRegisterViewModel.viewModelScope.launch { offersRegisterViewModel.setFechaFinOffer(
                            it.toString().trim()
                        ) }
                    }else{
                        inCorrectFechaFin = false
                        offersRegisterViewModel.viewModelScope.launch {
                            offersRegisterViewModel.setFechasErrorOffer(false)
                        }
                    }

                }
            } else {
                if(!inCorrectFechaFin){
                    offersRegisterViewModel.viewModelScope.launch { offersRegisterViewModel.setFechaFinOffer(
                        it.toString().trim()
                    ) }
                }else{
                    inCorrectFechaFin = false
                    offersRegisterViewModel.viewModelScope.launch {
                        offersRegisterViewModel.setFechasErrorOffer(false)
                    }
                }

            }
        }

        checkDeInmediato.setOnCheckedChangeListener { _, isChecked ->
            offersRegisterViewModel.viewModelScope.launch {
                offersRegisterViewModel.setInmediatoOffer(isChecked)
            }
        }

        descripcionTxtOffer.addTextChangedListener{
            if (!isDescripcionTypedOffer){
                if (it.toString().isNotEmpty()){
                    isDescripcionTypedOffer = true
                    offersRegisterViewModel.viewModelScope.launch {
                        offersRegisterViewModel.setDescripcionOffer(it.toString().trim())
                    }
                }
            } else {
                offersRegisterViewModel.viewModelScope.launch {
                    offersRegisterViewModel.setDescripcionOffer(it.toString().trim())
                }
            }
        }

        checkSoloPersona.setOnCheckedChangeListener{ _, isChecked ->
            offersRegisterViewModel.viewModelScope.launch {
                offersRegisterViewModel.setSoloUnaPersonaOffer(isChecked)
            }
        }

        cantidadTxtOffer.addTextChangedListener {
            if (!isCantidadTypedOffer){
                if (it.toString().isNotEmpty()){
                    isCantidadTypedOffer = true
                    offersRegisterViewModel.viewModelScope.launch {
                        offersRegisterViewModel.setCantidadOffer(it.toString().trim())
                    }
                }
            } else {
                offersRegisterViewModel.viewModelScope.launch {
                    offersRegisterViewModel.setCantidadOffer(it.toString().trim())
                }
            }
        }

        ubicacionTxtOffer.addTextChangedListener {
            if (!isUbicacionTypedOffer){
                if (it.toString().isNotEmpty()){
                    isUbicacionTypedOffer = true
                    offersRegisterViewModel.viewModelScope.launch {
                        offersRegisterViewModel.setUbicacionOffer(it.toString().trim())
                    }
                }
            } else {
                offersRegisterViewModel.viewModelScope.launch {
                    offersRegisterViewModel.setUbicacionOffer(it.toString().trim())
                }
            }
        }

        fotosList.add("")
        fotosList.add("")
        fotosList.add("")
        fotosList.add("")
        fotosList.add("")

        adapter = context?.let { RespFotosGridAdapter(it, fotosList, this) }!!
        gridFografiasRespOffer.adapter = adapter

        telefonoTxtOffer.addTextChangedListener {
            offersRegisterViewModel.viewModelScope.launch {
                offersRegisterViewModel.setTelefonoOffer(it.toString().trim())
            }
        }

        ubicacionTxtOffer.setOnClickListener {
            val internetConnection: Boolean? = activity?.let { it1 -> Utilitity.isNetworkAvailable(it1) }
            if (internetConnection == true){
                context?.let { it1 -> MapCityFragment(it1, this,false,null,null) }
                    ?.show(childFragmentManager, "mapcityfragment")
            }
        }

        btnRegisterOffer.setOnClickListener {
            val internetConnection: Boolean? = activity?.let { it1 -> Utilitity.isNetworkAvailable(it1) }
            if (internetConnection == true){
                offersRegisterViewModel.viewModelScope.launch {
                    val uid:String = offersRegisterViewModel.registerViewOffer() as String
                    if(uid.isNotEmpty()){
                        val offerPicture = offersRegisterViewModel.registerViewOfferPictures(uid, requireContext())
                        if(offerPicture != null){
                            Snackbar
                                .make(rltContenedorDatosSolicitudOffer, "Solicitud ingresada exitosamente.", Snackbar.LENGTH_INDEFINITE)
                                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                .setBackgroundTint(resources.getColor(R.color.black))
                                .setActionTextColor(resources.getColor(R.color.purple_500))
                                .setAction("OK"){}
                                .show()
                            lifecycleScope.launch {
                                currentCategoria?.let { it1 ->
                                    clearFormTypedsView()
                                    clearFormValuesView()
                                    offersRegisterViewModel.clearFormViewModel(
                                        it1.uid
                                    )
                                    rltDatosSolicitudOffer.isVisible = false
                                    titleDetallesOffer.isVisible = false
                                    rltDetallesOffer.isVisible = false
                                    btnRegisterOffer.isVisible = false
                                    spinnerCategoriasOffer.text = "Escoja una categoría"
                                }
                            }
                        }else{
                            Snackbar
                                .make(rltContenedorDatosSolicitudOffer, "Error al ingresar las fotografias de la solicitud.", Snackbar.LENGTH_SHORT)
                                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                .setBackgroundTint(resources.getColor(R.color.black))
                                .show()
                        }
                    }else{
                        Snackbar
                            .make(rltContenedorDatosSolicitudOffer, "Error al crear la solicitud.", Snackbar.LENGTH_SHORT)
                            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                            .setBackgroundTint(resources.getColor(R.color.black))
                            .show()
                    }
                }
            }
        }
    }

    fun updateFieldsInterface(categoria: CategoriasData?, isCategoriaSelected: Boolean){

        if(currentCategoria == null){
            currentCategoria = categoria
        }

        if(categoria != null){
            if(currentCategoria?.uid == categoria.uid){
                offersRegisterViewModel.fechaIniFin = categoria.fechaIniFin
                offersRegisterViewModel.masPersonas = categoria.masPersonas
                offersRegisterViewModel.detalleFoto = categoria.detalleFoto

                rltDatosSolicitudOffer.visibility = if(isCategoriaSelected) View.VISIBLE else View.GONE
                titleDetallesOffer.visibility = if(isCategoriaSelected) View.VISIBLE else View.GONE
                rltDetallesOffer.visibility = if(isCategoriaSelected) View.VISIBLE else View.GONE
                btnRegisterOffer.visibility = if(isCategoriaSelected) View.VISIBLE else View.GONE

                rltFografiasRespOffer.visibility = if(categoria.detalleFoto) View.VISIBLE else View.GONE
                rltFechaFinOffer.visibility = if(categoria.fechaIniFin) View.VISIBLE else View.GONE
                fechaInOffer.hint = if(categoria.fechaIniFin) "Fecha Inicio" else "Fecha"
                rltCantidadOffer.visibility = if(categoria.masPersonas) View.VISIBLE else View.GONE
                checkSoloPersona.visibility = if(categoria.masPersonas) View.GONE else View.VISIBLE

                when(checkSoloPersona.visibility){
                    View.VISIBLE ->{
                        checkSoloPersona.isChecked = true
                        checkSoloPersona.isEnabled = false
                    }
                }
            }else{
                clearFormTypedsView()
                lifecycleScope.launch {
                    offersRegisterViewModel.clearFormViewModel(categoria.uid)
                }

                clearFormValuesView()

                offersRegisterViewModel.fechaIniFin = categoria.fechaIniFin
                offersRegisterViewModel.masPersonas = categoria.masPersonas
                offersRegisterViewModel.detalleFoto = categoria.detalleFoto

                rltDatosSolicitudOffer.visibility = if(isCategoriaSelected) View.VISIBLE else View.GONE
                titleDetallesOffer.visibility = if(isCategoriaSelected) View.VISIBLE else View.GONE
                rltDetallesOffer.visibility = if(isCategoriaSelected) View.VISIBLE else View.GONE
                btnRegisterOffer.visibility = if(isCategoriaSelected) View.VISIBLE else View.GONE

                rltFografiasRespOffer.visibility = if(categoria.detalleFoto) View.VISIBLE else View.GONE
                rltFechaFinOffer.visibility = if(categoria.fechaIniFin) View.VISIBLE else View.GONE
                fechaInOffer.hint = if(categoria.fechaIniFin) "Fecha Inicio" else "Fecha"
                rltCantidadOffer.visibility = if(categoria.masPersonas) View.VISIBLE else View.GONE
                checkSoloPersona.visibility = if(categoria.masPersonas) View.GONE else View.VISIBLE

                when(checkSoloPersona.visibility){
                    View.VISIBLE ->{
                        checkSoloPersona.isChecked = true
                        checkSoloPersona.isEnabled = false
                    }
                }
            }
            currentCategoria = categoria
        }else{
            clearFormTypedsView()
            clearFormValuesView()

            rltDatosSolicitudOffer.visibility = if(isCategoriaSelected) View.VISIBLE else View.GONE
            titleDetallesOffer.visibility = if(isCategoriaSelected) View.VISIBLE else View.GONE
            rltDetallesOffer.visibility = if(isCategoriaSelected) View.VISIBLE else View.GONE
            btnRegisterOffer.visibility = if(isCategoriaSelected) View.VISIBLE else View.GONE

            lifecycleScope.launch {
                offersRegisterViewModel.clearFormViewModel("")
            }
        }
    }

    private fun clearFormTypedsView() {
        currentCategoria = null
        isFechaIniTypedOffer = false
        isFechaFinTypedOffer = false
        isDescripcionTypedOffer = false
        isCantidadTypedOffer = false
        isUbicacionTypedOffer = false

        inCorrectFechaIni = false
        inCorrectFechaFin = false
    }

    private fun clearFormValuesView() {
        fechaInOffer.text.clear()
        fechaFinOffer.text.clear()
        checkDeInmediato.isChecked = false
        descripcionTxtOffer.text.clear()
        checkSoloPersona.isChecked = false
        checkSoloPersona.isEnabled = true
        cantidadTxtOffer.text.clear()
        ubicacionTxtOffer.text.clear()
        telefonoTxtOffer.text.clear()

        fotosList.clear()

        fotosList.add("")
        fotosList.add("")
        fotosList.add("")
        fotosList.add("")
        fotosList.add("")

        adapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUESTCAMERA){
            var indice = adapter.getItemIndex()

            if(indice == -1){
                for (i in fotosList.indices){
                    if (fotosList[i].isBlank()){
                        indice = i
                        break
                    }
                }
                fotosList[indice] = adapter.getCurrentPhotoPath()
            }else{
                if(fotosList[indice].isNotBlank()){
                    fotosList[indice] = adapter.getCurrentPhotoPath()
                }
            }

            offersRegisterViewModel.viewModelScope.launch {
                offersRegisterViewModel.setFotosOffer(fotosList.joinToString())
            }
            adapter.notifyDataSetChanged()
        }

        if (resultCode == Activity.RESULT_OK && requestCode == REQUESTGALLERY){
            var indice = adapter.getItemIndex()

            if(indice == -1){
                for (i in fotosList.indices){
                    if (fotosList[i].isBlank()){
                        indice = i
                        break
                    }
                }
                fotosList[indice] = data?.data?.let { getRealPathFromURI(it) }.toString()
            }else{
                if(fotosList[indice].isNotBlank()){
                    fotosList[indice] = data?.data?.let { getRealPathFromURI(it) }.toString()
                }
            }

            offersRegisterViewModel.viewModelScope.launch {
                offersRegisterViewModel.setFotosOffer(fotosList.joinToString())
            }

            adapter.notifyDataSetChanged()
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

    @SuppressLint("SimpleDateFormat")
    private fun showDatePickerDialog(isIni: Boolean) {
        val calendar: Calendar = Calendar.getInstance()

        val fragment = activity?.let { DatePickerFragment(it) }
        if (fragment != null) {
            fragment.setup(calendar) { _, year, month, dayOfMonth ->
                run {
                    val sdf = SimpleDateFormat("yyyy-MM-dd")
                    val calendar = Calendar.getInstance()
                    calendar.set(year, month, dayOfMonth)
                    val sDate = sdf.format(calendar.time)
                    if (isIni) {
                        fechaInOffer.text = Editable.Factory.getInstance().newEditable(sDate)
                    } else {
                        fechaFinOffer.text = Editable.Factory.getInstance().newEditable(sDate)
                    }
                }
            }
            activity?.let { fragment.show(it.supportFragmentManager, null) }
        }
    }

    private fun collectorFlow() {
        lifecycleScope.launch {
            offersRegisterViewModel.getViewCategorias().collect{ value ->
                categoriasViewRepository = value as MutableList<CategoriasData>
                var categoriasAux = arrayListOf<String>()

                value.forEachIndexed { _, b ->
                    if(b.estado)
                        categoriasAux.add(b.nombre)
                }

                categoriasRepository = categoriasAux.toTypedArray()
                selectecCategoria =  BooleanArray(categoriasRepository.size)
            }
            // cancelar el realtime
            this.cancel()
        }

        lifecycleScope.launch {
            offersRegisterViewModel.isFormOfferSucess.collect { value ->
                btnRegisterOffer.isEnabled = value
            }
        }

        lifecycleScope.launch {
            offersRegisterViewModel.isCatetoriasOfferOK.collect { value ->
                if (isCategoriaTypedOffer) {
                    when (value.respuesta) {
                        0 -> {
                            errorCategoriasOffer.isVisible = false
                            errorCategoriasOffer.text = value.mensaje
                            rltCategoriasOffer.background = context?.let {
                                ContextCompat.getDrawable(
                                    it,
                                    R.drawable.background_fields
                                )
                            }
                        }
                        else -> {
                            errorCategoriasOffer.isVisible = true
                            errorCategoriasOffer.text = value.mensaje
                            rltCategoriasOffer.background = context?.let { ContextCompat.getDrawable(
                                it,
                                R.drawable.background_fields_error
                            ) }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            offersRegisterViewModel.isFechaInOfferOK.collect { value ->
                if (isFechaIniTypedOffer) {
                    when (value.respuesta) {
                        0 -> {
                            errorFechasOffer.isVisible = false
                            errorFechasOffer.text = value.mensaje
                            rltFechaInOffer.background = context?.let {
                                ContextCompat.getDrawable(
                                    it,
                                    R.drawable.background_fields
                                )
                            }
                        }
                        else -> {
                            errorFechasOffer.isVisible = true
                            errorFechasOffer.text = value.mensaje
                            inCorrectFechaIni = true
                            fechaInOffer.text = Editable.Factory.getInstance().newEditable("")
                            rltFechaInOffer.background = context?.let { ContextCompat.getDrawable(
                                it,
                                R.drawable.background_fields_error
                            ) }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            offersRegisterViewModel.isFechaFinOfferOK.collect { value ->
                if (isFechaFinTypedOffer) {
                    when (value.respuesta) {
                        0 -> {
                            errorFechasOffer.isVisible = false
                            errorFechasOffer.text = value.mensaje
                            rltFechaFinOffer.background = context?.let {
                                ContextCompat.getDrawable(
                                    it,
                                    R.drawable.background_fields
                                )
                            }
                        }
                        else -> {
                            errorFechasOffer.isVisible = true
                            errorFechasOffer.text = value.mensaje
                            inCorrectFechaFin = true
                            fechaFinOffer.text = Editable.Factory.getInstance().newEditable("")
                            rltFechaFinOffer.background = context?.let { ContextCompat.getDrawable(
                                it,
                                R.drawable.background_fields_error
                            ) }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            offersRegisterViewModel.isDescripcionOfferOK.collect { value ->
                if (isDescripcionTypedOffer) {
                    when (value.respuesta) {
                        0 -> {
                            errorDescripcionOffer.isVisible = false
                            errorDescripcionOffer.text = value.mensaje
                            rltDescripcionOffer.background = context?.let {
                                ContextCompat.getDrawable(
                                    it,
                                    R.drawable.background_fields
                                )
                            }
                        }
                        else -> {
                            errorDescripcionOffer.isVisible = true
                            errorDescripcionOffer.text = value.mensaje
                            rltDescripcionOffer.background = context?.let { ContextCompat.getDrawable(
                                it,
                                R.drawable.background_fields_error
                            ) }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            offersRegisterViewModel.isCantidadOfferOK.collect { value ->
                if (isCantidadTypedOffer) {
                    when (value.respuesta) {
                        0 -> {
                            errorCantidadOffer.isVisible = false
                            errorCantidadOffer.text = value.mensaje
                            rltCantidadOffer.background = context?.let {
                                ContextCompat.getDrawable(
                                    it,
                                    R.drawable.background_fields
                                )
                            }
                        }
                        else -> {
                            errorCantidadOffer.isVisible = true
                            errorCantidadOffer.text = value.mensaje
                            rltCantidadOffer.background = context?.let { ContextCompat.getDrawable(
                                it,
                                R.drawable.background_fields_error
                            ) }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            offersRegisterViewModel.isUbicacionOfferOK.collect { value ->
                if (isUbicacionTypedOffer) {
                    when (value.respuesta) {
                        0 -> {
                            errorUbicacionOffer.isVisible = false
                            errorUbicacionOffer.text = value.mensaje
                            rltUbicacionOffer.background = context?.let {
                                ContextCompat.getDrawable(
                                    it,
                                    R.drawable.background_fields
                                )
                            }
                        }
                        else -> {
                            errorUbicacionOffer.isVisible = true
                            errorUbicacionOffer.text = value.mensaje
                            rltUbicacionOffer.background = context?.let { ContextCompat.getDrawable(
                                it,
                                R.drawable.background_fields_error
                            ) }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch{
            offersRegisterViewModel.isFotosOfferOK.collect{ value ->
                when (value.respuesta) {
                    0 ->{
                        errorFografiasRespOffer.isVisible = false
                        errorFografiasRespOffer.text = value.mensaje
                    }
                    else -> {
                        errorFografiasRespOffer.isVisible = true
                        errorFografiasRespOffer.text = value.mensaje
                    }
                }
            }
        }

        lifecycleScope.launch {
            offersRegisterViewModel.isTelefonoOfferOK.collect { value ->
                when (value.respuesta) {
                    0 -> {
                        errorTelefonoOffer.isVisible = false
                        errorTelefonoOffer.text = value.mensaje
                        rltTelefonoOffer.background = context?.let {
                            ContextCompat.getDrawable(
                                it,
                                R.drawable.background_fields
                            )
                        }
                    }
                    else -> {
                        errorTelefonoOffer.isVisible = true
                        errorTelefonoOffer.text = value.mensaje
                        rltTelefonoOffer.background = context?.let { ContextCompat.getDrawable(
                            it,
                            R.drawable.background_fields_error
                        ) }
                    }
                }

            }
        }

    }

    fun updateImages(postion:Int) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("¿Está seguro que desea eliminar la imagen?")
            .setPositiveButton("Aceptar") { dialog, _ ->
                fotosList[postion] = ""
                adapter.images = fotosList.sortedBy { it.length }.reversed() as ArrayList<String>
                adapter.notifyDataSetChanged()
                fotosList = adapter.images

                offersRegisterViewModel.viewModelScope.launch {
                    offersRegisterViewModel.setFotosOffer(fotosList.joinToString())
                }

                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

        builder.create()
            .show()
    }

    override fun responseMap(geocoder: String) {
        ubicacionTxtOffer.text = Editable.Factory.getInstance().newEditable(geocoder)
    }
}