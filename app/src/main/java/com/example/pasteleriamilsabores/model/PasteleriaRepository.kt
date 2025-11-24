package com.example.pasteleriamilsabores.model

import android.util.Log
import kotlinx.coroutines.flow.Flow

class PasteleriaRepository(private val productoDao: ProductoDao) {

    val productos: Flow<List<Producto>> = productoDao.getAll()

    suspend fun toggleFavorito(id: Int, esFavoritoActual: Boolean) {
        productoDao.updateFavorite(id, !esFavoritoActual)
    }

    suspend fun refrescarProductos(){
        try {
            val response = RetrofitClient.apiService.obtenerProductos()
            if(response.isSuccessful){
                response.body()?.let { listaApi ->
                    val idsFavoritos = productoDao.getFavoriteIds()

                    val listaParaGuardar = listaApi.map { prod ->
                        if (idsFavoritos.contains(prod.code)) {
                            prod.copy(isFavorite = true)
                        } else {
                            prod
                        }
                    }

                    productoDao.insertAll(listaParaGuardar)
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