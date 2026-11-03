package com.example.aiusage.repository

import android.content.Context
import com.example.aiusage.dao.UserDao
import com.example.aiusage.entity.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    @ApplicationContext private val context: Context
) : UserRepository {

    private val sharedPreferences = context.getSharedPreferences("ai_usage_prefs", Context.MODE_PRIVATE)

    override fun getUserFlow(): Flow<User?> {
        return userDao.getUserFlow()
    }

    override suspend fun getUserSync(): User? {
        return userDao.getUserSync()
    }

    override suspend fun insertUser(user: User): Long {
        return userDao.insertUser(user)
    }

    override suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    override suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }

    override suspend fun updateUserBudget(userId: Int, budgetLimit: Double) {
        userDao.updateUserBudget(userId, budgetLimit)
    }

    override fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("is_logged_in", false)
    }

    override fun setLoggedIn(loggedIn: Boolean) {
        sharedPreferences.edit().putBoolean("is_logged_in", loggedIn).apply()
    }
}
