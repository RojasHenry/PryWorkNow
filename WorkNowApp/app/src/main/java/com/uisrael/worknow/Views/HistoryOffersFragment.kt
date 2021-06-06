package com.uisrael.worknow.Views

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.viewModelScope
import com.uisrael.worknow.Model.Data.PublicationsData
import com.uisrael.worknow.R
import com.uisrael.worknow.ViewModel.HistoryOffersViewModel
import com.uisrael.worknow.Views.Adapters.OfferHistoricListAdapter
import com.uisrael.worknow.Views.Utilities.Utilitity
import kotlinx.android.synthetic.main.history_offers_fragment.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HistoryOffersFragment : DialogFragment() {
    private lateinit var adapterHistoric: OfferHistoricListAdapter
    private var listAdapter: ArrayList<PublicationsData> = arrayListOf()
    var jobForCancel : Job? = null

    private lateinit var viewModel: HistoryOffersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.history_offers_fragment, container, false)
    }

    override fun getTheme(): Int {
        return R.style.FullScreenDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(HistoryOffersViewModel::class.java)
        inicializateComponents()
        collectorFlow()
    }

    private fun inicializateComponents() {
        adapterHistoric = context?.let { activity?.let { it1 -> OfferHistoricListAdapter(it,listAdapter, it1.supportFragmentManager) } }!!
        offerListHistoric.adapter = adapterHistoric

        btnToolbarListHistoric.setOnClickListener {
            dismiss()
        }
    }

    private fun collectorFlow() {
        jobForCancel = viewModel.viewModelScope.launch {
            viewModel.getUserViewLogged().observe(viewLifecycleOwner,{
                viewModel.viewModelScope.launch {
                    viewModel.getOffersViewPorStatusAndCli(it.uid, Utilitity.ESTADO_SOL_TERMINADO).collect {
                        progressListHistoric.isVisible = false
                        if(it.size > 0){
                            adapterHistoric.publicaciones = it as ArrayList<PublicationsData>
                            adapterHistoric.notifyDataSetChanged()
                            jobForCancel?.cancel()
                            rltErrorListHistoric.isVisible = false
                            offerListHistoric.isVisible = true
                        }else{
                            rltErrorListHistoric.isVisible = true
                            offerListHistoric.isVisible = false
                        }
                    }
                }
            })
        }
    }

}