package com.android.mealme.fragments.search

import android.content.Context
import android.location.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController
import com.android.mealme.R
import com.android.mealme.databinding.FragmentSearchBinding
import com.android.mealme.fragments.home.HomeFragment
import com.google.android.material.snackbar.Snackbar
import java.io.IOException


class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null

    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var inputMethodManager: InputMethodManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        binding.nameSearch.editText?.setText(viewModel.name)
        binding.addressSearch.editText?.setText(viewModel.address)

        inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        binding.addressSearch.editText?.doAfterTextChanged {
            viewModel.setAddress(it.toString())
        }
        binding.nameSearch.editText?.doAfterTextChanged {
            viewModel.setName(it.toString())
        }

        binding.searchByAddress.setOnClickListener() {
            inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)

            if (viewModel.address.isNullOrEmpty() && viewModel.name.isNullOrEmpty()) {
                binding.addressSearch.isErrorEnabled = true
                binding.nameSearch.isErrorEnabled = true

                Snackbar.make(
                    activity?.findViewById(R.id.busquedaLayout)!!,
                    resources.getString(R.string.errorAddress), Snackbar.LENGTH_LONG
                ).show()

            } else {
                searchFromEnteredValues()
            }
        }


        return binding.root
    }

    private fun searchFromEnteredValues() {
        val bundle = Bundle().apply {
            putString(HomeFragment.HOME_SEARCH_NAME, viewModel.name)
            putString(HomeFragment.HOME_SEARCH_ADDRESS, viewModel.address)
        }

        findNavController().navigate(R.id.action_nav_search_to_nav_home, bundle)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}