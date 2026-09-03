package com.example.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.example.data.LokoLogEntity
import com.example.data.LokoRepository
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// ≺⧼ Loka & Spurada Manaĝero 🧭 ⧽≻

class LokoManagero(
  private val kunteksto: Context,
  private val deponejo: LokoRepository
) {
  private val lokoKliento: FusedLocationProviderClient? = try {
    LocationServices.getFusedLocationProviderClient(kunteksto)
  } catch (_: Throwable) {
    null
  }

  private val sistemaLokoManagero: android.location.LocationManager? = try {
    kunteksto.getSystemService(Context.LOCATION_SERVICE) as? android.location.LocationManager
  } catch (_: Throwable) {
    null
  }

  private val esceptoTraktilo = CoroutineExceptionHandler { _, _ -> }
  private val skopo = CoroutineScope(Dispatchers.Default + SupervisorJob() + esceptoTraktilo)

  private val _nunaLoko = MutableStateFlow<LokoStato>(LokoStato.Defaŭlta)
  val nunaLoko: StateFlow<LokoStato> = _nunaLoko.asStateFlow()

  private val _autoSpuradoAktiva = MutableStateFlow(true)
  val autoSpuradoAktiva: StateFlow<Boolean> = _autoSpuradoAktiva.asStateFlow()

  // 32 fojoj tage = 86400s / 32 = 2700s = 45 minutoj = 2700000ms
  // Por komforto de uzanto, permesi agordi: 16x (90m), 32x (45m), 64x (22.5m), aŭ rapida testo
  private val _spuraIntervaloMs = MutableStateFlow(2700000L)
  val spuraIntervaloMs: StateFlow<Long> = _spuraIntervaloMs.asStateFlow()

  private val _lastaRegistrita = MutableStateFlow<LokoLogEntity?>(null)
  val lastaRegistrita: StateFlow<LokoLogEntity?> = _lastaRegistrita.asStateFlow()

  private val _vivaTranslokiĝoMetroj = MutableStateFlow(0.0)
  val vivaTranslokiĝoMetroj: StateFlow<Double> = _vivaTranslokiĝoMetroj.asStateFlow()

  private var lastaVivaLatitudo: Double? = null
  private var lastaVivaLongitudo: Double? = null

  private var spuraTasko: Job? = null
  private var locationCallback: LocationCallback? = null

  init {
    komenciSpuranBuklon()
  }

  fun agordiSpuranIntervalon(intervaloMs: Long) {
    _spuraIntervaloMs.value = intervaloMs
    if (_autoSpuradoAktiva.value) {
      komenciSpuranBuklon()
    }
  }

  fun baskuliAutoSpuradon(aktiva: Boolean) {
    _autoSpuradoAktiva.value = aktiva
    if (aktiva) {
      komenciSpuranBuklon()
    } else {
      spuraTasko?.cancel()
    }
  }

  private fun komenciSpuranBuklon() {
    spuraTasko?.cancel()
    spuraTasko = skopo.launch {
      while (isActive && _autoSpuradoAktiva.value) {
        try {
          preniKajRegistriLokon(devigi = false)
        } catch (_: Throwable) {}
        delay(_spuraIntervaloMs.value)
      }
    }
  }

  @SuppressLint("MissingPermission")
  fun komenciVivajnGPSGisdatigojn() {
    try {
      val peto = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        10000L
      ).setMinUpdateIntervalMillis(5000L).build()

      locationCallback = object : LocationCallback() {
        override fun onLocationResult(rezulto: LocationResult) {
          rezulto.lastLocation?.let { loko ->
            ĝisdatigiLokanStaton(loko)
          }
        }
      }
      lokoKliento?.requestLocationUpdates(peto, locationCallback!!, android.os.Looper.getMainLooper())
    } catch (_: Throwable) {
      // Permeso ne donita aŭ servoj ne haveblaj
    }
  }

  fun haltiVivajnGPSGisdatigojn() {
    locationCallback?.let {
      try {
        lokoKliento?.removeLocationUpdates(it)
      } catch (_: Throwable) {}
    }
  }

  @SuppressLint("MissingPermission")
  suspend fun preniNunanLokon(): Location? = withContext(Dispatchers.IO) {
    // 1. Provi Google Play Services Fused Location
    lokoKliento?.let { kliento ->
      try {
        val task = kliento.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
        val loko = com.google.android.gms.tasks.Tasks.await(task)
        if (loko != null) {
          ĝisdatigiLokanStaton(loko)
          return@withContext loko
        }
      } catch (_: Throwable) {}

      try {
        val lastTask = kliento.lastLocation
        val loko = com.google.android.gms.tasks.Tasks.await(lastTask)
        if (loko != null) {
          ĝisdatigiLokanStaton(loko)
          return@withContext loko
        }
      } catch (_: Throwable) {}
    }

    // 2. Fallback al norma Android LocationManager
    sistemaLokoManagero?.let { mgr ->
      try {
        val provizantoj = listOf(
          android.location.LocationManager.GPS_PROVIDER,
          android.location.LocationManager.NETWORK_PROVIDER,
          android.location.LocationManager.PASSIVE_PROVIDER
        )
        for (prov in provizantoj) {
          if (mgr.isProviderEnabled(prov)) {
            val loko = mgr.getLastKnownLocation(prov)
            if (loko != null) {
              ĝisdatigiLokanStaton(loko)
              return@withContext loko
            }
          }
        }
      } catch (_: Throwable) {}
    }

    null
  }

  suspend fun preniKajRegistriLokon(
    devigi: Boolean = false,
    noto: String? = null
  ): LokoLogEntity? = withContext(Dispatchers.IO) {
    val loko = preniNunanLokon()
    val lat: Double
    val lon: Double
    val alteco: Double
    val rapido: Double

    if (loko != null) {
      lat = loko.latitude
      lon = loko.longitude
      alteco = if (loko.hasAltitude()) loko.altitude else _nunaLoko.value.alteco
      rapido = loko.speed.toDouble()
    } else {
      // Defaŭltaj koordinatoj se GPS ne haveblas ( 47.48 -122.21 el la specifo )
      val nuna = _nunaLoko.value
      lat = nuna.latitudo
      lon = nuna.longitudo
      alteco = nuna.alteco
      rapido = nuna.rapido
    }

    val registrita = deponejo.registriLokon(lat, lon, alteco, rapido, noto, devigiRegistradon = devigi)
    if (registrita != null) {
      _lastaRegistrita.value = registrita
    }
    registrita
  }

  fun manaĝiKoordinatojn(lat: Double, lon: Double, alteco: Double = 0.0) {
    _nunaLoko.value = LokoStato(
      latitudo = lat,
      longitudo = lon,
      alteco = alteco,
      akurateco = 1.0f,
      rapido = 0.0,
      fonto = "Manaĝe Agordita"
    )
  }

  private fun ĝisdatigiLokanStaton(loko: Location) {
    val novaLat = loko.latitude
    val novaLon = loko.longitude
    val novaAlteco = if (loko.hasAltitude()) loko.altitude else _nunaLoko.value.alteco
    val nunaRapido = loko.speed.toDouble()

    val lastLat = lastaVivaLatitudo
    val lastLon = lastaVivaLongitudo

    if (lastLat != null && lastLon != null) {
      val deltaMetroj = com.example.ktash.kalkuliDistancoMetroj(lastLat, lastLon, novaLat, novaLon)
      // Se precizeco estas sufiĉa kaj la moviĝo estas reala ( super 0.8m kaj kongrua kun precizeco )
      if (deltaMetroj in 0.8..50000.0) {
        _vivaTranslokiĝoMetroj.value = deltaMetroj
        lastaVivaLatitudo = novaLat
        lastaVivaLongitudo = novaLon
      }
    } else {
      lastaVivaLatitudo = novaLat
      lastaVivaLongitudo = novaLon
    }

    _nunaLoko.value = LokoStato(
      latitudo = novaLat,
      longitudo = novaLon,
      alteco = novaAlteco,
      akurateco = loko.accuracy,
      rapido = nunaRapido,
      fonto = "GPS"
    )
  }
}

data class LokoStato(
  val latitudo: Double,
  val longitudo: Double,
  val alteco: Double = 0.0,
  val akurateco: Float,
  val rapido: Double,
  val fonto: String
) {
  companion object {
    val Defaŭlta = LokoStato(
      latitudo = 47.48,
      longitudo = -122.21,
      alteco = 40.0,
      akurateco = 10.0f,
      rapido = 0.0,
      fonto = "Defaŭlta"
    )
  }
}
