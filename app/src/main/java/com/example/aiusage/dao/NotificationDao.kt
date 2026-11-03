package com.example.aiusage.dao

import androidx.room.*
import com.example.aiusage.entity.NotificationRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationRecord): Long

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotificationsFlow(): Flow<List<NotificationRecord>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadNotificationsCountFlow(): Flow<Int>

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: Int): Int

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsAsRead(): Int

    @Delete
    suspend fun deleteNotification(notification: NotificationRecord): Int

    @Query("DELETE FROM notifications")
    suspend fun clearAllNotifications(): Int
}
