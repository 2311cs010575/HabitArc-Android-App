package com.example.aiusage.repository

import com.example.aiusage.entity.UsageRecord
import kotlinx.coroutines.flow.Flow

interface UsageRepository {
    fun getUsageRecordsInDateRangeFlow(startDate: Long, endDate: Long): Flow<List<UsageRecord>>
    suspend fun getUsageRecordsInDateRangeSync(startDate: Long, endDate: Long): List<UsageRecord>
    fun getUsageRecordsForAppFlow(packageName: String, startDate: Long, endDate: Long): Flow<List<UsageRecord>>
    fun getUsageByDateFlow(date: Long): Flow<List<UsageRecord>>
    suspend fun getUsageByDateSync(date: Long): List<UsageRecord>
    suspend fun insertUsageRecord(record: UsageRecord): Long
    suspend fun insertUsageRecords(records: List<UsageRecord>)
    suspend fun deleteUsageForDate(date: Long)
}
