package com.android.mealme

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.android.mealme.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

const val NAV_CONTROLLER_ID = R.id.nav_host_fragment_content_main

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = Firebase.auth

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(NAV_CONTROLLER_ID)
        navController.addOnDestinationChangedListener(this)

        // Check firebase current user
        var currentUser = firebaseAuth.currentUser
        if(currentUser != null){
            // user logged in
            navController.graph.setStartDestination(R.id.nav_home)
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_search, R.id.nav_favorite), drawerLayout
        )



        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        addAuthListenner()

        navView.menu.findItem(R.id.nav_home)?.setChecked(true)
        navView.menu.findItem(R.id.nav_logout).setOnMenuItemClickListener {
            navView.menu.close()
            if(firebaseAuth.currentUser != null) firebaseAuth.signOut()
            true
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(firebaseAuth.currentUser == null){
            menuInflater.inflate(R.menu.main, menu)
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(NAV_CONTROLLER_ID)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_login -> {
            //OPCION 1 --> NO FUNCIONA EL NAV DEL HOME
//            val action = R.id.action_nav_home_to_nav_login
//            findNavController(R.id.nav_host_fragment_content_main).navigate(action)
            //OPCION 2 --> NO FUNCIONA EL NAV DEL HOME:
            //Se solucionó ocultando el app bar en el login obligando a que hagan un back o hagan el submit
            navigate(R.id.nav_login)
           true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        val destinationClassName = (controller.currentDestination as FragmentNavigator.Destination).className
        when(destinationClassName){
            "com.android.mealme.fragments.home.RegisterFragment",
            "com.android.mealme.fragments.login.LoginFragment" -> {
                binding.appBarMain.toolbar.menu.findItem(R.id.action_login)?.isVisible = false
            }
//            "com.android.mealme.fragments.home.HomeFragment" -> {}
            else -> {
                binding.appBarMain.toolbar.menu.findItem(R.id.action_login)?.isVisible = true
            }
        }

    }

    private fun navigate(idRes: Int){
        val navController = findNavController(NAV_CONTROLLER_ID)
        navController.navigate(idRes)
    }

    private fun addAuthListenner(){
        firebaseAuth.addAuthStateListener { authState ->
            val menu = binding.appBarMain.toolbar.menu
            if(menu != null){
                val loginItem = menu.findItem(R.id.action_login)
                loginItem?.isVisible = authState.currentUser == null

                val navViewMenu = binding.navView.menu
                navViewMenu.findItem(R.id.nav_logout).setVisible(authState.currentUser != null)
                navViewMenu.findItem(R.id.nav_login).setVisible(authState.currentUser == null)
            }

        }
    }
}