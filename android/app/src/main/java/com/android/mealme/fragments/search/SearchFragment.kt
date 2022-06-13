package com.android.mealme.fragments.search

import android.content.Context
import android.location.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.android.mealme.R
import com.android.mealme.databinding.FragmentSearchBinding
import com.google.android.material.snackbar.Snackbar
import java.io.IOException


class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null

    private val binding get() = _binding!!

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
            val imm: InputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireView().windowToken, 0)
            if(binding.addressSearch?.editText?.text.toString().isNullOrEmpty() &&
                binding.nameSearch?.editText?.text.toString().isNullOrEmpty()){
                Snackbar.make(activity?.findViewById(R.id.busquedaLayout)!!,
                    resources.getString(R.string.errorAddress), Snackbar.LENGTH_LONG)
                    .show();
            }else{
                if(!binding.addressSearch?.editText?.text.toString().isNullOrEmpty()){
                    searchByAddress(binding.addressSearch?.editText?.text.toString())
                }else{
                    searchByName(binding.nameSearch?.editText?.text.toString())
                }
            }
        }

        return root
    }

    private fun searchByName(toString: String) {
        val bundle = Bundle().apply {
            putSerializable("filtroName", true)
            putSerializable("name", toString)
        }
        findNavController().navigate(R.id.action_nav_search_to_nav_home,bundle)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun searchByAddress(strAddress: String?) {
        val coder = Geocoder(requireContext())
        val address: List<Address>?
        try {
            address = coder.getFromLocationName(strAddress, 5)
            if (address == null || address.isEmpty()) {
                Snackbar.make(activity?.findViewById(R.id.busquedaLayout)!!,
                    resources.getString(R.string.errorAddress), Snackbar.LENGTH_LONG)
                    .show();
            }else{
                val location: Address = address[0]

                val bundle = Bundle().apply {
                    putSerializable("filtroGeo", true)
                    putSerializable("latitud", location.latitude)
                    putSerializable("longitud", location.longitude)
                }
                findNavController().navigate(R.id.action_nav_search_to_nav_home,bundle)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}