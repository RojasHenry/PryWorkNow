package com.uisrael.worknow.Views

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.uisrael.worknow.R
import com.uisrael.worknow.ViewModel.ClientViewModel

class ClientRegisterFragment : Fragment() {

    companion object {
        fun newInstance() = ClientRegisterFragment()
    }

    private lateinit var viewModel: ClientViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.client_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ClientViewModel::class.java)
        // TODO: Use the ViewModel
    }

}