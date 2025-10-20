package com.example.pasteleriamilsabores.view

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pasteleriamilsabores.viewmodel.PasteleriaViewModel
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.Image
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioScreen (
    viewModel: PasteleriaViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precioTexto by remember { mutableStateOf("") }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }

    var nombreError by remember { mutableStateOf(false) }
    var precioError by remember { mutableStateOf(false) }

    val precioInt = precioTexto.toIntOrNull() ?: 0
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            imagenUri = uri
            if (uri != null){
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }
    )

    val esNombreValido = nombre.isNotBlank() && nombre.first().isUpperCase()
    val esDescValida = true
    val esPrecioValido = precioInt >0

    val esFormularioValido = esNombreValido && esPrecioValido

    Scaffold(
        topBar = {
            TopAppBar(
                title = {Text("Añadir Nuevo Producto")},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ){
            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    nombreError = !it.isNotBlank() || (it.isNotEmpty() && !it.first().isUpperCase())
                },
                label = {Text("Nombre del Producto")},
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = nombreError,
                supportingText = {
                    if(nombreError){
                        Text(
                            text = if(nombre.isBlank()) "El nombre no puede estar vacío." else "La primera letra debe ser mayúscula.",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = {descripcion = it},
                label = {Text("Descripción (ej: con merengue)")},
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            OutlinedTextField(
                value = precioTexto,
                onValueChange =  {
                    precioTexto = it.replace(",",".")
                    precioError = it.toIntOrNull()?.let{ p -> p <= 0 } ?: true
                },
                label = {Text("Precio")},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = precioError,
                supportingText = {
                    if (precioError){
                        Text(
                            text = "El precio debe ser mayor a 0.",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

            )

            Card(
                modifier = Modifier.fillMaxWidth().height(150.dp),
                onClick = {imagePickerLauncher.launch("image/*")}
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (imagenUri != null){
                        Image(
                            painter = rememberAsyncImagePainter(model = imagenUri),
                            contentDescription = "Imagen seleccionada",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else{
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.Image, contentDescription = "Seleccionar Imagen", modifier = Modifier.size(40.dp))
                            Text("Toca para seleccionar una imagen")
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    if (esFormularioValido){
                        viewModel.agregarNuevoProducto(nombre,descripcion,precioInt,imagenUri)
                        onNavigateBack()
                    }
                },
                enabled = esFormularioValido,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {Text("Añadir Producto") }
        }
    }

}