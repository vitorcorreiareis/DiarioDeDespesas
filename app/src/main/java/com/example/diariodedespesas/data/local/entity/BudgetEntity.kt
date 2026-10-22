package com.example.diariodedespesas.data.local.entity

import androidx.room.Entity

@Entity(tableName = "budgets", primaryKeys = ["category", "userId"])
data class BudgetEntity(
    val category: String,
    val limitAmount: Double,
    val userId: String
)
