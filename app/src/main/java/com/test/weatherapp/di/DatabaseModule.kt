package com.test.weatherapp.di

import android.content.Context
import androidx.room.Room
import com.test.data.local.WeatherDatabase
import com.test.weatherapp.data.local.ForecastDao
import com.test.weatherapp.data.local.CurrentWeatherDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideWeatherDatabase(
        @ApplicationContext context: Context
    ): WeatherDatabase {
        return Room.databaseBuilder(
            context,
            WeatherDatabase::class.java,
            "weather_database"
        )
            // スキーマ変更時に破壊的再作成する（必要に応じて Migration を設定してください）
            .fallbackToDestructiveMigration()
            .build()
    }

    // 5日間の天気情報キャッシュ用
    @Provides
    @Singleton
    fun provideForecastDao(database: WeatherDatabase): ForecastDao = database.forecastDao()

    // 現在の天気情報キャッシュ用
    @Provides
    @Singleton
    fun provideCurrentWeatherDao(database: WeatherDatabase): CurrentWeatherDao = database.currentWeatherDao()
}