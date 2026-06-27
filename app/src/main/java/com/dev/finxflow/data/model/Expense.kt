package com.dev.finxflow.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val category: String,
    val date: Long,          // midnight timestamp of the selected date
    val time: String,        // HH:mm:ss captured at save time (hidden in UI)
    val name: String,        // description from the Add screen
    val createdAt: Long = System.currentTimeMillis()
)