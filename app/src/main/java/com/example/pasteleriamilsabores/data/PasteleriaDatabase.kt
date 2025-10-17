package com.example.pasteleriamilsabores.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.pasteleriamilsabores.R
import com.example.pasteleriamilsabores.model.Producto
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

    private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback(){
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
                Producto(1,"Torta Cuadrada de Chocolate","Deliciosa torta de chocolate con capas de ganache y un toque \n" +
                        "de avellanas. Personalizable con mensajes especiales.",45000,drawableToUri(R.drawable.chocolate)),
                Producto(2,"Torta Cuadrada de Frutas","Una mezcla de frutas frescas y crema chantilly sobre un suave \n" +
                        "bizcocho de vainilla, ideal para celebraciones.",50000,drawableToUri(R.drawable.frutas)),
                Producto(3,"Torta Circular de Vainilla"," Bizcocho de vainilla clásico relleno con crema pastelera y cubierto \n" +
                        "con un glaseado dulce, perfecto para cualquier ocasión.",40000,drawableToUri(R.drawable.vainilla)),
                Producto(4,"Torta Circular de Manjar"," Torta tradicional chilena con manjar y nueces, un deleite para los \n" +
                        "amantes de los sabores dulces y clásicos.",42000,drawableToUri(R.drawable.manjar)),
                Producto(5,"Mousse de Chocolate","Postre individual cremoso y suave, hecho con chocolate de alta \n" +
                        "calidad, ideal para los amantes del chocolate.",5000,drawableToUri(R.drawable.mousse)),
                Producto(6,"Tiramisú Clásico","Un postre italiano individual con capas de café, mascarpone y cacao, \n" +
                        "perfecto para finalizar cualquier comida.",5500,drawableToUri(R.drawable.tiramisu)),
                Producto(7,"Torta Sin Azúcar de Naranja","Torta ligera y deliciosa, endulzada naturalmente, ideal para \n" +
                        "quienes buscan opciones más saludables.",48000,drawableToUri(R.drawable.naranja)),
                Producto(8,"Cheesecake Sin Azúcar","Suave y cremoso, este cheesecake es una opción perfecta para \n" +
                        "disfrutar sin culpa.",47000,drawableToUri(R.drawable.cheesecake)),
                Producto(9,"Empanada de Manzana","Pastelería tradicional rellena de manzanas especiadas, perfecta \n" +
                        "para un dulce desayuno o merienda.",3000,drawableToUri(R.drawable.empanada)),
                Producto(10,"Tarta de Santiago","Tradicional tarta española hecha con almendras, azúcar, y huevos, una \n" +
                        "delicia para los amantes de los postres clásicos.",6000,drawableToUri(R.drawable.santiago)),
                Producto(11,"Brownie Sin Gluten","Rico y denso, este brownie es perfecto para quienes necesitan evitar \n" +
                        "el gluten sin sacrificar el sabor.",4000,drawableToUri(R.drawable.brownie)),
                Producto(12,"Pan Sin Gluten","Suave y esponjoso, ideal para sándwiches o para acompañar cualquier \n" +
                        "comida.",3500,drawableToUri(R.drawable.pan)),
                Producto(13,"Torta Vegana de Chocolate"," Torta de chocolate húmeda y deliciosa, hecha sin productos de \n" +
                        "origen animal, perfecta para veganos. ",50000,drawableToUri(R.drawable.vegana)),
                Producto(14,"Galletas Veganas de Avena","Crujientes y sabrosas, estas galletas son una excelente opción \n" +
                        "para un snack saludable y vegano.",4500,drawableToUri(R.drawable.galletas)),
                Producto(15,"Torta Especial de Cumpleaños","Diseñada especialmente para celebraciones, personalizable \n" +
                        "con decoraciones y mensajes únicos.",55000,drawableToUri(R.drawable.cumpleanos)),
                Producto(16,"Torta Especial de Boda"," Elegante y deliciosa, esta torta está diseñada para ser el centro de \n" +
                        "atención en cualquier boda. ",60000,drawableToUri(R.drawable.boda)),
            )
            listaProductosIniciales.forEach { producto ->
                productoDao.insert(producto)
            }
        }
    }
}