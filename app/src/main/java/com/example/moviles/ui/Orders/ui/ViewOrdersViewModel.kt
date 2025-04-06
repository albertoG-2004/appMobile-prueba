package com.example.moviles.ui.Orders.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moviles.apiService.ApiService
import com.example.moviles.apiService.RetroClient
import com.example.moviles.ui.Products.ui.OrderResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

class ViewOrdersViewModel(private val apiService: ApiService) : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var orders by mutableStateOf<List<OrderResponse>>(emptyList())
        private set

    var orderUpdateStatus by mutableStateOf<String?>(null)
        private set

    init {
        fetchOrders()
    }

    private fun fetchOrders() {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                val response = apiService.getOrders()
                if (response.isSuccessful) {
                    val orderListFromApi = response.body() ?: emptyList()

                    // Mapear la lista de la API a OrderResponse - ¡SIN PARSEAR FECHAS!
                    orders = orderListFromApi.map { apiOrder ->
                        OrderResponse(
                            id = apiOrder.id,
                            product = apiOrder.product,
                            amount = apiOrder.amount,
                            total = apiOrder.total,
                            orderDate = apiOrder.orderDate,
                            deliveryDate = apiOrder.deliveryDate,
                            orderStatus = apiOrder.orderStatus
                        )
                    }
                } else {
                    error = "Error al obtener pedidos: ${response.errorBody()?.string()}"
                }
            } catch (e: HttpException) {
                error = "Error al obtener pedidos, excepción HTTP: ${e.message()}"
            } catch (e: Exception) {
                error = "Error al obtener pedidos, otra excepción: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun updateOrderStatus(orderId: Int) {
        viewModelScope.launch {
            orderUpdateStatus = null // Limpiar estado previo
            isLoading = true
            error = null
            try {
                val response = apiService.updateOrder(orderId)
                if (response.isSuccessful) {
                    orderUpdateStatus = "Pedido $orderId actualizado a Entregado"
                    fetchOrders() // Refrescar la lista de pedidos para ver el cambio
                } else {
                    error = "Error al actualizar el pedido $orderId: ${response.errorBody()?.string()}"
                }
            } catch (e: HttpException) {
                error = "Error al actualizar pedido $orderId, excepción HTTP: ${e.message()}"
            } catch (e: Exception) {
                error = "Error al actualizar pedido $orderId, otra excepción: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    companion object {
        fun provideFactory(): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ViewOrdersViewModel(RetroClient.instance) as T
                }
            }
        }
    }
}