package com.example.gepetinho.domain.usecase

import com.example.gepetinho.domain.repository.AuthRepository
import com.example.gepetinho.presentation.auth.User
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

    private class FakeAuthRepository(
        private val signUpResult: Result<User>
    ) : AuthRepository {
        var signUpEmail: String? = null
        var signUpPassword: String? = null

        override suspend fun login(email: String, password: String): Result<User> {
            error("Not used in this test.")
        }

        override suspend fun signUp(email: String, password: String): Result<User> {
            signUpEmail = email
            signUpPassword = password
            return signUpResult
        }

        override fun currentUser(): User? = null

        override fun logout() = Unit
    }
}
