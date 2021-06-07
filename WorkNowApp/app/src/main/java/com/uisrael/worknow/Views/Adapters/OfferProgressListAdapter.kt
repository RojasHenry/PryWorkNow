package com.uisrael.worknow.Views.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.uisrael.worknow.Model.Data.PublicationsData
import com.uisrael.worknow.R
import com.uisrael.worknow.ViewModel.TabsFragViewModel.InProgressViewModel
import com.uisrael.worknow.Views.Dialogs.OfferBottomSheetFragment
import com.uisrael.worknow.Views.Dialogs.QualificationFragment
import com.uisrael.worknow.Views.Utilities.Utilitity
import kotlinx.android.synthetic.main.offers_item_progress_adapter.view.*
import kotlinx.coroutines.launch

class OfferProgressListAdapter(
    var inProgressViewModel: InProgressViewModel,
    private var c: Context,
    var publicaciones: ArrayList<PublicationsData>,
    var supportFragmentManager: FragmentManager,
    var isProf: Boolean
) : BaseAdapter() {

    override fun getCount(): Int {
        return publicaciones.size
    }

    override fun getItem(position: Int): Any {
        return publicaciones[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ShowToast")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val convertView: View? = LayoutInflater.from(c).inflate(R.layout.offers_item_progress_adapter, null)

        if (convertView != null) {
            convertView.idTxtProgressOfferList.text = publicaciones[position].uid

            convertView.fechaInProgressOfferList.text = Editable.Factory.getInstance().newEditable(publicaciones[position].fechaIni)
            if(publicaciones[position].fechaFin.isNotBlank()){
                convertView.fechaFinProgressOfferList.text = Editable.Factory.getInstance().newEditable(publicaciones[position].fechaFin)
            }else{
                convertView.rltFechaFinProgressOfferList.visibility = View.GONE
                convertView.fechasTituloProgressOfferList.text = "Fecha del trabajo"
            }

            convertView.descripcionTxtProgressOfferList.text = publicaciones[position].descripcion

            val datosCiudad = publicaciones[position].ubicacion.split("%DIR%")
            val locationView = datosCiudad[0].replace("lat/lng: (","").replace(")","")
            val nameLocation = datosCiudad[1].replace("address(","").replace(")","")

            convertView.ubicacionTxtProgressOfferList.text = "${nameLocation}; (${locationView})"
            convertView.inmediatoTxtProgressOfferList.text = if(publicaciones[position].inmediato) "Si" else "No"
            convertView.cantidadTxtProgressOfferList.text = if(publicaciones[position].soloUnaPersona) "Una sola persona" else "${publicaciones[position].cantidad} personas"


            if (isProf){
                convertView.rltButtonsProgressOfferListCli.visibility = View.GONE
                convertView.rltButtonsProgressOfferListProf.visibility = View.VISIBLE

                when (publicaciones[position].estado){
                    Utilitity.ESTADO_ACEPTADO ->{
                        convertView.btnTerminarProgressOfferList.visibility = View.VISIBLE
                        convertView.btnDeshacerProgressOfferList.visibility = View.GONE
                    }
                    Utilitity.ESTADO_PRO_TERMINADO ->{
                        convertView.btnTerminarProgressOfferList.visibility = View.GONE
                        convertView.btnDeshacerProgressOfferList.visibility = View.VISIBLE
                    }
                }

                convertView.btnDeshacerProgressOfferList.setOnClickListener {
                    inProgressViewModel.viewModelScope.launch {
                        val response = inProgressViewModel.setOfferViewProfUpdateEstado(publicaciones[position].uid,Utilitity.ESTADO_ACEPTADO)
                        if (response != null){
                            if (parent != null) {
                                Snackbar
                                    .make(parent, "Estado de solicitud actualizado exitosamente.", Snackbar.LENGTH_SHORT)
                                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                    .setBackgroundTint(c.resources.getColor(R.color.black))
                                    .show()
                            }
                        }else{
                            if (parent != null) {
                                Snackbar
                                    .make(parent, "Error al deshacer terminar en la solicitud actual.", Snackbar.LENGTH_SHORT)
                                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                    .setBackgroundTint(c.resources.getColor(R.color.black))
                                    .show()
                            }
                        }
                    }
                }

                convertView.btnTerminarProgressOfferList.setOnClickListener {

                    Utilitity().showDialog(c,"Aviso", "Esta seguro que desea 'terminar' la oferta actual?",R.drawable.ic_warning_24)
                        ?.setPositiveButton("Aceptar") { dialog, _ ->
                            inProgressViewModel.viewModelScope.launch {
                                val response = inProgressViewModel.setOfferViewProfUpdateEstado(
                                    publicaciones[position].uid,
                                    Utilitity.ESTADO_PRO_TERMINADO
                                )
                                if (response != null) {
                                    if (parent != null) {
                                        Snackbar
                                            .make(
                                                parent,
                                                "Solicitud terminada exitosamente.",
                                                Snackbar.LENGTH_INDEFINITE
                                            )
                                            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                            .setBackgroundTint(c.resources.getColor(R.color.black))
                                            .setActionTextColor(c.resources.getColor(R.color.purple_500))
                                            .setAction("OK") {}
                                            .show()
                                    }
                                } else {
                                    if (parent != null) {
                                        Snackbar
                                            .make(
                                                parent,
                                                "Error al terminar la solicitud actual.",
                                                Snackbar.LENGTH_SHORT
                                            )
                                            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                            .setBackgroundTint(c.resources.getColor(R.color.black))
                                            .show()
                                    }
                                }
                            }
                            dialog.dismiss()
                        }
                        ?.setNegativeButton("Cancelar") { dialog, which -> dialog.dismiss() }
                        ?.show()

                }
            }else{
                when (publicaciones[position].estado){
                    Utilitity.ESTADO_ACEPTADO ->{
                        convertView.rltButtonsProgressOfferListCli.visibility = View.VISIBLE
                        convertView.rltButtonsFinishProgressOfferListCli.visibility = View.GONE
                    }
                    Utilitity.ESTADO_PRO_TERMINADO ->{
                        convertView.rltButtonsProgressOfferListCli.visibility = View.GONE
                        convertView.rltButtonsFinishProgressOfferListCli.visibility = View.VISIBLE
                    }
                }

                convertView.btnFinishProgressOfferList.setOnClickListener {
                    Utilitity().showDialog(c,"Aviso", "Esta seguro que desea 'finalizar' la oferta actual?",R.drawable.ic_warning_24)
                        ?.setPositiveButton("Aceptar") { dialog, _ ->
                            Utilitity.showLoading(c,"Cargando, por favor espere...",supportFragmentManager)
                            inProgressViewModel.viewModelScope.launch {
                                val response = inProgressViewModel.setOfferViewProfUpdateEstado(publicaciones[position].uid,Utilitity.ESTADO_SOL_TERMINADO)
                                if (response != null){
                                    val dialogCalif = QualificationFragment(publicaciones[position].idAceptadoProf,publicaciones[position].uid)
                                    dialogCalif.show(supportFragmentManager,"dialogQualification")
                                    if (parent != null) {
                                        Snackbar
                                            .make(parent, "Solicitud finalizada exitosamente.", Snackbar.LENGTH_SHORT)
                                            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                            .setBackgroundTint(c.resources.getColor(R.color.black))
                                            .show()
                                    }
                                }else{
                                    if (parent != null) {
                                        Snackbar
                                            .make(parent, "Error al finalizar la solicitud.", Snackbar.LENGTH_SHORT)
                                            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                            .setBackgroundTint(c.resources.getColor(R.color.black))
                                            .show()
                                    }
                                }
                            }
                            dialog.dismiss()
                        }
                        ?.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                        ?.show()

                }

                convertView.btnCancelProgressOfferList.setOnClickListener {
                    Utilitity().showDialog(c,"Aviso", "Esta seguro que desea 'cancelar' la oferta actual?",R.drawable.ic_warning_24)
                        ?.setPositiveButton("Aceptar") { dialog, _ ->
                            inProgressViewModel.viewModelScope.launch {
                                val response = inProgressViewModel.setOfferViewProfUpdateEstado(publicaciones[position].uid,Utilitity.ESTADO_CANCELADO)
                                if (response != null){
                                    if (parent != null) {
                                        Snackbar
                                            .make(parent, "Solicitud cancelada.", Snackbar.LENGTH_INDEFINITE)
                                            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                            .setBackgroundTint(c.resources.getColor(R.color.black))
                                            .setActionTextColor(c.resources.getColor(R.color.purple_500))
                                            .setAction("OK"){}
                                            .show()
                                    }
                                }else{
                                    if (parent != null) {
                                        Snackbar
                                            .make(parent, "Error al cancelar la solicitud.", Snackbar.LENGTH_SHORT)
                                            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                            .setBackgroundTint(c.resources.getColor(R.color.black))
                                            .show()
                                    }
                                }
                            }
                            dialog.dismiss()
                        }
                        ?.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                        ?.show()

                }

                convertView.btnVermasProgressOfferList.setOnClickListener {
                    val offerBottomSheetFragment = OfferBottomSheetFragment(c, publicaciones[position], fromDashboard = true,fromPubAccept = false, fromPubCli = false, supportFragmentManager)
                    offerBottomSheetFragment.show(supportFragmentManager, "ModalBottomOffer")
                }
            }

        }
        return convertView
    }
}