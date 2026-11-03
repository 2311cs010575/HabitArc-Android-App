package com.example.aiusage.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.aiusage.screens.SplashScreen
import com.example.aiusage.screens.LoginScreen
import com.example.aiusage.screens.RegisterScreen
import com.example.aiusage.screens.ForgotPasswordScreen
import com.example.aiusage.screens.HomeScreen
import com.example.aiusage.screens.ExpenseScreen
import com.example.aiusage.screens.AddExpenseScreen
import com.example.aiusage.screens.EditExpenseScreen
import com.example.aiusage.screens.BudgetManagerScreen
import com.example.aiusage.screens.PredictionScreen
import com.example.aiusage.screens.UsageScreen
import com.example.aiusage.screens.ReportsScreen
import com.example.aiusage.screens.NotificationsScreen
import com.example.aiusage.screens.ProfileScreen
import com.example.aiusage.screens.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }

        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController = navController)
        }

        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(Screen.Usage.route) {
            UsageScreen(navController = navController)
        }

        composable(Screen.Expense.route) {
            ExpenseScreen(navController = navController)
        }

        composable(Screen.Prediction.route) {
            PredictionScreen(navController = navController)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }

        composable(Screen.AddExpense.route) {
            AddExpenseScreen(navController = navController)
        }

        composable(
            route = Screen.EditExpense.route,
            arguments = listOf(navArgument("expenseId") { type = NavType.IntType })
        ) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getInt("expenseId") ?: -1
            EditExpenseScreen(navController = navController, expenseId = expenseId)
        }

        composable(Screen.BudgetManager.route) {
            BudgetManagerScreen(navController = navController)
        }

        composable(Screen.Reports.route) {
            ReportsScreen(navController = navController)
        }

        composable(Screen.Notifications.route) {
            NotificationsScreen(navController = navController)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
}

@Composable
fun PlaceholderScreen(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = name)
    }
}
