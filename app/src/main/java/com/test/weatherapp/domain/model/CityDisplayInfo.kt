package com.test.weatherapp.domain.model

data class CityDisplayInfo(
    val displayName: String,  // UI 表示用：例「東京」
    val queryName: String,    // API 呼び出し用：例「Tokyo」
    val latitude: Double,
    val longitude: Double,
    val country: String? = null // 任意で国情報などを保持する場合
)