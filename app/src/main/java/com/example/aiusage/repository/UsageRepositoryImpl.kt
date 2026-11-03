package com.example.aiusage.repository

import com.example.aiusage.dao.UsageDao
import com.example.aiusage.entity.UsageRecord
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsageRepositoryImpl @Inject constructor(
    private val usageDao: UsageDao
) : UsageRepository {

    override fun getUsageRecordsInDateRangeFlow(startDate: Long, endDate: Long): Flow<List<UsageRecord>> {
        return usageDao.getUsageRecordsInDateRangeFlow(startDate, endDate)
    }

    override suspend fun getUsageRecordsInDateRangeSync(startDate: Long, endDate: Long): List<UsageRecord> {
        return usageDao.getUsageRecordsInDateRangeSync(startDate, endDate)
    }

    override fun getUsageRecordsForAppFlow(packageName: String, startDate: Long, endDate: Long): Flow<List<UsageRecord>> {
        return usageDao.getUsageRecordsForAppFlow(packageName, startDate, endDate)
    }

    override fun getUsageByDateFlow(date: Long): Flow<List<UsageRecord>> {
        return usageDao.getUsageByDateFlow(date)
    }

    override suspend fun getUsageByDateSync(date: Long): List<UsageRecord> {
        return usageDao.getUsageByDateSync(date)
    }

    override suspend fun insertUsageRecord(record: UsageRecord): Long {
        return usageDao.insertUsageRecord(record)
    }

    override suspend fun insertUsageRecords(records: List<UsageRecord>) {
        usageDao.insertUsageRecords(records)
    }

    override suspend fun deleteUsageForDate(date: Long) {
        usageDao.deleteUsageForDate(date)
    }
}
