package com.android.mealme.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.mealme.MainActivity
import com.android.mealme.data.adapter.RestaurantAdapter
import com.android.mealme.data.model.ResponseApiModel
import com.android.mealme.data.service.RestaurantService
import com.android.mealme.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

    private var restaurantAdapter: RestaurantAdapter = RestaurantAdapter(object: OnFragmentInteractionListener {
        override fun showFragment(fragment: Fragment) {
            // findNavController().navigate() // TODO: navigate to restaurant detail
        }
    })


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
        binding.list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = restaurantAdapter
        }

        homeViewModel.isLoading.observe(activity as MainActivity) {
            binding.progressBar.visibility = if(it) { View.VISIBLE } else {View.GONE}
        }
        homeViewModel.restaurants.observe(activity as MainActivity) {
            restaurantAdapter.setRestaurants(it)
        }

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface OnFragmentInteractionListener {
        fun showFragment(fragment: Fragment)
    }


    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}