package com.uisrael.worknow.Views.TabsFragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.viewModelScope
import com.uisrael.worknow.Model.Data.PublicationsData
import com.uisrael.worknow.R
import com.uisrael.worknow.ViewModel.TabsFragViewModel.PublicationsViewModel
import com.uisrael.worknow.Views.Adapters.PublicationsListAdapter
import kotlinx.android.synthetic.main.fragment_publications.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PublicationsFragment : Fragment() {

    private lateinit var adapter: PublicationsListAdapter
    private var listAdapter: ArrayList<PublicationsData> = arrayListOf()

    companion object {
        fun newInstance() = PublicationsFragment()
    }

    private lateinit var viewModel: PublicationsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_publications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(PublicationsViewModel::class.java)
        adapter = context?.let { activity?.let { it1 -> PublicationsListAdapter(it, listAdapter, it1.supportFragmentManager) } }!!

        ofertasListaPublicacion.adapter = adapter
        getOffersPorCategoria()
    }

    fun getOffersPorCategoria(){
        viewModel.getUidProfesional().observe(viewLifecycleOwner, {
            viewModel.viewModelScope.launch {
                viewModel.getCategoriasProfesional(it.uid).collect {
                    viewModel.viewModelScope.launch {
                        if (it != null) {
                            viewModel.getOffersViewPorCategoria(it.datosProf.categorias).collect {
                                progressListaPublicacion.isVisible = false
                                if(it.size > 0){
                                    adapter.publicaciones = it as ArrayList<PublicationsData>
                                    adapter.notifyDataSetChanged()
                                    rltErrorListaPublicacion.isVisible = false
                                    ofertasListaPublicacion.isVisible = true
                                }else{
                                    rltErrorListaPublicacion.isVisible = true
                                    ofertasListaPublicacion.isVisible = false
                                }
                            }
                        }
                    }
                }
            }
        })
    }
}