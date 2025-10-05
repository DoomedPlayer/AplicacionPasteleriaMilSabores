package com.example.pasteleriamilsabores.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pasteleriamilsabores.model.CarritoItem
import com.example.pasteleriamilsabores.model.Producto
import com.example.pasteleriamilsabores.model.listaProductos
import kotlinx.coroutines.launch

class PasteleriaViewModel : ViewModel(){
    private val _productos = mutableStateOf(listaProductos)
    val productos: State<List<Producto>> = _productos

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

}
