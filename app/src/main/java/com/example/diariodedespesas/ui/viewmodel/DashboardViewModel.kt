package com.example.diariodedespesas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.diariodedespesas.data.local.entity.BudgetEntity
import com.example.diariodedespesas.data.local.entity.ExpenseEntity
import com.example.diariodedespesas.data.model.Category
import com.example.diariodedespesas.data.repository.BudgetRepository
import com.example.diariodedespesas.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CategorySummary(
    val category: Category,
    val spent: Double,
    val limit: Double
) {
    val progress: Float get() = if (limit > 0) (spent / limit).toFloat().coerceAtMost(1f) else 0f
    val isOverBudget: Boolean get() = spent > limit && limit > 0
    val remaining: Double get() = limit - spent
}

data class DashboardState(
    val categorySummaries: List<CategorySummary> = emptyList(),
    val totalSpent: Double = 0.0,
    val totalBudget: Double = 0.0,
    val totalIncome: Double = 0.0,
    val isLoading: Boolean = true
) {
    val balance: Double get() = totalIncome - totalSpent
}

class DashboardViewModel(
    private val expenseRepo: ExpenseRepository,
    private val budgetRepo: BudgetRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    fun load(userId: String) {
        viewModelScope.launch {
            combine(
                expenseRepo.getMonthlyExpenses(userId),
                budgetRepo.getBudgets(userId),
                expenseRepo.getMonthlyIncome(userId)
            ) { expenses, budgets, income ->
                buildDashboard(expenses, budgets, income)
            }.collect { dashState ->
                _state.value = dashState
            }
        }
    }

    private fun buildDashboard(
        expenses: List<ExpenseEntity>,
        budgets: List<BudgetEntity>,
        totalIncome: Double
    ): DashboardState {
        val budgetMap = budgets.associate { it.category to it.limitAmount }
        val spentMap = expenses
            .groupBy { it.category }
            .mapValues { (_, list) -> list.sumOf { it.amount } }

        // Só mostra categorias com gasto > 0 ou com limite configurado
        val summaries = Category.entries.mapNotNull { cat ->
            val spent = spentMap[cat.name] ?: 0.0
            val limit = budgetMap[cat.name] ?: 0.0
            if (spent > 0.0 || limit > 0.0) {
                CategorySummary(category = cat, spent = spent, limit = limit)
            } else null
        }

        return DashboardState(
            categorySummaries = summaries,
            totalSpent = summaries.sumOf { it.spent },
            totalBudget = summaries.sumOf { it.limit },
            totalIncome = totalIncome,
            isLoading = false
        )
    }

    companion object {
        fun factory(expenseRepo: ExpenseRepository, budgetRepo: BudgetRepository) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    DashboardViewModel(expenseRepo, budgetRepo) as T
            }
    }
}
