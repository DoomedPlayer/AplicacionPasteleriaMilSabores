package com.example.pasteleriamilsabores.view

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.pasteleriamilsabores.viewmodel.PasteleriaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModificarProducto(
    viewModel: PasteleriaViewModel,
    productoId: Int,
    onNavigateBack: () -> Unit
){
    val producto = remember { viewModel.obtenerProductoPorId(productoId) }

    if(producto==null){
        LaunchedEffect(Unit) {onNavigateBack() }
        return
    }

    var nombre by remember { mutableStateOf(producto.name) }
    var descripcion by remember { mutableStateOf(producto.description) }
    var precioTexto by remember { mutableStateOf(producto.price.toString()) }
    var imagenUri by remember { mutableStateOf<Uri?>(if (producto.image.isNotEmpty()) Uri.parse(producto.image) else null) }

    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                imagenUri = uri
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Producto") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = precioTexto,
                onValueChange = { precioTexto = it },
                label = { Text("Precio") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // Selector de Imagen
            Card(
                modifier = Modifier.fillMaxWidth().height(150.dp),
                onClick = { imagePickerLauncher.launch("image/*") }
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (imagenUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(model = imagenUri),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.Image, null)
                            Text("Cambiar Imagen")
                        }
                    }
                }
            }

            Button(
                onClick = {
                    val precioInt = precioTexto.toIntOrNull() ?: 0
                    if (nombre.isNotBlank() && precioInt > 0) {
                        viewModel.actualizarProducto(producto.code, nombre, descripcion, precioInt, imagenUri)
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Guardar Cambios") }
        }
    }
}