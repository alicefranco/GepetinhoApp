package com.example.gepetinho.domain.usecase

import com.example.gepetinho.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<String> {
        return authRepository.login(email = email, password = password)
    }
}
