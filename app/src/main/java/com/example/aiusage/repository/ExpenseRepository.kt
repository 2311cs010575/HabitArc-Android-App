package com.example.aiusage.repository

import com.example.aiusage.entity.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getAllExpensesFlow(): Flow<List<Expense>>
    suspend fun getAllExpensesSync(): List<Expense>
    fun getExpensesByCategoryFlow(category: String): Flow<List<Expense>>
    fun getExpensesInDateRangeFlow(startDate: Long, endDate: Long): Flow<List<Expense>>
    suspend fun getExpensesInDateRangeSync(startDate: Long, endDate: Long): List<Expense>
    fun getRecentExpensesFlow(limit: Int): Flow<List<Expense>>
    suspend fun insertExpense(expense: Expense): Long
    suspend fun updateExpense(expense: Expense)
    suspend fun deleteExpense(expense: Expense)
}

