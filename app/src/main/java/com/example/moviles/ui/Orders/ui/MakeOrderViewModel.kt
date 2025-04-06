package com.example.moviles.ui.Orders.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moviles.apiService.ApiService
import com.example.moviles.apiService.RetroClient
import com.example.moviles.ui.Products.ui.ProductResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response
import com.example.moviles.ui.Orders.ui.models.OrderReq

class MakeOrderViewModel(private val apiService: ApiService) : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    private val _products = MutableStateFlow<List<ProductOrderItem>>(emptyList())
    val products: StateFlow<List<ProductOrderItem>> = _products

    private val _totalAmount = MutableStateFlow(0.0)
    val totalAmount: StateFlow<Double> = _totalAmount

    private val _orderStatus = MutableStateFlow<String?>(null)
    val orderStatus: StateFlow<String?> = _orderStatus

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                val response = apiService.getProducts()
                if (response.isSuccessful) {
                    val productOrderItems = response.body()?.map { ProductOrderItem(it) } ?: emptyList()
                    _products.value = productOrderItems
                } else {
                    error = "Error al obtener productos: ${response.errorBody()?.string()}"
                }
            } catch (e: HttpException) {
                error = "Error al obtener productos, excepción HTTP: ${e.message()}"
            } catch (e: Exception) {
                error = "Error al obtener productos, otra excepción: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }


    fun updateQuantity(productOrderItem: ProductOrderItem, quantity: Int) {
        val updatedProducts = _products.value.map {
            if (it.product.id == productOrderItem.product.id) {
                it.copy(quantity = quantity)
            } else {
                it
            }
        }
        _products.value = updatedProducts
        calculateTotalAmount()
    }

    private fun calculateTotalAmount() {
        val total = _products.value.sumOf { it.product.precio * it.quantity }
        _totalAmount.value = total.toDouble()
    }

    fun placeOrder() {
        isLoading = true
        _orderStatus.value = null
        viewModelScope.launch {
            try {
                val productsToOrder = _products.value.filter { it.quantity > 0 }

                if (productsToOrder.isEmpty()) {
                    error = "No se han seleccionado productos para ordenar."
                    isLoading = false
                    return@launch
                }

                val orderStatuses = mutableListOf<String>()

                coroutineScope { // Crea un coroutineScope para lanzar coroutines hijas
                    productsToOrder.forEach { productOrderItem ->
                        launch { // Lanza una coroutine para cada pedido de producto
                            val orderReq = OrderReq(
                                product = productOrderItem.product.nombre,
                                amount = productOrderItem.quantity,
                                total = productOrderItem.product.precio * productOrderItem.quantity
                            )

                            try {
                                val response = apiService.createOrder(orderReq) // Enviar pedido individual
                                if (response.isSuccessful) {
                                    orderStatuses.add("Pedido de ${productOrderItem.product.nombre} realizado con éxito!")
                                    // Opcionalmente, actualizar la cantidad del producto a 0 después de un pedido exitoso
                                    updateQuantity(productOrderItem, 0) // o una actualización más específica si es necesario
                                } else {
                                    orderStatuses.add("Error al pedir ${productOrderItem.product.nombre}: ${response.errorBody()?.string()}")
                                }
                            } catch (e: HttpException) {
                                orderStatuses.add("Error de red al pedir ${productOrderItem.product.nombre}, excepción HTTP: ${e.message()}")
                            } catch (e: Exception) {
                                orderStatuses.add("Error de red al pedir ${productOrderItem.product.nombre}: ${e.message}")
                            }
                        }
                    }
                } // coroutineScope esperará a que todas las coroutines hijas finalicen

                _orderStatus.value = orderStatuses.joinToString("\n")
                error = if (orderStatuses.any { it.startsWith("Error") }) "Algunos pedidos fallaron. Ver detalles abajo." else null

            } finally {
                isLoading = false
            }
        }
    }

    // Factory to provide ApiService dependency to ViewModel
    companion object {
        fun provideFactory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MakeOrderViewModel(RetroClient.instance) as T // Use RetroClient.instance
            }
        }
    }
}


// Data class to hold ProductResponse and quantity
data class ProductOrderItem(
    val product: ProductResponse,
    var quantity: Int = 0
)