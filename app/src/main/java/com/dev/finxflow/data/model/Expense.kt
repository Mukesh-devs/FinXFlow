package com.dev.finxflow.data.model

data class Expense(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    val category: String,
    val amount: Double,
    val date: Long,
    val description: String = ""
)