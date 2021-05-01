package com.uisrael.worknow.Views

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.uisrael.worknow.R
import com.uisrael.worknow.ViewModel.ProfileUserViewModel

class ProfileUserFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileUserFragment()
    }

    private lateinit var viewModel: ProfileUserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.profile_user_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileUserViewModel::class.java)
        // TODO: Use the ViewModel
    }

}