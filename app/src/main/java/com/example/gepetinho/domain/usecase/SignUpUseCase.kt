package com.example.gepetinho.domain.usecase

import com.example.gepetinho.domain.repository.AuthRepository
import com.example.gepetinho.presentation.auth.User
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return authRepository.signUp(email = email, password = password)
    }
}
