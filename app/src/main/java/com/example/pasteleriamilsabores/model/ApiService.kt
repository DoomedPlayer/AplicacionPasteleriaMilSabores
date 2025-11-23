package com.example.pasteleriamilsabores.model

import retrofit2.Response
import retrofit2.http.GET

interface ApiService{
    @GET("api/producto")
    suspend fun obtenerProductos(): Response<List<Producto>>
}