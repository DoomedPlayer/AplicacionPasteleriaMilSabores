package com.example.pasteleriamilsabores.viewmodel

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pasteleriamilsabores.model.CarritoItem
import com.example.pasteleriamilsabores.model.Producto
import kotlinx.coroutines.launch
import com.example.pasteleriamilsabores.model.PasteleriaDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class PasteleriaViewModel(application: Application) : AndroidViewModel(application){

    private val productoDao = PasteleriaDatabase.getDatabase(application).productoDao()
    val productos: StateFlow<List<Producto>> = productoDao.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    private val _carrito = mutableStateOf(emptyList<CarritoItem>())
    val carrito: State<List<CarritoItem>> = _carrito

    val totalCarrito: Int
        get() =_carrito.value.sumOf { it.producto.precio * it.cantidad }

    fun agregarCarrito(producto: Producto){
        viewModelScope.launch {
            val itemExistente = _carrito.value.find { it.producto.id == producto.id }
            _carrito.value = if(itemExistente != null){
                _carrito.value.map {
                    if (it.producto.id==producto.id) it.copy(cantidad = it.cantidad+1) else it
                }
            }
            else{
                _carrito.value + CarritoItem(producto=producto, cantidad = 1)
            }.sortedBy{it.producto.id}
        }
    }

    fun modificarCantidad(producto: Producto,nuevaCantidad: Int){
        viewModelScope.launch {
            _carrito.value = _carrito.value.mapNotNull {
                if (it.producto.id == producto.id){
                    if (nuevaCantidad > 0) it.copy(cantidad = nuevaCantidad) else null
                } else {
                    it
                }
            }.sortedBy { it.producto.id }
        }
    }

    fun agregarNuevoProducto(nombre: String, descripcion: String, precio: Int, imagen: Uri?){
        viewModelScope.launch {


            val nuevoProducto = Producto(
                nombre= nombre,
                descripcion = descripcion,
                precio = precio,
                imagenSource = imagen?.toString()
            )
            productoDao.insert(nuevoProducto)
        }
    }
    fun limpiarCarrito() {
        _carrito.value = emptyList()
        // También es importante recalcular el total
        totalCarrito
    }

}
