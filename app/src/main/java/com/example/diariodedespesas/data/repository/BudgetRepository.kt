package com.example.diariodedespesas.data.repository

import com.example.diariodedespesas.data.local.dao.BudgetDao
import com.example.diariodedespesas.data.local.entity.BudgetEntity
import com.example.diariodedespesas.data.model.Category
import kotlinx.coroutines.flow.Flow

class BudgetRepository(private val dao: BudgetDao) {

    fun getBudgets(userId: String): Flow<List<BudgetEntity>> = dao.getBudgets(userId)

    suspend fun saveBudgets(userId: String, budgets: Map<Category, Double>) {
        val entities = budgets.map { (category, limit) ->
            BudgetEntity(
                category = category.name,
                limitAmount = limit,
                userId = userId
            )
        }
        dao.upsertAll(entities)
    }

    suspend fun getLimitForCategory(userId: String, category: Category): Double {
        return dao.getLimitForCategory(userId, category.name) ?: 0.0
    }
}
