package com.example.diariodedespesas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.diariodedespesas.data.local.entity.BudgetEntity
import com.example.diariodedespesas.data.model.Category
import com.example.diariodedespesas.data.repository.BudgetRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class BudgetFormState(
    val limits: Map<Category, String> = Category.entries.associateWith { "" },
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null
)

class BudgetViewModel(private val repo: BudgetRepository) : ViewModel() {

    private val _state = MutableStateFlow(BudgetFormState())
    val state: StateFlow<BudgetFormState> = _state.asStateFlow()

    fun load(userId: String) {
        viewModelScope.launch {
            repo.getBudgets(userId).collect { budgets ->
                val map = budgets.associate {
                    Category.fromName(it.category) to
                        it.limitAmount.toBigDecimal().toPlainString().replace('.', ',')
                }
                val filled = Category.entries.associateWith { cat -> map[cat] ?: "" }
                _state.value = _state.value.copy(limits = filled)
            }
        }
    }

    fun onLimitChange(category: Category, text: String) {
        val filtered = text.filter { it.isDigit() || it == ',' || it == '.' }
        val updated = _state.value.limits.toMutableMap().apply { put(category, filtered) }
        _state.value = _state.value.copy(limits = updated, error = null)
    }

    fun save(userId: String) {
        val current = _state.value
        val parsed = mutableMapOf<Category, Double>()
        for ((cat, text) in current.limits) {
            val v = text.replace(',', '.').toDoubleOrNull()
            if (v != null && v < 0) {
                _state.value = current.copy(error = "Valores não podem ser negativos.")
                return
            }
            if (v != null && v > 0) parsed[cat] = v
        }
        viewModelScope.launch {
            _state.value = current.copy(isSaving = true)
            repo.saveBudgets(userId, parsed)
            _state.value = _state.value.copy(isSaving = false, saved = true)
        }
    }

    fun resetSaved() {
        _state.value = _state.value.copy(saved = false)
    }

    companion object {
        fun factory(repo: BudgetRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                BudgetViewModel(repo) as T
        }
    }
}
