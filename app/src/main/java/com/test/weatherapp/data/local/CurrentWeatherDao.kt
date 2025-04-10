package com.test.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface CurrentWeatherDao {
    @Query("SELECT * FROM current_weather WHERE cityName = :cityName AND date = :date LIMIT 1")
    suspend fun getWeatherByCityAndDate(cityName: String, date: String): CurrentWeatherEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: CurrentWeatherEntity)

    @Query("DELETE FROM current_weather WHERE cityName = :cityName AND date != :date")
    suspend fun deleteOldWeather(cityName: String, date: String)
}