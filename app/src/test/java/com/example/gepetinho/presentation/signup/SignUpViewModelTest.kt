package com.example.gepetinho.presentation.signup

import com.example.gepetinho.domain.usecase.SignUpUseCase
import com.example.gepetinho.presentation.auth.User
import com.example.gepetinho.testing.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SignUpViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `signUp creates account and updates user`() = runTest {
        val user = User(id = "1", name = "Ash")
        val repository = FakeAuthRepository(signUpResult = Result.success(user))
        val viewModel = SignUpViewModel(SignUpUseCase(repository))

        viewModel.onEmailChanged(" ash@example.com ")
        viewModel.onPasswordChanged("pikachu123")
        viewModel.onConfirmPasswordChanged("pikachu123")
        viewModel.signUp()

        val uiState = viewModel.uiState.value
        assertEquals(user, uiState.user)
        assertFalse(uiState.isLoading)
        assertNull(uiState.errorMessage)
        assertEquals("ash@example.com", repository.signUpEmail)
        assertEquals("pikachu123", repository.signUpPassword)
    }

    @Test
    fun `signUp shows error when passwords do not match`() = runTest {
        val repository = FakeAuthRepository(signUpResult = Result.success(User("1", "Ash")))
        val viewModel = SignUpViewModel(SignUpUseCase(repository))

        viewModel.onEmailChanged("ash@example.com")
        viewModel.onPasswordChanged("pikachu123")
        viewModel.onConfirmPasswordChanged("bulbasaur123")
        viewModel.signUp()

        val uiState = viewModel.uiState.value
        assertEquals("Passwords do not match.", uiState.errorMessage)
        assertNull(uiState.user)
        assertNull(repository.signUpEmail)
    }

    @Test
    fun `signUp shows repository failure message`() = runTest {
        val repository = FakeAuthRepository(
            signUpResult = Result.failure(IllegalArgumentException("Password is too weak."))
        )
        val viewModel = SignUpViewModel(SignUpUseCase(repository))

        viewModel.onEmailChanged("ash@example.com")
        viewModel.onPasswordChanged("short")
        viewModel.onConfirmPasswordChanged("short")
        viewModel.signUp()

        val uiState = viewModel.uiState.value
        assertEquals("Password is too weak.", uiState.errorMessage)
        assertNull(uiState.user)
        assertFalse(uiState.isLoading)
    }

    @Test
    fun `isSignUpEnabled requires all fields and not loading`() {
        assertFalse(SignUpUiState().isSignUpEnabled)
        assertTrue(
            SignUpUiState(
                email = "ash@example.com",
                password = "pikachu123",
                confirmPassword = "pikachu123"
            ).isSignUpEnabled
        )
        assertFalse(
            SignUpUiState(
                email = "ash@example.com",
                password = "pikachu123",
                confirmPassword = "pikachu123",
                isLoading = true
            ).isSignUpEnabled
        )
    }
}
