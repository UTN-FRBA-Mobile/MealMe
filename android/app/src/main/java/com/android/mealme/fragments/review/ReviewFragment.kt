package com.android.mealme.fragments.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.android.mealme.MainActivity
import com.android.mealme.databinding.FragmentReviewBinding
import com.android.mealme.ui.RestaurantHeaderImage


class ReviewFragment : Fragment() {

    private val viewModel: ReviewViewModel by viewModels()

    private var _binding: FragmentReviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString("restaurantId")?.apply {
            viewModel.setRestaurantId(this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReviewBinding.inflate(inflater, container, false)

        setupToolbar()
        setupListeners()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
        viewModel.isLoading.observe(activity as MainActivity) {
            binding.reviewLoaderContainer.isVisible = it
        }
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
        viewModel.isLoading.removeObservers(activity as MainActivity)
    }


    private fun setupToolbar() {
        val restaurantHeader: RestaurantHeaderImage =
            binding.root.findViewById(RestaurantHeaderImage.Constants.COMPONENT_ID)

        val extraHandler = object : RestaurantHeaderImage.ExtraConfigurationsHandler {
            override fun onClickBack() = activity?.onBackPressed()
        }

        restaurantHeader.setupForIncludedCollapsible(
            binding.root,
            viewModel.restaurant.value!!,
            extraHandler
        )
    }

    private fun setupListeners() {
        binding.reviewStarsScoring.setOnRatingBarChangeListener { ratingBar, fl, b ->
            viewModel.setScore(fl)
        }

        binding.reviewMessageInput.editText?.doAfterTextChanged {
            viewModel.setMessageText(it.toString())
        }

        binding.reviewSubmitButton.setOnClickListener {
            viewModel.addReview(object : Runnable {
                override fun run() {
                    activity?.onBackPressed()
                }
            })
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ReviewFragment()
    }
}