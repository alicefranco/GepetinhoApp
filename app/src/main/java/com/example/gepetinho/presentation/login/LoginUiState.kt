package com.example.gepetinho.presentation.login

import com.example.gepetinho.presentation.auth.User

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val user: User? = null,
    val errorMessage: String? = null
) {
    val isLoginEnabled: Boolean
        get() = email.isNotBlank() && password.isNotBlank() && !isLoading
}
