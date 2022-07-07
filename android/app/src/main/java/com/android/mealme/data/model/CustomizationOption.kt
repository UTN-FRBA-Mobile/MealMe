package com.android.mealme.data.model

import kotlinx.serialization.Serializable

@Serializable
class CustomizationOption: java.io.Serializable {
    val name: String
    val price: Number
    val customizations : List<MenuItemCustomization>

    constructor(
        name: String,
        price: Number,
        customizations: List<MenuItemCustomization>
    ) {
        this.name = name
        this.price = price
        this.customizations = customizations
    }
}