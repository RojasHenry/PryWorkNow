package com.uisrael.worknow.Views.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.fragment.app.FragmentManager
import com.uisrael.worknow.Model.Data.PublicationsData
import com.uisrael.worknow.R
import com.uisrael.worknow.Views.Dialogs.OfferBottomSheetFragment
import com.uisrael.worknow.Views.Utilities.Utilitity
import kotlinx.android.synthetic.main.offers_item_publish_nocalif_adapter.view.*

class OfferPubNoCalifListAdapter (
    private var c: Context,
    var publicaciones: ArrayList<PublicationsData>,
    var supportFragmentManager: FragmentManager,
    var isPubliOffer:Boolean
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
                    val offerBottomSheetFragment = OfferBottomSheetFragment(c, publicaciones[position], fromDashboard = true,
                        fromPubAccept = false, fromPubCli = true)
                    offerBottomSheetFragment.show(supportFragmentManager, "ModalBottomOffer")
                }

            }else{
                convertView.imageOfferPubliNoCalif.setImageResource(R.drawable.ic_rate_review_24)
                convertView.rltIdOfferPubliNoCalif.visibility = View.GONE
                convertView.rltComentsVermasButtons.visibility = View.GONE
                convertView.rltCalificarButton.visibility = View.VISIBLE
                convertView.btnCalificarOfferPubliNoCalif.setOnClickListener {

                }
            }
        }
        return convertView
    }
}