package com.test.weatherapp.data.remote.repository

import com.test.weatherapp.BuildConfig
import com.test.weatherapp.data.remote.api.WeatherService
import com.test.weatherapp.data.remote.dto.CurrentWeatherResponse
import com.test.weatherapp.data.remote.dto.ForecastItem
import com.test.weatherapp.data.remote.dto.ForecastWeatherResponse
import com.test.weatherapp.data.local.CurrentWeatherDao
import com.test.weatherapp.data.local.CurrentWeatherEntity
import com.test.weatherapp.data.local.ForecastDao
import com.test.weatherapp.data.local.ForecastEntity
import com.test.weatherapp.domain.remote.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.time.LocalDate
import javax.inject.Inject

private inline fun <T> getFlow(crossinline block: suspend () -> T): Flow<T> =
    flow { emit(block()) }.flowOn(Dispatchers.IO)

class WeatherRepositoryImpl @Inject constructor(
    private val weatherService: WeatherService,
    private val forecastDao: ForecastDao,
    private val currentWeatherDao: CurrentWeatherDao
) : WeatherRepository {

    // キャッシュキー (都市の場合): "city-YYYY-MM-DD"
    private fun cacheKey(city: String): String = "$city-${LocalDate.now()}"

    //　都市名で現在の天気情報を取得（キャッシュ利用）
    override fun fetchCurrentWeatherFlowByCity(city: String): Flow<Double> = flow {
        val date = LocalDate.now().toString() // "YYYY-MM-DD"
        // 古いキャッシュを削除
        currentWeatherDao.deleteOldWeather(city, date)
        // キャッシュが存在するかチェック
        val cached = currentWeatherDao.getWeatherByCityAndDate(city, date)
        if (cached != null) {
            emit(cached.temp)
        } else {
            // キャッシュがない場合は API 呼び出し
            val response: CurrentWeatherResponse = weatherService.fetchCurrentWeatherByCity(
                city, BuildConfig.OWM_API_KEY, "metric", "ja"
            )
            // 取得結果を Room に保存
            currentWeatherDao.insertWeather(
                CurrentWeatherEntity(
                    cityName = city,
                    date = date,
                    temp = response.main.temp
                )
            )
            emit(response.main.temp)
        }
    }.flowOn(Dispatchers.IO)

    // 都市名で5日間の予報情報を取得（キャッシュ利用）
    override fun fetchForecastFlowByCity(city: String): Flow<List<ForecastItem>> = flow {
        val date = LocalDate.now().toString() // "YYYY-MM-DD"
        // 古いキャッシュを削除
        forecastDao.deleteOldForecasts(city, date)
        // キャッシュが存在するかチェック
        val cachedForecasts = forecastDao.getForecastsByCityAndDate(city, date)
        if (cachedForecasts.isNotEmpty()) {
            val forecastList = cachedForecasts.map { entity ->
                ForecastItem(
                    dt = entity.dt,
                    dtTxt = entity.dtTxt,
                    main = com.test.weatherapp.data.remote.dto.Main(
                        temp = entity.temp,
                        feelsLike = entity.temp, // 仮の値。実際は API の値に合わせる
                        tempMin = entity.temp,
                        tempMax = entity.temp,
                        pressure = 0,
                        humidity = 0
                    ),
                    weather = listOf(
                        com.test.weatherapp.data.remote.dto.Weather(
                            id = 0,
                            main = "",
                            description = "",
                            icon = entity.icon
                        )
                    ),
                    visibility = 0, // 適宜調整
                    wind = com.test.weatherapp.data.remote.dto.Wind(
                        speed = 0.0,
                        deg = 0
                    ),
                    clouds = com.test.weatherapp.data.remote.dto.Clouds(
                        all = 0
                    )
                )
            }
            emit(forecastList)
        } else {
            // キャッシュが無ければ、API 呼び出し
            val response: ForecastWeatherResponse = weatherService.fetchForecastWeatherByCity(
                city, BuildConfig.OWM_API_KEY, "metric", "ja"
            )
            // API 結果を ForecastEntity に変換して Room に保存
            val entities = response.list.map { forecastItem ->
                ForecastEntity(
                    cityName = city,
                    date = date,
                    dt = forecastItem.dt,
                    dtTxt = forecastItem.dtTxt,
                    temp = forecastItem.main.temp,
                    icon = forecastItem.weather.firstOrNull()?.icon ?: "01d"
                )
            }
            forecastDao.insertForecasts(entities)
            emit(response.list)
        }
    }.flowOn(Dispatchers.IO)

    // 緯度・経度で現在の天気情報を取得（常に API 呼び出し、キャッシュなし）
    override fun fetchCurrentWeatherFlowByLatLon(
        latitude: Double,
        longitude: Double
    ): Flow<Double> = getFlow {
        val response: CurrentWeatherResponse = weatherService.fetchCurrentWeatherByLatLon(
            latitude, longitude, BuildConfig.OWM_API_KEY, "metric", "ja"
        )
        response.main.temp
    }

    // 緯度・経度で5日間の予報情報を取得（常に API 呼び出し、キャッシュなし）
    override fun fetchForecastFlowByLatLon(
        latitude: Double,
        longitude: Double
    ): Flow<List<ForecastItem>> = getFlow {
        val response: ForecastWeatherResponse = weatherService.fetchForecastWeatherByLatLon(
            latitude, longitude, BuildConfig.OWM_API_KEY, "metric", "ja"
        )
        response.list
    }
}
