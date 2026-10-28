package com.example.diariodedespesas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.diariodedespesas.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val userId: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(private val repo: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state.asStateFlow()

    val isLoggedIn: Boolean get() = repo.currentUser != null
    val userId: String? get() = repo.currentUser?.uid

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = AuthState.Error("Preencha e-mail e senha.")
            return
        }
        viewModelScope.launch {
            _state.value = AuthState.Loading
            repo.login(email, password).fold(
                onSuccess = { user -> _state.value = AuthState.Success(user.uid) },
                onFailure = { e -> _state.value = AuthState.Error(e.message ?: "Falha no login") }
            )
        }
    }

    fun register(name: String, email: String, password: String, confirm: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _state.value = AuthState.Error("Preencha todos os campos.")
            return
        }
        if (password != confirm) {
            _state.value = AuthState.Error("As senhas não coincidem.")
            return
        }
        if (password.length < 6) {
            _state.value = AuthState.Error("Senha deve ter ao menos 6 caracteres.")
            return
        }
        viewModelScope.launch {
            _state.value = AuthState.Loading
            repo.register(email, password).fold(
                onSuccess = { user -> _state.value = AuthState.Success(user.uid) },
                onFailure = { e -> _state.value = AuthState.Error(e.message ?: "Falha no cadastro") }
            )
        }
    }

    fun logout() {
        repo.logout()
        _state.value = AuthState.Idle
    }

    fun resetState() {
        _state.value = AuthState.Idle
    }

    companion object {
        fun factory(repo: AuthRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                AuthViewModel(repo) as T
        }
    }
}
