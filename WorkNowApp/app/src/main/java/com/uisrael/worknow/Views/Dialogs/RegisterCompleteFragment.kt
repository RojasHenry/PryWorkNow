package com.uisrael.worknow.Views.Dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import com.uisrael.worknow.R
import com.uisrael.worknow.Views.Adapters.RegisterAdapter
import kotlinx.android.synthetic.main.calification_dialog_fragment.*
import kotlinx.android.synthetic.main.register_complete_fragment.*

class RegisterCompleteFragment(val user: FirebaseUser) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.register_complete_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (user.photoUrl != null){
            Picasso
                .get()
                .load(user.photoUrl)
                .resize(250, 250)
                .centerCrop()
                .into(imageUsuarioCompleteRegister)
            iconUsuarioCompleteRegister.isVisible = false
        }else{
            iconUsuarioDialogCalif.text = "${user.displayName[0]}"
        }
        tablayoutRegister.addTab(tablayoutRegister.newTab().setText("Cliente"))
        tablayoutRegister.addTab(tablayoutRegister.newTab().setText("Profesional"))

        val adapterTabs = context?.let { RegisterAdapter(childFragmentManager, it,tablayoutRegister.tabCount, user) }
        viewPagerRegister.adapter = adapterTabs

        viewPagerRegister.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tablayoutRegister))

        tablayoutRegister.setupWithViewPager(viewPagerRegister)

        btnToolbarCompleteRegister.setOnClickListener{
            dismiss()
        }
    }

    override fun getTheme(): Int {
        return R.style.FullScreenDialog
    }

}