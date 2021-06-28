package com.uisrael.worknow.Views.Dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.util.Base64
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.uisrael.worknow.Model.Data.PublicationsData
import com.uisrael.worknow.R
import com.uisrael.worknow.ViewModel.OfferBottomSheetViewModel
import com.uisrael.worknow.Views.Utilities.Utilitity
import kotlinx.android.synthetic.main.image_fullscreen_preview.*
import kotlinx.android.synthetic.main.offer_bottomdialog_fragment.view.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class OfferBottomSheetFragment(
    var c: Context,
    var publicationsData: PublicationsData,
    var validateEstadoOffer: Boolean,
    var showUsuarioCli: Boolean,
    var showButtonsAceptCan :Boolean,
    var showButtonsCanSol :Boolean,
    val supportFragmentManager:FragmentManager
) : BottomSheetDialogFragment() {

    private lateinit var viewModel:OfferBottomSheetViewModel
    var jobForCancel : Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setStyle(STYLE_NORMAL, R.style.MyBottomSheetDialogTheme)
        viewModel = ViewModelProvider(this).get(OfferBottomSheetViewModel::class.java)
    }

    @SuppressLint("ShowToast")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val convertView: View? = LayoutInflater.from(c).inflate(R.layout.offer_bottomdialog_fragment, null)
        if (convertView != null) {

            if(validateEstadoOffer){
                jobForCancel = viewModel.viewModelScope.launch {
                    viewModel.getEstadoViewCurrentOffer(publicationsData.uid).collect {
                        if(it?.estado.equals(Utilitity.ESTADO_ACEPTADO)){
                            if (isVisible){
                                dismiss()
                            }
                        }
                    }
                }
            }

            if(showUsuarioCli){
                viewModel.viewModelScope.launch {
                    viewModel.getCurrentViewUser(publicationsData.idUsuarioCli).collect {
                        if(it != null){
                            convertView.nombreClientOfferDialog.text = "Solicitante: ${it.nombre} ${it.apellido}"
                            if (it.foto.isNotBlank()){
                                if(Utilitity().isValidUrl(it.foto)){
                                    Picasso
                                        .get()
                                        .load(it.foto)
                                        .resize(250, 250)
                                        .centerCrop()
                                        .into(convertView.imageClientOfferDialog)
                                }else{
                                    val imageBytes = Base64.decode(it.foto, Base64.DEFAULT)
                                    val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                                    convertView.imageClientOfferDialog.setImageBitmap(Utilitity().getRoundedCornerBitmap(decodedImage))
                                }
                                convertView.iconClientOfferDialog.visibility = View.GONE
                            }else{
                                convertView.iconClientOfferDialog.text = "${it.nombre.get(0)}${it.apellido.get(0)}"
                            }
                        }
                    }
                }
            }else{
                convertView.rltClientOfferDataDialog.visibility = View.GONE
            }

            convertView.descripcionTxtOfferListDialog.text = publicationsData.descripcion
            convertView.fechaInOfferListDialog.text = Editable.Factory.getInstance().newEditable(publicationsData.fechaIni)
            if(publicationsData.fechaFin.isNotBlank()){
                convertView.fechaFinOfferListDialog.text = Editable.Factory.getInstance().newEditable(publicationsData.fechaFin)
            }else{
                convertView.rltFechaFinOfferListDialog.visibility = View.GONE
                convertView.fechasTituloOfferListDialog.text = "Fecha del trabajo"
            }

            val datosCiudad = publicationsData.ubicacion.split("%DIR%")
            val locationView = datosCiudad[0].replace("lat/lng: (","").replace(")","")
            val nameLocation = datosCiudad[1].replace("address(","").replace(")","")

            convertView.ubicacionTxtOfferListDialog.text = nameLocation

            convertView.ubicacionTxtOfferListDialog.setOnClickListener {
                val internetConnection: Boolean? = activity?.let { it1 -> Utilitity.isNetworkAvailable(it1) }
                if (internetConnection == true){
                    MapCityFragment(c, null,true,
                        LatLng(locationView.split(",")[0].toDouble(),locationView.split(",")[1].toDouble()),nameLocation)
                        .show(supportFragmentManager,"mapcityfragment")
                }
            }

            viewModel.viewModelScope.launch {
                viewModel.getCategoriaViewOfferName(publicationsData.idCategoria).collect {
                    if (it != null) {
                        convertView.categoriaTxtOfferListDialog.text = it.nombre
                    }
                }
            }

            convertView.inmediatoTxtOfferListDialog.text = if(publicationsData.inmediato) "Si" else "No"
            convertView.cantidadTxtOfferListDialog.text = if(publicationsData.soloUnaPersona) "Una sola persona" else "${publicationsData.cantidad} personas"

            convertView.rltRespFotografOfferListDialog.visibility = if(publicationsData.tieneFotos) View.VISIBLE else View.GONE
            if(publicationsData.tieneFotos){
                viewModel.viewModelScope.launch {
                    viewModel.getOffersViewRespFoto(publicationsData.uid).collect {
                        if (it != null) {
                            convertView.respFotografImagesOfferListDialog.removeAllViews()
                            for (foto in it.fotos){
                                val image = ImageView(c)
                                val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                                    paddingPixelDP(100),
                                    paddingPixelDP(100)
                                )
                                image.layoutParams = params
                                image.setPadding(paddingPixelDP(10),paddingPixelDP(10),
                                    paddingPixelDP(10),paddingPixelDP(10))

                                val imageBytes = Base64.decode(foto, Base64.DEFAULT)
                                val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                                image.setImageBitmap(decodedImage)

                                image.setOnClickListener {
                                    val nagDialog = Dialog(c,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
                                    nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                                    nagDialog.setContentView(R.layout.image_fullscreen_preview)
                                    nagDialog.imagePreview.setImageBitmap(decodedImage)
                                    nagDialog.show()
                                }
                                convertView.respFotografImagesOfferListDialog.addView(image)
                            }
                        }
                    }
                }
            }

            if(publicationsData.estado == Utilitity.ESTADO_PUBLICADO){
                convertView.cardViewButtonCancelOfferDialog.visibility = if (showButtonsCanSol) View.VISIBLE else View.GONE
                convertView.btnCancelOffer.setOnClickListener {
                    val internetConnection: Boolean? = activity?.let { it1 -> Utilitity.isNetworkAvailable(it1) }
                    if (internetConnection == true){
                        Utilitity().showDialog(c,"Aviso", "¿Está seguro que desea cancelar la oferta actual?",R.drawable.ic_warning_24)
                            ?.setPositiveButton("Aceptar") { dialog, _ ->
                                viewModel.getUidProfesional().observe(viewLifecycleOwner, {
                                    viewModel.viewModelScope.launch {
                                        val response = viewModel.setOfferViewUpdateEstado(publicationsData.uid,Utilitity.ESTADO_CANCELADO)
                                        if (response != null){
                                            dismiss()
                                            Snackbar
                                                .make(convertView,  "Solicitud cancelada.", Snackbar.LENGTH_INDEFINITE)
                                                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                                .setBackgroundTint(c.resources.getColor(R.color.black))
                                                .setActionTextColor(c.resources.getColor(R.color.purple_500))
                                                .setAction("OK"){}
                                                .show()
                                        }else{
                                            Snackbar
                                                .make(convertView, "Error al cancelar la solicitud.", Snackbar.LENGTH_SHORT)
                                                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                                .setBackgroundTint(c.resources.getColor(R.color.black))
                                                .show()
                                        }
                                    }

                                })
                                dialog.dismiss()
                            }
                            ?.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                            ?.show()
                    }
                }
            }

            if(showButtonsAceptCan){
                convertView.btnAceptarOfferDialog.setOnClickListener {
                    val internetConnection: Boolean? = activity?.let { it1 -> Utilitity.isNetworkAvailable(it1) }
                    if (internetConnection == true){
                        viewModel.getUidProfesional().observe(viewLifecycleOwner, {
                            viewModel.viewModelScope.launch {
                                Utilitity.showLoading(c,"Cargando, por favor espere...",supportFragmentManager)
                                val response = viewModel.setOfferViewAcceptProf(it.uid,publicationsData.uid,Utilitity.ESTADO_ACEPTADO)
                                if (response != null){
                                    dismiss()
                                }else{
                                    Snackbar
                                        .make(convertView, "Error al aceptar la solicitud actual.", Snackbar.LENGTH_SHORT)
                                        .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                        .setBackgroundTint(c.resources.getColor(R.color.black))
                                        .show()
                                }
                            }

                        })
                    }
                }

                convertView.btnCancelOfferDialog.setOnClickListener {
                    dismiss()
                }
            }else{
                convertView.cardViewButtonsOfferDialog.visibility = View.GONE
            }
        }
        return convertView
    }

    fun paddingPixelDP(paddingDp:Int): Int {
        val density = c.resources.displayMetrics.density
        return (paddingDp * density).toInt()
    }

    override fun dismiss() {
        if (validateEstadoOffer){
            jobForCancel?.cancel()
        }
        super.dismiss()
    }
}