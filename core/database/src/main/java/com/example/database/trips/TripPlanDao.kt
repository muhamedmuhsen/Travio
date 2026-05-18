package com.example.database.trips

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TripPlanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTripPlan(tripPlan: TripPlanEntity)

    @Query("SELECT * FROM trip_plans ORDER BY createdAt DESC")
    fun getAllTripPlans(): Flow<List<TripPlanEntity>>

    @Query("SELECT * FROM trip_plans WHERE id = :id")
    suspend fun getTripPlanById(id: String): TripPlanEntity?
}
