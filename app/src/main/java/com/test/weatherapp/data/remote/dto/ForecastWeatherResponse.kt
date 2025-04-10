package com.test.weatherapp.data.remote.dto

import com.squareup.moshi.Json

data class ForecastWeatherResponse(
    val cod: String,
    val message: Int,
    @Json(name = "cnt")
    val count: Int,
    val list: List<ForecastItem>,
    val city: City
)

data class ForecastItem(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    @Json(name = "dt_txt")
    val dtTxt: String
)

data class City(
    val id: Int,
    val name: String,
    val coord: Coord,
    val country: String,
    val population: Int?,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
)

