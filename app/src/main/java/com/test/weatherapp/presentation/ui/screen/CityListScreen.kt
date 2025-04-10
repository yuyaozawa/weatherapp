package com.test.weatherapp.presentation.ui.screen

import android.Manifest
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.test.weatherapp.domain.model.CityDisplayInfo
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CityListScreen(
    cityList: List<CityDisplayInfo>,
    onCitySelected: (CityDisplayInfo) -> Unit
) {
    val permissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
    // 「現在地」タップ時に権限リクエストダイアログを表示するかどうかのフラグ
    val showPermissionDialog = remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        item { Spacer(modifier = Modifier.height(30.dp)) }

        items(cityList) { city ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        when (city.queryName) {
                            "current_location" -> {
                                if (permissionState.status !is PermissionStatus.Granted) {
                                    showPermissionDialog.value = true
                                } else {
                                    onCitySelected(city)
                                }
                            }
                            else -> {
                                onCitySelected(city)
                            }
                        }
                    },
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = city.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "詳細へ",
                        tint = Color.White
                    )
                }
            }
        }
    }

    // 権限がない場合、ダイアログで権限リクエストを促す
    if (showPermissionDialog.value && permissionState.status !is PermissionStatus.Granted) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(text = "位置情報権限が必要です") },
            text = { Text(text = "『現在地』を利用するためには、位置情報の権限を許可してください。") },
            confirmButton = {
                Button(
                    onClick = {
                        permissionState.launchPermissionRequest()
                        showPermissionDialog.value = false
                    }
                ) {
                    Text("権限をリクエスト")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showPermissionDialog.value = false
                    }
                ) {
                    Text("キャンセル")
                }
            }
        )
    }
}
