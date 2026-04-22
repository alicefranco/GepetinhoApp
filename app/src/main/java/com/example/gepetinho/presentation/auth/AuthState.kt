package com.example.gepetinho.presentation.auth

sealed class AuthState {
    data object LoggedOut : AuthState()
    data class LoggedIn(val user: User) : AuthState()
}
