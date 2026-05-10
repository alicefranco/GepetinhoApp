package com.example.gepetinho.presentation.auth

import com.example.gepetinho.testing.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthViewModelTest {

    @Test
    fun `initial state is logged in when repository has current user`() = runTest {
        val repository = FakeAuthRepository(currentUser = User(id = "1", name = "Ash"))
        val viewModel = AuthViewModel(repository)

        assertEquals(AuthState.LoggedIn, viewModel.authState.value)
        assertEquals(1, repository.currentUserCallCount)
    }

    @Test
    fun `initial state is logged out when repository has no current user`() = runTest {
        val viewModel = AuthViewModel(FakeAuthRepository(currentUser = null))

        assertEquals(AuthState.LoggedOut, viewModel.authState.value)
    }

    @Test
    fun `onLoginSuccess updates state to logged in`() = runTest {
        val viewModel = AuthViewModel(FakeAuthRepository(currentUser = null))

        viewModel.onLoginSuccess()

        assertEquals(AuthState.LoggedIn, viewModel.authState.value)
    }

    @Test
    fun `logout signs out and updates state to logged out`() = runTest {
        val repository = FakeAuthRepository(currentUser = User(id = "1", name = "Ash"))
        val viewModel = AuthViewModel(repository)

        viewModel.logout()

        assertTrue(repository.logoutCalled)
        assertEquals(AuthState.LoggedOut, viewModel.authState.value)
    }

    @Test
    fun `logout signs out and remains logged out when no user is logged in`() = runTest {
        val repository = FakeAuthRepository(currentUser = null)
        val viewModel = AuthViewModel(repository)

        viewModel.logout()

        assertTrue(repository.logoutCalled)
        assertEquals(AuthState.LoggedOut, viewModel.authState.value)
    }
}
