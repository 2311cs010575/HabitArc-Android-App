package com.example.aiusage.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aiusage.ai.GroqApiClient
import com.example.aiusage.entity.PredictionRecord
import com.example.aiusage.entity.NotificationRecord
import com.example.aiusage.repository.ExpenseRepository
import com.example.aiusage.repository.NotificationRepository
import com.example.aiusage.repository.PredictionRepository
import com.example.aiusage.repository.UsageRepository
import com.example.aiusage.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class PredictionViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val usageRepository: UsageRepository,
    private val expenseRepository: ExpenseRepository,
    private val predictionRepository: PredictionRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _apiKey = MutableStateFlow("")
    val apiKey: StateFlow<String> = _apiKey

    val predictionsState: StateFlow<List<PredictionRecord>> = predictionRepository
        .getLatestPredictionsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val recommendationsState: StateFlow<List<NotificationRecord>> = notificationRepository
        .getAllNotificationsFlow()
        .map { list -> list.filter { it.type == "AI_INSIGHT" } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun loadApiKey(context: Context) {
        _apiKey.value = GroqApiClient.getApiKey(context)
    }

    fun updateApiKey(context: Context, key: String) {
        GroqApiClient.saveApiKey(context, key)
        _apiKey.value = key
    }

    fun runAiAnalysis(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Fetch User (Budget Limit)
                val user = userRepository.getUserSync()
                val budget = user?.monthlyBudgetLimit ?: 15000.0

                // 2. Fetch last 7 days of usage and expenses
                val calendar = Calendar.getInstance()
                val endTime = calendar.timeInMillis

                calendar.add(Calendar.DAY_OF_YEAR, -7)
                val startTime = calendar.timeInMillis

                val usageLogs = usageRepository.getUsageRecordsInDateRangeSync(startTime, endTime)
                val expenseLogs = expenseRepository.getExpensesInDateRangeSync(startTime, endTime)

                // 3. Request predictions from Groq / Math offline fallback
                val result = GroqApiClient.generatePredictions(
                    context = context,
                    historyUsage = usageLogs,
                    historyExpenses = expenseLogs,
                    monthlyBudget = budget
                )

                // 4. Save results to database
                val timestamp = System.currentTimeMillis()

                val screenTimePred = PredictionRecord(
                    predictedDate = timestamp + (24 * 60 * 60 * 1000), // prediction applies to tomorrow
                    predictionType = "SCREEN_TIME",
                    predictedValue = result.predictedScreenTimeMins,
                    trend = result.screenTimeTrend,
                    confidence = 0.85
                )

                val expensePred = PredictionRecord(
                    predictedDate = timestamp + (7 * 24 * 60 * 60 * 1000), // prediction applies to next week
                    predictionType = "EXPENSE",
                    predictedValue = result.predictedExpenseValue,
                    trend = result.expenseTrend,
                    confidence = 0.85
                )

                val recommendation = NotificationRecord(
                    title = "AI Insight",
                    message = result.recommendationText,
                    type = "AI_INSIGHT",
                    timestamp = timestamp
                )

                predictionRepository.insertPrediction(screenTimePred)
                predictionRepository.insertPrediction(expensePred)
                notificationRepository.insertNotification(recommendation)

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
