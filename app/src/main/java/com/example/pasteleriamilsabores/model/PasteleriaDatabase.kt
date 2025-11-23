package com.example.pasteleriamilsabores.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.pasteleriamilsabores.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Producto::class], version = 1, exportSchema = false)
abstract  class PasteleriaDatabase: RoomDatabase() {

    abstract fun productoDao(): ProductoDao

    companion object{
        @Volatile
        private var INSTANCE: PasteleriaDatabase? = null

        fun getDatabase(context: Context): PasteleriaDatabase{
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PasteleriaDatabase::class.java,
                    "pasteleria_database"
                )
                    .addCallback(DatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(private val context: Context) : Callback(){
        override fun onCreate(db: SupportSQLiteDatabase){
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(context, database.productoDao())
                }
            }
        }
        suspend fun populateDatabase(context: Context,productoDao: ProductoDao){
            fun drawableToUri(drawable: Int): String {
                val packageName = context.packageName
                return "android.resource://$packageName/$drawable"
            }
            val listaProductosIniciales = listOf(
                Producto(1,"Torta Cuadrada de Chocolate","Torta Circular",45000,"circular","Deliciosa torta de chocolate con capas de ganache y un toque \n" +
                        "de avellanas. Personalizable con mensajes especiales.",drawableToUri(R.drawable.chocolate)),
            )
            listaProductosIniciales.forEach { producto ->
                productoDao.insert(producto)
            }
        }
    }
}