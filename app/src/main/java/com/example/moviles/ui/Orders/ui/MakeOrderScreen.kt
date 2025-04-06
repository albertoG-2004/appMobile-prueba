package com.example.moviles.ui.Orders.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.moviles.ui.Products.ui.ProductResponse

@Composable
fun MakeOrderScreen(navController: NavHostController) {
    val viewModel: MakeOrderViewModel = viewModel(factory = MakeOrderViewModel.provideFactory())

    val products by viewModel.products.collectAsState()
    val loading = viewModel.isLoading // Access directly, no 'by'
    val error = viewModel.error     // Access directly, no 'by'
    val totalAmount by viewModel.totalAmount.collectAsState()
    val orderStatus by viewModel.orderStatus.collectAsState()

    MakeOrderScreenContent(
        products = products,
        loading = loading,
        error = error,
        totalAmount = totalAmount,
        orderStatus = orderStatus,
        onQuantityChange = viewModel::updateQuantity,
        onPlaceOrder = viewModel::placeOrder,
        navController = navController
    )
}

@Composable
fun MakeOrderScreenContent(
    products: List<ProductOrderItem>,
    loading: Boolean,
    error: String?,
    totalAmount: Double,
    orderStatus: String?,
    onQuantityChange: (ProductOrderItem, Int) -> Unit,
    onPlaceOrder: () -> Unit,
    navController: NavHostController
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Realizar Pedido", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (error != null) {
                Text("Error: $error", color = MaterialTheme.colorScheme.error)
            } else if (orderStatus != null) {
                Text(orderStatus, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
            }
            else {
                if (products.isEmpty()) {
                    Text("No hay productos disponibles en este momento.")
                } else {
                    LazyColumn {
                        items(products) { productOrderItem ->
                            ProductItem(
                                productOrderItem = productOrderItem,
                                onQuantityChange = { qty -> onQuantityChange(productOrderItem, qty) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Total del pedido: $${String.format("%.2f", totalAmount)}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onPlaceOrder,
                enabled = products.any { it.quantity > 0 } && !loading
            ) {
                Text("Realizar Pedido")
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
fun ProductItem(productOrderItem: ProductOrderItem, onQuantityChange: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = productOrderItem.product.nombre, style = MaterialTheme.typography.titleLarge)
                Text(text = "$${productOrderItem.product.precio}", style = MaterialTheme.typography.bodyMedium)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        if (productOrderItem.quantity > 0) {
                            onQuantityChange(productOrderItem.quantity - 1)
                        }
                    }
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Quitar uno")
                }
                Text(text = productOrderItem.quantity.toString(), fontSize = 20.sp)
                IconButton(
                    onClick = { onQuantityChange(productOrderItem.quantity + 1) }
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar uno")
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewMakeOrderScreen() {
    // Mock ProductOrderItem for preview
     val mockProducts = listOf(
        ProductOrderItem(ProductResponse(1, "Libreta", 15.0, null)), // ProductResponse is now imported
        ProductOrderItem(ProductResponse(2, "Lapicero", 2.0, null))  // ProductResponse is now imported
    )
    MakeOrderScreenContent(
        products = mockProducts,
        loading = false,
        error = null,
        totalAmount = 20.0, // Mock total
        orderStatus = null,
        onQuantityChange = { _, _ -> },
        onPlaceOrder = {},
        navController = rememberNavController()
    )
}