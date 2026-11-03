package com.example.aiusage.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String = "",
    val activeIcon: ImageVector? = null,
    val inactiveIcon: ImageVector? = null
) {
    // Auth Flow
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")

    // Bottom Navigation Bar Items
    object Home : Screen(
        route = "home",
        title = "Home",
        activeIcon = Icons.Filled.Home,
        inactiveIcon = Icons.Outlined.Home
    )
    object Usage : Screen(
        route = "usage",
        title = "Usage",
        activeIcon = Icons.Filled.QueryStats,
        inactiveIcon = Icons.Outlined.QueryStats
    )
    object Expense : Screen(
        route = "expense",
        title = "Expenses",
        activeIcon = Icons.AutoMirrored.Filled.ReceiptLong,
        inactiveIcon = Icons.AutoMirrored.Outlined.ReceiptLong
    )
    object Prediction : Screen(
        route = "prediction",
        title = "AI Predictions",
        activeIcon = Icons.Filled.SmartToy,
        inactiveIcon = Icons.Outlined.SmartToy
    )
    object Profile : Screen(
        route = "profile",
        title = "Profile",
        activeIcon = Icons.Filled.Person,
        inactiveIcon = Icons.Outlined.Person
    )

    // Inner Screens (Not in Bottom Bar)
    object AddExpense : Screen("add_expense")
    object EditExpense : Screen("edit_expense/{expenseId}") {
        fun createRoute(expenseId: Int) = "edit_expense/$expenseId"
    }
    object BudgetManager : Screen("budget_manager")
    object Reports : Screen(
        route = "reports",
        title = "Analytics",
        activeIcon = Icons.Filled.Analytics,
        inactiveIcon = Icons.Outlined.Analytics
    )
    object Notifications : Screen(
        route = "notifications",
        title = "Notifications",
        activeIcon = Icons.Filled.Notifications,
        inactiveIcon = Icons.Outlined.Notifications
    )
    object Settings : Screen(
        route = "settings",
        title = "Settings",
        activeIcon = Icons.Filled.Settings,
        inactiveIcon = Icons.Outlined.Settings
    )

    companion object {
        val bottomNavItems = listOf(Home, Usage, Expense, Prediction, Profile)
    }
}
