package com.uisrael.worknow.Views.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.uisrael.worknow.Model.Data.PublicationsData
import com.uisrael.worknow.R
import com.uisrael.worknow.ViewModel.HistoryOffersViewModel
import com.uisrael.worknow.Views.Dialogs.OfferBottomSheetFragment
import com.uisrael.worknow.Views.Utilities.Utilitity
import kotlinx.android.synthetic.main.offers_item_historic_adapter.view.*
import kotlinx.coroutines.launch

class OfferHistoricListAdapter(
    private var c: Context,
    var publicaciones: ArrayList<PublicationsData>,
    var supportFragmentManager: FragmentManager,
    val viewModel: HistoryOffersViewModel
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

            val datosCiudad = publicaciones[position].ubicacion.split("%DIR%")
            val nameLocation = datosCiudad[1].replace("address(","").replace(")","")
            convertView.ubicacionTxtHistoricOfferList.text = nameLocation
            convertView.inmediatoTxtHistoricOfferList.text = if(publicaciones[position].inmediato) "Si" else "No"
            convertView.cantidadTxtHistoricOfferList.text = if(publicaciones[position].soloUnaPersona) "Una sola persona" else "${publicaciones[position].cantidad} personas"

            convertView.btnVolverOfferHistoricOfferList.setOnClickListener {
                if(Utilitity.isNetworkAvailable(c)){
                    Utilitity().showDialog(c,"Aviso", "¿Está seguro que desea volver a publicar la  oferta seleccionada.",R.drawable.ic_warning_24)
                        ?.setPositiveButton("Aceptar"){ dialog, _ ->
                            viewModel.viewModelScope.launch {
                                val uid:String = viewModel.registerViewAgainOffer(publicaciones[position]) as String
                                viewModel.registerViewAgainOfferPictures(publicaciones[position].uid,uid)
                                if (parent != null) {
                                    Snackbar
                                        .make(parent, "La oferta solicitada fue nuevamente publicada exitosamente.", Snackbar.LENGTH_SHORT)
                                        .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                        .setBackgroundTint(c.resources.getColor(R.color.black))
                                        .show()
                                }
                            }
                            dialog.dismiss()
                        }
                        ?.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                        ?.show()
                }
            }

            convertView.btnVermasHistoricOfferList.setOnClickListener {
                if(Utilitity.isNetworkAvailable(c)){
                    val offerBottomSheetFragment = OfferBottomSheetFragment(c, publicaciones[position],
                        validateEstadoOffer = false,
                        showUsuarioCli = false,
                        showButtonsAceptCan = false,
                        showButtonsCanSol = false,
                        supportFragmentManager = supportFragmentManager)
                    offerBottomSheetFragment.show(supportFragmentManager, "ModalBottomOffer")
                }
            }
        }
        return convertView
    }
}