package com.example.pasteleriamilsabores.view

import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.DarkMode
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pasteleriamilsabores.ui.theme.PasteleriaMilSaboresTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.pasteleriamilsabores.model.CarritoItem
import com.example.pasteleriamilsabores.model.Producto
import com.example.pasteleriamilsabores.viewmodel.PasteleriaViewModel
import com.example.pasteleriamilsabores.R
import com.example.pasteleriamilsabores.navigation.PasteleriaHost
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PasteleriaApp(){
    val systemDarkMode = isSystemInDarkTheme()
    var isDarkMode by rememberSaveable { mutableStateOf(systemDarkMode) } //

    // 2. Aplicar el tema (PasteleriaMilSaboresTheme) con el estado
    PasteleriaMilSaboresTheme(darkTheme = isDarkMode) { //
        // 3. Llamar a PasteleriaHost pasándole el estado y el alternador
        PasteleriaHost(
            isDarkMode = isDarkMode,
            onToggleDarkMode = { isDarkMode = !isDarkMode }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasteleriaScreen(
    viewModel: PasteleriaViewModel,
    onNavigateToForm: ()  -> Unit,
    onNavigateToPago: ()  -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit
){
    val carritoItems = viewModel.carrito.value
    val productos by viewModel.productos.collectAsStateWithLifecycle()
    val total = viewModel.totalCarrito
    val carritoCount = carritoItems.sumOf { it.cantidad }

    var mostrarCarrito by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopBarPasteleria(
                carritoItemCount = carritoCount,
                onCarritoClick = { mostrarCarrito = true },
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToForm) {
                Icon(Icons.Filled.Add, contentDescription = "Añadir Producto")
            }
        }

    ) {
        paddingValues ->
        ContenidoPrincipalPasteleria(
            paddingValues=paddingValues,
            productos= productos,
            onAgregarClick = viewModel::agregarCarrito
        )
    }
    if (mostrarCarrito){
        ModalBottomSheet(
            onDismissRequest = {mostrarCarrito = false},
            sheetState = rememberModalBottomSheetState()
        ) {
            CarritoSheetContent(
                carritoItems = carritoItems,
                onModificarCantidad = viewModel::modificarCantidad,
                total = total,
                onCerrar = {mostrarCarrito = false},
                onProcederPago = {
                    mostrarCarrito = false
                    onNavigateToPago()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarPasteleria(carritoItemCount: Int, onCarritoClick: () -> Unit,
                     isDarkMode: Boolean,
                     onToggleDarkMode: () -> Unit){
    TopAppBar(
        title = {
            Text(
                text= "Pasteleria Mil Sabores",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
        },
        actions = {

            IconButton(onClick = onToggleDarkMode) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                    contentDescription = "Alternar modo oscuro"
                )
            }

            BadgedBox(
                badge = {
                    if (carritoItemCount > 0){
                        Badge { Text(carritoItemCount.toString())}
                    }
                }
            ) {
                IconButton(onClick = onCarritoClick) {
                    Icon(Icons.Filled.ShoppingCart, contentDescription = "Carrito de Compras")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    )
}

@Composable
fun ContenidoPrincipalPasteleria(
    paddingValues: PaddingValues,
    productos: List<Producto>,
    onAgregarClick: (Producto) -> Unit
){
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "¡El sabor de la tradición en cada bocado!",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        items(productos, key = {it.id}){ producto ->
            CardProductoPasteleria(
                producto= producto,
                onAgregarClick = { onAgregarClick(producto)}
            )
        }
    }
}

@Composable
fun CardProductoPasteleria(producto: Producto, onAgregarClick: () -> Unit){
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ){
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            val imageUri = producto.imagenSource?.let { Uri.parse(it) }
            val painter = if(imageUri != null){
                rememberAsyncImagePainter(model = imageUri)
            }else{
                painterResource(id = R.drawable.ic_default_cake)
            }

            Image(
                painter = painter,
                contentDescription = producto.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$${producto.precio}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Button(
                onClick = onAgregarClick,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Añadir a carrito", modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun CarritoSheetContent(
    carritoItems: List<CarritoItem>,
    onModificarCantidad: (Producto, Int) -> Unit,
    total: Int,
    onCerrar: () -> Unit,
    onProcederPago: () -> Unit
){
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tu Pedido",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onCerrar) {
                Icon(Icons.Filled.Close, contentDescription = "Cerrar")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (carritoItems.isEmpty()){
            Text("Tu carrito está vacío. ¡Añade un producto!", modifier = Modifier.fillMaxWidth().padding(32.dp))
        }else{
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(carritoItems,key =  {it.producto.id}){ item ->
                    CarritoItemRow(item, onModificarCantidad)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            val total = carritoItems.sumOf{it.producto.precio * it.cantidad}
            Text(
                text = "Total: $${total}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onProcederPago,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Proceder al Pago")
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun CarritoItemRow(item: CarritoItem, onModificarCantidad: (Producto,Int) -> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.producto.nombre, fontWeight = FontWeight.SemiBold)
            Text("$${item.producto.precio}", style = MaterialTheme.typography.bodySmall)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {onModificarCantidad(item.producto,item.cantidad-1)}) {
                Icon(Icons.Filled.Remove, contentDescription = "Quitar uno")
            }
            Text(item.cantidad.toString(), modifier = Modifier.padding(horizontal = 8.dp))

            IconButton(onClick = {onModificarCantidad(item.producto,item.cantidad +1)}) {
                Icon(Icons.Filled.Add, contentDescription = "Añadir uno")
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PasteleriaPreview(){
    var isDarkMode = true

    PasteleriaMilSaboresTheme {
        PasteleriaScreen(
            viewModel = viewModel(),
            onNavigateToForm = {},
            onNavigateToPago = {},
            isDarkMode = isDarkMode,
            onToggleDarkMode = {}
        )
    }
}