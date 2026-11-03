package com.example.aiusage.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.aiusage.entity.PredictionRecord
import com.example.aiusage.entity.NotificationRecord
import com.example.aiusage.viewmodel.PredictionViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictionScreen(
    navController: NavController,
    viewModel: PredictionViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    val apiKey by viewModel.apiKey.collectAsState()
    val predictions by viewModel.predictionsState.collectAsState()
    val recommendations by viewModel.recommendationsState.collectAsState()

    var apiInputText by remember { mutableStateOf("") }
    var hideKey by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()

    LaunchedEffect(key1 = true) {
        viewModel.loadApiKey(context)
    }

    LaunchedEffect(key1 = apiKey) {
        apiInputText = apiKey
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Predictor", fontWeight = FontWeight.Bold) },
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
            // Groq API Configuration Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Groq Llama 3 API Setup",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Enter your Groq API Key to enable real-time predictions and cognitive savings recommendations.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = apiInputText,
                        onValueChange = { apiInputText = it },
                        label = { Text("Groq API Key") },
                        leadingIcon = { Icon(Icons.Default.Key, contentDescription = "API Key") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        visualTransformation = if (hideKey) PasswordVisualTransformation() else VisualTransformation.None,
                        trailingIcon = {
                            TextButton(onClick = { hideKey = !hideKey }) {
                                Text(if (hideKey) "Show" else "Hide")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.updateApiKey(context, apiInputText)
                        },
                        modifier = Modifier.align(Alignment.End),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Save Key")
                    }
                }
            }

            // Run Diagnostics Button
            Button(
                onClick = { viewModel.runAiAnalysis(context) },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Analyzing Historical Logs...")
                } else {
                    Icon(Icons.Default.Psychology, contentDescription = "Run AI")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Run AI Predictions", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Latest Prediction Cards (Split Screen Time / Expense)
            val screenTimePred = predictions.find { it.predictionType == "SCREEN_TIME" }
            val expensePred = predictions.find { it.predictionType == "EXPENSE" }

            if (screenTimePred != null || expensePred != null) {
                Text(
                    text = "Smart Forecasts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Screen time prediction card
                    screenTimePred?.let {
                        val hours = it.predictedValue / 60.0
                        PredictionValueCard(
                            title = "Predicted Usage",
                            value = String.format(Locale.US, "%.1f hrs", hours),
                            trend = it.trend,
                            icon = Icons.Default.AccessTime,
                            cardColor = MaterialTheme.colorScheme.primaryContainer,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Expense prediction card
                    expensePred?.let {
                        PredictionValueCard(
                            title = "Predicted Spend",
                            value = String.format(Locale.US, "₹%.0f", it.predictedValue),
                            trend = it.trend,
                            icon = Icons.Default.AttachMoney,
                            cardColor = MaterialTheme.colorScheme.tertiaryContainer,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // AI Recommendations List
            if (recommendations.isNotEmpty()) {
                Text(
                    text = "Personalized Insights",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                recommendations.forEach { recommendation ->
                    val impactScore = if (recommendation.message.contains("Error", ignoreCase = true)) "LOW" else "HIGH"
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        color = if (impactScore == "HIGH") {
                                            MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
                                        } else {
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = "Insight",
                                    tint = if (impactScore == "HIGH") {
                                        MaterialTheme.colorScheme.error
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = recommendation.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    SuggestionChip(
                                        onClick = {},
                                        label = { Text("Impact: $impactScore") }
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                if (!isLoading && screenTimePred == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tap Run AI Predictions to analyze historical trends.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PredictionValueCard(
    title: String,
    value: String,
    trend: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    cardColor: Color,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = tint,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = tint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val (trendIcon, trendText, trendColor) = when (trend.uppercase()) {
                    "UP" -> Triple(Icons.Default.ArrowUpward, "Tending Up", MaterialTheme.colorScheme.error)
                    "DOWN" -> Triple(Icons.Default.ArrowDownward, "Tending Down", MaterialTheme.colorScheme.primary)
                    else -> Triple(Icons.Default.TrendingFlat, "Stable", MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Icon(
                    imageVector = trendIcon,
                    contentDescription = trendText,
                    tint = trendColor,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = trendText,
                    style = MaterialTheme.typography.bodySmall,
                    color = trendColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
