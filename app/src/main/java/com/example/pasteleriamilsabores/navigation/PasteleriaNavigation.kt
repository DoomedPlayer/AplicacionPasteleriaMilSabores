package com.example.pasteleriamilsabores.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pasteleriamilsabores.view.PasteleriaScreen
import com.example.pasteleriamilsabores.view.FormularioScreen
import com.example.pasteleriamilsabores.view.PagoScreen
import com.example.pasteleriamilsabores.viewmodel.PasteleriaViewModel

sealed class PasteleriaVista{
    data object Lista: PasteleriaVista()
    data object Formulario: PasteleriaVista()
    data object Pago: PasteleriaVista()
}



@Composable
fun PasteleriaHost(
    viewModel: PasteleriaViewModel = viewModel(),isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit
){

    var vistaActual by remember { mutableStateOf<PasteleriaVista>(PasteleriaVista.Lista) }
    val navigateToForm: () -> Unit = {vistaActual = PasteleriaVista.Formulario}
    val navigateToList: () -> Unit = {vistaActual = PasteleriaVista.Lista}
    val navigateToPago: () -> Unit = {vistaActual = PasteleriaVista.Pago}

    when (vistaActual){
        is PasteleriaVista.Lista ->{
            PasteleriaScreen(
                viewModel = viewModel,
                onNavigateToForm = navigateToForm,
                onNavigateToPago = navigateToPago,
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode
            )
        }
        is PasteleriaVista.Formulario ->{
            FormularioScreen(
                viewModel = viewModel,
                onNavigateBack = navigateToList
            )
        }
        is PasteleriaVista.Pago -> {
            PagoScreen(
                viewModel = viewModel,
                onNavigateBack = navigateToList
            )
        }
    }
}