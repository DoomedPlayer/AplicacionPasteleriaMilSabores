package com.example.pasteleriamilsabores.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import kotlinx.coroutines.suspendCancellableCoroutine
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pasteleriamilsabores.model.CarritoItem
import com.example.pasteleriamilsabores.viewmodel.PasteleriaViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import java.io.IOException
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.pasteleriamilsabores.R
import kotlinx.coroutines.delay
import java.util.Locale

typealias LocationCoordinate = Pair<Double, Double>

private const val NOTIFICATION_CHANNEL_ID = "pago_exitoso_channel"
private const val NOTIFICATION_ID = 1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagoScreen(
    viewModel: PasteleriaViewModel = viewModel(),
    onNavigateBack: () -> Unit
){
    val carritoItem =  viewModel.carrito.value
    val total = viewModel.totalCarrito
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var nombre by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var direccion by rememberSaveable { mutableStateOf("") }
    var comuna by rememberSaveable { mutableStateOf("") }

    var nombreError by rememberSaveable { mutableStateOf(false) }
    var telefonoError by rememberSaveable { mutableStateOf(false) }

    // Estado de carga
    var isLoading by remember { mutableStateOf(false) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permiso concedido, no es necesario hacer nada extra aquí.
        } else {
            Toast.makeText(context, "No se podrán mostrar notificaciones.", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        createNotificationChannel(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {Text("Confirmar Pago")},
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    ResumenPedido(items = carritoItem, total = total)
                }
                item {
                    // Pasa los estados y los callbacks al formulario
                    FormularioDespacho(
                        nombre = nombre,
                        onNombreChange = {
                            nombre = it
                            nombreError = false // Limpia el error al escribir
                        },
                        telefono = telefono,
                        onTelefonoChange = {
                            // Solo permite 9 dígitos numéricos
                            if (it.length <= 9 && it.all { char -> char.isDigit() }) {
                                telefono = it
                                telefonoError = false // Limpia el error al escribir
                            }
                        },
                        direccion = direccion,
                        onDireccionChange = { direccion = it },
                        comuna = comuna,
                        onComunaChange = { comuna = it },
                        nombreError = nombreError,
                        telefonoError = telefonoError
                    )
                }
                item {
                    Button(
                        onClick = {
                            val isNombreValido = nombre.trim().contains(" ") && nombre.trim().length > 3
                            val isTelefonoValido = telefono.length == 9

                            nombreError = !isNombreValido
                            telefonoError = !isTelefonoValido

                            if (isNombreValido && isTelefonoValido) {
                                isLoading = true
                                coroutineScope.launch {
                                    delay(2000)
                                    showPagoExitosoNotification(context, total)
                                    viewModel.limpiarCarrito()
                                    Toast.makeText(context, "Pedido realizado con éxito", Toast.LENGTH_LONG).show()
                                    isLoading = false
                                    onNavigateBack()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text("Pagar $$total", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
        if (isLoading) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(64.dp))
            }
        }
    }
}

private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Pagos Exitosos"
        val descriptionText = "Canal para notificar pagos realizados correctamente."
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

private fun showPagoExitosoNotification(context: Context, total: Int) {
    val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("¡Pedido en camino!")
        .setContentText("Tu pago de $$total ha sido procesado con éxito.")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(NOTIFICATION_ID, builder.build())
}

@Composable
fun ResumenPedido(items: List<CarritoItem>, total: Int){
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Resumen de tu Pedido", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${item.cantidad}x ${item.producto.name}")
                    Text("$${item.producto.price * item.cantidad}")
                }
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
                Text("$$total", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun FormularioDespacho(
    nombre: String,
    onNombreChange: (String) -> Unit,
    telefono: String,
    onTelefonoChange: (String) -> Unit,
    direccion: String,
    onDireccionChange: (String) -> Unit,
    comuna: String,
    onComunaChange: (String) -> Unit,
    // Parámetros para los errores
    nombreError: Boolean,
    telefonoError: Boolean

) {
    val context = LocalContext.current
    var userLocation by remember { mutableStateOf<LocationCoordinate?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                fetchLastLocation(context) { location ->
                    userLocation = location // Actualiza el mapa
                    // NUEVO: Inicia la geocodificación inversa para obtener la dirección
                    coroutineScope.launch {
                        val addressDetails = reverseGeocodeLocation(context, location)
                        if (addressDetails != null) {
                            onDireccionChange(addressDetails.first)
                            onComunaChange(addressDetails.second)
                        } else {
                            Toast.makeText(context, "No se pudo encontrar una dirección para la ubicación.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(context, "Permiso de ubicación denegado.", Toast.LENGTH_SHORT).show()
            }
        }
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Datos de Despacho", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = nombre,
            onValueChange = onNombreChange, // Usa el parámetro
            label = { Text("Nombre Completo") },
            modifier = Modifier.fillMaxWidth(),
            isError = nombreError, // <-- AÑADIDO
            supportingText = { // <-- AÑADIDO
                if (nombreError) {
                    Text("Debe contener nombre y apellido.")
                }
            },
            singleLine = true
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = direccion,
                    onValueChange = onDireccionChange,
                    label = { Text("Dirección de Despacho") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = comuna,
                    onValueChange = onComunaChange,
                    label = { Text("Comuna") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = {
                if (direccion.isNotBlank() && comuna.isNotBlank()) {
                    coroutineScope.launch {
                        val location = geocodeAddress(context, direccion, comuna)
                        if (location != null) {
                            userLocation = location
                        } else {
                            Toast.makeText(context, "Dirección no encontrada", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Ingresa dirección y comuna para buscar", Toast.LENGTH_SHORT).show()
                }
            }) {
                Icon(Icons.Filled.Search, contentDescription = "Buscar Dirección")
            }
        }

        OutlinedTextField(
            value = telefono,
            onValueChange = onTelefonoChange,
            label = { Text("Teléfono de Contacto") },
            modifier = Modifier.fillMaxWidth(),
            isError = telefonoError,
            supportingText = {
                if (telefonoError) {
                    Text("Debe ser un número de 9 dígitos.")
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            MapboxView(
                modifier = Modifier.matchParentSize(),
                userLocation = userLocation
            )

            Button(
                onClick = {
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
            ) {
                Text("Ubicarme")
            }
        }
    }
}

@Composable
fun MapboxView(
    modifier: Modifier = Modifier,
    userLocation: LocationCoordinate?
) {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS)
        }
    }
    var pointAnnotationManager by remember { mutableStateOf<PointAnnotationManager?>(null) }

    AndroidView(
        factory = {
            mapView.apply {
                val initialPoint = Point.fromLngLat(-70.6483, -33.4569) // Santiago
                getMapboxMap().setCamera(
                    CameraOptions.Builder()
                        .center(initialPoint)
                        .zoom(11.0)
                        .build()
                )
                val annotationApi = this.annotations
                pointAnnotationManager = annotationApi.createPointAnnotationManager()
            }
        },
        update = { view ->
            userLocation?.let { (lat, lon) ->
                pointAnnotationManager?.deleteAll()

                val iconBitmap = bitmapFromDrawableRes(context, R.drawable.red_marker)

                iconBitmap?.let {
                    val pointAnnotationOptions = PointAnnotationOptions()
                        .withPoint(Point.fromLngLat(lon, lat))
                        .withIconImage(it) // <- Esta línea agrega el ícono
                    pointAnnotationManager?.create(pointAnnotationOptions)
                }

                view.getMapboxMap().flyTo(
                    CameraOptions.Builder()
                        .center(Point.fromLngLat(lon, lat))
                        .zoom(15.0)
                        .build()
                )
            }
        },
        modifier = modifier
    )
}

private suspend fun geocodeAddress(context: Context, address: String, comuna: String): LocationCoordinate? {
    return withContext(Dispatchers.IO) {
        val fullAddress = "$address, $comuna, Chile"
        val geocoder = Geocoder(context)
        try {
            val addresses = geocoder.getFromLocationName(fullAddress, 1)
            if (addresses?.isNotEmpty() == true) {
                val location = addresses[0]
                Pair(location.latitude, location.longitude)
            } else {
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}

private suspend fun reverseGeocodeLocation(context: Context, location: LocationCoordinate): Pair<String, String>? {
    val geocoder = Geocoder(context, Locale.getDefault())

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        return suspendCancellableCoroutine { continuation ->
            geocoder.getFromLocation(location.first, location.second, 1,
                object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        if (addresses.isNotEmpty()) {
                            val address = addresses[0]
                            val streetAddress = "${address.thoroughfare ?: ""} ${address.subThoroughfare ?: ""}".trim()
                            val comuna = address.locality ?: address.subAdminArea ?: ""
                            continuation.resume(Pair(streetAddress, comuna)) { /* on cancellation */ }
                        } else {
                            continuation.resume(null) { /* on cancellation */ }
                        }
                    }

                    override fun onError(errorMessage: String?) {
                        super.onError(errorMessage)
                        continuation.resume(null) { /* on cancellation */ }
                    }
                }
            )
        }
    } else {
        return withContext(Dispatchers.IO) {
            try {
                @Suppress("DEPRECATION")
                val addresses: List<Address>? = geocoder.getFromLocation(location.first, location.second, 1)
                if (addresses?.isNotEmpty() == true) {
                    val address = addresses[0]
                    val streetAddress = "${address.thoroughfare ?: ""} ${address.subThoroughfare ?: ""}".trim()
                    val comuna = address.subAdminArea ?: address.locality ?: ""
                    Pair(streetAddress, comuna)
                } else {
                    null
                }
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun fetchLastLocation(context: Context, onLocationFetched: (LocationCoordinate) -> Unit) {
    // This function remains the same
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    fusedLocationClient.lastLocation
        .addOnSuccessListener { location ->
            if (location != null) {
                onLocationFetched(Pair(location.latitude, location.longitude))
            } else {
                Toast.makeText(context, "No se pudo obtener la ubicación. Activa el GPS.", Toast.LENGTH_SHORT).show()
            }
        }
        .addOnFailureListener {
            Toast.makeText(context, "Error al obtener la ubicación.", Toast.LENGTH_SHORT).show()
        }
}

private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int): Bitmap? {
    val drawable = AppCompatResources.getDrawable(context, resourceId) ?: return null
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}