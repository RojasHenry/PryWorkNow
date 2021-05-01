package com.uisrael.worknow.Views.TabsFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.uisrael.worknow.R
import com.uisrael.worknow.ViewModel.TabsFragViewModel.InProgressViewModel

class InProgressFragment : Fragment() {

    companion object {
        fun newInstance() = InProgressFragment()
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
    }
}