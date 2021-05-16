package com.uisrael.worknow.Views.Adapters

import android.content.Context
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.fragment.app.FragmentManager
import com.uisrael.worknow.Model.Data.PublicationsData
import com.uisrael.worknow.R
import com.uisrael.worknow.Views.Dialogs.OfferBottomSheetFragment
import kotlinx.android.synthetic.main.offers_item_accept_adapter.view.*

class OfferAcceptListAdapter (
    private var c: Context,
    var publicaciones: ArrayList<PublicationsData>,
    var supportFragmentManager: FragmentManager
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
            convertView.ubicacionTxtAcceptOfferList.text = publicaciones[position].ubicacion
            convertView.inmediatoTxtAcceptOfferList.text = if(publicaciones[position].inmediato) "Si" else "No"
            convertView.cantidadTxtAcceptOfferList.text = if(publicaciones[position].soloUnaPersona) "Una sola persona" else "${publicaciones[position].cantidad} personas"

            convertView.btnVerComentsAcceptOfferList.setOnClickListener {

            }

            convertView.btnVermasAcceptOfferList.setOnClickListener {
                val offerBottomSheetFragment = OfferBottomSheetFragment(c, publicaciones[position], fromDashboard = false,fromPubAccept = true)
                offerBottomSheetFragment.show(supportFragmentManager, "ModalBottomOffer")
            }

        }
        return convertView
    }
}