package com.example.diariodedespesas.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.diariodedespesas.data.local.entity.ExpenseEntity
import com.example.diariodedespesas.data.model.Category
import com.example.diariodedespesas.data.model.PaymentMethod
import com.example.diariodedespesas.data.repository.AuthRepository
import com.example.diariodedespesas.data.repository.ExpenseRepository
import com.example.diariodedespesas.ui.screens.dashboard.formatCurrency
import com.example.diariodedespesas.ui.theme.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryViewModel(private val repo: ExpenseRepository) : ViewModel() {
    private val _expenses = MutableStateFlow<List<ExpenseEntity>>(emptyList())
    val expenses: StateFlow<List<ExpenseEntity>> = _expenses.asStateFlow()

    fun load(userId: String) {
        viewModelScope.launch {
            repo.getAllExpenses(userId).collect { _expenses.value = it }
        }
    }

    companion object {
        fun factory(repo: ExpenseRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                HistoryViewModel(repo) as T
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    expenseRepo: ExpenseRepository,
    authRepo: AuthRepository,
    onBack: () -> Unit
) {
    val vm: HistoryViewModel = viewModel(factory = HistoryViewModel.factory(expenseRepo))
    val expenses by vm.expenses.collectAsState()
    val userId = authRepo.currentUser?.uid ?: ""

    LaunchedEffect(userId) { vm.load(userId) }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Histórico", fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        }
    ) { padding ->
        if (expenses.isEmpty()) {
            Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📭", fontSize = 48.sp)
                    Spacer(Modifier.height(16.dp))
                    Text("Nenhum lançamento ainda", fontSize = 16.sp, color = TextSecondary)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(expenses) { item ->
                    TransactionListItem(item)
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun TransactionListItem(item: ExpenseEntity) {
    val isIncome = item.type == "INCOME"
    val payment = PaymentMethod.fromName(item.paymentMethod)
    val dateStr = SimpleDateFormat("dd/MM/yy HH:mm", Locale("pt", "BR")).format(Date(item.date))

    val iconEmoji: String
    val iconBg: Color
    val tagLabel: String
    val tagColor: Color

    if (isIncome) {
        iconEmoji = "💰"
        iconBg = Color(0xFF38B57D).copy(alpha = 0.15f)
        tagLabel = "Receita"
        tagColor = Color(0xFF38B57D)
    } else {
        val cat = Category.fromName(item.category)
        iconEmoji = cat.emoji
        iconBg = cat.color.copy(alpha = 0.15f)
        tagLabel = cat.displayName
        tagColor = cat.color
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Border)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(iconEmoji, fontSize = 20.sp)
                }
                Column {
                    Text(
                        item.description,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(tagColor)
                        )
                        Text(dateStr, fontSize = 12.sp, color = TextSecondary)
                        Text("• ${payment.displayName}", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    (if (isIncome) "+ " else "- ") + formatCurrency(item.amount),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isIncome) Color(0xFF38B57D) else TextPrimary
                )
                Text(tagLabel, fontSize = 11.sp, color = tagColor, fontWeight = FontWeight.Medium)
            }
        }
    }
}
