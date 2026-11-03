package com.example.aiusage.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aiusage.entity.Expense
import com.example.aiusage.entity.PredictionRecord
import com.example.aiusage.entity.UsageRecord
import com.example.aiusage.entity.User
import com.example.aiusage.repository.ExpenseRepository
import com.example.aiusage.repository.PredictionRepository
import com.example.aiusage.repository.UsageRepository
import com.example.aiusage.repository.UserRepository
import com.example.aiusage.utils.UsageStatsHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userRepository: UserRepository,
    private val expenseRepository: ExpenseRepository,
    private val usageRepository: UsageRepository,
    private val predictionRepository: PredictionRepository
) : ViewModel() {

    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> = _userState

    private val _totalScreenTimeToday = MutableStateFlow(0L)
    val totalScreenTimeToday: StateFlow<Long> = _totalScreenTimeToday

    private val _totalExpensesThisMonth = MutableStateFlow(0.0)
    val totalExpensesThisMonth: StateFlow<Double> = _totalExpensesThisMonth

    private val _remainingBudget = MutableStateFlow(0.0)
    val remainingBudget: StateFlow<Double> = _remainingBudget

    private val _topAppsToday = MutableStateFlow<List<UsageRecord>>(emptyList())
    val topAppsToday: StateFlow<List<UsageRecord>> = _topAppsToday

    private val _recentExpenses = MutableStateFlow<List<Expense>>(emptyList())
    val recentExpenses: StateFlow<List<Expense>> = _recentExpenses

    private val _latestPrediction = MutableStateFlow<PredictionRecord?>(null)
    val latestPrediction: StateFlow<PredictionRecord?> = _latestPrediction

    private val _hasUsagePermission = MutableStateFlow(false)
    val hasUsagePermission: StateFlow<Boolean> = _hasUsagePermission

    // Lists of (Day Label, Value)
    private val _weeklyUsageChartData = MutableStateFlow<List<Pair<String, Float>>>(emptyList())
    val weeklyUsageChartData: StateFlow<List<Pair<String, Float>>> = _weeklyUsageChartData

    private val _weeklyExpenseChartData = MutableStateFlow<List<Pair<String, Float>>>(emptyList())
    val weeklyExpenseChartData: StateFlow<List<Pair<String, Float>>> = _weeklyExpenseChartData

    init {
        loadDashboardData()
    }

    fun checkPermissions(context: Context) {
        _hasUsagePermission.value = UsageStatsHelper.hasUsageStatsPermission(context)
    }

    fun updateMonthlyBudgetLimit(limit: Double) {
        viewModelScope.launch {
            _userState.value?.let { user ->
                userRepository.updateUserBudget(user.id, limit)
                _userState.value = user.copy(monthlyBudgetLimit = limit)
                calculateBudgetRemaining()
            }
        }
    }

    fun logout() {
        userRepository.setLoggedIn(false)
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            // 1. Load User
            userRepository.getUserFlow().collect { user ->
                _userState.value = user
                calculateBudgetRemaining()
            }
        }

        viewModelScope.launch {
            // 2. Load Recent & Monthly Expenses
            expenseRepository.getAllExpensesFlow().collect { list ->
                _recentExpenses.value = list.take(5)
                calculateMonthlyExpenses(list)
            }
        }

        viewModelScope.launch {
            // 3. Load Latest AI Prediction
            predictionRepository.getLatestPredictionsFlow().collect { predictions ->
                _latestPrediction.value = predictions.firstOrNull()
            }
        }

        // 4. Load screen time and chart data
        refreshUsageAndCharts()
    }

    fun refreshUsageAndCharts() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val now = calendar.timeInMillis

            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfToday = calendar.timeInMillis

            val stats = UsageStatsHelper.fetchUsageStats(context, startOfToday, now)
            if (stats.isNotEmpty()) {
                try {
                    usageRepository.deleteUsageForDate(startOfToday)
                    usageRepository.insertUsageRecords(stats)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Load screen time and aggregate by appName to eliminate duplicates
            usageRepository.getUsageRecordsInDateRangeFlow(startOfToday, now).collect { records ->
                val aggregated = records.groupBy { it.appName }.map { (appName, appRecords) ->
                    val firstRecord = appRecords.first()
                    UsageRecord(
                        id = firstRecord.id,
                        packageName = firstRecord.packageName,
                        appName = appName,
                        usageTimeMs = appRecords.sumOf { it.usageTimeMs },
                        launchCount = appRecords.sumOf { it.launchCount },
                        date = firstRecord.date
                    )
                }.sortedByDescending { it.usageTimeMs }

                _totalScreenTimeToday.value = aggregated.sumOf { it.usageTimeMs }
                _topAppsToday.value = aggregated.take(5)
            }
        }

        viewModelScope.launch {
            loadWeeklyCharts()
        }
    }

    private fun calculateBudgetRemaining() {
        val budgetLimit = _userState.value?.monthlyBudgetLimit ?: 15000.0
        val spent = _totalExpensesThisMonth.value
        _remainingBudget.value = (budgetLimit - spent).coerceAtLeast(0.0)
    }

    private fun calculateMonthlyExpenses(expenses: List<Expense>) {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        val monthlyTotal = expenses.filter { exp ->
            val expCal = Calendar.getInstance().apply { timeInMillis = exp.date }
            expCal.get(Calendar.MONTH) == currentMonth && expCal.get(Calendar.YEAR) == currentYear
        }.sumOf { it.amount }

        _totalExpensesThisMonth.value = monthlyTotal
        calculateBudgetRemaining()
    }

    private suspend fun loadWeeklyCharts() {
        val calendar = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        
        val usageChartPoints = mutableListOf<Pair<String, Float>>()
        val expenseChartPoints = mutableListOf<Pair<String, Float>>()

        // Gather statistics for the last 7 days
        for (i in 6 downTo 0) {
            val loopCal = Calendar.getInstance()
            loopCal.add(Calendar.DAY_OF_YEAR, -i)
            
            // Start of that day
            loopCal.set(Calendar.HOUR_OF_DAY, 0)
            loopCal.set(Calendar.MINUTE, 0)
            loopCal.set(Calendar.SECOND, 0)
            loopCal.set(Calendar.MILLISECOND, 0)
            val dayStart = loopCal.timeInMillis

            // End of that day
            loopCal.set(Calendar.HOUR_OF_DAY, 23)
            loopCal.set(Calendar.MINUTE, 59)
            loopCal.set(Calendar.SECOND, 59)
            val dayEnd = loopCal.timeInMillis

            val label = dayFormat.format(Date(dayStart))

            // Fetch usage
            val dailyRecords = usageRepository.getUsageRecordsInDateRangeSync(dayStart, dayEnd)
            val totalTimeHours = dailyRecords.sumOf { it.usageTimeMs } / (1000f * 60f * 60f) // convert to hours
            usageChartPoints.add(Pair(label, totalTimeHours))

            // Fetch expenses
            val dailyExpenses = expenseRepository.getExpensesInDateRangeSync(dayStart, dayEnd)
            val totalSpent = dailyExpenses.sumOf { it.amount }.toFloat()
            expenseChartPoints.add(Pair(label, totalSpent))
        }

        _weeklyUsageChartData.value = usageChartPoints
        _weeklyExpenseChartData.value = expenseChartPoints
    }
}
