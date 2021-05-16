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
import com.uisrael.worknow.ViewModel.TabsFragViewModel.InProgressViewModel
import com.uisrael.worknow.Views.Adapters.OfferProgressListAdapter
import kotlinx.android.synthetic.main.fragment_inprogress.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class InProgressFragment(var isProf: Boolean) : Fragment() {

    private lateinit var adapterProgress: OfferProgressListAdapter
    private var listAdapter: ArrayList<PublicationsData> = arrayListOf()

    companion object {
        fun newInstance(isProf: Boolean) = InProgressFragment(isProf)
    }

    private lateinit var inProgressViewModel: InProgressViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inprogress, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        inProgressViewModel = ViewModelProvider(this).get(InProgressViewModel::class.java)
        inicializateComponents()
        collectorFlow()
    }

    private fun inicializateComponents() {
        adapterProgress = context?.let { activity?.let { it1 -> OfferProgressListAdapter(it, listAdapter, it1.supportFragmentManager,isProf) } }!!
        offerListOnProgress.adapter = adapterProgress
    }

    private fun collectorFlow() {
        if(isProf){
            inProgressViewModel.viewModelScope.launch {
                inProgressViewModel.getUserViewLogged().observe(viewLifecycleOwner,{
                    inProgressViewModel.viewModelScope.launch {
                        inProgressViewModel.getOffersViewProfAceptedOnProgress(it.uid).collect {
                            adapterProgress.publicaciones = it as ArrayList<PublicationsData>
                            adapterProgress.notifyDataSetChanged()
                        }
                    }
                })
            }
        }else{
            inProgressViewModel.viewModelScope.launch {
                inProgressViewModel.getUserViewLogged().observe(viewLifecycleOwner,{
                    inProgressViewModel.viewModelScope.launch {
                        inProgressViewModel.getOffersViewCliAceptedOnProgress(it.uid).collect {
                            adapterProgress.publicaciones = it as ArrayList<PublicationsData>
                            adapterProgress.notifyDataSetChanged()
                        }
                    }
                })
            }
        }
    }


}