package com.android.mealme.fragments.menuList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.mealme.R
import com.android.mealme.data.adapter.MenuItemsAdapter
import com.android.mealme.data.model.RestaurantCategory
import com.android.mealme.data.model.RestaurantMenuItem
import com.android.mealme.databinding.FragmentMenuListBinding

class MenuListFragment : Fragment() {
    private var _binding: FragmentMenuListBinding? = null
    val binding get() = _binding!!

    private var category: RestaurantCategory? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category = arguments?.getSerializable("CATEGORY") as RestaurantCategory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMenuListBinding.inflate(inflater)

        val _adapter = MenuItemsAdapter()
        _adapter.setList(category?.menuItemList!!)
        binding.menuListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = _adapter
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = MenuListFragment()
    }
}