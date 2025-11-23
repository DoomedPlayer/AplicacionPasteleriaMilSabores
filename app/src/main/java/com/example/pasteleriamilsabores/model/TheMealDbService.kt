package com.example.pasteleriamilsabores.model

import retrofit2.Response
import retrofit2.http.GET

interface TheMealDbService {
    @GET("filter.php?c=Dessert")
    suspend fun getPostresInternacionales(): Response<MealResponse>
}

object RetrofitMealClient{
    private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

    val service: TheMealDbService by lazy {
        retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
            .create(TheMealDbService::class.java)
    }
}