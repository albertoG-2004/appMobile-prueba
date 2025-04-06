package com.example.moviles.ui.Products.ui

data class OrderResponse(
    val id: Int,
    val product: String,
    val amount: Int,
    val total: String,
    val orderDate: String?,
    val deliveryDate: String?,
    val orderStatus: String?
)