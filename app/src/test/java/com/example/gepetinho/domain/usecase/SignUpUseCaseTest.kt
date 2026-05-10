package com.example.gepetinho.domain.usecase

import com.example.gepetinho.presentation.auth.User
import com.example.gepetinho.testing.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SignUpUseCaseTest {

    @Test
    fun `invoke creates account using trimmed caller values`() = runTest {
        val expectedUser = User(id = "1", name = "Ash")
        val repository = FakeAuthRepository(signUpResult = Result.success(expectedUser))
        val useCase = SignUpUseCase(repository)

        val result = useCase(
            email = "ash@example.com",
            password = "pikachu123"
        )

        assertTrue(result.isSuccess)
        assertEquals(expectedUser, result.getOrNull())
        assertEquals("ash@example.com", repository.signUpEmail)
        assertEquals("pikachu123", repository.signUpPassword)
    }

    @Test
    fun `invoke returns repository failure`() = runTest {
        val failure = IllegalArgumentException("An account already exists for this email.")
        val repository = FakeAuthRepository(signUpResult = Result.failure(failure))
        val useCase = SignUpUseCase(repository)

        val result = useCase(
            email = "ash@example.com",
            password = "pikachu123"
        )

        assertTrue(result.isFailure)
        assertEquals(failure, result.exceptionOrNull())
    }
}
