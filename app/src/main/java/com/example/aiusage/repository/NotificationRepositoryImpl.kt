package com.example.aiusage.repository

import com.example.aiusage.dao.NotificationDao
import com.example.aiusage.entity.NotificationRecord
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val notificationDao: NotificationDao
) : NotificationRepository {

    override fun getAllNotificationsFlow(): Flow<List<NotificationRecord>> {
        return notificationDao.getAllNotificationsFlow()
    }

    override fun getUnreadNotificationsCountFlow(): Flow<Int> {
        return notificationDao.getUnreadNotificationsCountFlow()
    }

    override suspend fun insertNotification(notification: NotificationRecord): Long {
        return notificationDao.insertNotification(notification)
    }

    override suspend fun markNotificationAsRead(id: Int) {
        notificationDao.markNotificationAsRead(id)
    }

    override suspend fun markAllNotificationsAsRead() {
        notificationDao.markAllNotificationsAsRead()
    }

    override suspend fun deleteNotification(notification: NotificationRecord) {
        notificationDao.deleteNotification(notification)
    }

    override suspend fun clearAllNotifications() {
        notificationDao.clearAllNotifications()
    }
}
