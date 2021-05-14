package com.uisrael.worknow.Views.Adapters

import android.content.Context
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.offers_item_adapter.view.*
import com.uisrael.worknow.Model.Data.PublicationsData
import com.uisrael.worknow.R
import com.uisrael.worknow.Views.Dialogs.OfferBottomSheetFragment

class PublicationsListAdapter(
    private var c: Context,
    var publicaciones: ArrayList<PublicationsData>,
    var supportFragmentManager: FragmentManager
): BaseAdapter() {
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
        val convertView: View? = LayoutInflater.from(c).inflate(R.layout.offers_item_adapter, null)

        if (convertView != null) {
            convertView.tituloTxtNumeroOffer.text = "Solicitud ${position + 1} "
            convertView.descripcionTxtOfferList.text = publicaciones[position].descripcion
            convertView.fechaInOfferList.text = Editable.Factory.getInstance().newEditable(publicaciones[position].fechaIni)
            if(publicaciones[position].fechaFin.isNotBlank()){
                convertView.fechaFinOfferList.text = Editable.Factory.getInstance().newEditable(publicaciones[position].fechaFin)
            }else{
                convertView.rltFechaFinOfferList.visibility = View.GONE
                convertView.fechasTituloOfferList.text = "Fecha del trabajo"
            }
            convertView.ubicacionTxtOfferList.text = publicaciones[position].ubicacion
            convertView.inmediatoTxtOfferList.text = if(publicaciones[position].inmediato) "Si" else "No"
            convertView.cantidadTxtOfferList.text = if(publicaciones[position].soloUnaPersona) "Una sola persona" else "${publicaciones[position].cantidad} personas"

            convertView.btnVermasOfferList.setOnClickListener {
                val offerBottomSheetFragment = OfferBottomSheetFragment(c, publicaciones[position])
                offerBottomSheetFragment.show(supportFragmentManager, "ModalBottomOffer")
            }
        }

        return convertView
    }
}