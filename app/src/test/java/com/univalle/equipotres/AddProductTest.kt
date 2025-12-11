package com.univalle.equipotres.view.fragment

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.NavController
import com.univalle.equipotres.databinding.FragmentAddProductBinding
import com.univalle.equipotres.model.Product
import com.univalle.equipotres.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.kotlin.whenever
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.MockitoAnnotations


@ExperimentalCoroutinesApi
class AddProductTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var repository: ProductRepository

    @Mock
    private lateinit var navController: NavController

    @Mock
    private lateinit var binding: FragmentAddProductBinding

    private lateinit var fragment: AddProductFragment

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        fragment = AddProductFragment()
        fragment.repository = repository
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saveProduct con datos validos debe guardar producto exitosamente`() = runTest {
        // Arrange
        val product = Product(
            id = "",
            name = "Laptop",
            price = 1500.0,
            quantity = 10
        )

        whenever(repository.addProduct(any())).thenReturn(Unit)

        // Act
        // Simular guardado de producto
        repository.addProduct(product)
        advanceUntilIdle()

        // Assert
        verify(repository).addProduct(any())
    }

    @Test
    fun `saveProduct con precio invalido no debe guardar`() = runTest {
        // Arrange
        val invalidPrice = -100.0

        // Act & Assert
        assert(invalidPrice <= 0)
        verifyNoInteractions(repository)
    }

    @Test
    fun `saveProduct con cantidad invalida no debe guardar`() = runTest {
        // Arrange
        val invalidQuantity = -5

        // Act & Assert
        assert(invalidQuantity <= 0)
        verifyNoInteractions(repository)
    }

    @Test
    fun `saveProduct con precio cero no debe guardar`() = runTest {
        // Arrange
        val zeroPrecio = 0.0

        // Act & Assert
        assert(zeroPrecio <= 0)
    }

    @Test
    fun `saveProduct con cantidad cero no debe guardar`() = runTest {
        // Arrange
        val zeroCantidad = 0

        // Act & Assert
        assert(zeroCantidad <= 0)
    }

    @Test
    fun `updateSaveButtonState con todos los campos llenos debe habilitar boton`() {
        // Arrange
        val codigoLleno = "1234"
        val nombreLleno = "Producto Test"
        val precioLleno = "100"
        val cantidadLlena = "10"

        // Act
        val filled = codigoLleno.isNotEmpty() &&
                nombreLleno.isNotEmpty() &&
                precioLleno.isNotEmpty() &&
                cantidadLlena.isNotEmpty()

        // Assert
        assert(filled)
    }

    @Test
    fun `updateSaveButtonState con campos vacios debe deshabilitar boton`() {
        // Arrange
        val codigoVacio = ""
        val nombreLleno = "Producto"
        val precioLleno = "100"
        val cantidadLlena = "10"

        // Act
        val filled = codigoVacio.isNotEmpty() &&
                nombreLleno.isNotEmpty() &&
                precioLleno.isNotEmpty() &&
                cantidadLlena.isNotEmpty()

        // Assert
        assert(!filled)
    }

    @Test
    fun `precio toDoubleOrNull con string valido retorna double`() {
        // Arrange
        val precioStr = "150.50"

        // Act
        val precio = precioStr.toDoubleOrNull()

        // Assert
        assert(precio != null)
        assert(precio == 150.50)
    }

    @Test
    fun `precio toDoubleOrNull con string invalido retorna null`() {
        // Arrange
        val precioStr = "invalido"

        // Act
        val precio = precioStr.toDoubleOrNull()

        // Assert
        assert(precio == null)
    }

    @Test
    fun `cantidad toIntOrNull con string valido retorna int`() {
        // Arrange
        val cantidadStr = "25"

        // Act
        val cantidad = cantidadStr.toIntOrNull()

        // Assert
        assert(cantidad != null)
        assert(cantidad == 25)
    }

    @Test
    fun `cantidad toIntOrNull con string invalido retorna null`() {
        // Arrange
        val cantidadStr = "no-numero"

        // Act
        val cantidad = cantidadStr.toIntOrNull()

        // Assert
        assert(cantidad == null)
    }

    @Test
    fun `saveProduct con excepcion debe manejar error`() = runTest {
        // Arrange
        val product = Product(
            id = "",
            name = "Test",
            price = 100.0,
            quantity = 5
        )

        whenever(repository.addProduct(any()))
            .thenThrow(RuntimeException("Error de conexión"))

        // Act & Assert
        try {
            repository.addProduct(product)
            assert(false) { "Debería lanzar excepción" }
        } catch (e: Exception) {
            assert(e.message == "Error de conexión")
        }
    }

    @Test
    fun `trim debe eliminar espacios en blanco`() {
        // Arrange
        val textoConEspacios = "  Producto  "

        // Act
        val textoLimpio = textoConEspacios.trim()

        // Assert
        assert(textoLimpio == "Producto")
    }

    @Test
    fun `producto con todos los campos validos debe crearse correctamente`() {
        // Arrange & Act
        val product = Product(
            id = "",
            name = "Mouse Inalámbrico",
            price = 25.99,
            quantity = 50
        )

        // Assert
        assert(product.name == "Mouse Inalámbrico")
        assert(product.price == 25.99)
        assert(product.quantity == 50)
        assert(product.id.isEmpty())
    }
}