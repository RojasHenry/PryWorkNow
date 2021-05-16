package com.uisrael.worknow.Views.TabsFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.uisrael.worknow.Model.Data.PublicationsData
import com.uisrael.worknow.R
import com.uisrael.worknow.ViewModel.TabsFragViewModel.DashboardViewModel
import com.uisrael.worknow.Views.Adapters.OfferAcceptListAdapter
import com.uisrael.worknow.Views.Adapters.OfferPubNoCalifListAdapter
import com.uisrael.worknow.Views.Adapters.PublicationsListAdapter
import com.uisrael.worknow.Views.Utilities.Utilitity
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DashboardFragment(var isProf: Boolean) : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var adapterPublic: OfferPubNoCalifListAdapter
    private lateinit var adapterNoCalif: OfferPubNoCalifListAdapter
    private lateinit var adapterAccepted: OfferAcceptListAdapter
    private var listAdapter: ArrayList<PublicationsData> = arrayListOf()

    companion object {
        fun newInstance(isProf: Boolean) = DashboardFragment(isProf)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        inicializateComponents()
        collectorFlow()
    }

    private fun inicializateComponents() {
        if (isProf){
            rltDaschboardCli.visibility = View.GONE
            rltDaschboardProf.visibility = View.VISIBLE
            adapterAccepted = context?.let { activity?.let { it1 -> OfferAcceptListAdapter(it, listAdapter, it1.supportFragmentManager) } }!!
            listOffersAccept.adapter = adapterAccepted
        }else{
            adapterPublic = context?.let { activity?.let { it1 -> OfferPubNoCalifListAdapter(it, listAdapter, it1.supportFragmentManager,true) } }!!
            adapterNoCalif = context?.let { activity?.let { it1 -> OfferPubNoCalifListAdapter(it, listAdapter, it1.supportFragmentManager,false) } }!!

            listOffersPublic.adapter = adapterPublic
            listOffersNoCalif.adapter = adapterNoCalif
        }
    }

    private fun collectorFlow() {
        if(isProf){
            dashboardViewModel.viewModelScope.launch {
                dashboardViewModel.getUserViewLogged().observe(viewLifecycleOwner,{
                    dashboardViewModel.viewModelScope.launch {
                        dashboardViewModel.getOfferViewAccepted(it.uid,Utilitity.ESTADO_ACEPTADO).collect {
                            adapterAccepted.publicaciones = it as ArrayList<PublicationsData>
                            adapterAccepted.notifyDataSetChanged()
                        }
                    }
                })
            }
        }else{
            dashboardViewModel.viewModelScope.launch {
                dashboardViewModel.getUserViewLogged().observe(viewLifecycleOwner,{
                    dashboardViewModel.viewModelScope.launch {
                        dashboardViewModel.getOffersViewPorStatusAndCli(it.uid,Utilitity.ESTADO_PUBLICADO).collect {
                            adapterPublic.publicaciones = it as ArrayList<PublicationsData>
                            adapterPublic.notifyDataSetChanged()
                        }
                    }

                    dashboardViewModel.viewModelScope.launch {
                        dashboardViewModel.getOffersViewNoCalifCli(it.uid,Utilitity.ESTADO_SOL_TERMINADO).collect {
                            adapterNoCalif.publicaciones = it as ArrayList<PublicationsData>
                            adapterNoCalif.notifyDataSetChanged()
                        }
                    }
                })
            }
        }
    }
}