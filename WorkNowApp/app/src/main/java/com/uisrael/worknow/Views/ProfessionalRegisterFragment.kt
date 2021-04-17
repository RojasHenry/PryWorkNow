package com.uisrael.worknow.Views

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.uisrael.worknow.R
import com.uisrael.worknow.ViewModel.ProfessionalViewModel

class ProfessionalRegisterFragment : Fragment() {

    companion object {
        fun newInstance() = ProfessionalRegisterFragment()
    }

    private lateinit var viewModel: ProfessionalViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.professional_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfessionalViewModel::class.java)
        // TODO: Use the ViewModel
    }

}