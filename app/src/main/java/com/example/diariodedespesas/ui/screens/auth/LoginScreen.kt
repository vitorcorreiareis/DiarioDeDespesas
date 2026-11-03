package com.example.diariodedespesas.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.diariodedespesas.data.repository.AuthRepository
import com.example.diariodedespesas.ui.theme.*
import com.example.diariodedespesas.ui.viewmodel.AuthState
import com.example.diariodedespesas.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    authRepo: AuthRepository,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val vm: AuthViewModel = viewModel(factory = AuthViewModel.factory(authRepo))
    val state by vm.state.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        if (state is AuthState.Success) onLoginSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(100.dp))

        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("💰", fontSize = 36.sp)
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "Diário de Despesas",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Text(
            "Controle seus gastos com inteligência",
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Surface)
        ) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Entrar", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

                AppTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "E-mail",
                    placeholder = "seu@email.com",
                    keyboardType = KeyboardType.Email
                )

                AppTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Senha",
                    placeholder = "••••••••",
                    isPassword = true
                )

                if (state is AuthState.Error) {
                    Text(
                        (state as AuthState.Error).message,
                        color = CategoryRed,
                        fontSize = 13.sp
                    )
                }

                Button(
                    onClick = { vm.login(email, password) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = state !is AuthState.Loading,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    if (state is AuthState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = OnPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Entrar", fontWeight = FontWeight.SemiBold, color = OnPrimary, fontSize = 16.sp)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Border)
                    Text("  ou  ", color = TextSecondary, fontSize = 13.sp)
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Border)
                }

                OutlinedButton(
                    onClick = { vm.resetState(); onNavigateToRegister() },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Primary)
                ) {
                    Text("Criar conta", fontWeight = FontWeight.SemiBold, color = Primary, fontSize = 16.sp)
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}
