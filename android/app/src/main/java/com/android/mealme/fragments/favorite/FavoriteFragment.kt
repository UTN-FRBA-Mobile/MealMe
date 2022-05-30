package com.android.mealme.fragments.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.android.mealme.MainActivity
import com.android.mealme.R
import com.android.mealme.data.adapter.RestaurantAdapter
import com.android.mealme.data.adapter.RestaurantAdapterListener
import com.android.mealme.data.controller.FavoriteController
import com.android.mealme.data.model.Restaurant
import com.android.mealme.databinding.FragmentFavoriteBinding

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val viewModel: FavoriteViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val restaurantAdapterListener = object : RestaurantAdapterListener {
        override fun onPressItem(restaurant: Restaurant) {
            val bundle = Bundle().apply {
                putSerializable("RESTAURANT", restaurant)
            }
            findNavController().navigate(R.id.action_nav_favorite_to_restaurantDetailFragment,bundle)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FavoriteController.instance.getFavorites()?.thenApply {
            viewModel.favorites.value = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        val restaurantAdapter = RestaurantAdapter.init(
            binding.favoriteFragmentRecyclerView,
            requireContext(),
            restaurantAdapterListener
        )
        viewModel.favorites.observe(activity as MainActivity) { restaurants ->
            restaurantAdapter.setRestaurants(restaurants)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.favorites.value = FavoriteController.instance.getRestaurantsFromFavoritesIds()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.favorites.removeObservers(activity as MainActivity)
        _binding = null
    }
}