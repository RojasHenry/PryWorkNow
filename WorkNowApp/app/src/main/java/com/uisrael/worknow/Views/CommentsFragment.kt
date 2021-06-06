package com.uisrael.worknow.Views

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.uisrael.worknow.Model.Data.ComentariosData
import com.uisrael.worknow.R
import com.uisrael.worknow.ViewModel.CommentsViewModel
import com.uisrael.worknow.Views.Adapters.CommentsOfferListAdapter
import kotlinx.android.synthetic.main.fragment_comments.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CommentsFragment (
    val uidPub: String?,
    private val uidUserAcceptProf: String?,
    private val uidSolClient: String?,
    private var commentsActivity: CommentsActivity) : Fragment() {

    var isMensajeTypedComment : Boolean = false
    private lateinit var adapterComments: CommentsOfferListAdapter
    private var listAdapter: ArrayList<ComentariosData> = arrayListOf()

    companion object {
        fun newInstance(
            uidPub: String?,
            uidUserAcceptProf: String?,
            uidSolClient: String?,
            commentsActivity: CommentsActivity
        ) = CommentsFragment(uidPub,uidUserAcceptProf,uidSolClient, commentsActivity)
    }

    private lateinit var viewModel: CommentsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_comments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(CommentsViewModel::class.java)

        viewModel.viewModelScope.launch {
            viewModel.getViewUserLogged().observe(viewLifecycleOwner, Observer { userFire ->
                viewModel.viewModelScope.launch {
                    viewModel.getCurrentUser(userFire.uid, true).collect {
                        if (it != null) {
                            viewModel.usuarioCurrentData = it
                            viewModel.usuarioUidData = userFire.uid
                            inicializarListeners()
                            collectorFlow()
                        }
                    }
                }
            })
        }
    }

    @SuppressLint("ShowToast")
    private fun inicializarListeners() {
        solicitudToolbarComments.text = "Solicitud: ${uidPub}"

        btnToolbarComments.setOnClickListener {
            commentsActivity.onBackPressed()
        }
        adapterComments = context?.let { CommentsOfferListAdapter(it,listAdapter,viewModel.usuarioUidData) }!!
        offerCommentsList.adapter = adapterComments

        viewModel.viewModelScope.launch {
            if (uidPub != null) {
                viewModel.getCommentsOffer(uidPub).collect {
                    adapterComments.comentarios = it as ArrayList<ComentariosData>
                    adapterComments.notifyDataSetChanged()
                    offerCommentsList.smoothScrollToPosition(adapterComments.count -1)
                    viewModel.setUpdateComment(uidPub,viewModel.usuarioUidData,adapterComments.comentarios)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isComentarioMensajeOk.collect { value ->
                if (isMensajeTypedComment) {
                    when (value.respuesta) {
                        0 -> {

                        }
                        else -> {

                        }
                    }
                }
            }
        }

        btnSendComments.setOnClickListener {
            if(editTxtComments.text?.isNotEmpty() == true){
                viewModel.viewModelScope.launch {
                    when(viewModel.usuarioUidData){
                        uidSolClient ->{
                            if (uidPub != null && uidUserAcceptProf != null) {
                                val response = viewModel.sendViewCommentOffer(uidPub, uidSolClient, uidUserAcceptProf)
                                if (response == null){
                                    Snackbar
                                        .make(rltOfferComments, "Error al enviar el comentario.", Snackbar.LENGTH_SHORT)
                                        .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                        .setBackgroundTint(resources.getColor(R.color.black))
                                        .show()
                                }else{
                                    editTxtComments.text!!.clear()
                                    hideKeyboard()
                                }
                            }
                        }
                        uidUserAcceptProf ->{
                            if (uidPub != null && uidSolClient != null) {
                                val response = viewModel.sendViewCommentOffer(uidPub, uidUserAcceptProf, uidSolClient)
                                if (response == null){
                                    Snackbar
                                        .make(rltOfferComments, "Error al enviar el comentario.", Snackbar.LENGTH_SHORT)
                                        .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                        .setBackgroundTint(resources.getColor(R.color.black))
                                        .show()
                                }else{
                                    editTxtComments.text!!.clear()
                                    hideKeyboard()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun collectorFlow() {
        editTxtComments.addTextChangedListener {
            if(!isMensajeTypedComment){
                if (it.toString().isNotEmpty()){
                    isMensajeTypedComment = true
                    viewModel.viewModelScope.launch {
                        viewModel.setComentarioMensaje(it.toString().trim())
                    }
                }
            }else{
                viewModel.viewModelScope.launch {
                    viewModel.setComentarioMensaje(it.toString().trim())
                }
            }
        }
    }

}