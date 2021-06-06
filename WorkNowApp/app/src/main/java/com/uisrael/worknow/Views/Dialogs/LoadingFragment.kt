package com.uisrael.worknow.Views.Dialogs

import android.app.Activity
import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.uisrael.worknow.R
import kotlinx.android.synthetic.main.loading_dialog_fragment.*

class LoadingFragment (val ctx: Context, val titulo: CharSequence?) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCancelable = false
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent);
        return inflater.inflate(R.layout.loading_dialog_fragment, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inicializarComponentes()
    }

    private fun inicializarComponentes() {
        cp_title.text = titulo
        cp_title.setTextColor(Color.WHITE)
        cp_cardview.setCardBackgroundColor(Color.parseColor("#70000000"))
        setColorFilter(cp_pbar.indeterminateDrawable, ResourcesCompat.getColor(ctx.resources, R.color.purple_200, null))
    }

    fun showLoading (manager: FragmentManager){
        show(manager, "dialog_loading")
    }

    fun dissmissLoading (){
        dismiss()
    }

    private fun setColorFilter(drawable: Drawable, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        } else {
            @Suppress("DEPRECATION")
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }

}