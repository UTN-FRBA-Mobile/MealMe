package com.android.mealme.data.model

import java.io.Serializable

class Restaurant: Serializable {
    val _id: String
    val name: String
    val phone_number: String
    val logo_photos: Array<String>
    val address: RestaurantAddress
    val cusines: Array<String>
    val weighted_rating_value: Double
    val aggregated_rating_count: Int
    lateinit var categories: List<RestaurantCategory>


    constructor(
        _id: String,
        name: String,
        phone_number: String,
        logo_photos: Array<String>,
        address: RestaurantAddress,
        cusines: Array<String>,
        weighted_rating_value: Double,
        aggregated_rating_count: Int,
    ) {
        this._id = _id
        this.name = name
        this.phone_number = phone_number
        this.logo_photos = logo_photos
        this.address = address
        this.cusines = cusines
        this.weighted_rating_value = weighted_rating_value
        this.aggregated_rating_count = aggregated_rating_count
    }
}
