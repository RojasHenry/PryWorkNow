package com.uisrael.worknow.Views.Dialogs

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        inicializateComponents()
    }

    private fun inicializateComponents() {
        btnEnviarDialogCalif.isEnabled = false
        viewModel.viewModelScope.launch {
            viewModel.getCurrentViewUser(uidProf).collect {
                if(it != null){
                    viewModel.currentProf = it
                    nombreUProfDialogCalif.text = "${it.nombre} ${it.apellido}"
                    if (it.foto.isNotBlank()){
                        val imageBytes = Base64.decode(it.foto, Base64.DEFAULT)
                        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        imageUsuarioDialogCalif.setImageBitmap(Utilitity().getRoundedCornerBitmap(decodedImage))
                        iconUsuarioDialogCalif.visibility = View.GONE
                    }else{
                        iconUsuarioDialogCalif.text = "${it.nombre.get(0)}${it.apellido.get(0)}"
                    }
                }
            }
        }

        ratingBarDialogCalif.setOnRatingBarChangeListener { _, rating, _ ->
            btnEnviarDialogCalif.isEnabled = !rating.equals(0f)
        }

        btnEnviarDialogCalif.setOnClickListener {
            viewModel.viewModelScope.launch {
                val responseQua = viewModel.setQualificationViewOffer(uidPub,ratingBarDialogCalif.rating.toDouble())
                if(responseQua != null){
                    val response = viewModel.setQualificationViewProf(uidPub,uidProf,ratingBarDialogCalif.rating.toDouble())
                    if(response != null){
                        dismiss()
                    }else{
                        Toast.makeText(activity, "Error al calificar al profesional", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(activity, "Error al calificar al profesional", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}