package com.uisrael.worknow.Views.TabsFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        inicializateComponents()
        collectorFlow()
    }

    private fun inicializateComponents() {
        if (isProf){
            rltDaschboardCli.visibility = View.GONE
            rltDaschboardProf.visibility = View.VISIBLE
            adapterAccepted = context?.let { it -> OfferAcceptListAdapter(it, listAdapter, requireActivity().supportFragmentManager, requireActivity(), dashboardViewModel) }!!
            listOffersAccept.adapter = adapterAccepted
        }else{
            adapterPublic = context?.let { activity?.let { it1 -> OfferPubNoCalifListAdapter(it, listAdapter, it1.supportFragmentManager,true, requireActivity(), dashboardViewModel) } }!!
            adapterNoCalif = context?.let { activity?.let { it1 -> OfferPubNoCalifListAdapter(
                it,
                listAdapter,
                it1.supportFragmentManager,
                false,
                null,
                dashboardViewModel
            ) } }!!

            listOffersPublic.adapter = adapterPublic
            listOffersNoCalif.adapter = adapterNoCalif
        }
    }

    private fun collectorFlow() {
        if(isProf){
            dashboardViewModel.viewModelScope.launch {
                dashboardViewModel.getUserViewLogged().observe(viewLifecycleOwner,{ user ->
                    dashboardViewModel.currentUser = user
                    dashboardViewModel.viewModelScope.launch {
                        dashboardViewModel.getOfferViewAccepted(user.uid).collect {
                            progressAcceptOffer.visibility = View.GONE
                            if(it.size > 0){
                                listOffersAccept.isVisible = true
                                tabUsersActivity.moveTabViewpagerFragment(0)
                                tabUsersActivity.disableViewpagerFragment(1,false)
                                adapterAccepted.publicaciones = it as ArrayList<PublicationsData>
                                adapterAccepted.notifyDataSetChanged()
                                rltErrorlistOffersAccept.isVisible = false
                                Utilitity.dissMissLoading(3000)
                            }else{
                                tabUsersActivity.disableViewpagerFragment(1,true)
                                rltErrorlistOffersAccept.isVisible = true
                                listOffersAccept.isVisible = false
                                Utilitity.dissMissLoading(0)
                            }
                        }
                    }
                })
            }
        }else{
            dashboardViewModel.viewModelScope.launch {
                dashboardViewModel.getUserViewLogged().observe(viewLifecycleOwner,{ user ->
                    dashboardViewModel.currentUser = user
                    dashboardViewModel.viewModelScope.launch {
                        dashboardViewModel.getOffersViewAcceptAndPublic(user.uid).collect {
                            progressOffersPublic.visibility = View.GONE
                            if(it.size > 0){
                                if(it.size <= 2){
                                    listOffersPublic.isVisible = true
                                    tabUsersActivity.moveTabViewpagerFragment(0)
                                    tabUsersActivity.disableViewpagerFragment(1,false)
                                    adapterPublic.publicaciones = it as ArrayList<PublicationsData>
                                    adapterPublic.notifyDataSetChanged()
                                    rltErrorlistOffersPublic.isVisible = false
                                    Utilitity.dissMissLoading(3000)
                                }
                            }else{
                                rltErrorlistOffersPublic.isVisible = true
                                listOffersPublic.isVisible = false
                                tabUsersActivity.disableViewpagerFragment(1,true)
                                Utilitity.dissMissLoading(0)
                            }

                        }
                    }

                    dashboardViewModel.viewModelScope.launch {
                        dashboardViewModel.getOffersViewNoCalifCli(user.uid,Utilitity.ESTADO_SOL_TERMINADO).collect {
                            progressOffersNoCalif.visibility = View.GONE
                            if(it.size > 0){
                                listOffersNoCalif.isVisible = true
                                adapterNoCalif.publicaciones = it as ArrayList<PublicationsData>
                                adapterNoCalif.notifyDataSetChanged()
                                rltErrorlistOffersNoCalif.isVisible = false
                            }else{
                                rltErrorlistOffersNoCalif.isVisible = true
                                listOffersNoCalif.isVisible = false
                            }
                        }
                    }
                })
            }
        }
    }
}