package com.example.aiusage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String,
    val limitAmount: Double,
    val monthYear: String // Format: "MM-YYYY" (e.g. "07-2026")
)
