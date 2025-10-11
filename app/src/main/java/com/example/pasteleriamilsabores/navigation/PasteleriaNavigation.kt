package com.example.pasteleriamilsabores.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pasteleriamilsabores.ui.screen.PasteleriaScreen
import com.example.pasteleriamilsabores.ui.screen.FormularioScreen
import com.example.pasteleriamilsabores.viewmodel.PasteleriaViewModel

sealed class PasteleriaVista{
    data object Lista: PasteleriaVista()
    data object Formulario: PasteleriaVista()
}

@Composable
fun PasteleriaHost(
    viewModel: PasteleriaViewModel = viewModel()
){
    var vistaActual by remember { mutableStateOf<PasteleriaVista>(PasteleriaVista.Lista) }
    val navigateToForm: () -> Unit = {vistaActual = PasteleriaVista.Formulario}
    val navigateToList: () -> Unit = {vistaActual = PasteleriaVista.Lista}

    when (vistaActual){
        is PasteleriaVista.Lista ->{
            PasteleriaScreen(
                viewModel = viewModel,
                onNavigateToForm = navigateToForm
            )
        }
        is PasteleriaVista.Formulario ->{
            FormularioScreen(
                viewModel = viewModel,
                onNavigateBack = navigateToList
            )
        }
    }
}