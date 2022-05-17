package com.android.mealme.data.model

data class RestaurantAddress(
    val street_addr: String,
    val city: String,
    val state: String,
    val country: String,
    val lat: Float,
    val lon: Float,
) {
    override fun toString(): String {
        return "${this.street_addr}, ${this.city}, ${this.state} - ${this.country}"
    }
}