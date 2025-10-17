package com.example.pasteleriamilsabores.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.pasteleriamilsabores.model.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    @Insert
    suspend fun insert(producto: Producto)

    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun getAll(): Flow<List<Producto>>

}