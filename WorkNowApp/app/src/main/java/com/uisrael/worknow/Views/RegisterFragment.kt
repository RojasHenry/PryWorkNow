package com.uisrael.worknow.Views

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.google.android.material.tabs.TabLayout
import com.uisrael.worknow.R
import com.uisrael.worknow.ViewModel.RegisterViewModel
import com.uisrael.worknow.Views.Adapters.RegisterAdapter
import kotlinx.android.synthetic.main.register_fragment.*

class RegisterFragment : Fragment() {

    companion object {
        fun newInstance() = RegisterFragment()
    }

    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.register_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

        tablayoutRegister.addTab(tablayoutRegister.newTab().setText("Cliente"))
        tablayoutRegister.addTab(tablayoutRegister.newTab().setText("Profesional"))

        viewPagerRegister.adapter = RegisterAdapter(
            childFragmentManager,
            tablayoutRegister.tabCount,
            null,
            null,
            this
        )

        viewPagerRegister.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tablayoutRegister))

        tablayoutRegister.setupWithViewPager(viewPagerRegister)

        backBtn.setOnClickListener{
            returnToLogin()
        }
    }

    fun returnToLogin() {
        view?.findNavController()?.navigate(R.id.action_registerFragment_to_loginFragment)
    }

}