package com.test.weatherapp.domain.remote

import com.test.weatherapp.data.remote.dto.ForecastItem
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    // 通常の都市用：キャッシュを活用
    fun fetchCurrentWeatherFlowByCity(city: String): Flow<Double>
    // 通常の都市用：キャッシュを活用
    fun fetchForecastFlowByCity(city: String): Flow<List<ForecastItem>>
    // 現在地用：常に API 呼び出し（キャッシュしない）
    fun fetchCurrentWeatherFlowByLatLon(latitude: Double, longitude: Double): Flow<Double>
    // 現在地用：常に API 呼び出し（キャッシュしない）
    fun fetchForecastFlowByLatLon(latitude: Double, longitude: Double): Flow<List<ForecastItem>>
}