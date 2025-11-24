package com.example.pasteleriamilsabores.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pasteleriamilsabores.model.*
import kotlinx.coroutines.launch
import com.example.pasteleriamilsabores.model.PasteleriaDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

class PasteleriaViewModel(application: Application) : AndroidViewModel(application){

    private val database = PasteleriaDatabase.getDatabase(application)
    private val repository= PasteleriaRepository(database.productoDao())

    private val _postresInspiracion = MutableStateFlow<List<MealApi>>(emptyList())
    val postresInspiracion: StateFlow<List<MealApi>> = _postresInspiracion.asStateFlow()
    private val _cargandoInspiracion = MutableStateFlow(true)
    val cargandoInspiracion: StateFlow<Boolean> = _cargandoInspiracion.asStateFlow()
    private val _recetaSeleccionada = MutableStateFlow<MealApi?>(null)
    val recetaSeleccionada: StateFlow<MealApi?> = _recetaSeleccionada.asStateFlow()

    private val _cargandoReceta = MutableStateFlow(false)
    val cargandoReceta: StateFlow<Boolean> = _cargandoReceta.asStateFlow()

    val productos: StateFlow<List<Producto>> = repository.productos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _carrito = mutableStateOf(emptyList<CarritoItem>())
    val carrito: State<List<CarritoItem>> = _carrito

    val totalCarrito: Int
        get() = _carrito.value.sumOf { it.producto.price * it.cantidad }

    init {
        cargarProductosApi()
        cargarInspiracion()
    }

    fun cargarProductosApi() {
        viewModelScope.launch {
            repository.refrescarProductos()
        }
    }

    fun cargarInspiracion() {
        viewModelScope.launch {
            _cargandoInspiracion.value = true
            try {
                val response = RetrofitMealClient.service.getPostresInternacionales()
                if (response.isSuccessful) {
                    val listaCompleta = response.body()?.meals ?: emptyList()
                    _postresInspiracion.value = listaCompleta.shuffled().take(5)
                }
            } catch (e: Exception) {
                Log.e("API_MEAL", "Error: ${e.message}")
            } finally {
                _cargandoInspiracion.value = false
            }
        }
    }

    fun agregarCarrito(producto: Producto) {
        viewModelScope.launch {
            val itemExistente = _carrito.value.find { it.producto.code == producto.code }

            _carrito.value = if (itemExistente != null) {
                _carrito.value.map {
                    if (it.producto.code == producto.code) it.copy(cantidad = it.cantidad + 1) else it
                }
            } else {
                _carrito.value + CarritoItem(producto = producto, cantidad = 1)
            }.sortedBy { it.producto.code }
        }
    }

    fun modificarCantidad(producto: Producto, nuevaCantidad: Int) {
        viewModelScope.launch {
            _carrito.value = _carrito.value.mapNotNull {
                if (it.producto.code == producto.code) {
                    if (nuevaCantidad > 0) it.copy(cantidad = nuevaCantidad) else null
                } else {
                    it
                }
            }.sortedBy { it.producto.code }
        }
    }

    fun limpiarCarrito() {
        _carrito.value = emptyList()
    }

    fun toggleFavorito(producto: Producto) {
        viewModelScope.launch {
            repository.toggleFavorito(producto.code, producto.isFavorite)
        }
    }

    fun seleccionarRecetaInspiracion(idMeal: String) {
        viewModelScope.launch {
            _cargandoReceta.value = true
            _recetaSeleccionada.value = null // Limpiamos la anterior para que no parpadee
            try {
                val response = RetrofitMealClient.service.getDetallePostre(idMeal)
                if (response.isSuccessful) {
                    // La API devuelve una lista de 1 elemento
                    val detalle = response.body()?.meals?.firstOrNull()
                    _recetaSeleccionada.value = detalle
                }
            } catch (e: Exception) {
                Log.e("API_DETAIL", "Error: ${e.message}")
            } finally {
                _cargandoReceta.value = false
            }
        }
    }

    fun cerrarModalReceta() {
        _recetaSeleccionada.value = null
    }
    fun agregarNuevoProducto(id:Int,nombre: String, descripcion: String, precio: Int,categoria:String,tipo:String, imagen: Uri?){
        viewModelScope.launch {
            val nuevoProducto = Producto(
                code = id,
                name= nombre,
                description = descripcion,
                category = categoria,
                type = tipo,
                price = precio,
                image = imagen.toString()
            )
            repository.insertarProductoManual(nuevoProducto)
        }
    }

    fun actualizarProducto(id: Int, nombre: String, descripcion: String, precio: Int, imagen: Uri?) {
        viewModelScope.launch {
            val productoOriginal = productos.value.find { it.code == id }

            if (productoOriginal != null) {
                val productoActualizado = productoOriginal.copy(
                    name = nombre,
                    description = descripcion,
                    price = precio,
                    image = imagen?.toString() ?: productoOriginal.image
                )
                repository.actualizarProducto(productoActualizado)
            }
        }
    }

    fun eliminarProducto(producto: Producto) {
        viewModelScope.launch {
            repository.eliminarProducto(producto)
            _carrito.value = _carrito.value.filter { it.producto.code != producto.code }
        }
    }

    fun obtenerProductoPorId(id: Int): Producto? {
        return productos.value.find { it.code == id }
    }

}
