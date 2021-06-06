package com.uisrael.worknow.Views.Adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.firebase.auth.FirebaseUser
import com.uisrael.worknow.Views.ClientRegisterFragment
import com.uisrael.worknow.Views.ProfessionalRegisterFragment

class RegisterAdapter(fm: FragmentManager, ctx: Context, private var totalTabs: Int, val user: FirebaseUser?) : FragmentPagerAdapter(
    fm
) {

    private val tabTitles = arrayOf("Cliente", "Profesional")

    override fun getCount(): Int {
        return totalTabs
    }

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> {
                ClientRegisterFragment.newInstance(user)
            }
            1 -> {
                ProfessionalRegisterFragment.newInstance(user)
            }
            else -> ClientRegisterFragment.newInstance(user)
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitles[position]
    }
}