package com.example.pasteleriamilsabores

import android.app.Application
import com.example.pasteleriamilsabores.model.PasteleriaRepository
import com.example.pasteleriamilsabores.model.Producto
import com.example.pasteleriamilsabores.viewmodel.PasteleriaViewModel
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class PasteleriaViewModelTest : BehaviorSpec({

    val testDispatcher = UnconfinedTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(testDispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    isolationMode = IsolationMode.InstancePerLeaf

    val repository = mockk<PasteleriaRepository>(relaxed = true)
    val application = mockk<Application>(relaxed = true)

    val producto1 = Producto(1, "Torta Chocolate", "Rica", 1000,"General", "Pastel", "")
    val producto2 = Producto(2, "Pie de Limón", "Acido", 2000,"General", "Pie",  "" )

    Given("Un ViewModel de Pastelería vacío") {

        val viewModel = PasteleriaViewModel(application, repository)
        When("Agrego un producto al carrito") {
            viewModel.agregarCarrito(producto1)

            Then("El carrito debe tener 1 elemento") {
                viewModel.carrito.value shouldHaveSize 1
            }
            Then("El nombre del producto debe ser correcto") {
                viewModel.carrito.value[0].producto.name shouldBe "Torta Chocolate"
            }
            Then("La cantidad inicial debe ser 1") {
                viewModel.carrito.value[0].cantidad shouldBe 1
            }
        }

        When("Agrego el MISMO producto dos veces") {
            viewModel.agregarCarrito(producto1)
            viewModel.agregarCarrito(producto1)

            Then("El carrito NO debe duplicar la fila (tamaño 1)") {
                viewModel.carrito.value shouldHaveSize 1
            }
            Then("La cantidad debe aumentar a 2") {
                viewModel.carrito.value[0].cantidad shouldBe 2
            }
            Then("El total debe ser el precio multiplicado por 2 (2000)") {
                viewModel.totalCarrito shouldBe 2000
            }
        }

        When("Agrego dos productos DIFERENTES") {
            viewModel.agregarCarrito(producto1)
            viewModel.agregarCarrito(producto2)

            Then("El carrito debe tener 2 elementos") {
                viewModel.carrito.value shouldHaveSize 2
            }
            Then("El total debe ser la suma de ambos (3000)") {
                viewModel.totalCarrito shouldBe 3000
            }
        }
    }

    Given("Un carrito con productos existentes") {
        val viewModel = PasteleriaViewModel(application, repository)
        viewModel.agregarCarrito(producto1)

        When("Modifico la cantidad de 1 a 5") {
            viewModel.modificarCantidad(producto1, 5)

            Then("La cantidad se actualiza a 5") {
                viewModel.carrito.value[0].cantidad shouldBe 5
            }
            Then("El total se actualiza correctamente (5000)") {
                viewModel.totalCarrito shouldBe 5000
            }
        }

        When("Modifico la cantidad a 0") {
            viewModel.modificarCantidad(producto1, 0)

            Then("El producto se elimina del carrito") {
                viewModel.carrito.value.shouldBeEmpty()
            }
        }

        When("Presiono limpiar carrito") {
            viewModel.limpiarCarrito()

            Then("El carrito queda completamente vacío") {
                viewModel.carrito.value.shouldBeEmpty()
            }
        }
    }
})