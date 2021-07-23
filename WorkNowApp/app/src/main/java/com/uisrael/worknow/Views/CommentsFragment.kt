package com.uisrael.worknow.Views

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.uisrael.worknow.Model.Data.ComentariosData
import com.uisrael.worknow.R
import com.uisrael.worknow.ViewModel.CommentsViewModel
import com.uisrael.worknow.Views.Adapters.CommentsOfferListAdapter
import com.uisrael.worknow.Views.Utilities.EventsNetwork
import com.uisrael.worknow.Views.Utilities.Utilitity
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

    private var manager: ConnectivityManager? = null
    private lateinit var eventsNetwork: EventsNetwork

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

        manager = activity?.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager?
        eventsNetwork = context?.let { EventsNetwork(rltOfferSendComments, it) }!!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            manager?.registerDefaultNetworkCallback(eventsNetwork)
        }else {
            manager?.registerNetworkCallback(
                NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build(),
                eventsNetwork
            )
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
                    if(it.size > 0){
                        offerCommentsList.isVisible = true
                        rltErrorListaComentarios.isVisible = false
                        adapterComments.comentarios = it as ArrayList<ComentariosData>
                        adapterComments.notifyDataSetChanged()
                        offerCommentsList.smoothScrollToPosition(adapterComments.count -1)
                        viewModel.setUpdateComment(uidPub,viewModel.usuarioUidData,adapterComments.comentarios)
                    }else{
                        offerCommentsList.isVisible = false
                        rltErrorListaComentarios.isVisible = true
                    }
                }
            }
        }

        btnSendComments.setOnClickListener {
            val internetConnection: Boolean? = activity?.let { it1 -> Utilitity.isNetworkAvailable(it1) }
            if (internetConnection == true && editTxtComments.text?.isNotEmpty() == true){
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

    override fun onDestroy() {
        super.onDestroy()
        manager?.unregisterNetworkCallback(eventsNetwork)
    }

}