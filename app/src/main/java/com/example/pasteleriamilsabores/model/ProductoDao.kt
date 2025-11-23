package com.example.pasteleriamilsabores.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(productos: List<Producto>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(producto: Producto)

    @Query("SELECT * FROM productos ORDER BY code ASC")
    fun getAll(): Flow<List<Producto>>

    @Query("UPDATE productos SET isFavorite = :esFavorito WHERE code = :id")
    suspend fun updateFavorite(id: Int, esFavorito: Boolean)

    @Query("SELECT code FROM productos WHERE isFavorite = 1")
    suspend fun getFavoriteIds(): List<Int>

}