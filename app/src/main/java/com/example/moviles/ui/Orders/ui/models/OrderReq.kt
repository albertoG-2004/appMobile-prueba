package com.example.moviles.ui.Orders.ui.models

data class OrderReq(
    val product: String,
    val amount: Int,
    val total: Double
)