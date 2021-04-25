package com.uisrael.worknow.Views.TabsFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.uisrael.worknow.R
import com.uisrael.worknow.ViewModel.TabsFragViewModel.InProgressViewModel

class OffersRegisterFragment : Fragment() {

    private lateinit var inProgressViewModel: InProgressViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        inProgressViewModel =
                ViewModelProvider(this).get(InProgressViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_offersregister, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        inProgressViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}