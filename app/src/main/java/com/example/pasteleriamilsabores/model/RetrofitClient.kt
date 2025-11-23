package com.example.pasteleriamilsabores.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient{
    private const val BASE_URL = "https://api-mil-sabores-5.onrender.com/"

     val apiService: ApiService by lazy {
         Retrofit.Builder()
             .baseUrl(BASE_URL)
             .addConverterFactory(GsonConverterFactory.create())
             .build()
             .create(ApiService::class.java)
     }
}