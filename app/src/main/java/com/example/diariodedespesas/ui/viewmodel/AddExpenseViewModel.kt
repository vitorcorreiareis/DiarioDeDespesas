package com.example.diariodedespesas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.diariodedespesas.data.model.Category
import com.example.diariodedespesas.data.model.PaymentMethod
import com.example.diariodedespesas.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AddExpenseState(
    val isIncome: Boolean = false,
    val amountText: String = "",
    val selectedCategory: Category = Category.SUPERMERCADO,
    val description: String = "",
    val selectedPayment: PaymentMethod = PaymentMethod.PIX,
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null
)

class AddExpenseViewModel(private val repo: ExpenseRepository) : ViewModel() {

    private val _state = MutableStateFlow(AddExpenseState())
    val state: StateFlow<AddExpenseState> = _state.asStateFlow()

    fun onTypeChange(isIncome: Boolean) {
        _state.value = _state.value.copy(isIncome = isIncome, error = null)
    }

    fun onAmountChange(text: String) {
        val filtered = text.filter { it.isDigit() || it == ',' || it == '.' }
        _state.value = _state.value.copy(amountText = filtered, error = null)
    }

    fun onCategoryChange(cat: Category) {
        _state.value = _state.value.copy(selectedCategory = cat)
    }

    fun onDescriptionChange(text: String) {
        _state.value = _state.value.copy(description = text, error = null)
    }

    fun onPaymentChange(method: PaymentMethod) {
        _state.value = _state.value.copy(selectedPayment = method)
    }

    fun save(userId: String) {
        val current = _state.value
        val amount = current.amountText.replace(',', '.').toDoubleOrNull()

        if (amount == null || amount <= 0) {
            _state.value = current.copy(error = "Informe um valor válido maior que zero.")
            return
        }
        if (current.description.isBlank()) {
            _state.value = current.copy(error = "Informe a descrição.")
            return
        }

        viewModelScope.launch {
            _state.value = current.copy(isSaving = true)
            if (current.isIncome) {
                repo.addIncome(
                    userId = userId,
                    amount = amount,
                    description = current.description.trim(),
                    paymentMethod = current.selectedPayment
                )
            } else {
                repo.addExpense(
                    userId = userId,
                    amount = amount,
                    category = current.selectedCategory,
                    description = current.description.trim(),
                    paymentMethod = current.selectedPayment
                )
            }
            _state.value = _state.value.copy(isSaving = false, saved = true)
        }
    }

    fun resetSaved() {
        _state.value = AddExpenseState()
    }

    companion object {
        fun factory(repo: ExpenseRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                AddExpenseViewModel(repo) as T
        }
    }
}
