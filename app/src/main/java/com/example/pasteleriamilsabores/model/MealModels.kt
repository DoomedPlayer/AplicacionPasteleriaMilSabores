package com.example.pasteleriamilsabores.model

import com.google.gson.annotations.SerializedName

data class MealResponse(
    @SerializedName("meals") val meals: List<MealApi>
)

data class MealApi(
    @SerializedName("idMeal") val id: String,
    @SerializedName("strMeal") val name: String,
    @SerializedName("strMealThumb") val image: String
)