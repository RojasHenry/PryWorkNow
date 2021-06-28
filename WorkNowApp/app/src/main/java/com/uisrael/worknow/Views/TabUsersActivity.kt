package com.uisrael.worknow.Views

import android.content.Context
import android.content.ContextWrapper
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.ActivityNavigator
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.picasso.Picasso
import com.uisrael.worknow.Model.Data.ProfesionalData
import com.uisrael.worknow.R
import com.uisrael.worknow.ViewModel.TabUsersViewModel
import com.uisrael.worknow.Views.Adapters.SectionsPagerAdapter
import com.uisrael.worknow.Views.Utilities.EventsNetwork
import com.uisrael.worknow.Views.Utilities.Utilitity
import kotlinx.android.synthetic.main.activity_tab_users.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class TabUsersActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var manager: ConnectivityManager? = null
    private lateinit var drawerLayout: DrawerLayout
    private var isProf = false
    private lateinit var navView: BottomNavigationView
    private lateinit var viewModel: TabUsersViewModel
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var uidUsuario: String
    private lateinit var pagerAdapter: SectionsPagerAdapter
    private lateinit var navDrawableLayout: NavigationView
    private lateinit var eventsNetwork: EventsNetwork

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab_users)

        viewModel = ViewModelProvider(this).get(TabUsersViewModel::class.java)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_bottom_view)
        navDrawableLayout = findViewById(R.id.nav_view)


        setSupportActionBar(toolbar)
        isProf = intent.getStringExtra("rolUser")?.equals(Utilitity.ROL_PROFESIONAL) == true
        uidUsuario = intent.getStringExtra("uid")


        pagerAdapter = if(isProf){
            navView.menu.clear()
            navView.inflateMenu(R.menu.bottom_nav_menu_prof)

            navDrawableLayout.menu.clear()
            navDrawableLayout.inflateMenu(R.menu.left_nav_menu_prof)
            SectionsPagerAdapter(supportFragmentManager, arrayListOf("Dashboard", "Register", "InProgress"), isProf, this)
        }else{
            SectionsPagerAdapter(supportFragmentManager, arrayListOf("Dashboard", "Register", "InProgress"), isProf, this)
        }

        viewpager_fragments.adapter = pagerAdapter
        viewpager_fragments.isPagingEnabled = false
        viewpager_fragments.offscreenPageLimit = 3

        toggle = ActionBarDrawerToggle(
                this,
                drawerLayout,
                null,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navDrawableLayout.setNavigationItemSelectedListener(this)

        changeToolbarFuncions(false)

        navView.setOnNavigationItemSelectedListener {
            titleToolbar.text = it.toString()
            when(it.itemId){
                R.id.navigation_dashboard_cli, R.id.navigation_dashboard_prof -> {
                    unlockDrawer()
                    showBottomNav()
                    changeToolbarFuncions(false)
                    moveTabViewpagerFragment(0)
                }
                R.id.navigation_publications_prof, R.id.navigation_offersregister_cli -> {
                    unlockDrawer()
                    showBottomNav()
                    changeToolbarFuncions(false)
                    moveTabViewpagerFragment(1)
                }
                R.id.navigation_in_progress_cli, R.id.navigation_in_progress_prof -> {
                    unlockDrawer()
                    showBottomNav()
                    changeToolbarFuncions(false)
                    moveTabViewpagerFragment(2)
                }
            }
            return@setOnNavigationItemSelectedListener true
        }

        viewpager_fragments.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageSelected(arg0: Int) {
                if(arg0 < navView.menu.size())
                    navView.menu.getItem(arg0).isChecked = true
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {
            }

            override fun onPageScrollStateChanged(arg0: Int) {
            }
        })

        Utilitity.showLoading(this,"Cargando...",supportFragmentManager)

        getCurrentUser()

        manager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager?
        eventsNetwork = EventsNetwork(nav_bottom_view,this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            manager?.registerDefaultNetworkCallback(eventsNetwork)
        }else {
            manager?.registerNetworkCallback(
                NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build(),
                eventsNetwork
            )
        }

    }

    private fun getCurrentUser() {
        viewModel.viewModelScope.launch {
            viewModel.getCurrentUser(uidUsuario, false).collect {
                val cabeceraView = navDrawableLayout.getHeaderView(0)
                val nombreUsuarioActual = cabeceraView.findViewById<TextView>(R.id.nombreUsuarioActual)
                val iconUsuarioActual = cabeceraView.findViewById<TextView>(R.id.iconUsuarioActual)
                if (it != null) {
                    viewModel.userCurrent = it
                    nombreUsuarioActual.text = "${it.nombre} ${it.apellido}"

                    if (it.foto.isNotBlank()){
                        val imageUsuarioActual = cabeceraView.findViewById<ImageView>(R.id.imageUsuarioActual)
                        if(Utilitity().isValidUrl(it.foto)){
                            Picasso
                                .get()
                                .load(it.foto)
                                .resize(250, 250)
                                .centerCrop()
                                .into(imageUsuarioActual)
                        }else{
                            val imageBytes = Base64.decode(it?.foto, Base64.DEFAULT)
                            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            imageUsuarioActual.setImageBitmap(decodedImage)
                        }
                        iconUsuarioActual.visibility = View.GONE
                    }else{
                        iconUsuarioActual.text = "${it.nombre[0]}${it.apellido[0]}"
                    }

                    if(it.rol == Utilitity.ROL_PROFESIONAL){
                        val rltCalifContenedor = cabeceraView.findViewById<RelativeLayout>(R.id.rltCalifContenedor)
                        val califTxtUsuarioProf = cabeceraView.findViewById<TextView>(R.id.califTxtUsuarioProf)
                        val ratingUsuarioProf = cabeceraView.findViewById<RatingBar>(R.id.ratingUsuarioProf)
                        var califacionSum = 0.0

                        if(it.datosProf.calificaciones.isNotEmpty()){
                            rltCalifContenedor.isVisible = true
                            it.datosProf.calificaciones.map { calificacionData ->
                                califacionSum += calificacionData.calificacion
                            }

                            val promed =  (califacionSum / it.datosProf.calificaciones.size)

                            califTxtUsuarioProf.text = String.format("%.2f",promed)
                            ratingUsuarioProf.rating = String.format("%.2f",promed).toFloat()
                        }

                        ratingUsuarioProf.isEnabled = false
                    }

                    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                        if(!task.isSuccessful){
                            return@addOnCompleteListener
                        }

                        // Get new FCM registration token
                        val token = task.result
                        viewModel.updateTokenUserLooged(uidUsuario,token)
                    }
                }
            }
        }

        viewModel.viewModelScope.launch {
            val cabeceraView = navDrawableLayout.getHeaderView(0)
            val emailUsuarioActual = cabeceraView.findViewById<TextView>(R.id.emailUsuarioActual)
            lifecycleOwner()?.let {
                viewModel.getUserLogged().observe(it,{ user->
                    emailUsuarioActual.text = user.email
                })
            }

        }
    }


    fun Context.lifecycleOwner(): LifecycleOwner? {
        var curContext = this
        var maxDepth = 20
        while (maxDepth-- > 0 && curContext !is LifecycleOwner) {
            curContext = (curContext as ContextWrapper).baseContext
        }
        return if (curContext is LifecycleOwner) {
            curContext
        } else {
            null
        }
    }

    private fun changeToolbarFuncions(isBack: Boolean){
        if(isBack){
            btnToolbar.setOnClickListener(null)
            btnToolbar.setImageResource(R.drawable.ic_back)
            btnToolbar.setOnClickListener{
                onBackPressed()
            }
        }else{
            btnToolbar.setOnClickListener(null)
            btnToolbar.setImageResource(R.drawable.ic_menu_24)
            btnToolbar.setOnClickListener{
                drawerLayout.openDrawer(GravityCompat.START);
            }
        }
    }

    private fun showBottomNav() {
        navView.visibility = View.VISIBLE

    }

    fun unlockDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            if(viewpager_fragments.currentItem == 0){
                Utilitity().showDialog(this,"Aviso", "¿Está seguro que desea salir de la aplicación?",R.drawable.ic_warning_24)
                    ?.setPositiveButton("Aceptar"){ dialog, _ ->
                        super.onBackPressed()
                    }
                    ?.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                    ?.show()
            }else{
                titleToolbar.text =  getString(R.string.title_dashboard)
                unlockDrawer()
                showBottomNav()
                changeToolbarFuncions(false)
                moveTabViewpagerFragment(0)
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.navigation_logout -> {
                if (Utilitity.isNetworkAvailable(this)){
                    Utilitity().showDialog(this,"Aviso", "¿Está seguro que desea cerrar la sesión actual?",R.drawable.ic_warning_24)
                        ?.setPositiveButton("Aceptar"){ dialog, _ ->
                            lifecycleScope.launch {
                                viewModel.logOutUsuario()
                                finish()
                            }
                            dialog.dismiss()
                        }
                        ?.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                        ?.show()
                }
            }

            R.id.navigation_profile -> {
                if (Utilitity.isNetworkAvailable(this)){
                    viewModel.viewModelScope.launch {
                        viewModel.getCurrentUser(uidUsuario, true).collect {
                            val dialogProfileUserFragment = it?.let { it1 -> ProfileUserFragment(uidUsuario, it1,
                                it.datosProf
                            ) }
                            dialogProfileUserFragment?.show(supportFragmentManager,"dialogProfileUser")
                        }
                    }
                }
            }

            R.id.navigation_history_offer_cli -> {
                if (Utilitity.isNetworkAvailable(this)){
                    val dialogHistoryOffersFragment = HistoryOffersFragment()
                    dialogHistoryOffersFragment.show(supportFragmentManager,"dialogHistoryOffers")
                }
            }
        }
        drawerLayout.closeDrawers();
        return true
    }

    fun moveTabViewpagerFragment(numTab:Int) {
        if(numTab != viewpager_fragments.currentItem){
            viewpager_fragments.currentItem = numTab
        }
    }
    fun disableViewpagerFragment(numTab:Int, status:Boolean) {
        navView.menu.getItem(numTab).isVisible = status
    }

    override fun finish() {
        super.finish()
        ActivityNavigator.applyPopAnimationsToPendingTransition(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        manager?.unregisterNetworkCallback(eventsNetwork)
    }




}