package com.android.mealme.data.model

import kotlinx.serialization.Serializable

@Serializable
class RestaurantMenuItem: java.io.Serializable {
    val name: String
    val price: Double
    val description: String
    val product_id: String
    val image: String

    constructor(
        name: String,
        price: Double,
        description: String,
        product_id: String,
        image: String
    ) {
        this.name = name
        this.price = price
        this.description = description
        this.product_id = product_id
        this.image = image
    }
}