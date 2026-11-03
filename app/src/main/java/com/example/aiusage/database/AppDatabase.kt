package com.example.aiusage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.aiusage.dao.BudgetDao
import com.example.aiusage.dao.ExpenseDao
import com.example.aiusage.dao.NotificationDao
import com.example.aiusage.dao.PredictionDao
import com.example.aiusage.dao.UsageDao
import com.example.aiusage.dao.UserDao
import com.example.aiusage.entity.Budget
import com.example.aiusage.entity.Expense
import com.example.aiusage.entity.NotificationRecord
import com.example.aiusage.entity.PredictionRecord
import com.example.aiusage.entity.UsageRecord
import com.example.aiusage.entity.User

@Database(
    entities = [
        User::class,
        Expense::class,
        UsageRecord::class,
        Budget::class,
        PredictionRecord::class,
        NotificationRecord::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun usageDao(): UsageDao
    abstract fun budgetDao(): BudgetDao
    abstract fun predictionDao(): PredictionDao
    abstract fun notificationDao(): NotificationDao
}
