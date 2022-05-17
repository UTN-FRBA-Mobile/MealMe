package com.android.mealme.data.model

data class Restaurant(
    val _id: String,
    val name: String,
    val phone_number: String,
    val logo_photos: Array<String>,
    val address: RestaurantAddress,
    val cusines: Array<String>,
    val weighted_rating_value: Double,
    val aggregated_rating_count: Int,
    )
