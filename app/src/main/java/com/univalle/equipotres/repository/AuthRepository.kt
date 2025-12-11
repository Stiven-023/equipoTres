package com.univalle.equipotres.repository

import com.google.firebase.auth.FirebaseAuth
import com.univalle.equipotres.model.AuthResult
import com.univalle.equipotres.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    suspend fun login(email: String, password: String): AuthResult {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            if (firebaseUser != null) {
                AuthResult.Success(
                    User(
                        uid = firebaseUser.uid,
                        email = firebaseUser.email ?: ""
                    )
                )
            } else {
                AuthResult.Error("Login incorrecto")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Login incorrecto")
        }
    }

    suspend fun register(email: String, password: String): AuthResult {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            if (firebaseUser != null) {
                AuthResult.Success(
                    User(
                        uid = firebaseUser.uid,
                        email = firebaseUser.email ?: ""
                    )
                )
            } else {
                AuthResult.Error("Error en el registro")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error en el registro")
        }
    }

    fun getCurrentUser(): User? {
        val firebaseUser = firebaseAuth.currentUser
        return if (firebaseUser != null) {
            User(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: ""
            )
        } else {
            null
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
}