package com.uisrael.worknow.Views.Dialogs

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.uisrael.worknow.R
import com.uisrael.worknow.ViewModel.QualificationViewModel
import com.uisrael.worknow.Views.Utilities.Utilitity
import kotlinx.android.synthetic.main.calification_dialog_fragment.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class QualificationFragment (private var uidProf:String, var uidPub: String) : DialogFragment() {

    private lateinit var viewModel: QualificationViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCancelable = false
        viewModel = ViewModelProvider(this).get(QualificationViewModel::class.java)
        return  inflater.inflate(R.layout.calification_dialog_fragment, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inicializateComponents()
    }

    @SuppressLint("ShowToast")
    private fun inicializateComponents() {
        btnEnviarDialogCalif.isEnabled = false
        viewModel.viewModelScope.launch {
            viewModel.getCurrentViewUser(uidProf).collect {
                if(it != null){
                    viewModel.currentProf = it
                    nombreUProfDialogCalif.text = "${it.nombre} ${it.apellido}"
                    if (it.foto.isNotBlank()){
                        if(Utilitity().isValidUrl(it.foto)){
                            Picasso
                                .get()
                                .load(it.foto)
                                .resize(250, 250)
                                .centerCrop()
                                .into(imageUsuarioDialogCalif)
                        }else{
                            val imageBytes = Base64.decode(it.foto, Base64.DEFAULT)
                            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            imageUsuarioDialogCalif.setImageBitmap(Utilitity().getRoundedCornerBitmap(decodedImage))
                        }
                        iconUsuarioDialogCalif.visibility = View.GONE
                    }else{
                        iconUsuarioDialogCalif.text = "${it.nombre[0]}${it.apellido[0]}"
                    }
                }
            }
        }

        ratingBarDialogCalif.setOnRatingBarChangeListener { _, rating, _ ->
            btnEnviarDialogCalif.isEnabled = !rating.equals(0f)
        }

        btnEnviarDialogCalif.setOnClickListener {
            val internetConnection: Boolean? = activity?.let { it1 -> Utilitity.isNetworkAvailable(it1) }
            if (internetConnection == true){
                viewModel.viewModelScope.launch {
                    val responseQua = viewModel.setQualificationViewOffer(uidPub,ratingBarDialogCalif.rating.toDouble())
                    if(responseQua != null){
                        val response = viewModel.setQualificationViewProf(uidPub,uidProf,ratingBarDialogCalif.rating.toDouble())
                        if(response != null){
                            dismiss()
                        }else{
                            Snackbar
                                .make(constrContenedorDialogCalif, "Error al calificar al profesional.", Snackbar.LENGTH_SHORT)
                                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                .setBackgroundTint(resources.getColor(R.color.black))
                                .show()
                        }
                    }else{
                        Snackbar
                            .make(constrContenedorDialogCalif, "Error al calificar al profesional.", Snackbar.LENGTH_SHORT)
                            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                            .setBackgroundTint(resources.getColor(R.color.black))
                            .show()
                    }
                }
            } else {
                Snackbar
                    .make(constrContenedorDialogCalif, "Sin conexión a internet, revise los ajustes de conexión para continuar.", Snackbar.LENGTH_SHORT)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                    .setAnchorView(view)
                    .setBackgroundTint(resources.getColor(R.color.purple_700))
                    .show()
            }
        }
    }


}