package com.example.diariodedespesas.ui.screens.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.diariodedespesas.data.model.Category
import com.example.diariodedespesas.data.repository.AuthRepository
import com.example.diariodedespesas.data.repository.BudgetRepository
import com.example.diariodedespesas.ui.theme.*
import com.example.diariodedespesas.ui.viewmodel.BudgetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetConfigScreen(
    budgetRepo: BudgetRepository,
    authRepo: AuthRepository,
    onBack: () -> Unit
) {
    val vm: BudgetViewModel = viewModel(factory = BudgetViewModel.factory(budgetRepo))
    val state by vm.state.collectAsState()
    val userId = authRepo.currentUser?.uid ?: ""

    LaunchedEffect(userId) { vm.load(userId) }
    LaunchedEffect(state.saved) {
        if (state.saved) { vm.resetSaved(); onBack() }
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Configurar Orçamento", fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Defina o teto mensal para cada categoria",
                fontSize = 14.sp,
                color = TextSecondary
            )

            Category.entries.forEach { cat ->
                BudgetCategoryCard(
                    category = cat,
                    value = state.limits[cat] ?: "",
                    onValueChange = { vm.onLimitChange(cat, it) }
                )
            }

            if (state.error != null) {
                Text(state.error!!, color = CategoryRed, fontSize = 13.sp)
            }

            Button(
                onClick = { vm.save(userId) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !state.isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = OnPrimary, strokeWidth = 2.dp)
                } else {
                    Text("Salvar Orçamentos", fontWeight = FontWeight.SemiBold, color = OnPrimary, fontSize = 16.sp)
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun BudgetCategoryCard(category: Category, value: String, onValueChange: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Border)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(category.color.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(category.emoji, fontSize = 20.sp)
                }
                Text(category.displayName, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            }

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text("0,00", color = TextSecondary) },
                prefix = { Text("R$ ", color = category.color, fontWeight = FontWeight.SemiBold) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = category.color,
                    unfocusedTextColor = category.color,
                    focusedContainerColor = Background,
                    unfocusedContainerColor = Background,
                    focusedBorderColor = category.color,
                    unfocusedBorderColor = category.color.copy(alpha = 0.5f)
                )
            )
        }
    }
}
