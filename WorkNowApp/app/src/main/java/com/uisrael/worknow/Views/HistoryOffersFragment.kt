package com.uisrael.worknow.Views

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class HistoryOffersFragment : Fragment() {
    private lateinit var adapterHistoric: OfferHistoricListAdapter
    private var listAdapter: ArrayList<PublicationsData> = arrayListOf()
    var jobForCancel : Job? = null
    companion object {
        fun newInstance() = HistoryOffersFragment()
    }

    private lateinit var viewModel: HistoryOffersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.history_offers_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HistoryOffersViewModel::class.java)
        inicializateComponents()
        collectorFlow()
    }

    private fun inicializateComponents() {
        adapterHistoric = context?.let { activity?.let { it1 -> OfferHistoricListAdapter(it,listAdapter, it1.supportFragmentManager) } }!!
        offerListHistoric.adapter = adapterHistoric
    }

    private fun collectorFlow() {
        jobForCancel = viewModel.viewModelScope.launch {
            viewModel.getUserViewLogged().observe(viewLifecycleOwner,{
                viewModel.viewModelScope.launch {
                    viewModel.getOffersViewPorStatusAndCli(it.uid, Utilitity.ESTADO_SOL_TERMINADO).collect {
                        adapterHistoric.publicaciones = it as ArrayList<PublicationsData>
                        adapterHistoric.notifyDataSetChanged()
                        jobForCancel?.cancel()
                    }
                }
            })
        }
    }

}