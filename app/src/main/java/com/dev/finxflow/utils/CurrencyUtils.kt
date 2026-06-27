package com.dev.finxflow.utils

object CurrencyUtils {
    fun formatAmount(amount: Double): String {
        return "₹${String.format("%,.2f", amount)}"
    }
}