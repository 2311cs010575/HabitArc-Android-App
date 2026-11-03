package com.example.aiusage.di

import android.content.Context
import androidx.room.Room
import com.example.aiusage.database.AppDatabase
import com.example.aiusage.dao.BudgetDao
import com.example.aiusage.dao.ExpenseDao
import com.example.aiusage.dao.NotificationDao
import com.example.aiusage.dao.PredictionDao
import com.example.aiusage.dao.UsageDao
import com.example.aiusage.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "ai_usage_expense.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    @Singleton
    fun provideExpenseDao(db: AppDatabase): ExpenseDao = db.expenseDao()

    @Provides
    @Singleton
    fun provideUsageDao(db: AppDatabase): UsageDao = db.usageDao()

    @Provides
    @Singleton
    fun provideBudgetDao(db: AppDatabase): BudgetDao = db.budgetDao()

    @Provides
    @Singleton
    fun providePredictionDao(db: AppDatabase): PredictionDao = db.predictionDao()

    @Provides
    @Singleton
    fun provideNotificationDao(db: AppDatabase): NotificationDao = db.notificationDao()
}
