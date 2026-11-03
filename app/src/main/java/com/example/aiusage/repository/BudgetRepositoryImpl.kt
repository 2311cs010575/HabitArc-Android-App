package com.example.aiusage.repository

import com.example.aiusage.dao.BudgetDao
import com.example.aiusage.entity.Budget
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDao
) : BudgetRepository {

    override fun getBudgetsForMonthFlow(monthYear: String): Flow<List<Budget>> {
        return budgetDao.getBudgetsForMonthFlow(monthYear)
    }

    override suspend fun getBudgetsForMonthSync(monthYear: String): List<Budget> {
        return budgetDao.getBudgetsForMonthSync(monthYear)
    }

    override suspend fun getBudgetByCategoryAndMonth(category: String, monthYear: String): Budget? {
        return budgetDao.getBudgetByCategoryAndMonth(category, monthYear)
    }

    override suspend fun insertBudget(budget: Budget): Long {
        return budgetDao.insertBudget(budget)
    }

    override suspend fun updateBudget(budget: Budget) {
        budgetDao.updateBudget(budget)
    }

    override suspend fun deleteBudget(budget: Budget) {
        budgetDao.deleteBudget(budget)
    }
}
