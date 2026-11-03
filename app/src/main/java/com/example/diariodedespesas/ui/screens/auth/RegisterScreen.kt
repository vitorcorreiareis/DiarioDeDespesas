package com.example.diariodedespesas.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.diariodedespesas.data.repository.AuthRepository
import com.example.diariodedespesas.ui.theme.*
import com.example.diariodedespesas.ui.viewmodel.AuthState
import com.example.diariodedespesas.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    authRepo: AuthRepository,
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val vm: AuthViewModel = viewModel(factory = AuthViewModel.factory(authRepo))
    val state by vm.state.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        if (state is AuthState.Success) onRegisterSuccess()
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Text("Criar Conta", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(
                "Preencha seus dados para começar",
                fontSize = 14.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    AppTextField(value = name, onValueChange = { name = it }, label = "Nome completo", placeholder = "João da Silva")
                    AppTextField(value = email, onValueChange = { email = it }, label = "E-mail", placeholder = "seu@email.com", keyboardType = KeyboardType.Email)
                    AppTextField(value = password, onValueChange = { password = it }, label = "Senha", placeholder = "••••••••", isPassword = true)
                    AppTextField(value = confirm, onValueChange = { confirm = it }, label = "Confirmar senha", placeholder = "••••••••", isPassword = true)

                    if (state is AuthState.Error) {
                        Text((state as AuthState.Error).message, color = CategoryRed, fontSize = 13.sp)
                    }

                    Button(
                        onClick = { vm.register(name, email, password, confirm) },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = state !is AuthState.Loading,
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        if (state is AuthState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = OnPrimary, strokeWidth = 2.dp)
                        } else {
                            Text("Criar conta", fontWeight = FontWeight.SemiBold, color = OnPrimary, fontSize = 16.sp)
                        }
                    }

                    Text(
                        "Ao criar conta, você concorda com os Termos de Uso",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, fontSize = 13.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = TextSecondary, fontSize = 15.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = Surface,
                unfocusedContainerColor = Surface,
                focusedBorderColor = Primary,
                unfocusedBorderColor = Border
            )
        )
    }
}
