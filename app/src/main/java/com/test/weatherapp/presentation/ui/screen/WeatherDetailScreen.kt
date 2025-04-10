package com.test.weatherapp.presentation.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.test.weatherapp.domain.model.CityDisplayInfo
import com.test.weatherapp.presentation.viewmodel.WeatherViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherDetailScreen(
    cityInfo: CityDisplayInfo?,  // CityDisplayInfo を受け取る（null 許可）
    weatherViewModel: WeatherViewModel = hiltViewModel()
) {
    LaunchedEffect(cityInfo) {
        when {
            cityInfo == null -> {
                // 必要な情報が得られなかった場合の処理（例: エラーメッセージ表示）
            }

            cityInfo.queryName == "current_location" -> {
                // 現在地の場合は、ViewModel 側で現在位置取得と API 呼び出しを行う
                weatherViewModel.fetchWeatherUsingCurrentLocation()
            }

            else -> {
                // 通常の都市の場合は、都市名で天気情報取得
                weatherViewModel.getCurrentWeatherByCity(cityInfo.queryName)
                weatherViewModel.getForecastByCity(cityInfo.queryName)
            }
        }
    }

    // ViewModel の状態を StateFlow から取得
    val currentTemp by weatherViewModel.currentTemperature.collectAsState()
    val forecastList by weatherViewModel.forecastList.collectAsState()
    val forecastError by weatherViewModel.forecastError.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {

        Spacer(modifier = Modifier.height(30.dp))

        // 都市名（displayName）を横中央に大きく表示
        Text(
            text = cityInfo?.displayName ?: "情報なし",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 40.sp)
        )
        Spacer(modifier = Modifier.height(30.dp))

        when {
            currentTemp != null -> {

                Text(
                    text = "現在の気温",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
                //現在の気温表示
                Text(
                    text = currentTemp?.let { "${it.roundToInt()}°" } ?: "情報取得中",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 120.sp)
                )
            }

            else -> {
                Text(
                    text = "現在の天気情報を取得中・・・",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "週間天気予報",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))

        when {
            forecastError != null -> {
                Text(
                    text = "通信に失敗しました。",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        when {
                            cityInfo?.queryName == "current_location" -> {
                                weatherViewModel.fetchWeatherUsingCurrentLocation()
                            }

                            cityInfo != null -> {
                                weatherViewModel.getForecastByCity(cityInfo.queryName)
                            }
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "リトライ")
                }
            }

            forecastList.isEmpty() -> {
                Text(
                    text = "予報情報を取得中・・・",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            else -> {
                // 日付フォーマット: 年は省き、"M/d(EEE)" 形式、例："4/9(水)"
                val dateFormatter = DateTimeFormatter.ofPattern("M/d(EEE)", Locale.JAPAN)
                // forecastList を日付ごとにグループ化する
                val groupedForecasts = forecastList.groupBy { forecastItem ->
                    Instant.ofEpochSecond(forecastItem.dt)
                        .atZone(ZoneId.of("Asia/Tokyo"))
                        .toLocalDate()
                        .format(dateFormatter)
                }
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    groupedForecasts.forEach { (date, forecastsForDate) ->
                        // 日付表示
                        item {
                            Text(
                                text = date,
                                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp),
                                modifier = Modifier.padding(4.dp)
                            )
                        }

                        item {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(forecastsForDate) { forecastItem ->
                                    Column(
                                        modifier = Modifier
                                            .background(
                                                color = Color(0xFFA0A0A0),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(4.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        val time = Instant.ofEpochSecond(forecastItem.dt)
                                            .atZone(ZoneId.of("Asia/Tokyo"))
                                            .toLocalTime()
                                        val formattedTime =
                                            DateTimeFormatter.ofPattern("HH:mm", Locale.JAPAN)
                                                .format(time)
                                        // 時間表示
                                        Text(
                                            text = formattedTime,
                                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                                        )
                                        // 天気アイコン表示（Coil の AsyncImage を利用）
                                        AsyncImage(
                                            model = "https://openweathermap.org/img/wn/${forecastItem.weather.firstOrNull()?.icon ?: "01d"}@2x.png",
                                            contentDescription = "Weather Icon",
                                            modifier = Modifier
                                                .height(48.dp)
                                                .padding(vertical = 4.dp)
                                        )
                                        // 気温表示：四捨五入して整数値
                                        Text(
                                            text = "${forecastItem.main.temp.roundToInt()}°",
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
