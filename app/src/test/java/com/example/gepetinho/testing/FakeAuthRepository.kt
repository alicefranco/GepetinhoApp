package com.example.gepetinho.testing

import com.example.gepetinho.domain.repository.AuthRepository
import com.example.gepetinho.presentation.auth.User

class FakeAuthRepository(
    private val currentUser: User? = null,
    private val loginResult: Result<User> = Result.failure(
        IllegalStateException("Login result was not configured.")
    ),
    private val signUpResult: Result<User> = Result.failure(
        IllegalStateException("Sign up result was not configured.")
    )
) : AuthRepository {

    var loginEmail: String? = null
        private set
    var loginPassword: String? = null
        private set
    var signUpEmail: String? = null
        private set
    var signUpPassword: String? = null
        private set
    var currentUserCallCount = 0
        private set
    var logoutCalled = false
        private set

    override suspend fun login(email: String, password: String): Result<User> {
        loginEmail = email
        loginPassword = password
        return loginResult
    }

    override suspend fun signUp(email: String, password: String): Result<User> {
        signUpEmail = email
        signUpPassword = password
        return signUpResult
    }

    override fun currentUser(): User? {
        currentUserCallCount++
        return currentUser
    }

    override fun logout() {
        logoutCalled = true
    }
}
