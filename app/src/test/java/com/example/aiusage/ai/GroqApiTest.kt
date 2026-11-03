package com.example.aiusage.ai

import com.example.aiusage.entity.UsageRecord
import kotlinx.coroutines.runBlocking
import org.junit.Test

class GroqApiTest {

    @Test
    fun testAiOutput() = runBlocking {
        // Since we can't easily mock Context and SharedPreferences in a local unit test without Robolectric,
        // we'll just simulate the prompt generation and logic if we were to call the "server".
        
        println("--- SIMULATING SERVER REQUEST ---")
        val usageSummary = "Instagram: 2.5h, WhatsApp: 1.2h, LinkedIn: 0.5h"
        val totalSpent = 120.0
        val expenseSummary = "Food: $50, Shopping: $70"
        val monthlyBudget = 500.0

        val prompt = """
            The user has the following usage and expense logs over the past week:
            Total screen usage summary: $usageSummary
            Total spending: $$totalSpent (Detailed breakdown: $expenseSummary)
            Monthly budget limit: $$monthlyBudget
            
            Predict screen time for tomorrow (in minutes) and weekly expense projection (in dollars).
            Analyze patterns and generate one personalized actionable recommendation.
        """.trimIndent()

        println("Prompt sent to Groq AI:")
        println(prompt)
        println("\n--- SIMULATING SERVER RESPONSE ---")
        val mockResponse = """
            {
              "predictedScreenTimeMins": 210.0,
              "predictedExpensesNextWeek": 115.0,
              "screenTimeTrend": "DOWN",
              "expenseTrend": "STABLE",
              "recommendation": "You spent 40% of your budget on food this week. Consider meal prepping to save $20 next week!"
            }
        """.trimIndent()
        println("Response received from Groq AI:")
        println(mockResponse)
    }
}
