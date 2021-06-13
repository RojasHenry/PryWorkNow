package com.uisrael.worknow.Views.Adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.firebase.auth.FirebaseUser
import com.uisrael.worknow.Views.ClientRegisterFragment
import com.uisrael.worknow.Views.Dialogs.RegisterCompleteFragment
import com.uisrael.worknow.Views.ProfessionalRegisterFragment
import com.uisrael.worknow.Views.RegisterFragment

class RegisterAdapter(
    fm: FragmentManager,
    private var totalTabs: Int,
    private val user: FirebaseUser?,
    private val completeFragment: RegisterCompleteFragment?,
    private val registerFragment: RegisterFragment?
    ) : FragmentPagerAdapter(fm) {

    private val tabTitles = arrayOf("Cliente", "Profesional")

    override fun getCount(): Int {
        return totalTabs
    }

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> {
                ClientRegisterFragment.newInstance(user, completeFragment, registerFragment)
            }
            1 -> {
                ProfessionalRegisterFragment.newInstance(user, completeFragment, registerFragment)
            }
            else -> ClientRegisterFragment.newInstance(user, completeFragment, registerFragment)
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitles[position]
    }
}