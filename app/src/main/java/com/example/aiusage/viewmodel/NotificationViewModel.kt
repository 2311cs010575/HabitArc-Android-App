package com.example.aiusage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aiusage.entity.NotificationRecord
import com.example.aiusage.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    val notificationsState: StateFlow<List<NotificationRecord>> = notificationRepository
        .getAllNotificationsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val unreadCountState: StateFlow<Int> = notificationRepository
        .getUnreadNotificationsCountFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    fun markAsRead(id: Int) {
        viewModelScope.launch {
            notificationRepository.markNotificationAsRead(id)
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            notificationRepository.markAllNotificationsAsRead()
        }
    }

    fun deleteNotification(notification: NotificationRecord) {
        viewModelScope.launch {
            notificationRepository.deleteNotification(notification)
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            notificationRepository.clearAllNotifications()
        }
    }
}
