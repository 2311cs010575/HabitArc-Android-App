package com.example.aiusage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aiusage.repository.UserRepository
import com.example.aiusage.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination

    init {
        checkUserRegistration()
    }

    private fun checkUserRegistration() {
        viewModelScope.launch {
            val user = userRepository.getUserSync()
            if (user != null) {
                if (userRepository.isLoggedIn()) {
                    _startDestination.value = Screen.Home.route
                } else {
                    _startDestination.value = Screen.Login.route
                }
            } else {
                _startDestination.value = Screen.Register.route
            }
        }
    }
}
