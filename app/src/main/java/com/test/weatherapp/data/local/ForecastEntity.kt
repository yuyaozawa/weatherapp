package com.test.weatherapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "forecast")
data class ForecastEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cityName: String, // 都市名または "lat,lon" の形式
    val date: String,     // キャッシュの日付。例："2025-04-09"
    val dt: Long,
    val dtTxt: String,
    val temp: Double,
    val icon: String
)
