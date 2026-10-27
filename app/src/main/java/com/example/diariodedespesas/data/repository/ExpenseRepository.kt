package com.example.diariodedespesas.data.repository

import com.example.diariodedespesas.data.local.dao.ExpenseDao
import com.example.diariodedespesas.data.local.entity.ExpenseEntity
import com.example.diariodedespesas.data.model.Category
import com.example.diariodedespesas.data.model.PaymentMethod
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class ExpenseRepository(private val dao: ExpenseDao) {

    fun getMonthlyExpenses(userId: String): Flow<List<ExpenseEntity>> =
        dao.getExpensesFromMonth(userId, startOfCurrentMonth())

    fun getMonthlyIncome(userId: String): Flow<Double> =
        dao.getMonthlyIncomeTotal(userId, startOfCurrentMonth())

    fun getAllExpenses(userId: String): Flow<List<ExpenseEntity>> =
        dao.getAllExpenses(userId)

    fun getMonthlyTotalByCategory(userId: String, category: Category): Flow<Double?> =
        dao.getTotalByCategory(userId, category.name, startOfCurrentMonth())

    suspend fun addExpense(
        userId: String,
        amount: Double,
        category: Category,
        description: String,
        paymentMethod: PaymentMethod,
        date: Long = System.currentTimeMillis()
    ): Long = dao.insert(
        ExpenseEntity(
            amount = amount,
            category = category.name,
            description = description,
            paymentMethod = paymentMethod.name,
            date = date,
            userId = userId,
            type = "EXPENSE"
        )
    )

    suspend fun addIncome(
        userId: String,
        amount: Double,
        description: String,
        paymentMethod: PaymentMethod,
        date: Long = System.currentTimeMillis()
    ): Long = dao.insert(
        ExpenseEntity(
            amount = amount,
            category = "",
            description = description,
            paymentMethod = paymentMethod.name,
            date = date,
            userId = userId,
            type = "INCOME"
        )
    )

    suspend fun deleteExpense(expense: ExpenseEntity) = dao.delete(expense)

    private fun startOfCurrentMonth(): Long =
        Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
}
