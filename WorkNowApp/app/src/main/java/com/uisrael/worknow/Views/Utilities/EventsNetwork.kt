package com.uisrael.worknow.Views.Utilities

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.uisrael.worknow.R

class EventsNetwork(val view:View, ctx: Context) : ConnectivityManager.NetworkCallback() {

    private var internetSnack = Snackbar
        .make(view, "Sin conexión a internet, revise los ajustes de conexión para continuar.", Snackbar.LENGTH_INDEFINITE)
        .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
        .setAnchorView(view)
        .setBackgroundTint(ctx.resources.getColor(R.color.purple_700))

    override fun onAvailable(network: Network?) {
        super.onAvailable(network)
        if (internetSnack.isShown){
            internetSnack.dismiss()
        }
    }

    override fun onLost(network: Network?) {
        super.onLost(network)
        internetSnack.show()
    }
}