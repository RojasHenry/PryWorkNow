package com.uisrael.worknow.Views.Dialogs

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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
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
    var fromDashboard: Boolean,
    var fromPubAccept: Boolean
) : BottomSheetDialogFragment() {

    private lateinit var viewModel:OfferBottomSheetViewModel
    var jobForCancel : Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setStyle(STYLE_NORMAL, R.style.MyBottomSheetDialogTheme)
        viewModel = ViewModelProvider(this).get(OfferBottomSheetViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val convertView: View? = LayoutInflater.from(c).inflate(R.layout.offer_bottomdialog_fragment, null)
        if (convertView != null) {

            if (fromDashboard){
                convertView.rltClientOfferDataDialog.visibility = View.GONE
            }else{
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
            viewModel.viewModelScope.launch {
                viewModel.getCurrentViewUser(publicationsData.idUsuarioCli).collect {
                    if(it != null){
                        convertView.nombreClientOfferDialog.text = "Solicitante: ${it.nombre} ${it.apellido}"

                        if (it.foto.isNotBlank()){
                            val imageBytes = Base64.decode(it.foto, Base64.DEFAULT)
                            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            convertView.imageClientOfferDialog.setImageBitmap(Utilitity().getRoundedCornerBitmap(decodedImage))
                            convertView.iconClientOfferDialog.visibility = View.GONE
                        }else{
                            convertView.iconClientOfferDialog.text = "${it.nombre.get(0)}${it.apellido.get(0)}"
                        }
                    }
                }
            }

            convertView.descripcionTxtOfferListDialog.text = publicationsData.descripcion
            convertView.fechaInOfferListDialog.text = Editable.Factory.getInstance().newEditable(publicationsData.fechaIni)
            if(publicationsData.fechaFin.isNotBlank()){
                convertView.fechaFinOfferListDialog.text = Editable.Factory.getInstance().newEditable(publicationsData.fechaFin)
            }else{
                convertView.rltFechaFinOfferListDialog.visibility = View.GONE
                convertView.fechasTituloOfferListDialog.text = "Fecha del trabajo"
            }

            convertView.ubicacionTxtOfferListDialog.text = publicationsData.ubicacion

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


            if(fromPubAccept){
                convertView.cardViewButtonsOfferDialog.visibility = View.GONE
            }else{
                convertView.cardViewButtonsOfferDialog.visibility = if (fromDashboard) View.GONE else View.VISIBLE

                if(!fromDashboard){
                    convertView.btnAceptarOfferDialog.setOnClickListener {
                        dismiss()
                    }

                    convertView.btnCancelOfferDialog.setOnClickListener {
                        dismiss()
                    }
                }
            }

        }
        return convertView
    }

    fun paddingPixelDP(paddingDp:Int): Int {
        val density = c.resources.displayMetrics.density
        return (paddingDp * density).toInt()
    }

    override fun dismiss() {
        if (!fromDashboard){
            jobForCancel?.cancel()
        }
        super.dismiss()
    }
}