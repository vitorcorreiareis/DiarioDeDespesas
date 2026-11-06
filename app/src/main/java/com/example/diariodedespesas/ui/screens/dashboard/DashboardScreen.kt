package com.example.diariodedespesas.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.diariodedespesas.data.repository.AuthRepository
import com.example.diariodedespesas.data.repository.BudgetRepository
import com.example.diariodedespesas.data.repository.ExpenseRepository
import com.example.diariodedespesas.ui.theme.*
import com.example.diariodedespesas.ui.viewmodel.CategorySummary
import com.example.diariodedespesas.ui.viewmodel.DashboardViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    expenseRepo: ExpenseRepository,
    budgetRepo: BudgetRepository,
    authRepo: AuthRepository,
    onNavigateToAddExpense: () -> Unit,
    onNavigateToBudgetConfig: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onLogout: () -> Unit
) {
    val vm: DashboardViewModel = viewModel(
        factory = DashboardViewModel.factory(expenseRepo, budgetRepo)
    )
    val state by vm.state.collectAsState()
    val userId = authRepo.currentUser?.uid ?: ""
    val monthLabel = SimpleDateFormat("MMMM yyyy", Locale("pt", "BR"))
        .format(Date()).replaceFirstChar { it.uppercase() }

    LaunchedEffect(userId) { vm.load(userId) }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp)
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Olá! 👋", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Text(monthLabel, fontSize = 13.sp, color = TextSecondary)
                }
                TextButton(onClick = onLogout) {
                    Text("Sair", color = TextSecondary, fontSize = 13.sp)
                }
            }

            // Summary card
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Primary)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SummaryColumn(
                        label = "Receita",
                        value = formatCurrency(state.totalIncome),
                        valueColor = Color(0xFFD4F5E6)
                    )
                    VerticalDivider(
                        modifier = Modifier.height(40.dp),
                        color = Color.White.copy(alpha = 0.25f)
                    )
                    SummaryColumn(
                        label = "Despesa",
                        value = formatCurrency(state.totalSpent),
                        valueColor = Color(0xFFFFD6D6)
                    )
                    VerticalDivider(
                        modifier = Modifier.height(40.dp),
                        color = Color.White.copy(alpha = 0.25f)
                    )
                    SummaryColumn(
                        label = "Saldo",
                        value = formatCurrency(state.balance),
                        valueColor = if (state.balance < 0) Color(0xFFFFD6D6) else Color(0xFFD4F5E6)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Section header
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Gastos por Categoria",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                TextButton(onClick = onNavigateToBudgetConfig) {
                    Text("Configurar limite", fontSize = 13.sp, color = Primary)
                }
            }

            when {
                state.isLoading -> {
                    Box(
                        Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Primary)
                    }
                }
                state.categorySummaries.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📊", fontSize = 40.sp)
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "Nenhum gasto ainda",
                                fontSize = 15.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                "Lance sua primeira despesa",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
                else -> {
                    state.categorySummaries.forEach { summary ->
                        CategoryBudgetCard(summary)
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }

        // Bottom nav
        BottomNavBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            onAddExpense = onNavigateToAddExpense,
            onHistory = onNavigateToHistory
        )

        // FAB
        FloatingActionButton(
            onClick = onNavigateToAddExpense,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 90.dp),
            containerColor = Primary,
            contentColor = OnPrimary,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, "Lançar", modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
private fun SummaryColumn(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 11.sp, color = Color.White.copy(alpha = 0.75f), fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(4.dp))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

@Composable
fun CategoryBudgetCard(summary: CategorySummary) {
    val barColor = when {
        summary.limit == 0.0 -> TextSecondary
        summary.isOverBudget -> CategoryRed
        summary.progress > 0.75f -> CategoryYellow
        else -> summary.category.color
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Border)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(summary.category.color.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(summary.category.emoji, fontSize = 18.sp)
                    }
                    Text(
                        summary.category.displayName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        formatCurrency(summary.spent),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        if (summary.limit > 0) "de ${formatCurrency(summary.limit)}" else "sem limite",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            if (summary.limit > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "${(summary.progress * 100).toInt()}% utilizado",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        if (summary.isOverBudget) "⚠ Limite excedido"
                        else "Restam ${formatCurrency(summary.remaining)}",
                        fontSize = 12.sp,
                        color = barColor,
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Border)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(summary.progress.coerceIn(0f, 1f))
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(barColor)
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier,
    onAddExpense: () -> Unit,
    onHistory: () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Surface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NavItem("🏠", "Início", selected = true, onClick = {})
            NavItem("➕", "Lançar", selected = false, onClick = onAddExpense)
            NavItem("📊", "Histórico", selected = false, onClick = onHistory)
        }
    }
}

@Composable
fun NavItem(icon: String, label: String, selected: Boolean, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 22.sp)
            Text(
                label,
                fontSize = 10.sp,
                color = if (selected) Primary else TextSecondary,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

fun formatCurrency(value: Double): String =
    "R$ " + String.format(Locale("pt", "BR"), "%.2f", value)
