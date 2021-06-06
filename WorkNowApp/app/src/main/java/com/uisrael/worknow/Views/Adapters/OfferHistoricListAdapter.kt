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
import kotlinx.android.synthetic.main.offers_item_historic_adapter.view.*

class OfferHistoricListAdapter (
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
        val convertView: View? = LayoutInflater.from(c).inflate(R.layout.offers_item_historic_adapter, null)

        if (convertView != null) {
            convertView.idTxtHistoricOfferList.text = publicaciones[position].uid

            convertView.fechaInHistoricOfferList.text = Editable.Factory.getInstance().newEditable(publicaciones[position].fechaIni)
            if(publicaciones[position].fechaFin.isNotBlank()){
                convertView.fechaFinHistoricOfferList.text = Editable.Factory.getInstance().newEditable(publicaciones[position].fechaFin)
            }else{
                convertView.rltFechaFinHistoricOfferList.visibility = View.GONE
                convertView.fechasTituloHistoricOfferList.text = "Fecha del trabajo"
            }

            convertView.descripcionTxtHistoricOfferList.text = publicaciones[position].descripcion
            convertView.ubicacionTxtHistoricOfferList.text = publicaciones[position].ubicacion
            convertView.inmediatoTxtHistoricOfferList.text = if(publicaciones[position].inmediato) "Si" else "No"
            convertView.cantidadTxtHistoricOfferList.text = if(publicaciones[position].soloUnaPersona) "Una sola persona" else "${publicaciones[position].cantidad} personas"

            convertView.btnVolverOfferHistoricOfferList.setOnClickListener {

            }

            convertView.btnVermasHistoricOfferList.setOnClickListener {
                val offerBottomSheetFragment = OfferBottomSheetFragment(c, publicaciones[position], fromDashboard = true,fromPubAccept = false, fromPubCli = false, supportFragmentManager)
                offerBottomSheetFragment.show(supportFragmentManager, "ModalBottomOffer")
            }
        }
        return convertView
    }
}