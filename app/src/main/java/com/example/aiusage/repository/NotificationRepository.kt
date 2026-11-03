package com.example.aiusage.repository

import com.example.aiusage.entity.NotificationRecord
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getAllNotificationsFlow(): Flow<List<NotificationRecord>>
    fun getUnreadNotificationsCountFlow(): Flow<Int>
    suspend fun insertNotification(notification: NotificationRecord): Long
    suspend fun markNotificationAsRead(id: Int)
    suspend fun markAllNotificationsAsRead()
    suspend fun deleteNotification(notification: NotificationRecord)
    suspend fun clearAllNotifications()
}
