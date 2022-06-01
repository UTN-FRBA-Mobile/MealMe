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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.mealme.MainActivity
import com.android.mealme.R
import com.android.mealme.data.adapter.RestaurantAdapter
import com.android.mealme.data.adapter.RestaurantAdapterListener
import com.android.mealme.data.model.Restaurant
import com.android.mealme.data.utils.Permissions
import com.android.mealme.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth

    private val homeViewModel: HomeViewModel by viewModels()

    private var restaurantAdapterListener: RestaurantAdapterListener = object: RestaurantAdapterListener {
        override fun onPressItem(restaurant: Restaurant) {
            val bundle = Bundle().apply {
                putSerializable("RESTAURANT", restaurant)
            }
            findNavController().navigate(R.id.action_nav_home_to_restaurantDetailFragment,bundle)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val restaurantAdapter = RestaurantAdapter.init(binding.list, requireContext(), restaurantAdapterListener)

        homeViewModel.isLoading.observe(activity as MainActivity) {
            binding.progressBar.visibility = if(it) { View.VISIBLE } else {View.GONE}
        }
        homeViewModel.restaurants.observe(activity as MainActivity) {
            restaurantAdapter.setRestaurants(it)
        }
        requireActivity().invalidateOptionsMenu()

        binding.buttonClose.setOnClickListener() {
            if (checkForPermission()) {
                searchByGeoLocation()
            }
        }

        if (arguments?.getSerializable("filtroGeo") != null){
            //Viene por filtro de busqueda de dirección
            searchByGeoLocation(arguments?.getSerializable("latitud") as Double,
                arguments?.getSerializable("longitud") as Double)
        }else if(arguments?.getSerializable("filtroName") != null){
            //Viene por filtro de busqueda de nombre
            homeViewModel.getRestaurantByName(arguments?.getSerializable("name") as String, binding!!)

        }else if(checkForPermission()){
            //Si tiene permisos de ubicación carga las más cercanas
            searchByGeoLocation()
        }else{
            //Si no tiene permisos, carga la lista entera
            homeViewModel.getRestaurants()
        }

        binding.buttonAll.setOnClickListener(){
            homeViewModel.cleanRestaurants()
            homeViewModel.getRestaurants()
        }
        return binding.root
    }

    fun checkForPermission(): Boolean {
        return Permissions.checkForPermissions(this,android.Manifest.permission.ACCESS_FINE_LOCATION,
            PERMISSION_LOCATION_ID, resources.getString(R.string.permissionTitle))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_LOCATION_ID -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // tenemos permiso, continuar con la tarea
                    homeViewModel.cleanRestaurants()
                    searchByGeoLocation()
                }
                else {
                    homeViewModel.getRestaurants()
                    Toast.makeText(activity, "No se otorgaron permisos de ubicación", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @SuppressLint("MissingPermission")
    private fun searchByGeoLocation() {
        homeViewModel.isLoading.value = true;
        var locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 5000, 10f
        ) { loc ->
            searchByGeoLocation(loc.latitude, loc.longitude)
        }
    }

    @SuppressLint("MissingPermission")
    private fun searchByGeoLocation(latitude: Double, longitude: Double) {
        homeViewModel.isLoading.value = true;
        homeViewModel.getRestaurantsByGeoLocation(latitude, longitude,binding!!)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        const val PERMISSION_LOCATION_ID = 2
        @JvmStatic
        fun newInstance() = HomeFragment()
    }

    override fun onPrepareOptionsMenu(menu: Menu){
        super.onPrepareOptionsMenu(menu)
        firebaseAuth.addAuthStateListener { authState ->
            val item = menu.findItem(R.id.action_login)
            item?.isVisible = authState.currentUser == null
        }
    }

}