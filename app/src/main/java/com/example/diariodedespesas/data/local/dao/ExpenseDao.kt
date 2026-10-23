package com.example.diariodedespesas.data.local.dao

import androidx.room.*
import com.example.diariodedespesas.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: ExpenseEntity): Long

    @Delete
    suspend fun delete(expense: ExpenseEntity)

    @Query("SELECT * FROM expenses WHERE userId = :userId AND type = 'EXPENSE' AND date >= :startOfMonth ORDER BY date DESC")
    fun getExpensesFromMonth(userId: String, startOfMonth: Long): Flow<List<ExpenseEntity>>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expenses WHERE userId = :userId AND type = 'INCOME' AND date >= :startOfMonth")
    fun getMonthlyIncomeTotal(userId: String, startOfMonth: Long): Flow<Double>

    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC")
    fun getAllExpenses(userId: String): Flow<List<ExpenseEntity>>

    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId AND type = 'EXPENSE' AND category = :category AND date >= :startOfMonth")
    fun getTotalByCategory(userId: String, category: String, startOfMonth: Long): Flow<Double?>
}
