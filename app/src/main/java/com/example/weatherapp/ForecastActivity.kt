package com.example.weatherapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.weatherapp.model.WeatherData
import com.example.weatherapp.network.WeatherAPIClient
import java.io.IOException

class ForecastActivity : AppCompatActivity() {

    private val weatherAPIClient = WeatherAPIClient(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forecast)

        config()

        val city = intent.getStringExtra("Input")
        if (city != null) {
            Log.d("ForecastActivity", "City: $city")
            getWeatherData(city)
        } else {
            Log.e("ForecastActivity", "City is null")
        }
    }

    private fun config() {
        val todaySection = findViewById<LinearLayout>(R.id.TodaySection)
        val nextForecastSection = findViewById<LinearLayout>(R.id.NextForecastSection)
        val nextForecastBtn = findViewById<Button>(R.id.ForecastBtn)
        val backBtn: Button = findViewById(R.id.BackBtn)

        nextForecastBtn.setOnClickListener {
            if (todaySection.visibility == View.VISIBLE) {
                backBtn.visibility = View.GONE
                todaySection.visibility = View.GONE
                nextForecastSection.visibility = View.VISIBLE
                nextForecastBtn.hint = "Today"
            } else {
                backBtn.visibility = View.VISIBLE
                nextForecastSection.visibility = View.GONE
                todaySection.visibility = View.VISIBLE
                nextForecastBtn.hint = "Next Forecast"
            }
        }

        backBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getWeatherData(city: String) {
        weatherAPIClient.getWeatherData(city,
            onSuccess = { weatherData ->
                updateUI(weatherData)
            },
            onFailure = {
                showCustomDialog()
            }
        )
    }

    private fun updateUI(weatherData: WeatherData) {
        runOnUiThread {
            val activity = this@ForecastActivity

            findViewById<TextView>(R.id.City).text = "${weatherData.cityName}, ${weatherData.country}"
            findViewById<TextView>(R.id.Temp).text = "${weatherData.tempC}°"
            Glide.with(activity).load("https:${weatherData.forecastIcon}").into(findViewById(R.id.WeatherIcon))
            findViewById<TextView>(R.id.Condition).text = weatherData.conditionText
            findViewById<TextView>(R.id.MaxTemp).text = "Max.: ${weatherData.maxTempC}°"
            findViewById<TextView>(R.id.MinTemp).text = "Min.: ${weatherData.minTempC}°"
            findViewById<TextView>(R.id.ChanceOfRain).text = "${weatherData.chanceOfRain}%"
            findViewById<TextView>(R.id.Uv).text = "${weatherData.uv} UV"
            findViewById<TextView>(R.id.Wind).text = "${weatherData.wind} kph"
            findViewById<TextView>(R.id.Date).text = weatherData.date
            findViewById<TextView>(R.id.CurrentTime).text = weatherData.currentTime
            findViewById<TextView>(R.id.CurrentTemp).text = "${weatherData.currentTemp}°"
            Glide.with(activity).load("https:${weatherData.currentIcon}").into(findViewById(R.id.WeatherIcon1))
            findViewById<TextView>(R.id.OneTime).text = weatherData.oneTime
            Glide.with(activity).load("https:${weatherData.oneIcon}").into(findViewById(R.id.WeatherIcon2))
            findViewById<TextView>(R.id.OneTemp).text = "${weatherData.oneTemp}°"
            findViewById<TextView>(R.id.TwoTime).text = weatherData.twoTime
            Glide.with(activity).load("https:${weatherData.twoIcon}").into(findViewById(R.id.WeatherIcon3))
            findViewById<TextView>(R.id.TwoTemp).text = "${weatherData.twoTemp}°"
            findViewById<TextView>(R.id.ThreeTime).text = weatherData.threeTime
            Glide.with(activity).load("https:${weatherData.threeIcon}").into(findViewById(R.id.WeatherIcon4))
            findViewById<TextView>(R.id.ThreeTemp).text = "${weatherData.threeTemp}°"

            Glide.with(activity).load("https:${weatherData.forecastIcon2}").into(findViewById(R.id.ForecastIcon3))
            Glide.with(activity).load("https:${weatherData.forecastIcon1}").into(findViewById(R.id.ForecastIcon2))
            Glide.with(activity).load("https:${weatherData.forecastIcon0}").into(findViewById(R.id.ForecastIcon1))

            findViewById<TextView>(R.id.CurrentMaxTemp).text = "${weatherData.maxTempC0}°"
            findViewById<TextView>(R.id.CurrentMinTemp).text = "${weatherData.minTempC0}°"
            findViewById<TextView>(R.id.OneMaxTemp).text = "${weatherData.maxTempC1}°"
            findViewById<TextView>(R.id.OneMinTemp).text = "${weatherData.minTempC1}°"
            findViewById<TextView>(R.id.TwoMaxTemp).text = "${weatherData.maxTempC2}°"
            findViewById<TextView>(R.id.TwoMinTemp).text = "${weatherData.minTempC2}°"
        }
    }

    private fun showCustomDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Location does not exist")
            .create()

        val dialogButton = dialogView.findViewById<Button>(R.id.DialogBtn)
        dialogButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        dialogBuilder.show()
    }
}
