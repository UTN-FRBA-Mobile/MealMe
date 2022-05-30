package com.android.mealme.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel.getRestaurants()
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

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
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