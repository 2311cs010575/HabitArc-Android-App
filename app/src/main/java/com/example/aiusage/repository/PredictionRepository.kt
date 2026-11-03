package com.example.aiusage.repository

import com.example.aiusage.entity.PredictionRecord
import kotlinx.coroutines.flow.Flow

interface PredictionRepository {
    fun getLatestPredictionsFlow(): Flow<List<PredictionRecord>>
    suspend fun getLatestPredictionsSync(): List<PredictionRecord>
    suspend fun insertPrediction(prediction: PredictionRecord): Long
    suspend fun deleteAllPredictions()
}
