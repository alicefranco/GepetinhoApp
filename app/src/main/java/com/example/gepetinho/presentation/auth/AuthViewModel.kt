package com.example.gepetinho.presentation.auth

import androidx.lifecycle.ViewModel
import com.example.gepetinho.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow(
        authRepository.currentUser()?.let { AuthState.LoggedIn } ?: AuthState.LoggedOut
    )
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun onLoginSuccess() {
        _authState.value = AuthState.LoggedIn
    }

    fun logout() {
        authRepository.logout()
        _authState.value = AuthState.LoggedOut
    }
}
