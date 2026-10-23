package com.example.diariodedespesas.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.diariodedespesas.data.local.dao.BudgetDao
import com.example.diariodedespesas.data.local.dao.ExpenseDao
import com.example.diariodedespesas.data.local.dao.UserDao
import com.example.diariodedespesas.data.local.entity.BudgetEntity
import com.example.diariodedespesas.data.local.entity.ExpenseEntity
import com.example.diariodedespesas.data.local.entity.UserEntity

@Database(
    entities = [ExpenseEntity::class, BudgetEntity::class, UserEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun budgetDao(): BudgetDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "diario_despesas.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
