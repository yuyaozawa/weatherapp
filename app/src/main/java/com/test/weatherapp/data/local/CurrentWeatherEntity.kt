package com.test.weatherapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_weather")
data class CurrentWeatherEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cityName: String,    // 都市名（例："Tokyo"）または位置情報のキーとして利用（例："lat,lon"）
    val date: String,        // キャッシュの日付、例："2025-04-09"
    val temp: Double         // 現在の気温
)