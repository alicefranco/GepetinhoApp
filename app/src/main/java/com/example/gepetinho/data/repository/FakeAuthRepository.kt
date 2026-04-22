package com.example.gepetinho.data.repository

import com.example.gepetinho.domain.repository.AuthRepository
import com.example.gepetinho.presentation.auth.User
import kotlinx.coroutines.delay
import javax.inject.Inject

class FakeAuthRepository @Inject constructor() : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        delay(1500)

        return when {
            email.isBlank() || password.isBlank() -> {
                Result.failure(IllegalArgumentException("Email and password are required."))
            }

            !email.contains("@") -> {
                Result.failure(IllegalArgumentException("Please enter a valid email address."))
            }

            email == VALID_EMAIL && password == VALID_PASSWORD -> {
                Result.success(User("1", "Demo User"))
            }

            else -> {
                Result.failure(IllegalArgumentException("Invalid email or password."))
            }
        }
    }

    private companion object {
        const val VALID_EMAIL = "demo@example.com"
        const val VALID_PASSWORD = "password123"
    }
}
