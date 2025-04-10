package com.test.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ForecastDao {
    @Query("SELECT * FROM forecast WHERE cityName = :cityName AND date = :date")
    suspend fun getForecastsByCityAndDate(cityName: String, date: String): List<ForecastEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecasts(forecastEntities: List<ForecastEntity>)

    @Query("DELETE FROM forecast WHERE cityName = :cityName AND date != :date")
    suspend fun deleteOldForecasts(cityName: String, date: String)
}