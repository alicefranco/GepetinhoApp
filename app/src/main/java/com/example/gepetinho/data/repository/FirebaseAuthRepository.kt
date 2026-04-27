package com.example.gepetinho.data.repository

import com.example.gepetinho.domain.repository.AuthRepository
import com.example.gepetinho.presentation.auth.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return runCatching {
            val authResult = firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()

            val firebaseUser = authResult.user ?: error("Firebase returned an empty user session.")
            firebaseUser.toAppUser()
        }.recoverCatching { throwable ->
            throw mapLoginError(throwable)
        }
    }

    override fun currentUser(): User? = firebaseAuth.currentUser?.toAppUser()

    override fun logout() {
        firebaseAuth.signOut()
    }

    private fun mapLoginError(throwable: Throwable): Throwable {
        return when (throwable) {
            is FirebaseAuthInvalidCredentialsException -> {
                IllegalArgumentException("Invalid email or password.")
            }

            is FirebaseAuthInvalidUserException -> {
                IllegalArgumentException("No account was found for this email.")
            }

            is FirebaseAuthException -> {
                IllegalStateException("Firebase authentication failed. Check your Firebase project setup and credentials.")
            }

            else -> {
                IllegalStateException("Unable to sign in right now. Please try again.")
            }
        }
    }

    private fun com.google.firebase.auth.FirebaseUser.toAppUser(): User {
        val displayName = displayName?.takeIf { it.isNotBlank() }
            ?: email?.substringBefore("@")?.takeIf { it.isNotBlank() }
            ?: "User"

        return User(
            id = uid,
            name = displayName
        )
    }
}
