package com.example.aiusage.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.aiusage.entity.Budget
import com.example.aiusage.viewmodel.DashboardViewModel
import com.example.aiusage.viewmodel.ExpenseViewModel
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetManagerScreen(
    navController: NavController,
    expenseViewModel: ExpenseViewModel = hiltViewModel(),
    dashboardViewModel: DashboardViewModel = hiltViewModel()
) {
    val user by dashboardViewModel.userState.collectAsState()
    val expenses by expenseViewModel.expensesState.collectAsState()
    val budgets by expenseViewModel.budgetsState.collectAsState()

    val categories = listOf("Food", "Shopping", "Entertainment", "Bills", "Transport", "Others")
    val scrollState = rememberScrollState()

    // Overall budget edit state
    var editOverallMode by remember { mutableStateOf(false) }
    var overallBudgetText by remember { mutableStateOf("") }

    // Category budget edit dialog state
    var showCategoryDialog by remember { mutableStateOf(false) }
    var selectedCategoryForEdit by remember { mutableStateOf("") }
    var categoryLimitText by remember { mutableStateOf("") }

    LaunchedEffect(key1 = user) {
        user?.let {
            overallBudgetText = it.monthlyBudgetLimit.toString()
        }
    }

    // Aggregate monthly expenses by category
    val categorySpendMap = remember(expenses) {
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
                title = { Text("Budget Manager", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                .verticalScroll(scrollState)
                .padding(24.dp)
        ) {
            // Overall Limit Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Monthly Spending Limit",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (editOverallMode) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = overallBudgetText,
                                onValueChange = { overallBudgetText = it },
                                label = { Text("Overall Limit (₹)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            IconButton(
                                onClick = {
                                    val doubleLimit = overallBudgetText.toDoubleOrNull()
                                    if (doubleLimit != null && doubleLimit > 0) {
                                        dashboardViewModel.updateMonthlyBudgetLimit(doubleLimit)
                                        editOverallMode = false
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Save, contentDescription = "Save")
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = String.format(Locale.US, "₹%.2f", user?.monthlyBudgetLimit ?: 15000.0),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            IconButton(onClick = { editOverallMode = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
                        }
                    }
                }
            }

            // Category Limits Header
            Text(
                text = "Category Budget Limits",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Category Limit Items list
            categories.forEach { category ->
                val spent = categorySpendMap[category] ?: 0.0
                val budget = budgets.find { it.category.equals(category, ignoreCase = true) }
                val limit = budget?.limitAmount ?: 0.0

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                            shape = androidx.compose.foundation.shape.CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = getCategoryIcon(category),
                                        contentDescription = category,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = category,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = String.format(Locale.US, "₹%.0f of ₹%.0f spent", spent, limit),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            IconButton(
                                onClick = {
                                    selectedCategoryForEdit = category
                                    categoryLimitText = if (limit > 0) limit.toString() else ""
                                    showCategoryDialog = true
                                }
                            ) {
                                Icon(
                                    imageVector = if (limit > 0) Icons.Default.Edit else Icons.Default.Add,
                                    contentDescription = "Edit Category Limit",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        if (limit > 0) {
                            val ratio = (spent / limit).toFloat().coerceIn(0f, 1f)
                            val pctVal = (spent / limit) * 100
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LinearProgressIndicator(
                                    progress = { ratio },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = when {
                                        ratio > 0.90f -> MaterialTheme.colorScheme.error
                                        ratio > 0.70f -> MaterialTheme.colorScheme.errorContainer
                                        else -> MaterialTheme.colorScheme.primary
                                    },
                                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = String.format(Locale.US, "%.0f%%", pctVal),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Category Budget Setup Dialog
    if (showCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = {
                Text(
                    text = "Set Limit for $selectedCategoryForEdit",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    OutlinedTextField(
                        value = categoryLimitText,
                        onValueChange = { categoryLimitText = it },
                        label = { Text("Limit Amount (₹)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val limit = categoryLimitText.toDoubleOrNull()
                        if (limit != null && limit >= 0) {
                            expenseViewModel.saveBudget(
                                category = selectedCategoryForEdit,
                                limit = limit,
                                onSuccess = {
                                    showCategoryDialog = false
                                }
                            )
                        }
                    }
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCategoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
