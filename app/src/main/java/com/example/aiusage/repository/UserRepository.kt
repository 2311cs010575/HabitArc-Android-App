package com.example.aiusage.repository

import com.example.aiusage.entity.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserFlow(): Flow<User?>
    suspend fun getUserSync(): User?
    suspend fun insertUser(user: User): Long
    suspend fun updateUser(user: User)
    suspend fun getUserByEmail(email: String): User?
    suspend fun updateUserBudget(userId: Int, budgetLimit: Double)
    fun isLoggedIn(): Boolean
    fun setLoggedIn(loggedIn: Boolean)
}
