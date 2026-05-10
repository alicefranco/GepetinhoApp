package com.example.gepetinho.presentation.login

import com.example.gepetinho.domain.usecase.LoginUseCase
import com.example.gepetinho.presentation.auth.User
import com.example.gepetinho.presentation.signup.MainDispatcherRule
import com.example.gepetinho.testing.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `login signs in and updates user`() = runTest {
        val user = User(id = "1", name = "Ash")
        val repository = FakeAuthRepository(loginResult = Result.success(user))
        val viewModel = LoginViewModel(LoginUseCase(repository))

        viewModel.onEmailChanged(" ash@example.com ")
        viewModel.onPasswordChanged("pikachu123")
        viewModel.login()

        val uiState = viewModel.uiState.value
        assertEquals(user, uiState.user)
        assertFalse(uiState.isLoading)
        assertNull(uiState.errorMessage)
        assertEquals("ash@example.com", repository.loginEmail)
        assertEquals("pikachu123", repository.loginPassword)
    }

    @Test
    fun `login shows failure message`() = runTest {
        val repository = FakeAuthRepository(
            loginResult = Result.failure(IllegalArgumentException("Invalid email or password."))
        )
        val viewModel = LoginViewModel(LoginUseCase(repository))

        viewModel.onEmailChanged("ash@example.com")
        viewModel.onPasswordChanged("wrong-password")
        viewModel.login()

        val uiState = viewModel.uiState.value
        assertEquals("Invalid email or password.", uiState.errorMessage)
        assertNull(uiState.user)
        assertFalse(uiState.isLoading)
    }

    @Test
    fun `login does nothing when form is incomplete`() = runTest {
        val repository = FakeAuthRepository(loginResult = Result.success(User("1", "Ash")))
        val viewModel = LoginViewModel(LoginUseCase(repository))

        viewModel.onEmailChanged("ash@example.com")
        viewModel.login()

        assertNull(repository.loginEmail)
        assertNull(viewModel.uiState.value.user)
    }

    @Test
    fun `isLoginEnabled requires email password and not loading`() {
        assertFalse(LoginUiState().isLoginEnabled)
        assertTrue(
            LoginUiState(
                email = "ash@example.com",
                password = "pikachu123"
            ).isLoginEnabled
        )
        assertFalse(
            LoginUiState(
                email = "ash@example.com",
                password = "pikachu123",
                isLoading = true
            ).isLoginEnabled
        )
    }
}
