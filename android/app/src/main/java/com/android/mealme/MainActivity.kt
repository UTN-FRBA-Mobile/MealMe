package com.android.mealme

import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.ui.*
import com.android.mealme.data.controller.FavoriteController
import com.android.mealme.databinding.ActivityMainBinding
import com.android.mealme.fragments.home.HomeFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

const val NAV_CONTROLLER_ID = R.id.nav_host_fragment_content_main

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private var firebaseAuthStateListener: FirebaseAuth.AuthStateListener =
        FirebaseAuth.AuthStateListener { authState ->
            val isLoggedIn = authState.currentUser != null

            // SET USER EMAIL ON HEADER
            binding.navView.getHeaderView(0).findViewById<TextView>(R.id.emailUser).text =
                if (isLoggedIn) {
                    authState?.currentUser?.email
                } else {
                    "-"
                }

            val menu = binding.appBarMain.toolbar.menu
            if (menu != null) {
                val loginItem = menu.findItem(R.id.action_login)
                loginItem?.isVisible = !isLoggedIn

                val navViewMenu = binding.navView.menu
                navViewMenu.findItem(R.id.nav_logout).setVisible(isLoggedIn)
                navViewMenu.findItem(R.id.nav_login).setVisible(!isLoggedIn)
            }

            if (isLoggedIn) {
                FavoriteController.instance.getFavorites()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavigation()
    }

    private fun setupNavigation() {
        setSupportActionBar(binding.appBarMain.toolbar)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(NAV_CONTROLLER_ID)
        navController.addOnDestinationChangedListener(this)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home
            ), drawerLayout
        )

        NavigationUI.setupWithNavController(
            binding.appBarMain.toolbar,
            navController,
            appBarConfiguration
        )
        navView.setupWithNavController(navController)

        navView.menu.findItem(R.id.nav_home)?.setChecked(true)
        navView.menu.findItem(R.id.nav_logout).setOnMenuItemClickListener {
            navView.menu.close()
            if (FirebaseAuth.getInstance().currentUser != null) {
                FirebaseAuth.getInstance().signOut()
                firebaseAuthStateListener.onAuthStateChanged(FirebaseAuth.getInstance())
            }
            true
        }
        setupHomeMenu()
    }

    private fun setupHomeMenu(id: Int = R.drawable.ic_menu) {
        Handler().postDelayed({
            binding.appBarMain.toolbar.navigationIcon = getDrawable(id)
            binding.appBarMain.toolbar.setNavigationOnClickListener {
                when (id) {
                    R.drawable.ic_menu -> binding.drawerLayout.open()
                    R.drawable.ic_arrow_back -> onBackPressed()
                }
            }
        }, 0)
    }


    override fun onStart() {
        super.onStart()
        firebaseAuthStateListener.onAuthStateChanged(FirebaseAuth.getInstance())
        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuthStateListener)
    }

    override fun onPause() {
        super.onPause()
        FirebaseAuth.getInstance().removeAuthStateListener(firebaseAuthStateListener)
    }

    private var previousDestinationClassName: String = ""
    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        val destinationClassName =
            (controller.currentDestination as FragmentNavigator.Destination).className
        if (previousDestinationClassName.contains("LoginFragment") && destinationClassName.contains(
                "HomeFragment"
            )
        ) {
            firebaseAuthStateListener.onAuthStateChanged(FirebaseAuth.getInstance())
        }
        when (destinationClassName) {
            "com.android.mealme.fragments.home.HomeFragment" -> {
                val name = arguments?.getString(HomeFragment.HOME_SEARCH_NAME, "")
                val address = arguments?.getString(HomeFragment.HOME_SEARCH_ADDRESS, "")
                val hasSearch = name?.isNotEmpty() ?: false || address?.isNotEmpty() ?: false
                if (hasSearch) {
                    setupHomeMenu(R.drawable.ic_arrow_back)
                } else {
                    setupHomeMenu()
                }
            }
            else -> setupHomeMenu(R.drawable.ic_arrow_back)
        }
        previousDestinationClassName = destinationClassName
    }
}