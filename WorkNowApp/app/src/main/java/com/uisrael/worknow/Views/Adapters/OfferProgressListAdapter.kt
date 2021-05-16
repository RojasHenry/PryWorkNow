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
import com.uisrael.worknow.Views.Utilities.Utilitity
import kotlinx.android.synthetic.main.offers_item_progress_adapter.view.*

class OfferProgressListAdapter (
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
            convertView.ubicacionTxtProgressOfferList.text = publicaciones[position].ubicacion
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

                }

                convertView.btnTerminarProgressOfferList.setOnClickListener {

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

                }

                convertView.btnVerCancelProgressOfferList.setOnClickListener {

                }

                convertView.btnVermasProgressOfferList.setOnClickListener {
                    val offerBottomSheetFragment = OfferBottomSheetFragment(c, publicaciones[position], fromDashboard = true,fromPubAccept = false)
                    offerBottomSheetFragment.show(supportFragmentManager, "ModalBottomOffer")
                }
            }




        }
        return convertView
    }
}