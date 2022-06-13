package com.android.mealme.fragments.restaurantDetail

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.android.mealme.MainActivity
import com.android.mealme.R
import com.android.mealme.data.adapter.MenutabAdapter
import com.android.mealme.data.controller.FavoriteController
import com.android.mealme.data.model.Restaurant
import com.android.mealme.data.model.RestaurantCategory
import com.android.mealme.databinding.FragmentRestaurantDetailBinding
import com.android.mealme.ui.RestaurantHeaderImage
import com.android.mealme.utils.numberUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth


class RestaurantDetailFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentRestaurantDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RestaurantDetailViewModel by viewModels()

    private lateinit var menuViewPagerAdapter: MenutabAdapter
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setRestaurant(arguments?.getSerializable("RESTAURANT") as Restaurant)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRestaurantDetailBinding.inflate(inflater, container, false)
        // Set menu tabs view pager
        menuViewPagerAdapter = MenutabAdapter(this)
        binding.restaurantDetailMenuViewPager.apply {
            adapter = menuViewPagerAdapter
        }

        setToolbar()
        fillDetailContent()
        addViewModelObservers()

        binding.restaurantDetailAddReviewButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_restaurantDetailFragment_to_reviewFragment,
                Bundle().apply { putString("restaurantId", viewModel.restaurant?._id!!) })
        }

        binding.restaurantDetailRating.setOnClickListener {
            findNavController().navigate(
                R.id.action_restaurantDetailFragment_to_reviewListFragment,
                Bundle().apply { putString("restaurantId", viewModel.restaurant?._id!!) }
            )
        }

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
        setupMap()

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
    }

    private fun fillDetailContent() {
        var rating =
            "${numberUtils.roundOffDecimal(viewModel.restaurant?.weighted_rating_value!!)}/5 "
        rating += "(${viewModel.restaurant?.aggregated_rating_count})"
        binding.restaurantDetailRating.text = rating
    }

    private fun setToolbar() {
        val restaurantHeader: RestaurantHeaderImage =
            binding.root.findViewById(RestaurantHeaderImage.Constants.COMPONENT_ID)

        val extraHandler = object : RestaurantHeaderImage.ExtraConfigurationsHandler {
            override fun getMenuId(): Int? = R.menu.restaurant_detail_menu
            override fun onClickBack() = activity?.onBackPressed()
        }

        val toolbar = restaurantHeader.setupForIncludedCollapsible(
            binding.root,
            viewModel.restaurant!!,
            extraHandler
        )


        val favoriteButton = toolbar.menu?.findItem(R.id.detail_menu_favorite)
        FavoriteController.instance.isLoading.observe(activity as MainActivity) {
            favoriteButton?.isVisible = FirebaseAuth.getInstance().currentUser != null && !it
        }
        // Set favorite press listener
        viewModel.isFavorite.observe(activity as MainActivity) { isFavorite ->
            favoriteButton?.setIcon(
                if (isFavorite) {
                    R.drawable.ic_baseline_favorite_24
                } else {
                    R.drawable.ic_baseline_favorite_border_24
                }
            )
        }

        favoriteButton?.setOnMenuItemClickListener {
            if (!viewModel.isFavorite.value!!) {
                viewModel.addFavorite()
            } else {
                viewModel.removeFavorite()
            }

            true
        }

    }

    private fun setupMap() {
        try {
            val mapFragment: SupportMapFragment = SupportMapFragment.newInstance()
            activity?.supportFragmentManager?.beginTransaction()
                ?.add(R.id.restaurant_detail_map, mapFragment)?.disallowAddToBackStack()
                ?.commit()

            mapFragment.getMapAsync(this)
        } catch (e: Exception) {
            // TODO: handle mpa error
            Log.i("Test", "Map error ${e.message}")
        }
    }

    private fun focusMarker(latLng: LatLng){
        val center = CameraUpdateFactory.newLatLng(latLng)
        val zoom = CameraUpdateFactory.zoomTo(15f)

        mMap.moveCamera(center)
        mMap.animateCamera(zoom)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.setAllGesturesEnabled(false)

        val position = LatLng(
            viewModel.restaurant?.address?.lat?.toDouble()!!,
            viewModel.restaurant?.address?.lon?.toDouble()!!,
        )

        mMap.addMarker(
            MarkerOptions()
                .position(position)
                .title(viewModel.restaurant?.name)
        )
        focusMarker(position)
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15.0f));
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