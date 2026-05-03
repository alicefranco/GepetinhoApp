package com.example.gepetinho.presentation.auth

sealed class AuthState {
    data object LoggedOut : AuthState()
    data object LoggedIn : AuthState()
}
