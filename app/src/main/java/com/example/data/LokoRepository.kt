package com.example.data

import com.example.ktash.*
import com.example.network.VeteroServo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

// ≺⧼ Loka Deponejo 📦 ⧽≻

class LokoRepository(
  private val lokoLogDao: LokoLogDao,
  private val veteroServo: VeteroServo = VeteroServo()
) {

  val ciujProtokoloj: Flow<List<LokoLogEntity>> = lokoLogDao.ciujProtokoloj()
  val ciujDistinctTagoj: Flow<List<KtashTagoIdentigilo>> = lokoLogDao.ciujDistinctKtashTagoj()

  fun protokolojLauKtashTago(stibix: Long, pal2stif: Long, stafl2: Long): Flow<List<LokoLogEntity>> {
    return lokoLogDao.protokolojLauKtashDato(stibix, pal2stif, stafl2)
  }

  suspend fun akiriLastanProtokolon(): LokoLogEntity? = withContext(Dispatchers.IO) {
    lokoLogDao.lastaProtokolo()
  }

  suspend fun registriLokon(
    lat: Double,
    lon: Double,
    rapido: Double = 0.0,
    noto: String? = null,
    devigiRegistradon: Boolean = false
  ): LokoLogEntity? = withContext(Dispatchers.IO) {
    val nunMs = System.currentTimeMillis()
    val lasta = lokoLogDao.lastaProtokolo()

    val distMetroj: Double
    if (lasta != null) {
      distMetroj = kalkuliDistancoMetroj(lasta.latitudo, lasta.longitudo, lat, lon)
      // Se koordinatoj ne ŝanĝiĝis kaj ne estas deviga
      if (!devigiRegistradon && distMetroj < 5.0) {
        return@withContext null
      }
    } else {
      distMetroj = 0.0
    }

    val kadro = akiriKadrajnKoordinatojn(lat, lon)
    val nomoj = akiriNomojn(kadro)
    val dato = cax2lStafl2(nunMs)

    val cels = veteroServo.preniTemperaturonCelsius(lat, lon) ?: kalkuliProksimumanTemperaturonCelsius(lat, lon, nunMs)
    val temperaturoK = celsiusAlKelvino(cels)

    val novaProtokolo = LokoLogEntity(
      latitudo = lat,
      longitudo = lon,
      tempoMilisekundoj = nunMs,
      ksakaNomo = nomoj.ksaka,
      latinaNomo = nomoj.latina,
      chmuahNomo = nomoj.chmuah,
      v1 = kadro.v1,
      h1 = kadro.h1,
      v2 = kadro.v2,
      h2 = kadro.h2,
      v3 = kadro.v3,
      h3 = kadro.h3,
      v4 = kadro.v4,
      h4 = kadro.h4,
      stibix = dato.stibix,
      pal2stif = dato.pal2stif,
      stafl2 = dato.stafl2,
      temperaturoKelvino = temperaturoK,
      distancoDeAntauaMetroj = distMetroj,
      distancoDeAntauaPeu = metrojAlPeu(distMetroj),
      distancoDeAntauaC2ta = metrojAlC2ta(distMetroj),
      rapidoMetrojSekundo = rapido,
      noto = noto
    )

    val id = lokoLogDao.enmetiProtokolon(novaProtokolo)
    novaProtokolo.copy(id = id)
  }

  suspend fun akiriCiujnListon(): List<LokoLogEntity> = withContext(Dispatchers.IO) {
    lokoLogDao.akiriCiujnListon()
  }

  suspend fun enmetiCiujn(protokoloj: List<LokoLogEntity>) = withContext(Dispatchers.IO) {
    lokoLogDao.enmetiCiujn(protokoloj)
  }

  suspend fun forigiLauId(id: Long) = withContext(Dispatchers.IO) {
    lokoLogDao.forigiLauId(id)
  }

  suspend fun vakigiCiujn() = withContext(Dispatchers.IO) {
    lokoLogDao.vakigiCiujn()
  }
}
