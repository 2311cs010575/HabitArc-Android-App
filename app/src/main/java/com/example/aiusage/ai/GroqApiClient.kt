package com.example.aiusage.ai

import android.content.Context
import androidx.core.content.edit
import com.example.aiusage.entity.Expense
import com.example.aiusage.entity.UsageRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GroqApiClient {

    private val client = okhttp3.OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private const val GROQ_URL = "https://api.groq.com/openai/v1/chat/completions"

    data class AiPredictionResult(
        val predictedScreenTimeMins: Double,
        val predictedExpenseValue: Double,
        val screenTimeTrend: String,
        val expenseTrend: String,
        val recommendationText: String
    )

    fun getApiKey(context: Context): String {
        val prefs = context.getSharedPreferences("ai_usage_prefs", Context.MODE_PRIVATE)
        val key = prefs.getString("groq_api_key", "") ?: ""
        return key.ifBlank { "" }
    }

    fun saveApiKey(context: Context, key: String) {
        val prefs = context.getSharedPreferences("ai_usage_prefs", Context.MODE_PRIVATE)
        prefs.edit { putString("groq_api_key", key.trim()) }
    }

    suspend fun generatePredictions(
        context: Context,
        historyUsage: List<UsageRecord>,
        historyExpenses: List<Expense>,
        monthlyBudget: Double
    ): AiPredictionResult = withContext(Dispatchers.IO) {
        val apiKey = getApiKey(context)

        if (apiKey.isBlank()) {
            val avgDailyUsageMs = if (historyUsage.isNotEmpty()) historyUsage.sumOf { it.usageTimeMs } / 7.0 else 0.0
            val avgDailyUsageMins = avgDailyUsageMs / (1000.0 * 60.0)
            val predictedMins = (avgDailyUsageMins * 1.05).coerceAtLeast(120.0)

            val totalSpentThisWeek = historyExpenses.sumOf { it.amount }
            val predictedWeeklySpend = (totalSpentThisWeek * 0.98).coerceAtLeast(15.0)

            return@withContext AiPredictionResult(
                predictedScreenTimeMins = predictedMins,
                predictedExpenseValue = predictedWeeklySpend,
                screenTimeTrend = "STABLE",
                expenseTrend = "STABLE",
                recommendationText = "AI insights are running in Offline Fallback Mode. To unlock advanced Llama 3 insights, enter your Groq API Key in settings!"
            )
        }

        try {
            val usageSummary = historyUsage.groupBy { it.appName }.map { (appName, records) ->
                val hours = records.sumOf { it.usageTimeMs } / (1000.0 * 60.0 * 60.0)
                "$appName: ${String.format(java.util.Locale.US, "%.1fh", hours)}"
            }.joinToString(", ")

            val totalSpent = historyExpenses.sumOf { it.amount }
            val expenseSummary = historyExpenses.groupBy { it.category }.map { (category, records) ->
                "$category: ₹${records.sumOf { it.amount }}"
            }.joinToString(", ")

            val prompt = """
                The user has the following usage and expense logs over the past week:
                Total screen usage summary: $usageSummary
                Total spending: ₹$totalSpent (Detailed breakdown: $expenseSummary)
                Monthly budget limit: ₹$monthlyBudget
                
                Predict screen time for tomorrow (in minutes) and weekly expense projection (in Indian Rupees - ₹).
                Analyze patterns and generate one personalized actionable recommendation.
            """.trimIndent()

            val systemMessage = """
                You are a premium AI Digital Wellbeing and Budget Advisor. 
                You analyze logs and return screen time predictions and financial analytics. 
                You MUST return a JSON object with EXACTLY the following format:
                {
                  "predictedScreenTimeMins": 250.5,
                  "predictedExpensesNextWeek": 1500.0,
                  "screenTimeTrend": "UP",
                  "expenseTrend": "DOWN",
                  "recommendation": "Your screen time on social media has risen by 15%; consider setting a 1-hour limit to stay focused."
                }
                Values for screenTimeTrend and expenseTrend must be one of: UP, DOWN, STABLE.
            """.trimIndent()

            val jsonBody = JSONObject().apply {
                put("model", "llama-3.3-70b-versatile")
                put("response_format", JSONObject().put("type", "json_object"))
                val messagesArray = org.json.JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", systemMessage)
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                }
                put("messages", messagesArray)
            }

            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = jsonBody.toString().toRequestBody(mediaType)

            val request = okhttp3.Request.Builder()
                .url(GROQ_URL)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw Exception("Groq API Call Failed: ${response.code} - ${response.message}")
                }
                val responseString = response.body?.string() ?: throw Exception("Response body was empty")
                val responseJson = JSONObject(responseString)
                val choice = responseJson.getJSONArray("choices").getJSONObject(0)
                val contentString = choice.getJSONObject("message").getString("content")
                val parsedContent = JSONObject(contentString)

                AiPredictionResult(
                    predictedScreenTimeMins = parsedContent.getDouble("predictedScreenTimeMins"),
                    predictedExpenseValue = parsedContent.getDouble("predictedExpensesNextWeek"),
                    screenTimeTrend = parsedContent.getString("screenTimeTrend"),
                    expenseTrend = parsedContent.getString("expenseTrend"),
                    recommendationText = parsedContent.getString("recommendation")
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            AiPredictionResult(
                predictedScreenTimeMins = 320.0,
                predictedExpenseValue = 150.0,
                screenTimeTrend = "UP",
                expenseTrend = "UP",
                recommendationText = "Error contacting Groq API. Check your internet connection and API key. Error: ${e.message}"
            )
        }
    }
}
