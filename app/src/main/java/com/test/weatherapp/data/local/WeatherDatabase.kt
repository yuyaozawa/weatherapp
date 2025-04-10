package com.test.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.test.weatherapp.data.local.CurrentWeatherDao
import com.test.weatherapp.data.local.CurrentWeatherEntity
import com.test.weatherapp.data.local.ForecastDao
import com.test.weatherapp.data.local.ForecastEntity

@Database(entities = [ForecastEntity::class, CurrentWeatherEntity::class], version = 1, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun forecastDao(): ForecastDao
    abstract fun currentWeatherDao(): CurrentWeatherDao
}