package com.uisrael.worknow.Views.TabsFragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.uisrael.worknow.R
import com.uisrael.worknow.ViewModel.TabsFragViewModel.PublicationsViewModel

class PublicationsFragment : Fragment() {

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PublicationsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}