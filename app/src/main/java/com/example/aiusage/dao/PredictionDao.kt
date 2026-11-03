package com.example.aiusage.dao

import androidx.room.*
import com.example.aiusage.entity.PredictionRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface PredictionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrediction(prediction: PredictionRecord): Long

    @Query("SELECT * FROM predictions ORDER BY createdAt DESC")
    fun getLatestPredictionsFlow(): Flow<List<PredictionRecord>>

    @Query("SELECT * FROM predictions ORDER BY createdAt DESC")
    suspend fun getLatestPredictionsSync(): List<PredictionRecord>

    @Query("DELETE FROM predictions")
    suspend fun deleteAllPredictions(): Int
}
