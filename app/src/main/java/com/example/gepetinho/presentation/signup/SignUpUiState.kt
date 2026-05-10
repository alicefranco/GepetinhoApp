package com.example.gepetinho.presentation.signup

import com.example.gepetinho.presentation.auth.User

data class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val user: User? = null,
    val errorMessage: String? = null
) {
    val isSignUpEnabled: Boolean
        get() = email.isNotBlank() &&
            password.isNotBlank() &&
            confirmPassword.isNotBlank() &&
            !isLoading
}
