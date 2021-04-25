package com.uisrael.worknow.Views

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.uisrael.worknow.R
import com.uisrael.worknow.ViewModel.TabUsersViewModel
import kotlinx.android.synthetic.main.activity_tab_users.*
import kotlinx.coroutines.launch

class TabUsersActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    private lateinit var drawerLayout: DrawerLayout
    private var isProf = false
    private lateinit var navController:NavController
    private lateinit var navView: BottomNavigationView
    private lateinit var viewModel: TabUsersViewModel
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab_users)

        viewModel = ViewModelProvider(this).get(TabUsersViewModel::class.java)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_bottom_view)
        val navDrawableLayout: NavigationView = findViewById(R.id.nav_view)

        isProf = intent.getStringExtra("rolUser")?.equals("Profesional") == true

        navController = findNavController(R.id.nav_host_fragment)

        val appBarConfiguration: AppBarConfiguration

        if(isProf){
            navView.menu.clear()
            navView.inflateMenu(R.menu.bottom_nav_menu_prof)

            navController.graph.clear()
            navController.setGraph(R.navigation.nav_navigationtabs_prof)

            navDrawableLayout.menu.clear()
            navDrawableLayout.inflateMenu(R.menu.left_nav_menu_prof)

            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_dashboard_prof, R.id.navigation_publications_prof,
                    R.id.navigation_inprogress_prof, R.id.navigation_profile
                )
            )
        }else{
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_dashboard_cli,
                    R.id.navigation_offersregister_cli,
                    R.id.navigation_inprogress_cli,
                    R.id.navigation_history_offer_cli,
                    R.id.navigation_profile
                )
            )
        }

        setSupportActionBar(toolbar)

        setupActionBarWithNavController(navController, appBarConfiguration)

        navView.setupWithNavController(navController)
        navDrawableLayout.setupWithNavController(navController)

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
        navController.addOnDestinationChangedListener{ _, destination, _ ->
            titleToolbar.text = destination.label
            // hide bottonnavview
            when (destination.id){
                R.id.navigation_offersregister_cli, R.id.navigation_publications_prof, R.id.navigation_inprogress_prof, R.id.navigation_inprogress_cli -> {
                    unlockDrawer()
                    showBottomNav()
                    changeToolbarFuncions(false)
                }
                R.id.navigation_profile, R.id.navigation_history_offer_cli -> {
                    hideBottomNav()
                    lockDrawer()
                    changeToolbarFuncions(true)
                }
                else -> {
                    unlockDrawer()
                    showBottomNav()
                    changeToolbarFuncions(false)
                }

            }
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

    private fun hideBottomNav() {
        navView.visibility = View.GONE

    }

    fun lockDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }


    fun unlockDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.navigation_logout -> {
                lifecycleScope.launch {
                    viewModel.logOutUsuario()
                    finish()
                }
            }
            R.id.navigation_profile -> {
                navController.navigate(R.id.navigation_profile)
            }

            R.id.navigation_history_offer_cli -> {
                navController.navigate(R.id.navigation_history_offer_cli)
            }
        }
        drawerLayout.closeDrawers();
        return true
    }

    override fun finish() {
        super.finish()
        ActivityNavigator.applyPopAnimationsToPendingTransition(this)

    }
}