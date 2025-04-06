package com.example.moviles.ui.Orders.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.moviles.ui.Products.ui.OrderResponse
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.moviles.ui.Orders.ui.ViewOrdersViewModel

@Composable
fun ViewOrdersScreen(navController: NavHostController) {
    val viewModel: ViewOrdersViewModel = viewModel(factory = ViewOrdersViewModel.provideFactory())

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Lista de Pedidos",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (viewModel.isLoading) {
                CircularProgressIndicator()
            }
            if (viewModel.error != null) {
                Text(text = "Error: ${viewModel.error}", color = MaterialTheme.colorScheme.error)
            }
            if (viewModel.orderUpdateStatus != null) {
                Text(text = viewModel.orderUpdateStatus!!, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(viewModel.orders) { order ->
                    OrderCard(order = order, onEntregadoClick = { viewModel.updateOrderStatus(order.id) })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF800080), contentColor = Color.White),
            ) {
                Text(
                    text = "Volver",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun OrderCard(order: OrderResponse, onEntregadoClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Pedido ID: ${order.id}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Producto: ${order.product}")
            Text(text = "Cantidad: ${order.amount}")
            Text(text = "Total: $${order.total}")

            val orderDateText = if (order.orderDate != null) {
                "Fecha del pedido: ${order.orderDate}"
            } else {
                "Fecha del pedido: No disponible"
            }
            Text(text = orderDateText)

            val orderDeliveryText = if (order.deliveryDate != null) {
                "Fecha de entrega: ${order.deliveryDate}"
            } else {
                "Fecha de entrega: No disponible"
            }
            Text(text = orderDeliveryText)


            Text(text = "Estado: ${order.orderStatus ?: "Pendiente"}")
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onEntregadoClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50), contentColor = Color.White)
                ) {
                    Text("Entregado")
                }
            }
        }
    }
}