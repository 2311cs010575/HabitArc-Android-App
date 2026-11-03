package com.example.aiusage.repository

import com.example.aiusage.dao.PredictionDao
import com.example.aiusage.entity.PredictionRecord
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PredictionRepositoryImpl @Inject constructor(
    private val predictionDao: PredictionDao
) : PredictionRepository {

    override fun getLatestPredictionsFlow(): Flow<List<PredictionRecord>> {
        return predictionDao.getLatestPredictionsFlow()
    }

    override suspend fun getLatestPredictionsSync(): List<PredictionRecord> {
        return predictionDao.getLatestPredictionsSync()
    }

    override suspend fun insertPrediction(prediction: PredictionRecord): Long {
        return predictionDao.insertPrediction(prediction)
    }

    override suspend fun deleteAllPredictions() {
        predictionDao.deleteAllPredictions()
    }
}
