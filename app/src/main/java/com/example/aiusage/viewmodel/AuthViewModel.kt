package com.example.aiusage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aiusage.entity.User
import com.example.aiusage.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun clearError() {
        _errorMessage.value = null
    }

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Email and Password cannot be empty."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val user = userRepository.getUserByEmail(email.trim())
                if (user != null && user.passwordHash == password) { // simple offline verification
                    userRepository.setLoggedIn(true)
                    onSuccess()
                } else {
                    _errorMessage.value = "Invalid email or password."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Login failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String,
        monthlyBudget: Double,
        onSuccess: () -> Unit
    ) {
        if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
            _errorMessage.value = "All fields are required."
            return
        }
        if (password != confirmPassword) {
            _errorMessage.value = "Passwords do not match."
            return
        }
        if (monthlyBudget <= 0) {
            _errorMessage.value = "Please enter a valid monthly budget limit."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val existingUser = userRepository.getUserByEmail(email.trim())
                if (existingUser != null) {
                    _errorMessage.value = "A user with this email already exists."
                    return@launch
                }

                val newUser = User(
                    username = fullName.trim(),
                    email = email.trim().lowercase(),
                    passwordHash = password, // offline plaintext password for simple demonstration
                    monthlyBudgetLimit = monthlyBudget
                )
                
                val resultId = userRepository.insertUser(newUser)
                if (resultId > 0) {
                    onSuccess()
                } else {
                    _errorMessage.value = "Registration failed. Try again."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Registration failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetPassword(email: String, onSuccess: () -> Unit) {
        if (email.isBlank()) {
            _errorMessage.value = "Please enter your email."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val user = userRepository.getUserByEmail(email.trim())
                if (user != null) {
                    // Simulating successful password reset email/trigger locally
                    onSuccess()
                } else {
                    _errorMessage.value = "No user found with this email."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Password reset failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
