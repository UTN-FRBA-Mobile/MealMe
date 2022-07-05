package com.android.mealme.fragments.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.android.mealme.MainActivity
import com.android.mealme.R
import com.android.mealme.data.adapter.RestaurantAdapter
import com.android.mealme.data.adapter.RestaurantAdapterListener
import com.android.mealme.data.model.Restaurant
import com.android.mealme.data.utils.Permissions
import com.android.mealme.databinding.FragmentHomeBinding
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth

    private val homeViewModel: HomeViewModel by viewModels()
    private var hasLocationPermissions: MutableLiveData<Boolean> = MutableLiveData(false)
    private lateinit var restaurantAdapter: RestaurantAdapter

    private var restaurantAdapterListener: RestaurantAdapterListener =
        object : RestaurantAdapterListener {
            override fun onPressItem(restaurant: Restaurant) {
                val bundle = Bundle().apply {
                    putSerializable("RESTAURANT", restaurant)
                }
                findNavController().navigate(
                    R.id.action_nav_home_to_restaurantDetailFragment,
                    bundle
                )
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hasLocationPermissions.value = Permissions.hasPermissions(
            requireContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        restaurantAdapter =
            RestaurantAdapter.init(binding.list, requireContext(), restaurantAdapterListener)

        binding.homeAskPermissionsButton.setOnClickListener() {
            checkForPermission()
            getLocationAndSearch()
        }

        binding.homeSearchButton.setOnClickListener() {
            val navController = findNavController()
            val options =
                NavOptions.Builder()
                    .setPopUpTo(navController.currentDestination?.id!!, true)
                    .setLaunchSingleTop(false)
                    .build()
            navController.navigate(
                R.id.action_nav_home_to_nav_search,
                null,
                navOptions {
                    restoreState = true
                    popUpTo(navController.currentDestination?.id!!){
                        saveState = true
                    }
                }
            )
        }

        requireActivity().invalidateOptionsMenu()

        this.searchFromArguments()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.isLoading.observe(activity as MainActivity) {
            binding.progressBar.visibility = if (it) {
                View.VISIBLE
            } else {
                View.GONE
            }
            if (it) {
                binding.homeEmptyListContainer.visibility = View.GONE
            }

        }
        homeViewModel.restaurants.observe(activity as MainActivity) {
            restaurantAdapter.setRestaurants(it)
        }
    }

    override fun onPause() {
        super.onPause()
        homeViewModel.isLoading.removeObservers(activity as MainActivity)
        homeViewModel.restaurants.removeObservers(activity as MainActivity)
    }

    private fun searchFromArguments() {
        val name: String? = arguments?.getString(HOME_SEARCH_NAME, "")
        val address: String? = arguments?.getString(HOME_SEARCH_ADDRESS, "")
        if (name != null || address != null) {
            binding.homeEmptyListContainer.visibility = View.GONE
            homeViewModel.getRestaurantsByNameAndAddress(name, address)
        } else if (hasLocationPermissions.value == true) {
            binding.homeEmptyListContainer.visibility = View.GONE
            getLocationAndSearch()
        } else {
            binding.homeEmptyListContainer.visibility = View.VISIBLE
            binding.homeEmptyListTitle.text = getString(R.string.we_need_permissions)
            binding.homeAskPermissionsButton.visibility = View.VISIBLE
        }
    }

    fun checkForPermission(): Boolean {
        return Permissions.checkForPermissions(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            PERMISSION_LOCATION_ID,
            resources.getString(R.string.permissionTitle)
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_LOCATION_ID -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // tenemos permiso, continuar con la tarea
                    homeViewModel.cleanRestaurants()
                    getLocationAndSearch()
                    hasLocationPermissions.value = true
                    binding.homeEmptyListContainer.visibility = View.GONE
                } else {
                    homeViewModel.getRestaurants()
                    Toast.makeText(
                        activity,
                        "No se otorgaron permisos de ubicación",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @SuppressLint("MissingPermission")
    private fun getLocationAndSearch() {
        if (this.hasLocationPermissions.value == true) {
            homeViewModel.isLoading.value = true;
            val fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(activity as MainActivity)
            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                if (loc != null) {
                    searchByGeoLocation(loc.latitude, loc.longitude)
                } else {
                    binding.homeEmptyListContainer.visibility = View.VISIBLE
                    binding.homeEmptyListTitle.text = "No pudimos obtener tu ubicación"
                    binding.homeAskPermissionsButton.visibility = View.GONE
                }
            }
        }

    }

    private fun searchByGeoLocation(latitude: Double, longitude: Double) {
        homeViewModel.isLoading.value = true;
        homeViewModel.getRestaurantsByGeoLocation(latitude, longitude)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        firebaseAuth.addAuthStateListener { authState ->
            val item = menu.findItem(R.id.action_login)
            item?.isVisible = authState.currentUser == null
        }
    }

    companion object {
        const val PERMISSION_LOCATION_ID = 2
        const val HOME_SEARCH_NAME = "HOME_SEARCH_NAME"
        const val HOME_SEARCH_ADDRESS = "HOME_SEARCH_ADDRESS"

        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}