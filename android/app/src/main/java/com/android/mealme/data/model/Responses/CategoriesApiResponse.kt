package com.android.mealme.data.model.Responses

import com.android.mealme.data.model.RestaurantCategory

data class CategoriesApiResponse(
    val categories: List<RestaurantCategory>
)