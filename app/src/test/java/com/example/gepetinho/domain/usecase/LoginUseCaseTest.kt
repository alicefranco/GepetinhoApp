package com.example.gepetinho.domain.usecase

import com.example.gepetinho.presentation.auth.User
import com.example.gepetinho.testing.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginUseCaseTest {

    @Test
    fun `invoke logs in with email and password`() = runTest {
        val expectedUser = User(id = "1", name = "Ash")
        val repository = FakeAuthRepository(loginResult = Result.success(expectedUser))
        val useCase = LoginUseCase(repository)

        val result = useCase(
            email = "ash@example.com",
            password = "pikachu123"
        )

        assertTrue(result.isSuccess)
        assertEquals(expectedUser, result.getOrNull())
        assertEquals("ash@example.com", repository.loginEmail)
        assertEquals("pikachu123", repository.loginPassword)
    }

    @Test
    fun `invoke returns repository failure`() = runTest {
        val failure = IllegalArgumentException("Invalid email or password.")
        val repository = FakeAuthRepository(loginResult = Result.failure(failure))
        val useCase = LoginUseCase(repository)

        val result = useCase(
            email = "ash@example.com",
            password = "wrong-password"
        )

        assertTrue(result.isFailure)
        assertEquals(failure, result.exceptionOrNull())
    }
}
