package com.example.aiusage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val amount: Double,
    val category: String, // e.g. Food, Shopping, Entertainment, Bills, Transport, Others
    val date: Long, // Epoch timestamp in ms
    val description: String = "",
    val paymentMethod: String // e.g. UPI, Cash, Card, NetBanking
)
