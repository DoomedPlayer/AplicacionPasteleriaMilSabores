package com.example.pasteleriamilsabores.model

import com.example.pasteleriamilsabores.R
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey(autoGenerate = true)
    val id: Int =0,
    val nombre: String,
    val descripcion: String,
    val precio: Int,
    val imagenSource: String?
)

data class CarritoItem(
    val producto: Producto,
    val cantidad: Int
)
