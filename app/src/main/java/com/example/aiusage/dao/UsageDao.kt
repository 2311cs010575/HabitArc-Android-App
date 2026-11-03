package com.example.aiusage.dao

import androidx.room.*
import com.example.aiusage.entity.UsageRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface UsageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsageRecord(record: UsageRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsageRecords(records: List<UsageRecord>): List<Long>

    @Query("SELECT * FROM app_usage WHERE date BETWEEN :startDate AND :endDate ORDER BY usageTimeMs DESC")
    fun getUsageRecordsInDateRangeFlow(startDate: Long, endDate: Long): Flow<List<UsageRecord>>

    @Query("SELECT * FROM app_usage WHERE date BETWEEN :startDate AND :endDate ORDER BY usageTimeMs DESC")
    suspend fun getUsageRecordsInDateRangeSync(startDate: Long, endDate: Long): List<UsageRecord>

    @Query("SELECT * FROM app_usage WHERE packageName = :packageName AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getUsageRecordsForAppFlow(packageName: String, startDate: Long, endDate: Long): Flow<List<UsageRecord>>

    @Query("SELECT * FROM app_usage WHERE date = :date ORDER BY usageTimeMs DESC")
    fun getUsageByDateFlow(date: Long): Flow<List<UsageRecord>>

    @Query("SELECT * FROM app_usage WHERE date = :date ORDER BY usageTimeMs DESC")
    suspend fun getUsageByDateSync(date: Long): List<UsageRecord>

    @Query("DELETE FROM app_usage WHERE date = :date")
    suspend fun deleteUsageForDate(date: Long): Int
}
