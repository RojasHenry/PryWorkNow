package com.uisrael.worknow.Views.Adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.uisrael.worknow.Views.ClientRegisterFragment
import com.uisrael.worknow.Views.ProfessionalRegisterFragment

class RegisterAdapter(fm: FragmentManager, ctx: Context, private var totalTabs: Int) : FragmentPagerAdapter(
    fm
) {

    private val tabTitles = arrayOf("Cliente", "Profesional")

    override fun getCount(): Int {
        return totalTabs
    }

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> {
                ClientRegisterFragment()
            }
            1 -> {
                ProfessionalRegisterFragment()
            }
            else -> ClientRegisterFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitles[position]
    }
}