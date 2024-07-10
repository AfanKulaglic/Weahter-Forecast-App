package com.example.weatherapp.network

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import com.example.weatherapp.ForecastActivity
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.model.WeatherData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.IOException

class WeatherAPIClient(private val context: Context) {

    private val client = OkHttpClient()

    fun getWeatherData(city: String, onSuccess: (WeatherData) -> Unit, onFailure: () -> Unit) {
        val request = Request.Builder()
            .url("https://weatherapi-com.p.rapidapi.com/forecast.json?q=$city&days=3")
            .get()
            .addHeader("x-rapidapi-key", "65f8e28fc9msh740f5c710731f09p18f138jsn6eeb3143879e")
            .addHeader("x-rapidapi-host", "weatherapi-com.p.rapidapi.com")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("WeatherAPIClient", "Network request failed", e)
                e.printStackTrace()
                onFailure.invoke()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        Log.e("ForecastActivity", "Unexpected code $it")
                        (context as? ForecastActivity)?.runOnUiThread {
                            showCustomDialog()
                        }
                        throw IOException("Unexpected code $it")
                    }

                    val responseBody: ResponseBody? = it.body
                    if (responseBody != null) {
                        val responseString = responseBody.string()
                        Log.d("WeatherAPIClient", "Response: $responseString")

                        val weatherData = parseResponse(responseString)
                        onSuccess.invoke(weatherData)
                    } else {
                        Log.e("WeatherAPIClient", "Response body is null")
                        onFailure.invoke()
                    }
                }
            }
        })
    }

    private fun parseResponse(responseString: String): WeatherData {
        val gson = Gson()
        val mapType = object : TypeToken<Map<String, Any>>() {}.type
        val responseMap: Map<String, Any> = gson.fromJson(responseString, mapType)

        val location = responseMap["location"] as? Map<*, *>
        val cityName = location?.get("name") as? String
        val country = location?.get("country") as? String
        val localtime = location?.get("localtime") as? String
        val date = localtime?.substring(0, 10)

        val current = responseMap["current"] as? Map<*, *>
        val tempC = current?.get("temp_c") as? Double
        val wind = current?.get("wind_kph") as? Double
        val last_updated = current?.get("last_updated") as? String
        val desiredSubstring = last_updated?.takeLast(5)?.dropLast(3)

        val condition = current?.get("condition") as? Map<*, *>
        val forecastIcon = condition?.get("icon") as? String
        val conditionText = condition?.get("text") as? String

        val forecast = responseMap["forecast"] as? Map<*, *>
        val forecastDay = (forecast?.get("forecastday") as? List<*>)?.get(0) as? Map<*, *>
        val day = forecastDay?.get("day") as? Map<*, *>
        val maxTempC = day?.get("maxtemp_c") as? Double
        val minTempC = day?.get("mintemp_c") as? Double
        val chanceOfRain = day?.get("daily_chance_of_rain") as? Double
        val uv = day?.get("uv") as? Double

        var retryCount = 0

        val forecastCurrentHour = desiredSubstring?.let { it1 ->
            var index = it1.toIntOrNull() ?: 0
            val hourList = forecastDay?.get("hour") as? List<*>
            if (hourList != null) {
                while (retryCount < hourList.size) {
                    val currentIndex = (index + 1 + retryCount) % hourList.size
                    if (currentIndex < hourList.size) {
                        break
                    }
                    retryCount++
                }
                hourList.getOrNull((index + 1 + retryCount) % hourList.size)
            } else {
                null
            }
        } as? Map<*, *>

        val currentDate = forecastCurrentHour?.get("time") as? String
        val currentTemp = forecastCurrentHour?.get("temp_c") as? Double
        val currentCondition = forecastCurrentHour?.get("condition") as? Map<*, *>
        val currentIcon = currentCondition?.get("icon") as? String
        val currentTime = currentDate?.let { if (it.length >= 5) it.substring(it.length - 5) else it }

        retryCount = 0

        val forecastOneHour = desiredSubstring?.let { it1 ->
            var index = it1.toIntOrNull() ?: 0
            val hourList = forecastDay?.get("hour") as? List<*>
            if (hourList != null) {
                while (retryCount < hourList.size) {
                    val currentIndex = (index + 2 + retryCount) % hourList.size
                    if (currentIndex < hourList.size) {
                        break
                    }
                    retryCount++
                }
                hourList.getOrNull((index + 2 + retryCount) % hourList.size)
            } else {
                null
            }
        } as? Map<*, *>

        val oneDate = forecastOneHour?.get("time") as? String
        val oneTemp = forecastOneHour?.get("temp_c") as? Double
        val oneCondition = forecastOneHour?.get("condition") as? Map<*, *>
        val oneIcon = oneCondition?.get("icon") as? String
        val oneTime = oneDate?.let { if (it.length >= 5) it.substring(it.length - 5) else it }

        retryCount = 0

        val forecastTwoHour = desiredSubstring?.let { it1 ->
            var index = it1.toIntOrNull() ?: 0
            val hourList = forecastDay?.get("hour") as? List<*>
            if (hourList != null) {
                while (retryCount < hourList.size) {
                    val currentIndex = (index + 3 + retryCount) % hourList.size
                    if (currentIndex < hourList.size) {
                        break
                    }
                    retryCount++
                }
                hourList.getOrNull((index + 3 + retryCount) % hourList.size)
            } else {
                null
            }
        } as? Map<*, *>

        val twoDate = forecastTwoHour?.get("time") as? String
        val twoTemp = forecastTwoHour?.get("temp_c") as? Double
        val twoCondition = forecastTwoHour?.get("condition") as? Map<*, *>
        val twoIcon = twoCondition?.get("icon") as? String
        val twoTime = twoDate?.let { if (it.length >= 5) it.substring(it.length - 5) else it }

        retryCount = 0

        val forecastThreeHour = desiredSubstring?.let { it1 ->
            var index = it1.toIntOrNull() ?: 0
            val hourList = forecastDay?.get("hour") as? List<*>
            if (hourList != null) {
                while (retryCount < hourList.size) {
                    val currentIndex = (index + 4 + retryCount) % hourList.size
                    if (currentIndex < hourList.size) {
                        break
                    }
                    retryCount++
                }
                hourList.getOrNull((index + 4 + retryCount) % hourList.size)
            } else {
                null
            }
        } as? Map<*, *>

        val threeDate = forecastThreeHour?.get("time") as? String
        val threeTemp = forecastThreeHour?.get("temp_c") as? Double
        val threeCondition = forecastThreeHour?.get("condition") as? Map<*, *>
        val threeIcon = threeCondition?.get("icon") as? String
        val threeTime = threeDate?.let { if (it.length >= 5) it.substring(it.length - 5) else it }

        val forecastDay0 = (forecast?.get("forecastday") as? List<*>)?.get(0) as? Map<*, *>
        val day0 = forecastDay0?.get("day") as? Map<*, *>
        val condition0 = day0?.get("condition") as? Map<*, *>
        val forecastIcon0 = condition0?.get("icon") as? String
        val maxTempC0 = day0?.get("maxtemp_c") as? Double
        val minTempC0 = day0?.get("mintemp_c") as? Double

        val forecastDay2 = (forecast?.get("forecastday") as? List<*>)?.get(2) as? Map<*, *>
        val day2 = forecastDay2?.get("day") as? Map<*, *>
        val condition2 = day2?.get("condition") as? Map<*, *>
        val forecastIcon2 = condition2?.get("icon") as? String
        val maxTempC2 = day2?.get("maxtemp_c") as? Double
        val minTempC2 = day2?.get("mintemp_c") as? Double

        val forecastDay1 = (forecast?.get("forecastday") as? List<*>)?.get(1) as? Map<*, *>
        val day1 = forecastDay1?.get("day") as? Map<*, *>
        val condition1 = day1?.get("condition") as? Map<*, *>
        val forecastIcon1 = condition1?.get("icon") as? String
        val maxTempC1 = day1?.get("maxtemp_c") as? Double
        val minTempC1 = day1?.get("mintemp_c") as? Double

        return WeatherData(
            cityName, country, localtime, date, tempC, wind, last_updated, conditionText, forecastIcon,
            maxTempC, minTempC, chanceOfRain, uv, currentTime, currentTemp, currentIcon,
            oneTime, oneTemp, oneIcon, twoTime, twoTemp, twoIcon, threeTime, threeTemp, threeIcon,
            maxTempC0, minTempC0, maxTempC1, minTempC1, maxTempC2, minTempC2, forecastIcon0, forecastIcon1, forecastIcon2
        )
    }

    private fun showCustomDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialog, null)
        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setTitle("Location does not exist")
            .create()

        // Set button click listener
        val dialogButton = dialogView.findViewById<Button>(R.id.DialogBtn)
        dialogButton.setOnClickListener {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }

        dialogBuilder.show()
    }
}
