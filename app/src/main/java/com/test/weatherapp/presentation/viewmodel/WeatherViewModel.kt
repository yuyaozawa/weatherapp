package com.test.weatherapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.weatherapp.data.remote.dto.ForecastItem
import com.test.weatherapp.domain.local.LocationRepository
import com.test.weatherapp.domain.remote.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    // 現在の天気情報（温度）の状態管理用
    private val _currentTemperature = MutableStateFlow<Double?>(null)
    val currentTemperature: StateFlow<Double?> = _currentTemperature

    // 5日間の予報情報を保持する用
    private val _forecastList = MutableStateFlow<List<ForecastItem>>(emptyList())
    val forecastList: StateFlow<List<ForecastItem>> = _forecastList

    // 予報情報取得時のエラーメッセージを保持するための状態管理用
    private val _forecastError = MutableStateFlow<String?>(null)
    val forecastError: StateFlow<String?> = _forecastError

    //通常の都市の場合。キャッシュを利用し、現在の気温を取得
    fun getCurrentWeatherByCity(city: String) {
        viewModelScope.launch {
            weatherRepository.fetchCurrentWeatherFlowByCity(city)
                .catch { e ->
                    _forecastError.value = e.localizedMessage
                    _currentTemperature.value = null
                }
                .collect { temp ->
                    _forecastError.value = null
                    _currentTemperature.value = temp
                }
        }
    }

    // 通常の都市の場合。キャッシュを利用し、5日間の予報情報を取得
    fun getForecastByCity(city: String) {
        viewModelScope.launch {
            weatherRepository.fetchForecastFlowByCity(city)
                .catch { e ->
                    _forecastError.value = e.localizedMessage
                    _forecastList.value = emptyList()
                }
                .collect { forecasts ->
                    _forecastError.value = null
                    _forecastList.value = forecasts
                }
        }
    }

    //現在地の場合。常に API 呼び出しにより、現在の気温を取得
    fun getCurrentWeatherByLatLon(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            weatherRepository.fetchCurrentWeatherFlowByLatLon(latitude, longitude)
                .catch { e ->
                    _forecastError.value = e.localizedMessage
                    _currentTemperature.value = null
                }
                .collect { temp ->
                    _forecastError.value = null
                    _currentTemperature.value = temp
                }
        }
    }

    //現在地の場合。常に API 呼び出しにより、5日間の予報情報を取得
    fun getForecastByLatLon(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            weatherRepository.fetchForecastFlowByLatLon(latitude, longitude)
                .catch { e ->
                    _forecastError.value = e.localizedMessage
                    _forecastList.value = emptyList()
                }
                .collect { forecasts ->
                    _forecastError.value = null
                    _forecastList.value = forecasts
                }
        }
    }

    //位置情報取得
    fun fetchWeatherUsingCurrentLocation() {
        viewModelScope.launch {
            val location = locationRepository.getCurrentLocation()
            if (location != null) {
                val (lat, lon) = location
                getCurrentWeatherByLatLon(lat, lon)
                getForecastByLatLon(lat, lon)
            } else {
                _forecastError.value = "位置情報の取得に失敗しました。"
            }
        }
    }
}
