package com.uisrael.worknow.Views.TabsFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.uisrael.worknow.Model.Data.PublicationsData
import com.uisrael.worknow.R
import com.uisrael.worknow.ViewModel.TabsFragViewModel.DashboardViewModel
import com.uisrael.worknow.Views.Adapters.OfferAcceptListAdapter
import com.uisrael.worknow.Views.Adapters.OfferPubNoCalifListAdapter
import com.uisrael.worknow.Views.TabUsersActivity
import com.uisrael.worknow.Views.Utilities.Utilitity
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DashboardFragment(var isProf: Boolean, var tabUsersActivity: TabUsersActivity) : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var adapterPublic: OfferPubNoCalifListAdapter
    private lateinit var adapterNoCalif: OfferPubNoCalifListAdapter
    private lateinit var adapterAccepted: OfferAcceptListAdapter
    private var listAdapter: ArrayList<PublicationsData> = arrayListOf()

    companion object {
        fun newInstance(isProf: Boolean, tabUsersActivity: TabUsersActivity) = DashboardFragment(isProf, tabUsersActivity)
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
                        dashboardViewModel.getOfferViewAccepted(it.uid).collect {
                            progressAcceptOffer.visibility = View.GONE
                            if(it.size > 0){
                                tabUsersActivity.moveTabViewpagerFragment(0)
                                tabUsersActivity.disableViewpagerFragment(1,false)
                                adapterAccepted.publicaciones = it as ArrayList<PublicationsData>
                                adapterAccepted.notifyDataSetChanged()
                            }else{
                                tabUsersActivity.disableViewpagerFragment(1,true)
                            }
                        }
                    }
                })
            }
        }else{
            dashboardViewModel.viewModelScope.launch {
                dashboardViewModel.getUserViewLogged().observe(viewLifecycleOwner,{
                    dashboardViewModel.viewModelScope.launch {
                        dashboardViewModel.getOffersViewAcceptAndPublic(it.uid).collect {
                            progressOffersPublic.visibility = View.GONE
                            if(it.size > 2){
                                tabUsersActivity.moveTabViewpagerFragment(0)
                                tabUsersActivity.disableViewpagerFragment(1,false)
                                adapterPublic.publicaciones = it as ArrayList<PublicationsData>
                                adapterPublic.notifyDataSetChanged()
                            }else{
                                tabUsersActivity.disableViewpagerFragment(1,true)
                            }

                        }
                    }

                    dashboardViewModel.viewModelScope.launch {
                        dashboardViewModel.getOffersViewNoCalifCli(it.uid,Utilitity.ESTADO_SOL_TERMINADO).collect {
                            progressOffersNoCalif.visibility = View.GONE
                            adapterNoCalif.publicaciones = it as ArrayList<PublicationsData>
                            adapterNoCalif.notifyDataSetChanged()
                        }
                    }
                })
            }
        }
    }
}