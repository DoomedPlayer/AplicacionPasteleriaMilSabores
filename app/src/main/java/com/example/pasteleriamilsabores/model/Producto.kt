package com.example.pasteleriamilsabores.model

import androidx.room.Entity

import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey
    val code: Int,
    val name: String,
    val category: String,
    val price: Int,
    val type: String,
    val description: String,
    val image: String
)
data class CarritoItem(
    val producto: Producto,
    val cantidad: Int
)
