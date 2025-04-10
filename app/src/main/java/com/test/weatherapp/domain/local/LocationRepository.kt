package com.test.weatherapp.domain.local

interface LocationRepository {
    suspend fun getCurrentLocation(): Pair<Double, Double>?
}