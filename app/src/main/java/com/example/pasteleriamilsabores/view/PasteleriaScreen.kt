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
import androidx.compose.ui.text.style.TextOverflow
import com.example.pasteleriamilsabores.model.MealApi
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import coil.request.ImageRequest

enum class ModoInteraccion { NORMAL, EDITAR, ELIMINAR }
@Composable
fun PasteleriaApp(){
    val systemDarkMode = isSystemInDarkTheme()
    var isDarkMode by rememberSaveable { mutableStateOf(systemDarkMode) } //
    PasteleriaMilSaboresTheme(darkTheme = isDarkMode) { //
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
    onNavigateToModificar: (Int)  -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit
) {
    val carritoItems = viewModel.carrito.value
    val productos by viewModel.productos.collectAsStateWithLifecycle()
    val postresInspiracion by viewModel.postresInspiracion.collectAsStateWithLifecycle()
    val cargandoInspiracion by viewModel.cargandoInspiracion.collectAsStateWithLifecycle()
    val total = viewModel.totalCarrito
    val carritoCount = carritoItems.sumOf { it.cantidad }
    var mostrarCarrito by remember { mutableStateOf(false) }
    val recetaSeleccionada by viewModel.recetaSeleccionada.collectAsStateWithLifecycle()
    val cargandoReceta by viewModel.cargandoReceta.collectAsStateWithLifecycle()
    var menuExpandido by remember { mutableStateOf(false) }
    var modoActual by remember { mutableStateOf(ModoInteraccion.NORMAL) }
    Scaffold(
        topBar = {
            TopBarPasteleria(carritoCount, { mostrarCarrito = true }, isDarkMode, onToggleDarkMode)
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                if (menuExpandido) {
                    FloatingActionButton(
                        onClick = {
                            menuExpandido = false; modoActual =
                            ModoInteraccion.NORMAL; onNavigateToForm()
                        },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.padding(bottom = 8.dp).size(48.dp)
                    ) { Icon(Icons.Filled.Add, "Agregar Nuevo") }

                    FloatingActionButton(
                        onClick = {
                            menuExpandido = false
                            modoActual =
                                if (modoActual == ModoInteraccion.EDITAR) ModoInteraccion.NORMAL else ModoInteraccion.EDITAR
                        },
                        containerColor = if (modoActual == ModoInteraccion.EDITAR) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.padding(bottom = 8.dp).size(48.dp)
                    ) { Icon(Icons.Filled.Edit, "Modificar") }

                    FloatingActionButton(
                        onClick = {
                            menuExpandido = false
                            modoActual =
                                if (modoActual == ModoInteraccion.ELIMINAR) ModoInteraccion.NORMAL else ModoInteraccion.ELIMINAR
                        },
                        containerColor = if (modoActual == ModoInteraccion.ELIMINAR) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.padding(bottom = 8.dp).size(48.dp)
                    ) { Icon(Icons.Filled.Delete, "Eliminar") }
                }
                FloatingActionButton(onClick = { menuExpandido = !menuExpandido }) {
                    Icon(if (menuExpandido) Icons.Filled.Close else Icons.Filled.Settings, "Config")
                }
            }
        }

    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            if (modoActual != ModoInteraccion.NORMAL) {
                Surface(
                    color = if (modoActual == ModoInteraccion.ELIMINAR) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (modoActual == ModoInteraccion.EDITAR) "SELECCIONA PARA EDITAR" else "SELECCIONA PARA ELIMINAR",
                        color = MaterialTheme.colorScheme.onError,
                        modifier = Modifier.padding(8.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
            ContenidoPrincipalPasteleria(
                paddingValues = PaddingValues(0.dp),
                productos = productos,
                postresInspiracion = postresInspiracion,
                cargandoInspiracion = cargandoInspiracion,
                onAgregarClick = { producto ->
                    when (modoActual) {
                        ModoInteraccion.NORMAL -> viewModel.agregarCarrito(producto)
                        ModoInteraccion.EDITAR -> {
                            onNavigateToModificar(producto.code)
                            modoActual =
                                ModoInteraccion.NORMAL
                        }

                        ModoInteraccion.ELIMINAR -> viewModel.eliminarProducto(producto)
                    }
                },
                onInspiracionClick = { idMeal -> viewModel.seleccionarRecetaInspiracion(idMeal) },
                modoActual = modoActual
            )
        }

        if (mostrarCarrito) {
            ModalBottomSheet(
                onDismissRequest = { mostrarCarrito = false },
                sheetState = rememberModalBottomSheetState()
            ) {
                CarritoSheetContent(
                    carritoItems = carritoItems,
                    onModificarCantidad = viewModel::modificarCantidad,
                    total = total,
                    onCerrar = { mostrarCarrito = false },
                    onProcederPago = {
                        mostrarCarrito = false
                        onNavigateToPago()
                    }
                )
            }
        }
        if (cargandoReceta) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                CircularProgressIndicator()
            }
        }
        recetaSeleccionada?.let { receta ->
            AlertDialog(
                onDismissRequest = { viewModel.cerrarModalReceta() },
                title = {
                    Text(text = receta.name, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        // Imagen
                        val context = androidx.compose.ui.platform.LocalContext.current
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(context)
                                    .data(receta.image.replace("http:","https:"))
                                    .crossfade(true).build()
                            ),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Ingredientes:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        receta.obtenerIngredientesFormat().forEach { ing ->
                            Text(text = ing, style = MaterialTheme.typography.bodyMedium)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Instrucciones:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            text = receta.instructions ?: "Sin instrucciones disponibles.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Justify
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.cerrarModalReceta() }) {
                        Text("Cerrar")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarPasteleria(
    carritoItemCount: Int, onCarritoClick: () -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Pasteleria Mil Sabores",
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
                    if (carritoItemCount > 0) {
                        Badge { Text(carritoItemCount.toString()) }
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
    postresInspiracion: List<MealApi>,
    cargandoInspiracion: Boolean,
    onInspiracionClick: (String) -> Unit,
    onAgregarClick: (Producto) -> Unit,
    modoActual: ModoInteraccion
) {
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
        if (postresInspiracion.isNotEmpty()) {
            item {
                Text(
                    text = "Inspiración del Mundo \uD83C\uDF0D",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    if (cargandoInspiracion) {
                        items(3) {
                            SkeletonCardInspiracion()
                        }
                    } else {
                        items(postresInspiracion) { meal ->
                            CardInspiracion(
                                meal = meal,
                                onClick = { onInspiracionClick(meal.id) }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        items(productos, key = { it.code }) { producto ->
            CardProductoPasteleria(
                producto = producto,
                onAgregarClick = { onAgregarClick(producto) },
                modoActual = modoActual
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardProductoPasteleria(
    producto: Producto,
    onAgregarClick: () -> Unit,
    modoActual: ModoInteraccion
){
    val containerColor = when(modoActual) {
        ModoInteraccion.ELIMINAR -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
        ModoInteraccion.EDITAR -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(12.dp),
        onClick = {
            if (modoActual != ModoInteraccion.NORMAL) onAgregarClick()
        }
    ){
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val imageUri = producto.image.let { Uri.parse(it) }
                val painter = if (imageUri != null) rememberAsyncImagePainter(model = imageUri) else painterResource(id = R.drawable.ic_default_cake)

                Image(
                    painter = painter,
                    contentDescription = producto.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(90.dp).clip(RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = producto.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = "$${producto.price}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                }

                IconButton(
                    onClick = onAgregarClick,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .background(
                            when(modoActual){
                                ModoInteraccion.ELIMINAR -> MaterialTheme.colorScheme.error
                                ModoInteraccion.EDITAR -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.primaryContainer
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Icon(
                        imageVector = when(modoActual) {
                            ModoInteraccion.ELIMINAR -> Icons.Filled.Delete
                            ModoInteraccion.EDITAR -> Icons.Filled.Edit
                            else -> Icons.Filled.Add
                        },
                        contentDescription = "Acción",
                        tint = if (modoActual != ModoInteraccion.NORMAL) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun CardInspiracion(meal: MealApi, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(180.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = onClick
    ) {
        Column {
            val context = androidx.compose.ui.platform.LocalContext.current

            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(context)
                        .data(meal.image.replace("http:","https:"))
                        .crossfade(true)
                        .size(300,300)
                        .placeholder(R.drawable.ic_default_cake)
                        .error(R.drawable.ic_launcher_foreground)
                        .build()
                ),
                contentDescription = meal.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            )

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = meal.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun SkeletonCardInspiracion() {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(180.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            )
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .width(100.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            )
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
                items(carritoItems,key =  {it.producto.code}){ item ->
                    CarritoItemRow(item, onModificarCantidad)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            val total = carritoItems.sumOf{it.producto.price * it.cantidad}
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
            Text(item.producto.name, fontWeight = FontWeight.SemiBold)
            Text("$${item.producto.price}", style = MaterialTheme.typography.bodySmall)
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
            onToggleDarkMode = {},
            onNavigateToModificar ={}
        )
    }
}