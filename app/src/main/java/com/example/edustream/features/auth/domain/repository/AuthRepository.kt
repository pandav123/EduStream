package com.example.edustream.features.auth.domain.repository

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<FirebaseUser?>
    suspend fun login(email: String, password: String): Result<FirebaseUser>
    suspend fun register(email: String, password: String, name: String): Result<FirebaseUser>
    suspend fun logout()
}
