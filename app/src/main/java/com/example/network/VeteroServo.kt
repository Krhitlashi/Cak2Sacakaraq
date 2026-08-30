package com.example.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

// ≺⧼ Vetero & Temperaturo Servo 🌦️ ⧽≻

class VeteroServo {
  private val kliento = OkHttpClient.Builder()
    .connectTimeout(5, TimeUnit.SECONDS)
    .readTimeout(5, TimeUnit.SECONDS)
    .build()

  suspend fun preniTemperaturonCelsius(lat: Double, lon: Double): Double? = withContext(Dispatchers.IO) {
    try {
      val url = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current=temperature_2m,weather_code"
      val peto = Request.Builder().url(url).build()
      kliento.newCall(peto).execute().use { respondo ->
        if (respondo.isSuccessful) {
          val korpo = respondo.body?.string() ?: return@withContext null
          val json = JSONObject(korpo)
          if (json.has("current")) {
            val current = json.getJSONObject("current")
            if (current.has("temperature_2m")) {
              return@withContext current.getDouble("temperature_2m")
            }
          }
        }
      }
    } catch (_: Exception) {
      // Reto ne disponeblas
    }
    null
  }
}
