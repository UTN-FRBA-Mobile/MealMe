package com.android.mealme.fragments.restaurantDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.mealme.R
import com.android.mealme.databinding.FragmentRestaurantDetailBinding

class RestaurantDetailFragment : Fragment() {

    private var _binding: FragmentRestaurantDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRestaurantDetailBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = RestaurantDetailFragment()
    }
}