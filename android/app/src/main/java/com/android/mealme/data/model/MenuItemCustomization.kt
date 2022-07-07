package com.android.mealme.data.model

import kotlinx.serialization.Serializable

@Serializable
class MenuItemCustomization: java.io.Serializable {
    val name: String
    val options: List<CustomizationOption>

    constructor(
        name: String,
        options: List<CustomizationOption>
    ) {
        this.name = name
        this.options = options
    }
}