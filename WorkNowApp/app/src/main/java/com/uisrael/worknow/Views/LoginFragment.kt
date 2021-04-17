package com.uisrael.worknow.Views

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.uisrael.worknow.R
import com.uisrael.worknow.ViewModel.LoginViewModel
import kotlinx.android.synthetic.main.login_fragment.*
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        // TODO: Use the ViewModel
        btnLogin.setOnClickListener {
            if (correoTxt.length() > 0 && passwordTxt.length() > 0) {
                viewModel.viewModelScope.launch {
                    val user = viewModel.registerViewUser(
                            correoTxt.text.toString(),
                            passwordTxt.text.toString()
                    )
                    Toast.makeText(activity, "Usuario actual $user", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
            }
        }
        linkRegistro.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

}