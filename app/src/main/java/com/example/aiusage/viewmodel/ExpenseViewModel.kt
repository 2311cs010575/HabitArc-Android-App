package com.example.aiusage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aiusage.entity.Budget
import com.example.aiusage.entity.Expense
import com.example.aiusage.entity.NotificationRecord
import com.example.aiusage.repository.BudgetRepository
import com.example.aiusage.repository.ExpenseRepository
import com.example.aiusage.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val budgetRepository: BudgetRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _categoryFilter = MutableStateFlow("All")
    val categoryFilter: StateFlow<String> = _categoryFilter

    private val _selectedMonthYear = MutableStateFlow("")
    val selectedMonthYear: StateFlow<String> = _selectedMonthYear

    val expensesState: StateFlow<List<Expense>> = combine(
        expenseRepository.getAllExpensesFlow(),
        _searchQuery,
        _categoryFilter
    ) { list, query, category ->
        list.filter { exp ->
            val matchesQuery = exp.title.contains(query, ignoreCase = true) || 
                               exp.description.contains(query, ignoreCase = true)
            val matchesCategory = category == "All" || exp.category.equals(category, ignoreCase = true)
            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Category Budgets state for the current month
    val budgetsState: StateFlow<List<Budget>> = _selectedMonthYear
        .flatMapLatest { monthYear -> budgetRepository.getBudgetsForMonthFlow(monthYear) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _editingExpense = MutableStateFlow<Expense?>(null)
    val editingExpense: StateFlow<Expense?> = _editingExpense

    init {
        setCurrentMonthYear()
    }

    private fun setCurrentMonthYear() {
        val format = SimpleDateFormat("MM-yyyy", Locale.getDefault())
        _selectedMonthYear.value = format.format(Calendar.getInstance().time)
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateCategoryFilter(category: String) {
        _categoryFilter.value = category
    }

    fun loadExpenseForEditing(expenseId: Int) {
        viewModelScope.launch {
            val list = expenseRepository.getAllExpensesSync()
            _editingExpense.value = list.find { it.id == expenseId }
        }
    }

    fun saveExpense(
        title: String,
        amount: Double,
        category: String,
        date: Long,
        description: String,
        paymentMethod: String,
        id: Int = 0,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val expense = Expense(
                id = id,
                title = title.trim(),
                amount = amount,
                category = category,
                date = date,
                description = description.trim(),
                paymentMethod = paymentMethod
            )

            if (id == 0) {
                expenseRepository.insertExpense(expense)
                checkBudgetAlert(expense)
            } else {
                expenseRepository.updateExpense(expense)
                checkBudgetAlert(expense)
            }
            onSuccess()
        }
    }

    fun deleteExpense(expense: Expense, onSuccess: () -> Unit) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(expense)
            onSuccess()
        }
    }

    fun saveBudget(category: String, limit: Double, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val monthYear = _selectedMonthYear.value
            val existing = budgetRepository.getBudgetByCategoryAndMonth(category, monthYear)

            if (existing != null) {
                val updated = existing.copy(limitAmount = limit)
                budgetRepository.updateBudget(updated)
            } else {
                val newBudget = Budget(
                    category = category,
                    limitAmount = limit,
                    monthYear = monthYear
                )
                budgetRepository.insertBudget(newBudget)
            }
            onSuccess()
        }
    }

    private suspend fun checkBudgetAlert(newExpense: Expense) {
        val monthYear = _selectedMonthYear.value
        val budgetLimit = budgetRepository.getBudgetByCategoryAndMonth(newExpense.category, monthYear) ?: return

        // Aggregate current month spend for this category
        val expenses = expenseRepository.getAllExpensesSync()
        
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        val totalCategorySpent = expenses.filter { exp ->
            val expCal = Calendar.getInstance().apply { timeInMillis = exp.date }
            exp.category.equals(newExpense.category, ignoreCase = true) &&
                    expCal.get(Calendar.MONTH) == currentMonth &&
                    expCal.get(Calendar.YEAR) == currentYear
        }.sumOf { it.amount }

        if (totalCategorySpent > budgetLimit.limitAmount) {
            // Breached! Create notification record in database
            val notification = NotificationRecord(
                title = "Budget Exceeded!",
                message = String.format(
                    Locale.US,
                    "Your spending on %s (₹%.2f) has exceeded your category budget limit of ₹%.0f.",
                    newExpense.category,
                    totalCategorySpent,
                    budgetLimit.limitAmount
                ),
                type = "BUDGET_ALERT"
            )
            notificationRepository.insertNotification(notification)
        }
    }
}
