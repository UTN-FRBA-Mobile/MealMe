package com.android.mealme.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RestaurantCategory: Serializable{
    val name: String
    @SerializedName("menu_item_list")
    val menuItemList: List<RestaurantMenuItem>

    constructor(name: String, menuItemList: List<RestaurantMenuItem>) {
        this.name = name
        this.menuItemList = menuItemList
    }
}