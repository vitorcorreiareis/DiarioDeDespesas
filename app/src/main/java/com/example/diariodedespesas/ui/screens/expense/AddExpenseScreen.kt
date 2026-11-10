package com.example.diariodedespesas.ui.screens.expense

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.diariodedespesas.data.model.Category
import com.example.diariodedespesas.data.model.PaymentMethod
import com.example.diariodedespesas.data.repository.AuthRepository
import com.example.diariodedespesas.data.repository.ExpenseRepository
import com.example.diariodedespesas.ui.theme.*
import com.example.diariodedespesas.ui.viewmodel.AddExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    expenseRepo: ExpenseRepository,
    authRepo: AuthRepository,
    onBack: () -> Unit
) {
    val vm: AddExpenseViewModel = viewModel(factory = AddExpenseViewModel.factory(expenseRepo))
    val state by vm.state.collectAsState()
    val userId = authRepo.currentUser?.uid ?: ""

    LaunchedEffect(state.saved) {
        if (state.saved) { vm.resetSaved(); onBack() }
    }

    val incomeGreen = Color(0xFF38B57D)
    val expenseRed = Color(0xFFF24242)
    val activeColor = if (state.isIncome) incomeGreen else expenseRed

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (state.isIncome) "Lançar Receita" else "Lançar Despesa",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Toggle Despesa / Receita
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Surface)
                    .border(1.dp, Border, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TypeChip(
                    label = "💸  Despesa",
                    selected = !state.isIncome,
                    selectedColor = expenseRed,
                    modifier = Modifier.weight(1f),
                    onClick = { vm.onTypeChange(false) }
                )
                TypeChip(
                    label = "💰  Receita",
                    selected = state.isIncome,
                    selectedColor = incomeGreen,
                    modifier = Modifier.weight(1f),
                    onClick = { vm.onTypeChange(true) }
                )
            }

            // Amount card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        if (state.isIncome) "Valor recebido" else "Valor da despesa",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    OutlinedTextField(
                        value = state.amountText,
                        onValueChange = vm::onAmountChange,
                        placeholder = { Text("0,00", fontSize = 32.sp, color = TextSecondary) },
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        ),
                        prefix = {
                            Text("R$ ", fontSize = 20.sp, color = activeColor, fontWeight = FontWeight.Bold)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = Surface,
                            unfocusedContainerColor = Surface,
                            focusedBorderColor = activeColor,
                            unfocusedBorderColor = Border
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Category — só para despesas
            if (!state.isIncome) {
                Text("Categoria", fontSize = 14.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Category.entries.forEach { cat ->
                        val selected = state.selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (selected) cat.color else Color.Transparent)
                                .border(1.5.dp, if (selected) cat.color else Border, RoundedCornerShape(20.dp))
                                .clickable { vm.onCategoryChange(cat) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                "${cat.emoji} ${cat.displayName}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (selected) Color.Black else TextSecondary
                            )
                        }
                    }
                }
            }

            // Description
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "Descrição",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                OutlinedTextField(
                    value = state.description,
                    onValueChange = vm::onDescriptionChange,
                    placeholder = {
                        Text(
                            if (state.isIncome) "Ex: Salário, Freelance, Pix recebido..."
                            else "Ex: Mercado Extra, Posto Shell...",
                            color = TextSecondary,
                            fontSize = 15.sp
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = Surface,
                        unfocusedContainerColor = Surface,
                        focusedBorderColor = activeColor,
                        unfocusedBorderColor = Border
                    )
                )
            }

            // Payment method
            Text(
                "Forma de pagamento",
                fontSize = 14.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PaymentMethod.entries.forEach { method ->
                    val selected = state.selectedPayment == method
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (selected) activeColor else Color.Transparent)
                            .border(1.5.dp, if (selected) activeColor else Border, RoundedCornerShape(20.dp))
                            .clickable { vm.onPaymentChange(method) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            method.displayName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selected) Color.White else TextSecondary
                        )
                    }
                }
            }

            if (state.error != null) {
                Text(state.error!!, color = CategoryRed, fontSize = 13.sp)
            }

            Button(
                onClick = { vm.save(userId) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !state.isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = activeColor)
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(
                        if (state.isIncome) "Salvar Receita" else "Salvar Despesa",
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TypeChip(
    label: String,
    selected: Boolean,
    selectedColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(9.dp))
            .background(if (selected) selectedColor else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) Color.White else TextSecondary
        )
    }
}
