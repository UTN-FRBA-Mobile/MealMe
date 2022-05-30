package com.android.mealme.fragments.restaurantDetail

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.android.mealme.MainActivity
import com.android.mealme.R
import com.android.mealme.data.adapter.MenutabAdapter
import com.android.mealme.data.controller.FavoriteController
import com.android.mealme.data.model.Restaurant
import com.android.mealme.data.model.RestaurantCategory
import com.android.mealme.databinding.FragmentRestaurantDetailBinding
import com.android.mealme.utils.numberUtils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class RestaurantDetailFragment : Fragment() {

    private var _binding: FragmentRestaurantDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RestaurantDetailViewModel by viewModels()

    private lateinit var menuViewPagerAdapter: MenutabAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setRestaurant(arguments?.getSerializable("RESTAURANT") as Restaurant)
        menuViewPagerAdapter = MenutabAdapter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRestaurantDetailBinding.inflate(inflater, container, false)
        // Set menu tabs view pager
        binding.restaurantDetailMenuViewPager.adapter = menuViewPagerAdapter

        setToolbar()
        fillDetailContent()
        loadRestaurantImage()
        addViewModelObservers()

        FavoriteController.instance._favorites.observe(activity as MainActivity) {
            if (it.isNotEmpty()) {
                viewModel.isFavorite.value =
                    FavoriteController.instance.isRestaurantInFavorites(viewModel.restaurant?._id!!)
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.isLoading.removeObservers(activity as AppCompatActivity)
        viewModel.categories.removeObservers(activity as AppCompatActivity)
        viewModel.isFavorite.removeObservers(activity as AppCompatActivity)
        FavoriteController.instance.isLoading.removeObservers(activity as MainActivity)
        _binding = null
    }

    private fun fillDetailContent() {
        var rating =
            "${numberUtils.roundOffDecimal(viewModel.restaurant?.weighted_rating_value!!)}/5 "
        rating += "(${viewModel.restaurant?.aggregated_rating_count})"
        binding.restaurantDetailRating.text = rating
    }

    private fun setToolbar() {
        binding.restaurantDetailToolbar.inflateMenu(R.menu.restaurant_detail_menu)
        binding.restaurantDetailCollapsingToolbar.title = viewModel.restaurant?.name

        // Set back button listener
        binding.restaurantDetailToolbar.setNavigationOnClickListener { activity?.onBackPressed() }

        val favoriteButton =
            binding.restaurantDetailToolbar.menu.findItem(R.id.detail_menu_favorite)

        FavoriteController.instance.isLoading.observe(activity as MainActivity){
            favoriteButton.isVisible = !it
        }
        // Set favorite press listener
        viewModel.isFavorite.observe(activity as MainActivity) { isFavorite ->
            favoriteButton.setIcon(
                if (isFavorite) {
                    R.drawable.ic_baseline_favorite_24
                } else {
                    R.drawable.ic_baseline_favorite_border_24
                }
            )
        }

        favoriteButton.setOnMenuItemClickListener {
            if (!viewModel.isFavorite.value!!) {
                viewModel.addFavorite()
            } else {
                viewModel.removeFavorite()
            }

            true
        }
    }

    private fun loadRestaurantImage() {
        Picasso.get().load(viewModel.restaurant?.logo_photos?.first())
            .into(binding.restaurantDetailImage, object : Callback {
                override fun onSuccess() {
                    binding.restaurantDetailImageLoader.isVisible = false
                }

                override fun onError(e: Exception?) {
                    binding.restaurantDetailImageLoader.isVisible = false
                }
            })
    }

    private fun addViewModelObservers() {
        viewModel.isLoading.observe(activity as AppCompatActivity) {
            binding.restaurantDetailMenuLoader?.isVisible = it
        }


        viewModel.categories.observe(activity as AppCompatActivity) { categories ->
            menuViewPagerAdapter.setList(categories)
            for (category: RestaurantCategory in categories) {
                var tab: TabLayout.Tab = binding.restaurantDetailMenuTabs.newTab()
                tab.text = category.name
                binding.restaurantDetailMenuTabs.addTab(tab)
            }
            TabLayoutMediator(
                binding.restaurantDetailMenuTabs,
                binding.restaurantDetailMenuViewPager
            ) { tab, position ->
                tab.text = viewModel.categories.value?.get(position)?.name
            }.attach()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = RestaurantDetailFragment()
    }
}