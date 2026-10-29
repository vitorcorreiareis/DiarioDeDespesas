package com.example.diariodedespesas.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.diariodedespesas.data.local.AppDatabase
import com.example.diariodedespesas.data.repository.AuthRepository
import com.example.diariodedespesas.data.repository.BudgetRepository
import com.example.diariodedespesas.data.repository.ExpenseRepository
import com.example.diariodedespesas.ui.screens.auth.LoginScreen
import com.example.diariodedespesas.ui.screens.auth.RegisterScreen
import com.example.diariodedespesas.ui.screens.budget.BudgetConfigScreen
import com.example.diariodedespesas.ui.screens.dashboard.DashboardScreen
import com.example.diariodedespesas.ui.screens.expense.AddExpenseScreen
import com.example.diariodedespesas.ui.screens.history.HistoryScreen

object Route {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
    const val ADD_EXPENSE = "add_expense"
    const val BUDGET_CONFIG = "budget_config"
    const val HISTORY = "history"
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    db: AppDatabase,
    authRepo: AuthRepository,
    startDestination: String
) {
    val expenseRepo = ExpenseRepository(db.expenseDao())
    val budgetRepo = BudgetRepository(db.budgetDao())

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Route.LOGIN) {
            LoginScreen(
                authRepo = authRepo,
                onLoginSuccess = {
                    navController.navigate(Route.DASHBOARD) {
                        popUpTo(Route.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Route.REGISTER) }
            )
        }

        composable(Route.REGISTER) {
            RegisterScreen(
                authRepo = authRepo,
                onRegisterSuccess = {
                    navController.navigate(Route.DASHBOARD) {
                        popUpTo(Route.LOGIN) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Route.DASHBOARD) {
            DashboardScreen(
                expenseRepo = expenseRepo,
                budgetRepo = budgetRepo,
                authRepo = authRepo,
                onNavigateToAddExpense = { navController.navigate(Route.ADD_EXPENSE) },
                onNavigateToBudgetConfig = { navController.navigate(Route.BUDGET_CONFIG) },
                onNavigateToHistory = { navController.navigate(Route.HISTORY) },
                onLogout = {
                    navController.navigate(Route.LOGIN) {
                        popUpTo(Route.DASHBOARD) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.ADD_EXPENSE) {
            AddExpenseScreen(
                expenseRepo = expenseRepo,
                authRepo = authRepo,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Route.BUDGET_CONFIG) {
            BudgetConfigScreen(
                budgetRepo = budgetRepo,
                authRepo = authRepo,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Route.HISTORY) {
            HistoryScreen(
                expenseRepo = expenseRepo,
                authRepo = authRepo,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
