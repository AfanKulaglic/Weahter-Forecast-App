package com.example.weatherapp

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val checkButton = findViewById<Button>(R.id.checkBtn)
        val checkLocationEditText = findViewById<EditText>(R.id.checkLocation)

        checkButton.setOnClickListener {
            getLocation()
        }

        // Postavljanje EditorActionListener za EditText
        checkLocationEditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Ako korisnik pritisne Enter ili završi unos, izvrši akciju
                getLocation()
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun getLocation() {
        val input = findViewById<EditText>(R.id.checkLocation).text.toString().trim()
        if (input.isNotEmpty()) {
            redirectToForecast(input)
        }
    }

    private fun redirectToForecast(input: String) {
        val intent = Intent(this, ForecastActivity::class.java)
        intent.putExtra("Input", input)
        startActivity(intent)
    }
}
