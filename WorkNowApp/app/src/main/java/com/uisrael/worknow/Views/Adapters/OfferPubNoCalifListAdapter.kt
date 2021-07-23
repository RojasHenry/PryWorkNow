package com.uisrael.worknow.Views.Adapters

import android.content.Context
import android.content.Intent
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
import com.uisrael.worknow.Views.Dialogs.QualificationFragment
import com.uisrael.worknow.Views.Utilities.Utilitity
import kotlinx.android.synthetic.main.offers_item_publish_nocalif_adapter.view.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class OfferPubNoCalifListAdapter(
    private var c: Context,
    var publicaciones: ArrayList<PublicationsData>,
    var supportFragmentManager: FragmentManager,
    var isPubliOffer: Boolean,
    val requireActivity: FragmentActivity?,
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
        val convertView: View? = LayoutInflater.from(c).inflate(R.layout.offers_item_publish_nocalif_adapter, null)

        if (convertView != null) {
            convertView.idTxtOfferPubliNoCalif.text = publicaciones[position].uid
            convertView.descripcionTxtOfferPubliNoCalif.text = publicaciones[position].descripcion
            convertView.ubicacionTxtOfferPubliNoCalif.text = publicaciones[position].ubicacion
            convertView.inmediatoTxtOfferPubliNoCalif.text = if(publicaciones[position].inmediato) "Si" else "No"
            convertView.cantidadTxtOfferPubliNoCalif.text = if(publicaciones[position].soloUnaPersona) "Una sola persona" else "${publicaciones[position].cantidad} personas"

            if(isPubliOffer){
                when (publicaciones[position].estado){
                    Utilitity.ESTADO_ACEPTADO ->{
                        convertView.btnComentsOfferPubliNoCalif.visibility = View.VISIBLE
                        convertView.estadoRefOfferPubliNoCalif.visibility = View.GONE

                        convertView.btnComentsOfferPubliNoCalif.setOnClickListener {
                            if(Utilitity.isNetworkAvailable(c)){
                                when {
                                    requireActivity != null ->{
                                        val intent = Intent(c, CommentsActivity::class.java).apply {
                                            putExtra("uidPub", publicaciones[position].uid)
                                            putExtra("uidUserAcceptProf", publicaciones[position].idAceptadoProf)
                                            putExtra("uidSolClient",  publicaciones[position].idUsuarioCli)
                                        }
                                        requireActivity.startActivity(intent)
                                        requireActivity.overridePendingTransition(R.anim.anim_left_toright  ,
                                            R.anim.anim_right_toleft)
                                    }
                                }
                            }
                        }

                        dashboardViewModel.viewModelScope.launch {
                            dashboardViewModel.isNewViewComments(publicaciones[position].uid,dashboardViewModel.currentUser.uid).collect {
                                convertView.dotRedNewComments.isVisible = it.size > 0
                            }
                        }
                    }

                    Utilitity.ESTADO_PUBLICADO ->{
                        convertView.btnComentsOfferPubliNoCalif.visibility = View.GONE
                        convertView.estadoRefOfferPubliNoCalif.visibility = View.VISIBLE
                        convertView.estadoRefOfferPubliNoCalif.text = "Oferta Sin Aceptar"
                    }

                    Utilitity.ESTADO_PRO_TERMINADO ->{
                        convertView.btnComentsOfferPubliNoCalif.visibility = View.GONE
                        convertView.estadoRefOfferPubliNoCalif.visibility = View.VISIBLE
                        convertView.estadoRefOfferPubliNoCalif.text = "Por finalizar"
                    }
                }

                convertView.btnVermasOfferPubliNoCalif.setOnClickListener {
                    if(Utilitity.isNetworkAvailable(c)){
                        val offerBottomSheetFragment = OfferBottomSheetFragment(c, publicaciones[position],
                            validateEstadoOffer = false,
                            showUsuarioCli = false,
                            showButtonsAceptCan = false,
                            showButtonsCanSol = true,
                            supportFragmentManager =  supportFragmentManager)
                        offerBottomSheetFragment.show(supportFragmentManager, "ModalBottomOffer")
                    }
                }

            }else{
                convertView.imageOfferPubliNoCalif.setImageResource(R.drawable.ic_rate_review_24)
                convertView.rltIdOfferPubliNoCalif.visibility = View.GONE
                convertView.rltComentsVermasButtons.visibility = View.GONE
                convertView.rltCalificarButton.visibility = View.VISIBLE
                convertView.btnCalificarOfferPubliNoCalif.setOnClickListener {
                    if(Utilitity.isNetworkAvailable(c)){
                        val dialogCalif = QualificationFragment(publicaciones[position].idAceptadoProf,publicaciones[position].uid)
                        dialogCalif.show(supportFragmentManager,"dialogQualification")
                    }
                }
            }
        }
        return convertView
    }
}