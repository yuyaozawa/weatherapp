package com.test.weatherapp.data.remote.api

import com.test.weatherapp.data.remote.dto.CurrentWeatherResponse
import com.test.weatherapp.data.remote.dto.ForecastWeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    // 都市名で現在の天気情報を取得する
    @GET("data/2.5/weather/")
    suspend fun fetchCurrentWeatherByCity(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "ja"
    ): CurrentWeatherResponse

    // 緯度・経度で現在の天気情報を取得する
    @GET("data/2.5/weather/")
    suspend fun fetchCurrentWeatherByLatLon(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "ja"
    ): CurrentWeatherResponse

    // 都市名で5日間の予報情報を取得する
    @GET("data/2.5/forecast/")
    suspend fun fetchForecastWeatherByCity(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "ja"
    ): ForecastWeatherResponse

    // 緯度・経度で5日間の予報情報を取得する
    @GET("data/2.5/forecast/")
    suspend fun fetchForecastWeatherByLatLon(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "ja"
    ): ForecastWeatherResponse
}
