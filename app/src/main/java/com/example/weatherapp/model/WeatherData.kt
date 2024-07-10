package com.example.weatherapp.model

data class WeatherData(
    // Existing properties...
    val cityName: String?,
    val country: String?,
    val localtime: String?,
    val date: String?,
    val tempC: Double?,
    val wind: Double?,
    val lastUpdated: String?,
    val conditionText: String?,
    val forecastIcon: String?,
    val maxTempC: Double?,
    val minTempC: Double?,
    val chanceOfRain: Double?,
    val uv: Double?,
    val currentTime: String?,
    val currentTemp: Double?,
    val currentIcon: String?,
    val oneTime: String?,
    val oneTemp: Double?,
    val oneIcon: String?,
    val twoTime: String?,
    val twoTemp: Double?,
    val twoIcon: String?,
    val threeTime: String?,
    val threeTemp: Double?,
    val threeIcon: String?,
    val maxTempC0: Double?,
    val minTempC0: Double?,
    val maxTempC1: Double?,
    val minTempC1: Double?,
    val maxTempC2: Double?,
    val minTempC2: Double?,
    // Additional forecast icons
    val forecastIcon0: String?, // For current day
    val forecastIcon1: String?, // For next day
    val forecastIcon2: String?  // For the day after next
)
