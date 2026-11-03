package com.example.aiusage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_usage")
data class UsageRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val packageName: String,
    val appName: String,
    val usageTimeMs: Long,
    val launchCount: Int,
    val date: Long // Epoch timestamp representing the day (midnight start) in ms
)
