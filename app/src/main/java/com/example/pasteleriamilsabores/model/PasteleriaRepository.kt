package com.example.pasteleriamilsabores.model

import android.util.Log
import kotlinx.coroutines.flow.Flow

class PasteleriaRepository(private val productoDao: ProductoDao) {

    val productos: Flow<List<Producto>> = productoDao.getAll()

   
    suspend fun insertarProductoManual(producto: Producto){
        productoDao.insert(producto)
    }

    suspend fun actualizarProducto(producto: Producto) {
        productoDao.update(producto)
    }

    suspend fun eliminarProducto(producto: Producto) {
        productoDao.delete(producto)
    }
}