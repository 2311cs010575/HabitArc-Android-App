package com.example.aiusage.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.aiusage.charts.BarChart
import com.example.aiusage.charts.LineChart
import com.example.aiusage.entity.Expense
import com.example.aiusage.viewmodel.DashboardViewModel
import com.example.aiusage.viewmodel.ExpenseViewModel
import java.io.File
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    navController: NavController,
    dashboardViewModel: DashboardViewModel = hiltViewModel(),
    expenseViewModel: ExpenseViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Screen Time", "Expenses")

    val weeklyUsageChartData by dashboardViewModel.weeklyUsageChartData.collectAsState()
    val weeklyExpenseChartData by dashboardViewModel.weeklyExpenseChartData.collectAsState()
    val totalScreenTimeToday by dashboardViewModel.totalScreenTimeToday.collectAsState()
    val totalExpensesThisMonth by dashboardViewModel.totalExpensesThisMonth.collectAsState()
    val topAppsToday by dashboardViewModel.topAppsToday.collectAsState()
    val expenses by expenseViewModel.expensesState.collectAsState()

    val categories = listOf("Food", "Shopping", "Entertainment", "Bills", "Transport", "Others")
    val scrollState = rememberScrollState()

    // Aggregate expenses by category for share progress bars
    val categoryTotals = remember(expenses) {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        val monthlyExpenses = expenses.filter { exp ->
            val expCal = Calendar.getInstance().apply { timeInMillis = exp.date }
            expCal.get(Calendar.MONTH) == currentMonth && expCal.get(Calendar.YEAR) == currentYear
        }

        categories.associateWith { cat ->
            monthlyExpenses.filter { it.category.equals(cat, ignoreCase = true) }.sumOf { it.amount }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics Reports", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (selectedTab == 1) {
                        IconButton(onClick = { exportExpensesToCsv(context, expenses) }) {
                            Icon(Icons.Default.FileDownload, contentDescription = "Export CSV")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Tab Selector
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, label ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(label, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(24.dp)
            ) {
                if (selectedTab == 0) {
                    // SCREEN TIME REPORTS TAB
                    Text(
                        text = "Screen Time Overview",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Total Screen Time Today Widget
                    val hours = totalScreenTimeToday / (1000 * 60 * 60)
                    val minutes = (totalScreenTimeToday % (1000 * 60 * 60)) / (1000 * 60)

                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Today's Total Screen Usage",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${hours}h ${minutes}m",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    // Canvas Bar Chart
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Weekly Screen Usage (Hours)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            if (weeklyUsageChartData.isNotEmpty()) {
                                BarChart(data = weeklyUsageChartData)
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(150.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No usage records available")
                                }
                            }
                        }
                    }

                    // Top apps today breakdown
                    if (topAppsToday.isNotEmpty()) {
                        Text(
                            text = "Top Used Apps Share",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        topAppsToday.forEach { app ->
                            val appHours = app.usageTimeMs / (1000 * 60 * 60)
                            val appMins = (app.usageTimeMs % (1000 * 60 * 60)) / (1000 * 60)
                            val ratio = (app.usageTimeMs.toFloat() / totalScreenTimeToday.toFloat()).coerceIn(0f, 1f)

                            Card(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = app.appName, fontWeight = FontWeight.Bold)
                                        Text(text = "${appHours}h ${appMins}m", fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    LinearProgressIndicator(
                                        progress = { ratio },
                                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                        color = MaterialTheme.colorScheme.primary,
                                        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // EXPENSE REPORTS TAB
                    Text(
                        text = "Expenses Overview",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Total Expense Widget
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Monthly Total Expenditures",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = String.format(Locale.US, "₹%.2f", totalExpensesThisMonth),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }

                    // Canvas Line Chart
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Weekly Spending Trend (₹)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            if (weeklyExpenseChartData.isNotEmpty()) {
                                LineChart(data = weeklyExpenseChartData)
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(150.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No expense records available")
                                }
                            }
                        }
                    }

                    // Category Breakdown Share
                    Text(
                        text = "Monthly Spending by Category",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    categories.forEach { category ->
                        val amount = categoryTotals[category] ?: 0.0
                        val ratio = if (totalExpensesThisMonth > 0) (amount / totalExpensesThisMonth).toFloat() else 0f

                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier.size(24.dp).background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f), RoundedCornerShape(4.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = getCategoryIcon(category),
                                                contentDescription = category,
                                                tint = MaterialTheme.colorScheme.tertiary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = category, fontWeight = FontWeight.Bold)
                                    }
                                    Text(text = String.format(Locale.US, "₹%.2f", amount), fontWeight = FontWeight.Bold)
                                }
                                if (totalExpensesThisMonth > 0) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    LinearProgressIndicator(
                                        progress = { ratio },
                                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                        color = MaterialTheme.colorScheme.tertiary,
                                        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Exporter Utility implementation to write CSV outputs
private fun exportExpensesToCsv(context: Context, expenses: List<Expense>) {
    try {
        val fileName = "expenses_export_${System.currentTimeMillis()}.csv"
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
            output.write("ID,Title,Amount,Category,Date,PaymentMethod,Description\n".toByteArray())
            expenses.forEach { exp ->
                val line = "${exp.id},\"${exp.title}\",${exp.amount},\"${exp.category}\",${exp.date},\"${exp.paymentMethod}\",\"${exp.description}\"\n"
                output.write(line.toByteArray())
            }
        }
        Toast.makeText(context, "Exported successfully to: $fileName", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Error exporting CSV: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
