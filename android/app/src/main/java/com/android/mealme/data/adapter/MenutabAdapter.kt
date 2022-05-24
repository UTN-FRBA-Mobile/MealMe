package com.android.mealme.data.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.mealme.data.model.RestaurantCategory
import com.android.mealme.data.model.RestaurantMenuItem
import com.android.mealme.fragments.menuList.MenuListFragment

class MenutabAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    private var list: List<RestaurantCategory> = emptyList()

    override fun getItemCount(): Int = list.size

    override fun createFragment(position: Int): Fragment {
        val fragment = MenuListFragment()
        fragment.arguments = Bundle().apply {
            putSerializable("CATEGORY", list.get(position))
        }

        return fragment
    }

    fun setList(list: List<RestaurantCategory>){
        this.list = list
        this.notifyDataSetChanged()
    }

}