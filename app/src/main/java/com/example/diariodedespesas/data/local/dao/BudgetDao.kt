package com.example.diariodedespesas.data.local.dao

import androidx.room.*
import com.example.diariodedespesas.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(budget: BudgetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(budgets: List<BudgetEntity>)

    @Query("SELECT * FROM budgets WHERE userId = :userId")
    fun getBudgets(userId: String): Flow<List<BudgetEntity>>

    @Query("SELECT limitAmount FROM budgets WHERE userId = :userId AND category = :category")
    suspend fun getLimitForCategory(userId: String, category: String): Double?
}
