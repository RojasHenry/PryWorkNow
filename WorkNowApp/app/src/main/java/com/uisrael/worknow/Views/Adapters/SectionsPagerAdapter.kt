package com.uisrael.worknow.Views.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.uisrael.worknow.Views.HistoryOffersFragment
import com.uisrael.worknow.Views.ProfileUserFragment
import com.uisrael.worknow.Views.TabsFragments.DashboardFragment
import com.uisrael.worknow.Views.TabsFragments.InProgressFragment
import com.uisrael.worknow.Views.TabsFragments.OffersRegisterFragment
import com.uisrael.worknow.Views.TabsFragments.PublicationsFragment



class SectionsPagerAdapter (fragmentManager: FragmentManager, private  val tabs: ArrayList<String>, private val isProf: Boolean): FragmentStatePagerAdapter(fragmentManager) {

    override fun getCount(): Int {
        return tabs.size
    }

    override fun getItem(position: Int): Fragment {
        if(isProf){
            when (position){
                0 ->  return DashboardFragment.newInstance(isProf)
                1 ->  return PublicationsFragment.newInstance()
                2 ->  return InProgressFragment.newInstance(isProf)
                3 ->  return ProfileUserFragment.newInstance()
            }
        }else{
            when (position){
                0 ->  return DashboardFragment.newInstance(isProf)
                1 ->  return OffersRegisterFragment.newInstance()
                2 ->  return InProgressFragment.newInstance(isProf)
                3 ->  return ProfileUserFragment.newInstance()
                4 ->  return HistoryOffersFragment.newInstance()
            }
        }
        return OffersRegisterFragment.newInstance()
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabs[position]
    }
}