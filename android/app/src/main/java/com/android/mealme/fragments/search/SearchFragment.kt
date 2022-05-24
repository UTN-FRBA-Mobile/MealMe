package com.android.mealme.fragments.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.mealme.R
import com.android.mealme.data.utils.Permissions
import com.android.mealme.databinding.FragmentSearchBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.GeoPoint
import java.io.IOException


class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    companion object {
        const val PERMISSION_LOCATION_ID = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(SearchViewModel::class.java)

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.searchByAddress.setOnClickListener(){
            if(binding.address?.editText?.text.toString().isNullOrEmpty()){
                Snackbar.make(activity?.findViewById(R.id.busquedaLayout)!!,
                    resources.getString(R.string.errorAddress), Snackbar.LENGTH_LONG)
                    .show();
            }

            getLocationFromAddress(binding.address?.editText?.text.toString())
        }

        binding.searchByGeo.setOnClickListener(){
            if(Permissions.checkForPermissions(this,android.Manifest.permission.ACCESS_FINE_LOCATION,
                    PERMISSION_LOCATION_ID, resources.getString(R.string.permissionTitle))){
                searchByGeoLocation()
            }
        }
        return root
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_LOCATION_ID -> {
                if (grantResults.count() > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // tenemos permiso, continuar con la tarea
                    searchByGeoLocation()
                }
                else {
                    // Controlar que no nos dieron permiso, por ejemplo mostrando un Toast
                    Toast.makeText(activity, "No hay permisos para escribir/leer imagenes", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @SuppressLint("MissingPermission")
    private fun searchByGeoLocation() {
        var locationRequest: LocationRequest

        var locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val locationListener: LocationListener = MyLocationListener()
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10f, locationListener
            )
    }

    class MyLocationListener : LocationListener {
        override fun onLocationChanged(loc: Location) {
            val longitude = loc.getLongitude()
            val latitude = loc.getLatitude()
            val s = "Longitude: "+ longitude + "Latitude: "+ latitude
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun getLocationFromAddress(strAddress: String?): GeoPoint? {
        val coder = Geocoder(requireContext())
        val address: List<Address>?
        var p1: GeoPoint? = null
        try {
            address = coder.getFromLocationName(strAddress, 5)
            if (address == null) {
                return null
            }
            val location: Address = address[0]
            location.getLatitude()
            location.getLongitude()

            return p1
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}