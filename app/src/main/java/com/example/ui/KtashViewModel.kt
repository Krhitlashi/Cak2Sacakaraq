package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.ktash.*
import com.example.location.LokoManagero
import com.example.network.VeteroServo
import com.example.ui.i18n.ESPERANTO_TEKSTOJ
import com.example.ui.i18n.Lingvo
import com.example.ui.i18n.TradukTekstoj
import com.example.ui.i18n.preniTradukojn
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ≺⧼ Ĉefa Vido-Modelisto 🧠 ⧽≻

enum class NavigaLangeto {
  SPURILO,
  MAPO,
  MEZURILO,
  SUNO_VETERO,
  PROTOKOLO
}

data class EsplorStacio(
  val id: String = java.util.UUID.randomUUID().toString(),
  val nomo: String = "",
  val latitudo: Double = 0.0,
  val longitudo: Double = 0.0
) {
  constructor(nomo: String, latitudo: Double, longitudo: Double) : this(
    id = java.util.UUID.randomUUID().toString(),
    nomo = nomo,
    latitudo = latitudo,
    longitudo = longitudo
  )
}

val DEFAULTOJ_LOKOJ = listOf(
  EsplorStacio("def_1", "McMurdo", -77.8419, 166.6863),
  EsplorStacio("def_2", "Scott Base", -77.8491, 166.7647),
  EsplorStacio("def_3", "Vostok", -78.4647, 106.8378),
  EsplorStacio("def_4", "Concordia", -75.1000, 123.3300),
  EsplorStacio("def_5", "Halley", -75.5736, -25.5083),
  EsplorStacio("def_6", "South Pole", -90.0000, 0.0000),
  EsplorStacio("def_7", "Troll", -72.0117, 2.5350),
  EsplorStacio("def_8", "Rothera", -67.5689, -68.1300)
)
val ANTARKTAJ_STACIOJ = DEFAULTOJ_LOKOJ

class KtashViewModel(aplikaĵo: Application) : AndroidViewModel(aplikaĵo) {

  private val datumbazo = AppDatabase.akiriDatumbazon(aplikaĵo)
  private val deponejo = LokoRepository(datumbazo.lokoLogDao(), VeteroServo())
  val lokoManagero = LokoManagero(aplikaĵo, deponejo)
  val pasSpurilo = com.example.location.PasSpurilo(aplikaĵo)
  private val veteroServo = VeteroServo()

  val tutaPasoj: StateFlow<Long> = pasSpurilo.tutaPasoj
  val hodiauajPasoj: StateFlow<Long> = pasSpurilo.hodiauajPasoj
  val personDistancoPeu: StateFlow<Double> = pasSpurilo.personDistancoPeu
  val pasRapido: StateFlow<Double> = pasSpurilo.pasRapido
  val estasMoviĝanta: StateFlow<Boolean> = pasSpurilo.estasMoviĝanta

  fun restarigiPasojn() {
    pasSpurilo.restarigi()
  }

  // ⟪ Agordoj Konservado ( SharedPreferences ) ⟫
  private val agordojPref = aplikaĵo.getSharedPreferences("ktash_agordoj", Context.MODE_PRIVATE)

  // ⟪ Baza Agordo ⟫
  private val _aktivaLangeto = MutableStateFlow(NavigaLangeto.SPURILO)
  val aktivaLangeto: StateFlow<NavigaLangeto> = _aktivaLangeto.asStateFlow()

  private val _plenaEkranaMezurilo = MutableStateFlow(false)
  val plenaEkranaMezurilo: StateFlow<Boolean> = _plenaEkranaMezurilo.asStateFlow()

  fun agordiPlenanEkrananMezurilon(aktiva: Boolean) {
    _plenaEkranaMezurilo.value = aktiva
  }

  private val _uzuBazo10 = MutableStateFlow(agordojPref.getBoolean("uzu_bazo_10", false))
  val uzuBazo10: StateFlow<Boolean> = _uzuBazo10.asStateFlow()

  // ⟪ Lingvo & Tradukoj ⟫
  private val komencaLingvoKodo = agordojPref.getString("lingvo_kodo", Lingvo.ESPERANTO.kodo) ?: Lingvo.ESPERANTO.kodo
  private val komencaLingvo = Lingvo.values().find { it.kodo == komencaLingvoKodo } ?: Lingvo.ESPERANTO

  private val _elektitaLingvo = MutableStateFlow(komencaLingvo)
  val elektitaLingvo: StateFlow<Lingvo> = _elektitaLingvo.asStateFlow()

  val tradukoj: StateFlow<TradukTekstoj> = _elektitaLingvo
    .map { preniTradukojn(it) }
    .stateIn(viewModelScope, SharingStarted.Eagerly, preniTradukojn(komencaLingvo))

  // ⟪ Aspekto, Temo & Tiparo ⟫
  private val komencaTemoNomo = agordojPref.getString("temo_modo", com.example.ui.theme.TemoModo.SISTEMA.name) ?: com.example.ui.theme.TemoModo.SISTEMA.name
  private val komencaTemo = try {
    com.example.ui.theme.TemoModo.valueOf(komencaTemoNomo)
  } catch (e: Exception) {
    com.example.ui.theme.TemoModo.SISTEMA
  }

  private val _temoModo = MutableStateFlow(komencaTemo)
  val temoModo: StateFlow<com.example.ui.theme.TemoModo> = _temoModo.asStateFlow()

  private val _uzuMaterialYou = MutableStateFlow(agordojPref.getBoolean("uzu_material_you", false))
  val uzuMaterialYou: StateFlow<Boolean> = _uzuMaterialYou.asStateFlow()

  private val _uzuPropraTiparo = MutableStateFlow(agordojPref.getBoolean("uzu_propra_tiparo", true))
  val uzuPropraTiparo: StateFlow<Boolean> = _uzuPropraTiparo.asStateFlow()

  // ⟪ Vivantaj Tempoj ⟫
  private val _nunaTempoMs = MutableStateFlow(System.currentTimeMillis())
  val nunaTempoMs: StateFlow<Long> = _nunaTempoMs.asStateFlow()

  // ⟪ Protokoloj & Tagaj Filtriloj ⟫
  val ciujProtokoloj: StateFlow<List<LokoLogEntity>> = deponejo.ciujProtokoloj
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val distinctTagoj: StateFlow<List<KtashTagoIdentigilo>> = deponejo.ciujDistinctTagoj
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _elektitaTago = MutableStateFlow<KtashTagoIdentigilo?>(null)
  val elektitaTago: StateFlow<KtashTagoIdentigilo?> = _elektitaTago.asStateFlow()

  val filtritajProtokoloj: StateFlow<List<LokoLogEntity>> = combine(
    ciujProtokoloj,
    _elektitaTago
  ) { protokoloj, tago ->
    if (tago == null) {
      protokoloj
    } else {
      protokoloj.filter { it.stibix == tago.stibix && it.pal2stif == tago.pal2stif && it.stafl2 == tago.stafl2 }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // ⟪ Mapo & Voja Inspektado ⟫
  private val _elektitaPunkto = MutableStateFlow<LokoLogEntity?>(null)
  val elektitaPunkto: StateFlow<LokoLogEntity?> = _elektitaPunkto.asStateFlow()

  private val _sercaTeksto = MutableStateFlow("")
  val sercaTeksto: StateFlow<String> = _sercaTeksto.asStateFlow()

  // ⟪ Konservitaj Lokoj ( Eblo aldoni / forigi proprajn lokojn ) ⟫
  private val _konservitajLokoj = MutableStateFlow<List<EsplorStacio>>(DEFAULTOJ_LOKOJ)
  val konservitajLokoj: StateFlow<List<EsplorStacio>> = _konservitajLokoj.asStateFlow()

  // ⟪ Vetero & Temperaturo ⟫
  private val _nunaTemperaturoCelsius = MutableStateFlow(
    kalkuliProksimumanTemperaturonCelsius(47.48, -122.21)
  )
  val nunaTemperaturoCelsius: StateFlow<Double> = _nunaTemperaturoCelsius.asStateFlow()

  private val _esplorLoko = MutableStateFlow(
    EsplorStacio(
      id = "loc_cur",
      nomo = "Nuna Pozicio",
      latitudo = 47.48,
      longitudo = -122.21
    )
  )
  val esplorLoko: StateFlow<EsplorStacio> = _esplorLoko.asStateFlow()

  private val _esplorTemperaturoCelsius = MutableStateFlow(
    kalkuliProksimumanTemperaturonCelsius(47.48, -122.21)
  )
  val esplorTemperaturoCelsius: StateFlow<Double> = _esplorTemperaturoCelsius.asStateFlow()

  // ⟪ Mesaĝoj & Sciigoj ⟫
  private val _sciigoTeksto = MutableStateFlow<String?>(null)
  val sciigoTeksto: StateFlow<String?> = _sciigoTeksto.asStateFlow()

  init {
    val konservitaIntervalo = agordojPref.getLong("spura_intervalo_ms", 2700000L)
    val konservitaAutoSpurado = agordojPref.getBoolean("auto_spurado_aktiva", true)
    lokoManagero.agordiSpuranIntervalon(konservitaIntervalo)
    lokoManagero.baskuliAutoSpuradon(konservitaAutoSpurado)
    lokoManagero.komenciVivajnGPSGisdatigojn()

    // Defaŭlte elekti la plej freŝan tagon anstataŭ ĉiuj tagoj por mapo kaj protokolo
    viewModelScope.launch {
      distinctTagoj.collect { tagoj ->
        if (_elektitaTago.value == null && tagoj.isNotEmpty()) {
          _elektitaTago.value = tagoj.first()
        }
      }
    }

    // Perioda horloĝo por vivanta tempo
    viewModelScope.launch {
      while (true) {
        _nunaTempoMs.value = System.currentTimeMillis()
        delay(100)
      }
    }

    // Aŭtomata veter-peto por nuna loko kaj esplor-loko
    viewModelScope.launch {
      lokoManagero.nunaLoko.collect { loko ->
        val kalkulita = kalkuliProksimumanTemperaturonCelsius(loko.latitudo, loko.longitudo)
        _nunaTemperaturoCelsius.value = kalkulita
        if (_esplorLoko.value.id == "loc_cur") {
          _esplorLoko.value = _esplorLoko.value.copy(
            latitudo = loko.latitudo,
            longitudo = loko.longitudo
          )
          _esplorTemperaturoCelsius.value = kalkulita
        }
        val cels = veteroServo.preniTemperaturonCelsius(loko.latitudo, loko.longitudo)
        if (cels != null) {
          _nunaTemperaturoCelsius.value = cels
          if (_esplorLoko.value.id == "loc_cur") {
            _esplorTemperaturoCelsius.value = cels
          }
        }
      }
    }

    // Integri registritajn kaj vivajn GPS moviĝojn al person-distanca spurilo
    viewModelScope.launch {
      lokoManagero.lastaRegistrita.collect { ero ->
        if (ero != null && ero.distancoDeAntauaMetroj > 0.0) {
          pasSpurilo.registriGPSDistancon(ero.distancoDeAntauaMetroj)
        }
      }
    }

    viewModelScope.launch {
      lokoManagero.vivaTranslokiĝoMetroj.collect { deltaMetroj ->
        if (deltaMetroj > 0.0) {
          pasSpurilo.registriGPSDistancon(deltaMetroj)
        }
      }
    }
  }

  fun agordiSpuranIntervalon(ms: Long) {
    lokoManagero.agordiSpuranIntervalon(ms)
    agordojPref.edit().putLong("spura_intervalo_ms", ms).apply()
  }

  fun baskuliAutoSpuradon(aktiva: Boolean) {
    lokoManagero.baskuliAutoSpuradon(aktiva)
    agordojPref.edit().putBoolean("auto_spurado_aktiva", aktiva).apply()
  }

  fun ŝanĝiLangeton(langeto: NavigaLangeto) {
    _aktivaLangeto.value = langeto
  }

  fun baskuliBazon() {
    val nova = !_uzuBazo10.value
    _uzuBazo10.value = nova
    agordojPref.edit().putBoolean("uzu_bazo_10", nova).apply()
  }

  fun agordiLingvon(lingvo: Lingvo) {
    _elektitaLingvo.value = lingvo
    agordojPref.edit().putString("lingvo_kodo", lingvo.kodo).apply()
  }

  fun agordiTemoModon(modo: com.example.ui.theme.TemoModo) {
    _temoModo.value = modo
    agordojPref.edit().putString("temo_modo", modo.name).apply()
  }

  fun baskuliMaterialYou() {
    val nova = !_uzuMaterialYou.value
    _uzuMaterialYou.value = nova
    agordojPref.edit().putBoolean("uzu_material_you", nova).apply()
  }

  fun baskuliTiparon() {
    val nova = !_uzuPropraTiparo.value
    _uzuPropraTiparo.value = nova
    agordojPref.edit().putBoolean("uzu_propra_tiparo", nova).apply()
  }

  fun elektiTagonPorFiltro(tago: KtashTagoIdentigilo?) {
    _elektitaTago.value = tago
  }

  fun elektiPunkton(punkto: LokoLogEntity?) {
    _elektitaPunkto.value = punkto
  }

  fun agordiSercanTekston(teksto: String) {
    _sercaTeksto.value = teksto
  }

  fun registriNunanLokon(noto: String? = null) {
    viewModelScope.launch {
      val reg = lokoManagero.preniKajRegistriLokon(devigi = true, noto = noto)
      if (reg != null) {
        _sciigoTeksto.value = "Loko sukcese registrita."
      } else {
        _sciigoTeksto.value = "Koordinatoj ne ŝanĝiĝis."
      }
    }
  }

  fun forigiProtokolon(id: Long) {
    viewModelScope.launch {
      deponejo.forigiLauId(id)
      if (_elektitaPunkto.value?.id == id) {
        _elektitaPunkto.value = null
      }
    }
  }

  fun vakigiCiujnProtokolojn() {
    viewModelScope.launch {
      deponejo.vakigiCiujn()
      _elektitaPunkto.value = null
    }
  }

  fun akiriEksportDosierNomon(): String {
    val tago = _elektitaTago.value
    val bazo10 = _uzuBazo10.value
    return if (tago != null) {
      "${tago.alTeksto(bazo10)}.txt"
    } else {
      val nunaDato = cax2lStafl2(_nunaTempoMs.value)
      "${nunaDato.alTeksto(bazo10)}.txt"
    }
  }

  fun eksportiProtokolojnTXT(onFinita: (dosierNomo: String, teksto: String) -> Unit) {
    viewModelScope.launch {
      val tago = _elektitaTago.value
      val bazo10 = _uzuBazo10.value
      val dosierNomo = akiriEksportDosierNomon()

      if (tago != null) {
        val list = deponejo.akiriCiujnListon().filter {
          it.stibix == tago.stibix && it.pal2stif == tago.pal2stif && it.stafl2 == tago.stafl2
        }
        val sb = StringBuilder()
        list.forEach { ero ->
          val tempo = castifeh2(ero.tempoMilisekundoj)
          val hqQH = tempo.alTeksto(bazo10)
          val coords = ero.ksakaNomo.ifEmpty { ero.latinaNomo }
          val peuDist = formatiOksaleAuxDekume(ero.distancoDeAntauaPeu, bazo10, 2)
          val hiaTemp = formatiOksaleAuxDekume(kelvinoAlHia(ero.temperaturoKelvino ?: 0.0), bazo10, 1)
          sb.append("$hqQH — $coords — $peuDist — $hiaTemp\n")
        }
        onFinita(dosierNomo, sb.toString().trimEnd())
      } else {
        val list = deponejo.akiriCiujnListon()
        val tagGrupigita = list.groupBy { Cax2lDato(it.stibix, it.pal2stif, it.stafl2) }
        val sb = StringBuilder()
        tagGrupigita.forEach { (t, eroj) ->
          sb.append("${t.alTeksto(bazo10)}\n")
          eroj.forEach { ero ->
            val tempo = castifeh2(ero.tempoMilisekundoj)
            val hqQH = tempo.alTeksto(bazo10)
            val coords = ero.ksakaNomo.ifEmpty { ero.latinaNomo }
            val peuDist = formatiOksaleAuxDekume(ero.distancoDeAntauaPeu, bazo10, 2)
            val hiaTemp = formatiOksaleAuxDekume(kelvinoAlHia(ero.temperaturoKelvino ?: 0.0), bazo10, 1)
            sb.append("$hqQH — $coords — $peuDist — $hiaTemp\n")
          }
          sb.append("\n")
        }
        onFinita(dosierNomo, sb.toString().trimEnd())
      }
    }
  }

  fun importiProtokolojnTXT(
    teksto: String,
    onSukceso: (Int) -> Unit,
    onEraro: () -> Unit
  ) {
    viewModelScope.launch {
      try {
        val trimmed = teksto.trim()
        if (trimmed.isEmpty()) {
          onEraro()
          return@launch
        }

        // Se estas JSON, rezervu retrokongruon
        if (trimmed.startsWith("[") || trimmed.startsWith("{")) {
          importiProtokolojnJSON(trimmed, onSukceso, onEraro)
          return@launch
        }

        val linioj = trimmed.lines()
        val listo = mutableListOf<LokoLogEntity>()
        var nunaDato: Cax2lDato = cax2lStafl2(_nunaTempoMs.value)

        for (linio in linioj) {
          val l = linio.trim()
          if (l.isEmpty()) continue

          // Kontrolu ĉu la linio estas Tago-kapo (ekz: "ɿɔ ıꞟ ɿı" aŭ "12 3 45")
          if (!l.contains("—") && !l.contains("–") && !l.contains(" - ")) {
            val partoj = l.split(Regex("\\s+")).filter { it.isNotEmpty() }
            if (partoj.size == 3) {
              val s = if (partoj[0].any { ch -> K2FE_MAP.containsKey(ch.toString()) }) malvab6caja(partoj[0]) else partoj[0].toLongOrNull()
              val p = if (partoj[1].any { ch -> K2FE_MAP.containsKey(ch.toString()) }) malvab6caja(partoj[1]) else partoj[1].toLongOrNull()
              val st = if (partoj[2].any { ch -> K2FE_MAP.containsKey(ch.toString()) }) malvab6caja(partoj[2]) else partoj[2].toLongOrNull()
              if (s != null && p != null && st != null && s > 0 && p > 0 && st > 0) {
                nunaDato = Cax2lDato(s, p, st)
                continue
              }
            }
          }

          // Divido de protokola linio: Hq Q H — KsakaCoords — PeuDist — HiaTemp
          val partoj = l.split(Regex("\\s*—\\s*|\\s*–\\s*|\\s+-\\s+"))
          if (partoj.size >= 4) {
            val tempoParto = partoj[0].trim()
            val coordsParto = partoj[1].trim()
            val peuParto = partoj[2].trim()
            val hiaParto = partoj[3].trim()

            // 1. Tempo
            val tVortoj = tempoParto.split(Regex("\\s+")).filter { it.isNotEmpty() }
            val haqe = if (tVortoj.isNotEmpty()) malvab6caja(tVortoj[0]) else 0L
            val qe = if (tVortoj.size > 1) malvab6caja(tVortoj[1]) else 0L
            val he = if (tVortoj.size > 2) malvab6cajaDomani(tVortoj[2]) else 0.0
            val ktashTempo = Castifeh2Tempo(0, 0, 0, haqe, qe, he)
            val tempoMs = kalkuliDatoMsElKtash(nunaDato, ktashTempo)

            // 2. Koordinatoj
            val kadro = malakiriKsakaNomon(coordsParto) ?: continue
            val (lat, lon) = kadroAlLatLon(kadro.v1, kadro.h1, kadro.v2, kadro.h2, kadro.v3, kadro.h3, kadro.v4, kadro.h4)
            val nomoj = akiriNomojn(kadro)

            // 3. Distanco
            val distPeu = malvab6cajaDomani(peuParto)
            val distMetroj = peuAlMetroj(distPeu)
            val distC2ta = metrojAlC2ta(distMetroj)

            // 4. Temperaturo
            val tempHia = malvab6cajaDomani(hiaParto)
            val tempK = if (tempHia > 0.0) hiaAlKelvino(tempHia) else null

            listo.add(
              LokoLogEntity(
                id = 0L,
                latitudo = lat,
                longitudo = lon,
                tempoMilisekundoj = tempoMs,
                ksakaNomo = nomoj.ksaka,
                latinaNomo = nomoj.latina,
                chmuahNomo = nomoj.chmuah,
                v1 = kadro.v1, h1 = kadro.h1,
                v2 = kadro.v2, h2 = kadro.h2,
                v3 = kadro.v3, h3 = kadro.h3,
                v4 = kadro.v4, h4 = kadro.h4,
                stibix = nunaDato.stibix,
                pal2stif = nunaDato.pal2stif,
                stafl2 = nunaDato.stafl2,
                temperaturoKelvino = tempK,
                distancoDeAntauaMetroj = distMetroj,
                distancoDeAntauaPeu = distPeu,
                distancoDeAntauaC2ta = distC2ta,
                rapidoMetrojSekundo = 0.0,
                noto = null
              )
            )
          }
        }

        if (listo.isNotEmpty()) {
          deponejo.enmetiCiujn(listo)
          _sciigoTeksto.value = "Sukcese importis ${listo.size} punktojn."
          onSukceso(listo.size)
        } else {
          onEraro()
        }
      } catch (e: Exception) {
        _sciigoTeksto.value = "Eraro dum importado. ${e.message}"
        onEraro()
      }
    }
  }

  fun eksportiProtokolojnJSON(onFinita: (String) -> Unit) {
    viewModelScope.launch {
      val list = deponejo.akiriCiujnListon()
      val jsonArray = org.json.JSONArray()
      list.forEach { ero ->
        val obj = org.json.JSONObject().apply {
          put("latitudo", ero.latitudo)
          put("longitudo", ero.longitudo)
          put("tempoMilisekundoj", ero.tempoMilisekundoj)
          put("ksakaNomo", ero.ksakaNomo)
          put("latinaNomo", ero.latinaNomo)
          put("chmuahNomo", ero.chmuahNomo)
          put("v1", ero.v1)
          put("h1", ero.h1)
          put("v2", ero.v2)
          put("h2", ero.h2)
          put("v3", ero.v3)
          put("h3", ero.h3)
          put("v4", ero.v4)
          put("h4", ero.h4)
          put("stibix", ero.stibix)
          put("pal2stif", ero.pal2stif)
          put("stafl2", ero.stafl2)
          if (ero.temperaturoKelvino != null) put("temperaturoKelvino", ero.temperaturoKelvino)
          put("distancoDeAntauaMetroj", ero.distancoDeAntauaMetroj)
          put("distancoDeAntauaPeu", ero.distancoDeAntauaPeu)
          put("distancoDeAntauaC2ta", ero.distancoDeAntauaC2ta)
          put("rapidoMetrojSekundo", ero.rapidoMetrojSekundo)
          if (ero.noto != null) put("noto", ero.noto)
        }
        jsonArray.put(obj)
      }
      onFinita(jsonArray.toString(2))
    }
  }

  fun importiProtokolojnJSON(
    jsonTeksto: String,
    onSukceso: (Int) -> Unit,
    onEraro: () -> Unit
  ) {
    viewModelScope.launch {
      try {
        val trimmed = jsonTeksto.trim()
        val jsonArray = if (trimmed.startsWith("[")) {
          org.json.JSONArray(trimmed)
        } else if (trimmed.startsWith("{")) {
          val single = org.json.JSONObject(trimmed)
          org.json.JSONArray().put(single)
        } else {
          onEraro()
          return@launch
        }

        val listo = mutableListOf<LokoLogEntity>()
        for (i in 0 until jsonArray.length()) {
          val obj = jsonArray.getJSONObject(i)
          val lat = obj.optDouble("latitudo", 0.0)
          val lon = obj.optDouble("longitudo", 0.0)
          val tempo = obj.optLong("tempoMilisekundoj", System.currentTimeMillis())
          val ksaka = obj.optString("ksakaNomo", "")
          val latina = obj.optString("latinaNomo", "")
          val chmuah = obj.optString("chmuahNomo", "")
          val v1 = obj.optInt("v1", 0)
          val h1 = obj.optInt("h1", 0)
          val v2 = obj.optInt("v2", 0)
          val h2 = obj.optInt("h2", 0)
          val v3 = obj.optInt("v3", 0)
          val h3 = obj.optInt("h3", 0)
          val v4 = obj.optInt("v4", 0)
          val h4 = obj.optInt("h4", 0)
          val stibix = obj.optLong("stibix", 1L)
          val pal2stif = obj.optLong("pal2stif", 1L)
          val stafl2 = obj.optLong("stafl2", 1L)
          val tempK = if (obj.has("temperaturoKelvino")) obj.optDouble("temperaturoKelvino") else null
          val dM = obj.optDouble("distancoDeAntauaMetroj", 0.0)
          val dP = obj.optDouble("distancoDeAntauaPeu", 0.0)
          val dC = obj.optDouble("distancoDeAntauaC2ta", 0.0)
          val rap = obj.optDouble("rapidoMetrojSekundo", 0.0)
          val noto = if (obj.has("noto")) obj.optString("noto") else null

          listo.add(
            LokoLogEntity(
              id = 0L,
              latitudo = lat,
              longitudo = lon,
              tempoMilisekundoj = tempo,
              ksakaNomo = ksaka,
              latinaNomo = latina,
              chmuahNomo = chmuah,
              v1 = v1, h1 = h1,
              v2 = v2, h2 = h2,
              v3 = v3, h3 = h3,
              v4 = v4, h4 = h4,
              stibix = stibix,
              pal2stif = pal2stif,
              stafl2 = stafl2,
              temperaturoKelvino = tempK,
              distancoDeAntauaMetroj = dM,
              distancoDeAntauaPeu = dP,
              distancoDeAntauaC2ta = dC,
              rapidoMetrojSekundo = rap,
              noto = noto
            )
          )
        }

        if (listo.isNotEmpty()) {
          deponejo.enmetiCiujn(listo)
          _sciigoTeksto.value = "Importis ${listo.size} punktojn."
          onSukceso(listo.size)
        } else {
          onEraro()
        }
      } catch (e: Exception) {
        _sciigoTeksto.value = "Eraro dum importado. ${e.message}"
        onEraro()
      }
    }
  }

  fun aldoniLokon(nomo: String, latitudo: Double, longitudo: Double) {
    val nova = EsplorStacio(
      id = java.util.UUID.randomUUID().toString(),
      nomo = nomo.ifBlank { "Nova Loko" },
      latitudo = latitudo,
      longitudo = longitudo
    )
    _konservitajLokoj.value = _konservitajLokoj.value + nova
    elektiEsplorStacion(nova)
    _sciigoTeksto.value = "Aldonis lokon - ${nova.nomo}"
  }

  fun forigiLokon(stacio: EsplorStacio) {
    val novaListo = _konservitajLokoj.value.filter { it.id != stacio.id }
    _konservitajLokoj.value = novaListo
    if (_esplorLoko.value.id == stacio.id) {
      val nunaStacio = EsplorStacio("loc_cur", "Nuna Pozicio", lokoManagero.nunaLoko.value.latitudo, lokoManagero.nunaLoko.value.longitudo)
      elektiEsplorStacion(nunaStacio)
    }
    _sciigoTeksto.value = "Forigis lokon - ${stacio.nomo}"
  }

  fun elektiEsplorStacion(stacio: EsplorStacio) {
    _esplorLoko.value = stacio
    val kalkulita = if (stacio.id == "loc_cur") {
      _nunaTemperaturoCelsius.value
    } else {
      kalkuliProksimumanTemperaturonCelsius(stacio.latitudo, stacio.longitudo)
    }
    _esplorTemperaturoCelsius.value = kalkulita

    viewModelScope.launch {
      val cels = veteroServo.preniTemperaturonCelsius(stacio.latitudo, stacio.longitudo)
      if (cels != null) {
        _esplorTemperaturoCelsius.value = cels
        if (stacio.id == "loc_cur") {
          _nunaTemperaturoCelsius.value = cels
        }
      }
    }
  }

  fun purigiSciigon() {
    _sciigoTeksto.value = null
  }
}
