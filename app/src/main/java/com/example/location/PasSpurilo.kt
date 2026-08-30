package com.example.location

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.ktash.P0
import com.example.ktash.QE_L6VEM2
import com.example.ktash.cax2lStafl2
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

// ≺⧼ Preciza Person-Distanca & Paŝa Spurilo Bazita sur Peu 🏃 ⧽≻

class PasSpurilo(
  private val kunteksto: Context
) : SensorEventListener {

  private val sensoraManagero = try {
    kunteksto.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
  } catch (_: Throwable) {
    null
  }

  private val agordojPref = kunteksto.getSharedPreferences("ktash_pasoj_agordoj", Context.MODE_PRIVATE)

  // Baza norma paŝlongo en peu ( 0o60.0 peu = 48.0 peu ≈ 0.7827 m )
  val bazaNormaPasoPeu = 48.0

  private val _personDistancoPeu = MutableStateFlow(
    try {
      val rawBits = agordojPref.getLong("tuta_peu_bits", 0L)
      if (rawBits == 0L) 0.0 else Double.fromBits(rawBits)
    } catch (_: Throwable) { 0.0 }
  )
  val personDistancoPeu: StateFlow<Double> = _personDistancoPeu.asStateFlow()

  private val _hodiauaDistancoPeu = MutableStateFlow(
    try {
      val rawBits = agordojPref.getLong("hodiaua_peu_bits", 0L)
      if (rawBits == 0L) 0.0 else Double.fromBits(rawBits)
    } catch (_: Throwable) { 0.0 }
  )
  val hodiauaDistancoPeu: StateFlow<Double> = _hodiauaDistancoPeu.asStateFlow()

  private val _tutaPasoj = MutableStateFlow(agordojPref.getLong("tuta_pasoj_nombro", (_personDistancoPeu.value / bazaNormaPasoPeu).toLong()))
  val tutaPasoj: StateFlow<Long> = _tutaPasoj.asStateFlow()

  private val _hodiauajPasoj = MutableStateFlow(agordojPref.getLong("hodiauaj_pasoj_nombro", (_hodiauaDistancoPeu.value / bazaNormaPasoPeu).toLong()))
  val hodiauajPasoj: StateFlow<Long> = _hodiauajPasoj.asStateFlow()

  private val _pasRapido = MutableStateFlow(0.0) // paŝoj po qe
  val pasRapido: StateFlow<Double> = _pasRapido.asStateFlow()

  private val _estasMoviĝanta = MutableStateFlow(false)
  val estasMoviĝanta: StateFlow<Boolean> = _estasMoviĝanta.asStateFlow()

  private var komencaHardwarePaso: Float = -1f
  private var lastaHardwarePasoTempoMs: Long = 0L
  private var lastaPasoTempoMs: Long = 0L
  private var lastaKonservitaTagoIdentigilo: String = agordojPref.getString("lasta_tago_id", "") ?: ""

  // Akcelerometra analizo por dinamika paŝlongo kaj rezerva detekto
  private var minAkceloOndo = SensorManager.GRAVITY_EARTH
  private var maxAkceloOndo = SensorManager.GRAVITY_EARTH
  private var lastaAkceloMagnitudo = SensorManager.GRAVITY_EARTH
  private var lastaAkceloTempoMs = 0L
  private var lastaAkceloPasoMs = 0L
  private var akceloTrairisMezon = false

  // Spuraj mezuroj por preventi duoblan akumuladon inter paŝoj kaj GPS
  private var akumulitaPasaDistancoEkdeLastaGpsPeu = 0.0

  private val skopo = CoroutineScope(Dispatchers.Default + SupervisorJob())

  init {
    kontroliTagon()
    komenciSensorojn()

    // Perioda ĝisdatigo de moviĝstato kaj paŝrapido
    skopo.launch {
      while (isActive) {
        val nun = System.currentTimeMillis()
        if (nun - lastaPasoTempoMs > 3200L) {
          _estasMoviĝanta.value = false
          _pasRapido.value = 0.0
        }
        delay(1000L)
      }
    }
  }

  // ⟪ Registri Sensorojn por Vivanta Paŝ-Spurado ⟫
  fun komenciSensorojn() {
    val mgr = sensoraManagero ?: return

    val pasoKalkulilo = mgr.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    if (pasoKalkulilo != null) {
      mgr.registerListener(this, pasoKalkulilo, SensorManager.SENSOR_DELAY_UI)
    }

    val pasoDetektilo = mgr.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    if (pasoDetektilo != null) {
      mgr.registerListener(this, pasoDetektilo, SensorManager.SENSOR_DELAY_UI)
    }

    val akcelo = mgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    if (akcelo != null) {
      mgr.registerListener(this, akcelo, SensorManager.SENSOR_DELAY_GAME)
    }
  }

  fun haltiSensorojn() {
    sensoraManagero?.unregisterListener(this)
  }

  override fun onSensorChanged(event: SensorEvent?) {
    if (event == null) return
    val nunMs = System.currentTimeMillis()
    kontroliTagon()

    when (event.sensor.type) {
      Sensor.TYPE_STEP_DETECTOR -> {
        if (event.values.isNotEmpty() && event.values[0] > 0f) {
          lastaHardwarePasoTempoMs = nunMs
          registriUnuopanPason(nunMs, kalkuliDinamikanPasonPeu(nunMs))
        }
      }
      Sensor.TYPE_STEP_COUNTER -> {
        val nunaHardware = event.values[0]
        if (komencaHardwarePaso < 0f) {
          komencaHardwarePaso = nunaHardware
        } else {
          val diferenco = (nunaHardware - komencaHardwarePaso).toLong()
          if (diferenco > 0L) {
            komencaHardwarePaso = nunaHardware
            lastaHardwarePasoTempoMs = nunMs
            val kalkulitaPaso = kalkuliDinamikanPasonPeu(nunMs)
            for (i in 0 until diferenco.coerceAtMost(64L)) {
              registriUnuopanPason(nunMs, kalkulitaPaso)
            }
          }
        }
      }
      Sensor.TYPE_ACCELEROMETER -> {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        val magnitudo = sqrt((x * x + y * y + z * z).toDouble()).toFloat()

        if (magnitudo < minAkceloOndo) minAkceloOndo = magnitudo
        if (magnitudo > maxAkceloOndo) maxAkceloOndo = magnitudo

        // Se hardware step detektilo ne ĵus pafis dum la lastaj 400ms, uzi akcelerometron
        val hardwareNeJusPafis = (nunMs - lastaHardwarePasoTempoMs) > 400L

        if (hardwareNeJusPafis) {
          val gravito = SensorManager.GRAVITY_EARTH
          val superMezo = magnitudo > gravito + 0.9f
          val subMezo = magnitudo < gravito - 0.7f

          if (subMezo) {
            akceloTrairisMezon = true
          } else if (superMezo && akceloTrairisMezon) {
            val deltaTempo = nunMs - lastaAkceloPasoMs
            if (deltaTempo in 220L..2500L) {
              lastaAkceloPasoMs = nunMs
              akceloTrairisMezon = false

              val amplitudOndo = (maxAkceloOndo - minAkceloOndo).toDouble().coerceIn(1.0, 16.0)
              val dinamikaPeu = kalkuliDinamikanPasonPeuKunAmplitudo(deltaTempo, amplitudOndo)
              registriUnuopanPason(nunMs, dinamikaPeu)

              // Restarigi ondajn ekstremojn
              minAkceloOndo = gravito
              maxAkceloOndo = gravito
            }
          }
        }

        lastaAkceloMagnitudo = magnitudo
        lastaAkceloTempoMs = nunMs
      }
    }
  }

  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

  // ⟪ Dinamika Kalkulo de Paŝlongo en Peu ⟫
  private fun kalkuliDinamikanPasonPeu(nunMs: Long): Double {
    val deltaTempoMs = if (lastaPasoTempoMs > 0L) (nunMs - lastaPasoTempoMs).coerceIn(240L, 2500L) else 600L
    val amplitudOndo = (maxAkceloOndo - minAkceloOndo).toDouble().coerceIn(1.5, 14.0)
    // Restarigi ekstremojn por sekva paŝo
    minAkceloOndo = SensorManager.GRAVITY_EARTH
    maxAkceloOndo = SensorManager.GRAVITY_EARTH
    return kalkuliDinamikanPasonPeuKunAmplitudo(deltaTempoMs, amplitudOndo)
  }

  private fun kalkuliDinamikanPasonPeuKunAmplitudo(deltaTempoMs: Long, amplitudOndo: Double): Double {
    val sek = deltaTempoMs / 1000.0
    // Biomekanika adaptiĝo laŭ kadenco kaj vertikala akcelo ( Weinberg modelo adaptita al peu )
    // Baza rapido: 0.6 sek per paŝo, 3.5 m/s² amplitudo -> 48.0 peu
    val amplitudoSkalo = (amplitudOndo / 3.5).pow(0.25)
    val kadencoSkalo = (0.6 / sek).pow(0.20)

    val dinamikaValoro = bazaNormaPasoPeu * amplitudoSkalo * kadencoSkalo
    return dinamikaValoro.coerceIn(32.0, 68.0) // limigita inter 0o40 kaj 0o104 peu
  }

  // ⟪ Unuopa Paŝo Registrado kun Preciza Peu Distanco ⟫
  private fun registriUnuopanPason(nunMs: Long, aldonindaPeu: Double) {
    val novaTutaPeu = _personDistancoPeu.value + aldonindaPeu
    val novaHodiauaPeu = _hodiauaDistancoPeu.value + aldonindaPeu
    val novaTutaPasoj = _tutaPasoj.value + 1L
    val novaHodiauaPasoj = _hodiauajPasoj.value + 1L

    akumulitaPasaDistancoEkdeLastaGpsPeu += aldonindaPeu

    _personDistancoPeu.value = novaTutaPeu
    _hodiauaDistancoPeu.value = novaHodiauaPeu
    _tutaPasoj.value = novaTutaPasoj
    _hodiauajPasoj.value = novaHodiauaPasoj

    if (lastaPasoTempoMs > 0L) {
      val deltaS = (nunMs - lastaPasoTempoMs) / 1000.0
      if (deltaS in 0.15..3.2) {
        // Paŝrapido po qe ( 1 qe ≈ 29.90258 s )
        val ritmoQe = (QE_L6VEM2 / deltaS).coerceIn(8.0, 128.0)
        _pasRapido.value = (_pasRapido.value * 0.65) + (ritmoQe * 0.35)
      }
    }
    lastaPasoTempoMs = nunMs
    _estasMoviĝanta.value = true

    agordojPref.edit()
      .putLong("tuta_peu_bits", novaTutaPeu.toBits())
      .putLong("hodiaua_peu_bits", novaHodiauaPeu.toBits())
      .putLong("tuta_pasoj_nombro", novaTutaPasoj)
      .putLong("hodiauaj_pasoj_nombro", novaHodiauaPasoj)
      .apply()
  }

  // ⟪ Integri GPS Moviĝan Distancon en Peu kun Fuzia Protekto ⟫
  fun registriGPSDistancon(deltaMetroj: Double) {
    if (deltaMetroj <= 0.0 || deltaMetroj.isNaN()) return
    val deltaGpsPeu = deltaMetroj / P0
    if (deltaGpsPeu <= 0.0) return

    // Se paŝoj jam kalkulis la distancon dum tiu translokiĝo, eviti duoblan sumadon
    val ekstraPeu = if (akumulitaPasaDistancoEkdeLastaGpsPeu > 0.0) {
      val diferenco = deltaGpsPeu - akumulitaPasaDistancoEkdeLastaGpsPeu
      akumulitaPasaDistancoEkdeLastaGpsPeu = 0.0
      max(0.0, diferenco)
    } else {
      deltaGpsPeu
    }

    if (ekstraPeu > 0.0) {
      val novaTutaPeu = _personDistancoPeu.value + ekstraPeu
      val novaHodiauaPeu = _hodiauaDistancoPeu.value + ekstraPeu
      val kalkulitajEkstrajPasoj = (ekstraPeu / bazaNormaPasoPeu).toLong()

      val novaTutaPasoj = _tutaPasoj.value + kalkulitajEkstrajPasoj
      val novaHodiauaPasoj = _hodiauajPasoj.value + kalkulitajEkstrajPasoj

      _personDistancoPeu.value = novaTutaPeu
      _hodiauaDistancoPeu.value = novaHodiauaPeu
      _tutaPasoj.value = novaTutaPasoj
      _hodiauajPasoj.value = novaHodiauaPasoj

      agordojPref.edit()
        .putLong("tuta_peu_bits", novaTutaPeu.toBits())
        .putLong("hodiaua_peu_bits", novaHodiauaPeu.toBits())
        .putLong("tuta_pasoj_nombro", novaTutaPasoj)
        .putLong("hodiauaj_pasoj_nombro", novaHodiauaPasoj)
        .apply()
    }
  }

  // ⟪ Restarigi Personan Distancon kaj Paŝojn ⟫
  fun restarigi() {
    _personDistancoPeu.value = 0.0
    _hodiauaDistancoPeu.value = 0.0
    _tutaPasoj.value = 0L
    _hodiauajPasoj.value = 0L
    _pasRapido.value = 0.0
    _estasMoviĝanta.value = false
    akumulitaPasaDistancoEkdeLastaGpsPeu = 0.0

    agordojPref.edit()
      .putLong("tuta_peu_bits", 0L)
      .putLong("hodiaua_peu_bits", 0L)
      .putLong("tuta_pasoj_nombro", 0L)
      .putLong("hodiauaj_pasoj_nombro", 0L)
      .apply()
  }

  private fun kontroliTagon() {
    val nunMs = System.currentTimeMillis()
    val nunaTago = cax2lStafl2(nunMs).alIdentigilo()
    if (lastaKonservitaTagoIdentigilo != nunaTago) {
      lastaKonservitaTagoIdentigilo = nunaTago
      _hodiauaDistancoPeu.value = 0.0
      _hodiauajPasoj.value = 0L
      agordojPref.edit()
        .putString("lasta_tago_id", nunaTago)
        .putLong("hodiaua_peu_bits", 0L)
        .putLong("hodiauaj_pasoj_nombro", 0L)
        .apply()
    }
  }
}

