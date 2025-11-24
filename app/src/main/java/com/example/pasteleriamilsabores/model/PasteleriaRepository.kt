package com.example.pasteleriamilsabores.model

import android.util.Log
import kotlinx.coroutines.flow.Flow

class PasteleriaRepository(private val productoDao: ProductoDao) {

    val productos: Flow<List<Producto>> = productoDao.getAll()


    suspend fun refrescarProductos(){
        try {
            val response = RetrofitClient.apiService.obtenerProductos()
            if(response.isSuccessful){
                response.body()?.let { listaApi ->

                    productoDao.insertAll(listaApi)
                }
            }else {
                Log.e("REPO", "Error en API: ${response.code()}")
            }
        }catch (e: Exception){
            Log.e("REPO","Error de conexion: ${e.message}")
        }
    }

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