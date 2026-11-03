package com.example.aiusage.dao

import androidx.room.*
import com.example.aiusage.entity.Budget
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: Budget): Long

    @Update
    suspend fun updateBudget(budget: Budget): Int

    @Delete
    suspend fun deleteBudget(budget: Budget): Int

    @Query("SELECT * FROM budgets WHERE monthYear = :monthYear")
    fun getBudgetsForMonthFlow(monthYear: String): Flow<List<Budget>>

    @Query("SELECT * FROM budgets WHERE monthYear = :monthYear")
    suspend fun getBudgetsForMonthSync(monthYear: String): List<Budget>

    @Query("SELECT * FROM budgets WHERE category = :category AND monthYear = :monthYear LIMIT 1")
    suspend fun getBudgetByCategoryAndMonth(category: String, monthYear: String): Budget?
}
