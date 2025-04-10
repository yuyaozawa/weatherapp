package com.test.weatherapp.presentation.ui.activity

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.test.weatherapp.domain.model.CityDisplayInfo
import com.test.weatherapp.presentation.ui.screen.CityListScreen
import com.test.weatherapp.presentation.ui.screen.WeatherDetailScreen
import com.test.weatherapp.ui.theme.WeatherappTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherActivity : ComponentActivity() {

    // 都市情報リスト（CityDisplayInfo）。「現在地」は queryName="current_location" として扱う
    private val cityList = listOf(
        CityDisplayInfo(displayName = "現在地", queryName = "current_location", latitude = 0.0, longitude = 0.0),
        CityDisplayInfo(displayName = "東京", queryName = "Tokyo", latitude = 35.6895, longitude = 139.6917),
        CityDisplayInfo(displayName = "兵庫", queryName = "Hyogo", latitude = 34.6913, longitude = 135.1830),
        CityDisplayInfo(displayName = "大分", queryName = "Oita", latitude = 33.2382, longitude = 131.6126),
        CityDisplayInfo(displayName = "北海道", queryName = "Hokkaido", latitude = 43.0642, longitude = 141.3469)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestLocationPermissions()
        setContent {
            WeatherappTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WeatherNavHost(
                        cityList = cityList,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    // 位置情報のランタイムパーミッションをリクエストするヘルパーメソッド
    private fun requestLocationPermissions() {
        val permissions = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
        androidx.core.app.ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}

@Composable
fun WeatherNavHost(cityList: List<CityDisplayInfo>, modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "cityList", modifier = modifier) {
        // ホーム画面：都市一覧を表示
        composable("cityList") {
            CityListScreen(
                cityList = cityList,
                onCitySelected = { city ->
                    navController.navigate("weatherDetail/${city.queryName}")
                }
            )
        }
        // 天気詳細画面：渡された queryName をもとに、cityList から該当の CityDisplayInfo を検索して渡す
        composable(
            route = "weatherDetail/{queryName}",
            arguments = listOf(navArgument("queryName") { type = NavType.StringType })
        ) { backStackEntry ->
            val queryName = backStackEntry.arguments?.getString("queryName") ?: return@composable
            // cityList 内から queryName に一致する CityDisplayInfo を探す
            val selectedCity = cityList.firstOrNull { it.queryName == queryName }
            if (selectedCity != null) {
                WeatherDetailScreen(cityInfo = selectedCity)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CityListScreenPreview() {
    WeatherappTheme {
        CityListScreen(
            cityList = listOf(
                CityDisplayInfo(displayName = "東京", queryName = "Tokyo", latitude = 35.6895, longitude = 139.6917),
                CityDisplayInfo(displayName = "兵庫", queryName = "Hyogo", latitude = 34.6913, longitude = 135.1830),
                CityDisplayInfo(displayName = "大分", queryName = "Oita", latitude = 33.2382, longitude = 131.6126),
                CityDisplayInfo(displayName = "北海道", queryName = "Hokkaido", latitude = 43.0642, longitude = 141.3469)
            ),
            onCitySelected = {  }
        )
    }
}
