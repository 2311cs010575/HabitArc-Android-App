package com.example.aiusage.repository

import com.example.aiusage.entity.Budget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun getBudgetsForMonthFlow(monthYear: String): Flow<List<Budget>>
    suspend fun getBudgetsForMonthSync(monthYear: String): List<Budget>
    suspend fun getBudgetByCategoryAndMonth(category: String, monthYear: String): Budget?
    suspend fun insertBudget(budget: Budget): Long
    suspend fun updateBudget(budget: Budget)
    suspend fun deleteBudget(budget: Budget)
}
