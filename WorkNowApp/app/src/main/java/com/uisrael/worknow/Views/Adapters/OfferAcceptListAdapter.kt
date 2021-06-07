package com.uisrael.worknow.Views.Adapters

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.viewModelScope
import com.uisrael.worknow.Model.Data.PublicationsData
import com.uisrael.worknow.R
import com.uisrael.worknow.ViewModel.TabsFragViewModel.DashboardViewModel
import com.uisrael.worknow.Views.CommentsActivity
import com.uisrael.worknow.Views.Dialogs.OfferBottomSheetFragment
import com.uisrael.worknow.Views.Utilities.Utilitity
import kotlinx.android.synthetic.main.offers_item_accept_adapter.view.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class OfferAcceptListAdapter(
    private var c: Context,
    var publicaciones: ArrayList<PublicationsData>,
    var supportFragmentManager: FragmentManager,
    val activity: FragmentActivity,
    val dashboardViewModel: DashboardViewModel
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

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val convertView: View? = LayoutInflater.from(c).inflate(R.layout.offers_item_accept_adapter, null)

        if (convertView != null) {
            convertView.idTxtAcceptOfferList.text = publicaciones[position].uid

            convertView.fechaInAcceptOfferList.text = Editable.Factory.getInstance().newEditable(publicaciones[position].fechaIni)
            if(publicaciones[position].fechaFin.isNotBlank()){
                convertView.fechaFinAcceptOfferList.text = Editable.Factory.getInstance().newEditable(publicaciones[position].fechaFin)
            }else{
                convertView.rltFechaFinAcceptOfferList.visibility = View.GONE
                convertView.fechasTituloAcceptOfferList.text = "Fecha del trabajo"
            }

            convertView.descripcionTxtAcceptOfferList.text = publicaciones[position].descripcion

            val datosCiudad = publicaciones[position].ubicacion.split("%DIR%")
            val locationView = datosCiudad[0].replace("lat/lng: (","").replace(")","")
            val nameLocation = datosCiudad[1].replace("address(","").replace(")","")

            convertView.ubicacionTxtAcceptOfferList.text = "${nameLocation}; (${locationView})"
            convertView.inmediatoTxtAcceptOfferList.text = if(publicaciones[position].inmediato) "Si" else "No"
            convertView.cantidadTxtAcceptOfferList.text = if(publicaciones[position].soloUnaPersona) "Una sola persona" else "${publicaciones[position].cantidad} personas"


            when (publicaciones[position].estado){
                Utilitity.ESTADO_ACEPTADO->{
                    convertView.btnVerComentsAcceptOfferList.visibility = View.VISIBLE
                    convertView.estadoRefAcceptOfferList.visibility = View.GONE
                    convertView.btnVerComentsAcceptOfferList.setOnClickListener {
                        val intent = Intent(c, CommentsActivity::class.java).apply {
                            putExtra("uidPub", publicaciones[position].uid)
                            putExtra("uidUserAcceptProf", publicaciones[position].idAceptadoProf)
                            putExtra("uidSolClient",  publicaciones[position].idUsuarioCli)
                        }
                        activity.startActivity(intent)
                        activity.overridePendingTransition(R.anim.anim_left_toright,
                            R.anim.anim_right_toleft)
                    }

                    dashboardViewModel.viewModelScope.launch {
                        dashboardViewModel.isNewViewComments(publicaciones[position].uid,dashboardViewModel.currentUser.uid).collect {
                            convertView.dotRedNewComments.isVisible = it.size > 0
                        }
                    }
                }

                Utilitity.ESTADO_PRO_TERMINADO->{
                    convertView.btnVerComentsAcceptOfferList.visibility = View.GONE
                    convertView.estadoRefAcceptOfferList.visibility = View.VISIBLE
                    convertView.estadoRefAcceptOfferList.text = "En finalización"
                }
            }

            convertView.btnVermasAcceptOfferList.setOnClickListener {
                val offerBottomSheetFragment = OfferBottomSheetFragment(c, publicaciones[position], fromDashboard = false,fromPubAccept = true, fromPubCli = false, supportFragmentManager)
                offerBottomSheetFragment.show(supportFragmentManager, "ModalBottomOffer")
            }

        }
        return convertView
    }
}