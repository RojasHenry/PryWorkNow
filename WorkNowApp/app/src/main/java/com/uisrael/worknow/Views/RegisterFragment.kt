package com.uisrael.worknow.Views

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.google.android.material.tabs.TabLayout
import com.uisrael.worknow.Model.Data.UsuariosData
import com.uisrael.worknow.R
import com.uisrael.worknow.ViewModel.RegisterViewModel
import com.uisrael.worknow.Views.Adapters.RegisterAdapter
import kotlinx.android.synthetic.main.register_fragment.*
import kotlinx.coroutines.launch

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

        tablayoutRegister.addTab(tablayoutRegister.newTab().setText("Cliente"))
        tablayoutRegister.addTab(tablayoutRegister.newTab().setText("Profesional"))

        val adapterTabs = context?.let { RegisterAdapter(childFragmentManager, it,tablayoutRegister.tabCount) }
        viewPagerRegister.adapter = adapterTabs

        viewPagerRegister.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tablayoutRegister))

        tablayoutRegister.setupWithViewPager(viewPagerRegister)

        /*var usuario =
            UsuariosData(
            "1",
            "Henry",
            "Rojas",
            "Quito",
            "3442054",
                "",
            "Cliente")

        btnGuardar.setOnClickListener {
            viewModel.viewModelScope.launch {
                val result = viewModel.registerViewUser(usuario)
                Toast.makeText(activity, "Result: $result", Toast.LENGTH_SHORT).show()
            }
        }*/

        backBtn.setOnClickListener{
            view?.findNavController()?.navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

}