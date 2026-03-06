package com.luisdev.finanzapp.domain.model

data class Transaction(
    val id: Int? = null,
    val description: String,
    val amount: Double,
    val date: Long,
    val categoryId: String
)
