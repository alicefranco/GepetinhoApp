package com.example.gepetinho.domain.repository

import com.example.gepetinho.presentation.auth.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    fun currentUser(): User?
    fun logout()
}
