package com.android.mealme.fragments.reviewList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.mealme.data.adapter.ReviewsAdapter
import com.android.mealme.data.controller.ReviewController
import com.android.mealme.databinding.FragmentReviewListBinding

class ReviewListFragment : Fragment() {
    private var restaurantId: String? = null

    private lateinit var _binding: FragmentReviewListBinding
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        restaurantId = arguments?.getString("restaurantId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReviewListBinding.inflate(inflater, container, false)

        val adapter = ReviewsAdapter.init(binding.reviewListRecyclerView, requireContext())

        ReviewController.instance.getReviewsForRestaurant(restaurantId!!).thenApply {
            adapter.setReviews(it)
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = ReviewListFragment()
    }
}