package com.uisrael.worknow.Views.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.uisrael.worknow.Views.HistoryOffersFragment
import com.uisrael.worknow.Views.ProfileUserFragment
import com.uisrael.worknow.Views.TabUsersActivity
import com.uisrael.worknow.Views.TabsFragments.DashboardFragment
import com.uisrael.worknow.Views.TabsFragments.InProgressFragment
import com.uisrael.worknow.Views.TabsFragments.OffersRegisterFragment
import com.uisrael.worknow.Views.TabsFragments.PublicationsFragment



class SectionsPagerAdapter(
    fragmentManager: FragmentManager,
    private val tabs: ArrayList<String>,
    private val isProf: Boolean,
    private var tabUsersActivity: TabUsersActivity
): FragmentStatePagerAdapter(fragmentManager) {

    override fun getCount(): Int {
        return tabs.size
    }

    override fun getItem(position: Int): Fragment {
        if(isProf){
            when (position){
                0 ->  return DashboardFragment.newInstance(isProf, tabUsersActivity)
                1 ->  return PublicationsFragment.newInstance()
                2 ->  return InProgressFragment.newInstance(isProf)
            }
        }else{
            when (position){
                0 ->  return DashboardFragment.newInstance(isProf, tabUsersActivity)
                1 ->  return OffersRegisterFragment.newInstance()
                2 ->  return InProgressFragment.newInstance(isProf)
            }
        }
        return OffersRegisterFragment.newInstance()
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabs[position]
    }
}