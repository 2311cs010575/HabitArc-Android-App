package com.example.aiusage.dao

import androidx.room.*
import com.example.aiusage.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User): Int

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users LIMIT 1")
    fun getUserFlow(): Flow<User?>

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getUserSync(): User?

    @Query("UPDATE users SET monthlyBudgetLimit = :budgetLimit WHERE id = :userId")
    suspend fun updateUserBudget(userId: Int, budgetLimit: Double): Int
}
