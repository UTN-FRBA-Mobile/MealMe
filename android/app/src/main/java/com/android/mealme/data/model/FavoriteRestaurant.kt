package com.android.mealme.data.model

data class FavoriteRestaurant(
    val _id: String,
    val name: String,
    val address: RestaurantAddress,
    val logo_photos: List<String>,
    val aggregated_rating_count: Int,
    val weighted_rating_value: Double,
) {
    fun toRestaurant(): Restaurant {
        return Restaurant(
            _id,
            name,
            "",
            logo_photos,
            address,
            emptyArray(),
            weighted_rating_value,
            aggregated_rating_count
        )
    }
}