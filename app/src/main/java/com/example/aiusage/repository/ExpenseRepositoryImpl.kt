package com.example.aiusage.repository

import com.example.aiusage.dao.ExpenseDao
import com.example.aiusage.entity.Expense
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {

    override fun getAllExpensesFlow(): Flow<List<Expense>> {
        return expenseDao.getAllExpensesFlow()
    }

    override suspend fun getAllExpensesSync(): List<Expense> {
        return expenseDao.getAllExpensesSync()
    }

    override fun getExpensesByCategoryFlow(category: String): Flow<List<Expense>> {
        return expenseDao.getExpensesByCategoryFlow(category)
    }

    override fun getExpensesInDateRangeFlow(startDate: Long, endDate: Long): Flow<List<Expense>> {
        return expenseDao.getExpensesInDateRangeFlow(startDate, endDate)
    }

    override suspend fun getExpensesInDateRangeSync(startDate: Long, endDate: Long): List<Expense> {
        return expenseDao.getExpensesInDateRangeSync(startDate, endDate)
    }

    override fun getRecentExpensesFlow(limit: Int): Flow<List<Expense>> {
        return expenseDao.getRecentExpensesFlow(limit)
    }

    override suspend fun insertExpense(expense: Expense): Long {
        return expenseDao.insertExpense(expense)
    }

    override suspend fun updateExpense(expense: Expense) {
        expenseDao.updateExpense(expense)
    }

    override suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense)
    }
}
