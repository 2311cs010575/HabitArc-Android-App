package com.example.aiusage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "predictions")
data class PredictionRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val predictedDate: Long, // Target date the prediction applies to
    val predictionType: String, // "SCREEN_TIME" or "EXPENSE" or "CATEGORY_EXPENSE"
    val category: String = "", // e.g. "Food" or "Social Media" (empty if overall)
    val predictedValue: Double, // Predicted value (in minutes or currency)
    val trend: String, // "INCREASING", "DECREASING", "STABLE"
    val confidence: Double, // Score between 0.0 and 1.0
    val createdAt: Long = System.currentTimeMillis() // Generation date
)
