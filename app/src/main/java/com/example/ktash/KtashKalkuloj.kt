package com.example.ktash

import com.example.data.KtashTagoIdentigilo
import com.example.ui.i18n.Lingvo
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.*

// ≺⧼ Ktash Matematikaj Kalkuloj 🔢 ⧽≻

// ⟪ Oksala Nombra Sistemo ⟫

val K2FE = listOf("ɔ", "ı", "ɿ", "ц", "э", "ꞟ", "ɩ", "ƨ")
val K2FE_MAP = K2FE.mapIndexed { index, s -> s to index }.toMap()
const val KNAK2FE = 8

// ⟪ Plurlingva Nombra Sistemo & Ciferoj 🔢 ⟫

fun tradukiCiferojn(teksto: String, lingvo: Lingvo = Lingvo.ESPERANTO): String {
  if (teksto.isEmpty()) return teksto
  return when (lingvo) {
    Lingvo.THAI -> {
      val sb = java.lang.StringBuilder(teksto.length)
      for (ch in teksto) {
        when (ch) {
          '0' -> sb.append('๐')
          '1' -> sb.append('๑')
          '2' -> sb.append('๒')
          '3' -> sb.append('๓')
          '4' -> sb.append('๔')
          '5' -> sb.append('๕')
          '6' -> sb.append('๖')
          '7' -> sb.append('๗')
          '8' -> sb.append('๘')
          '9' -> sb.append('๙')
          else -> sb.append(ch)
        }
      }
      sb.toString()
    }
    Lingvo.ARABIC -> {
      val sb = java.lang.StringBuilder(teksto.length)
      for (ch in teksto) {
        when (ch) {
          '0' -> sb.append('٠')
          '1' -> sb.append('١')
          '2' -> sb.append('٢')
          '3' -> sb.append('٣')
          '4' -> sb.append('٤')
          '5' -> sb.append('٥')
          '6' -> sb.append('٦')
          '7' -> sb.append('٧')
          '8' -> sb.append('٨')
          '9' -> sb.append('٩')
          else -> sb.append(ch)
        }
      }
      sb.toString()
    }
    Lingvo.KHMER -> {
      val sb = java.lang.StringBuilder(teksto.length)
      for (ch in teksto) {
        when (ch) {
          '0' -> sb.append('០')
          '1' -> sb.append('១')
          '2' -> sb.append('២')
          '3' -> sb.append('៣')
          '4' -> sb.append('៤')
          '5' -> sb.append('៥')
          '6' -> sb.append('៦')
          '7' -> sb.append('៧')
          '8' -> sb.append('៨')
          '9' -> sb.append('៩')
          else -> sb.append(ch)
        }
      }
      sb.toString()
    }
    Lingvo.MANDARIN, Lingvo.JAPANESE -> {
      val sb = java.lang.StringBuilder(teksto.length)
      for (ch in teksto) {
        when (ch) {
          '0' -> sb.append('〇')
          '1' -> sb.append('一')
          '2' -> sb.append('二')
          '3' -> sb.append('三')
          '4' -> sb.append('四')
          '5' -> sb.append('五')
          '6' -> sb.append('六')
          '7' -> sb.append('七')
          '8' -> sb.append('八')
          '9' -> sb.append('九')
          else -> sb.append(ch)
        }
      }
      sb.toString()
    }
    Lingvo.AIH -> {
      val sb = java.lang.StringBuilder(teksto.length * 2)
      for (ch in teksto) {
        when (ch) {
          '0' -> sb.append("ɔ")
          '1' -> sb.append("ı")
          '2' -> sb.append("ɿ")
          '3' -> sb.append("ц")
          '4' -> sb.append("э")
          '5' -> sb.append("ꞟ")
          '6' -> sb.append("ɩ")
          '7' -> sb.append("ƨ")
          '8' -> sb.append("ƨ̵")
          '9' -> sb.append("ⱻ")
          else -> sb.append(ch)
        }
      }
      sb.toString()
    }
    else -> teksto
  }
}

fun normaligiCiferojnAlLatina(teksto: String): String {
  if (teksto.isEmpty()) return teksto
  val sb = StringBuilder()
  var i = 0
  while (i < teksto.length) {
    if (teksto.startsWith("ƨ̵", i)) {
      sb.append('8')
      i += 2
      continue
    }
    val ch = teksto[i]
    when (ch) {
      '๐', '٠', '០', '〇', '零' -> sb.append('0')
      '๑', '١', '១', '一' -> sb.append('1')
      '๒', '٢', '២', '二' -> sb.append('2')
      '๓', '٣', '៣', '三' -> sb.append('3')
      '๔', '٤', '៤', '四' -> sb.append('4')
      '๕', '٥', '៥', '五' -> sb.append('5')
      '๖', '٦', '៦', '六' -> sb.append('6')
      '๗', '٧', '៧', '七' -> sb.append('7')
      '๘', '٨', '៨', '八' -> sb.append('8')
      '๙', '٩', '៩', '九', 'ⱻ' -> sb.append('9')
      else -> sb.append(ch)
    }
    i++
  }
  return sb.toString()
}

fun vab6caja(valoro: Long): String {
  if (valoro < 0) return "›" + vab6caja(-valoro)
  if (valoro == 0L) return K2FE[0]
  var s = ""
  var n = valoro
  while (n > 0) {
    s = K2FE[(n % KNAK2FE).toInt()] + s
    n /= KNAK2FE
  }
  return s
}

fun vab6cajaDomani(valoro: Double, precizeco: Int = 6): String {
  if (valoro.isNaN() || valoro.isInfinite()) return K2FE[0]
  if (valoro < 0) return "›" + vab6cajaDomani(-valoro, precizeco)
  val sekuraPrecizeco = precizeco.coerceIn(1, 8)
  val mult = Math.pow(8.0, sekuraPrecizeco.toDouble())
  val totalUnits = Math.round(valoro * mult)
  val entjeraParto = (totalUnits / mult).toLong()
  var frakciaUnits = (totalUnits % mult).toLong()

  val entjeraTeksto = vab6caja(entjeraParto)
  if (frakciaUnits == 0L) return entjeraTeksto

  var frakciaTeksto = ""
  var divisor = (mult / 8.0).toLong()
  for (i in 0 until sekuraPrecizeco) {
    if (divisor <= 0L) break
    val cifero = (frakciaUnits / divisor).toInt()
    frakciaTeksto += K2FE[cifero.coerceIn(0, 7)]
    frakciaUnits %= divisor
    divisor /= 8L
  }
  val sendependaFrakcio = frakciaTeksto.trimEnd(K2FE[0][0])
  return if (sendependaFrakcio.isEmpty()) entjeraTeksto else "$entjeraTeksto $sendependaFrakcio"
}

fun malvab6caja(teksto: String): Long {
  val t = normaligiCiferojnAlLatina(teksto).trim()
  if (t.isEmpty()) return 0L
  val estasNegativa = t.startsWith("›") || t.startsWith("-")
  val pura = if (estasNegativa) t.substring(1).trim() else t

  if (pura.startsWith("0o", ignoreCase = true)) {
    return try {
      val n = java.lang.Long.parseLong(pura.substring(2), 8)
      if (estasNegativa) -n else n
    } catch (_: Exception) { 0L }
  }

  var n = 0L
  var trovita = false
  for (char in pura) {
    val s = char.toString()
    if (K2FE_MAP.containsKey(s)) {
      n = n * 8 + K2FE_MAP[s]!!
      trovita = true
    } else if (char in '0'..'7') {
      n = n * 8 + (char - '0')
      trovita = true
    }
  }
  if (!trovita) {
    try {
      val dec = pura.toLong()
      return if (estasNegativa) -dec else dec
    } catch (_: Exception) {}
  }
  return if (estasNegativa) -n else n
}

fun malvab6cajaDomani(teksto: String): Double {
  val t = normaligiCiferojnAlLatina(teksto).trim()
  if (t.isEmpty()) return 0.0
  val estasNegativa = t.startsWith("›") || t.startsWith("-")
  val pura = if (estasNegativa) t.substring(1).trim() else t

  val partoj = pura.split(Regex("[\\s.]+"))
  if (partoj.isEmpty()) return 0.0
  val entjera = malvab6caja(partoj[0]).toDouble()
  if (partoj.size <= 1) {
    return if (estasNegativa) -entjera else entjera
  }

  var frakcia = 0.0
  var potenco = 1.0 / 8.0
  for (char in partoj[1]) {
    val s = char.toString()
    val valoro = K2FE_MAP[s] ?: if (char in '0'..'7') (char - '0') else null
    if (valoro != null) {
      frakcia += valoro * potenco
      potenco /= 8.0
    }
  }
  val tuta = entjera + frakcia
  return if (estasNegativa) -tuta else tuta
}

fun analiziEnigonNombro(teksto: String, uzuBazo10: Boolean = false): Double {
  val t = normaligiCiferojnAlLatina(teksto).trim()
  if (t.isEmpty()) return 0.0
  val estasNegativa = t.startsWith("›") || t.startsWith("-")
  val pura = if (estasNegativa) t.substring(1).trim() else t

  // 1. Se enhavas Ktash glifojn ( ɔ, ı, ɿ, ц, э, ꞟ, ɩ, ƨ )
  var enhavasKtash = false
  for (ch in pura) {
    if (K2FE_MAP.containsKey(ch.toString())) {
      enhavasKtash = true
      break
    }
  }

  if (enhavasKtash) {
    val partoj = pura.split(Regex("[\\s.,]+"))
    val entjeraStr = partoj.getOrNull(0) ?: ""
    var entjera = 0.0
    for (ch in entjeraStr) {
      val s = ch.toString()
      val v = K2FE_MAP[s] ?: (if (ch in '0'..'7') ch - '0' else 0)
      entjera = entjera * 8.0 + v
    }

    var frakcia = 0.0
    if (partoj.size > 1) {
      val frakciaStr = partoj[1]
      var potenco = 1.0 / 8.0
      for (ch in frakciaStr) {
        val s = ch.toString()
        val v = K2FE_MAP[s] ?: (if (ch in '0'..'7') ch - '0' else null)
        if (v != null) {
          frakcia += v * potenco
          potenco /= 8.0
        }
      }
    }
    val tuta = entjera + frakcia
    return if (estasNegativa) -tuta else tuta
  }

  // 2. Se uzuBazo10 estas vera kaj ne komenciĝas per 0o
  if (uzuBazo10 && !pura.startsWith("0o", ignoreCase = true)) {
    val dec = pura.toDoubleOrNull() ?: 0.0
    return if (estasNegativa) -dec else dec
  }

  // 3. Oksala nombro ( 0o... aŭ puraj 0-7 ciferoj kun punkto aŭ spaco )
  val sens0o = if (pura.startsWith("0o", ignoreCase = true)) pura.substring(2) else pura
  val partoj = sens0o.split(Regex("[\\s.,]+"))
  val entjeraStr = partoj.getOrNull(0) ?: ""
  var entjera = 0.0
  for (ch in entjeraStr) {
    if (ch in '0'..'7') {
      entjera = entjera * 8.0 + (ch - '0')
    }
  }

  var frakcia = 0.0
  if (partoj.size > 1) {
    var potenco = 1.0 / 8.0
    for (ch in partoj[1]) {
      if (ch in '0'..'7') {
        frakcia += (ch - '0') * potenco
        potenco /= 8.0
      }
    }
  }
  val tuta = entjera + frakcia
  return if (estasNegativa) -tuta else tuta
}

fun alOksala(valoro: Long): String {
  if (valoro < 0) return "-0o" + java.lang.Long.toOctalString(-valoro)
  return "0o" + java.lang.Long.toOctalString(valoro)
}

fun formatiOksaleAuxDekume(
  valoro: Double,
  uzuBazo10: Boolean,
  decimaloj: Int = 4,
  lingvo: Lingvo = Lingvo.ESPERANTO
): String {
  val safeVal = if (valoro.isNaN() || valoro.isInfinite()) 0.0 else valoro
  return if (uzuBazo10) {
    val decStr = String.format(java.util.Locale.US, "%.${decimaloj}f", safeVal)
    tradukiCiferojn(decStr, lingvo)
  } else {
    vab6cajaDomani(safeVal, decimaloj)
  }
}

fun formatiTaglumon(
  progreso: Double,
  uzuBazo10: Boolean,
  lingvo: Lingvo = Lingvo.ESPERANTO
): String {
  val safeProg = if (progreso.isNaN() || progreso.isInfinite()) 0.0 else progreso.coerceIn(0.0, 1.0)
  return if (uzuBazo10) {
    val pctStr = String.format(java.util.Locale.US, "%.1f%%", safeProg * 100.0)
    tradukiCiferojn(pctStr, lingvo)
  } else {
    val frakcio64 = (safeProg * 64.0).toInt().coerceIn(0, 64)
    val numGlyphs = vab6caja(frakcio64.toLong())
    val tutaGlyphs = vab6caja(64L)
    "$numGlyphs / $tutaGlyphs"
  }
}

// ⟪ Ktash Kalendaro & Tempo 🗓️ ⟫

const val SAXENICAX2L_MS = 1283781780000L // 2010-09-06 14.03.00 UTC
const val J6STAFL2_BAR6Q_MS = 86400000L // 24 * 60 * 60 * 1000

const val J6STIBIX_PAL2 = 12
const val PAL2STIF = 13
const val J6PAL2_STAFL2 = 28
const val NLLAKU_J6PAL2_STAFL2 = 29
const val KSOZU_HASTAFL2 = 29

fun nlakStafl2(stibix: Long): Boolean = stibix % 4L == 0L

fun quqalJ6stibixStafl2(stibix: Long): Long = if (nlakStafl2(stibix)) 366L else 365L

fun quqalJ6pal2stifStafl2(stibix: Long, pal2stif: Long): Long {
  return when {
    pal2stif in 1 until J6STIBIX_PAL2 -> J6PAL2_STAFL2.toLong()
    pal2stif == J6STIBIX_PAL2.toLong() -> if (nlakStafl2(stibix)) NLLAKU_J6PAL2_STAFL2.toLong() else J6PAL2_STAFL2.toLong()
    pal2stif == PAL2STIF.toLong() -> KSOZU_HASTAFL2.toLong()
    else -> 0L
  }
}

data class Cax2lDato(
  val stibix: Long,
  val pal2stif: Long,
  val stafl2: Long
) {
  fun alTeksto(uzuBazo10: Boolean = false, lingvo: Lingvo = Lingvo.ESPERANTO): String {
    return if (uzuBazo10) {
      tradukiCiferojn("$stibix $pal2stif $stafl2", lingvo)
    } else {
      "${vab6caja(stibix)} ${vab6caja(pal2stif)} ${vab6caja(stafl2)}"
    }
  }

  fun alIdentigilo(): String = "$stibix-$pal2stif-$stafl2"
}

fun KtashTagoIdentigilo.alTeksto(uzuBazo10: Boolean = false, lingvo: Lingvo = Lingvo.ESPERANTO): String {
  return if (uzuBazo10) {
    tradukiCiferojn("$stibix $pal2stif $stafl2", lingvo)
  } else {
    "${vab6caja(stibix)} ${vab6caja(pal2stif)} ${vab6caja(stafl2)}"
  }
}

fun cax2lStafl2(datoMs: Long = System.currentTimeMillis()): Cax2lDato {
  val tagoj = floor((datoMs - SAXENICAX2L_MS).toDouble() / J6STAFL2_BAR6Q_MS).toLong()
  var stibix: Long
  var pal2stif: Long
  var stafl2: Long

  if (tagoj >= 0) {
    stibix = 1
    var resto = tagoj
    while (resto >= quqalJ6stibixStafl2(stibix)) {
      resto -= quqalJ6stibixStafl2(stibix)
      stibix++
    }
    pal2stif = 1
    while (resto >= quqalJ6pal2stifStafl2(stibix, pal2stif)) {
      resto -= quqalJ6pal2stifStafl2(stibix, pal2stif)
      pal2stif++
    }
    stafl2 = resto + 1
  } else {
    stibix = 0
    var resto = -tagoj - 1
    while (resto >= quqalJ6stibixStafl2(stibix)) {
      resto -= quqalJ6stibixStafl2(stibix)
      stibix--
    }
    pal2stif = PAL2STIF.toLong()
    var temp = resto
    while (temp >= quqalJ6pal2stifStafl2(stibix, pal2stif)) {
      temp -= quqalJ6pal2stifStafl2(stibix, pal2stif)
      pal2stif--
    }
    stafl2 = quqalJ6pal2stifStafl2(stibix, pal2stif) - temp
  }
  return Cax2lDato(stibix, pal2stif, stafl2)
}

// ⟪ Ktash Tempounuoj 🕛 ⟫

const val HE_L6HEINAK = 4294967296.0 / 9192631770.0
const val SAHE_P6ZUKANI = 64.0
val HE_L6VEM2 = HE_L6HEINAK
val QE_L6VEM2 = HE_L6VEM2 * SAHE_P6ZUKANI
val HAQE_L6VEM2 = QE_L6VEM2 * SAHE_P6ZUKANI
val SHE_L6VEM2 = HAQE_L6VEM2 * SAHE_P6ZUKANI
val SQE_L6VEM2 = SHE_L6VEM2 * SAHE_P6ZUKANI
val SHAQE_L6VEM2 = SQE_L6VEM2 * SAHE_P6ZUKANI

data class Castifeh2Tempo(
  val shaqe: Long = 0L,
  val sqe: Long = 0L,
  val she: Long = 0L,
  val haqe: Long,
  val qe: Long,
  val he: Double
) {
  fun alTeksto(uzuBazo10: Boolean = false, lingvo: Lingvo = Lingvo.ESPERANTO): String {
    return if (uzuBazo10) {
      tradukiCiferojn("$haqe $qe ${he.toInt()}", lingvo)
    } else {
      "${vab6caja(haqe)} ${vab6caja(qe)} ${vab6caja(he.toLong())}"
    }
  }
}

fun castifeh2(
  lat: Double = 47.48,
  lon: Double = -122.21,
  datoMs: Long = System.currentTimeMillis()
): Castifeh2Tempo {
  val suna = kalkuliSunon(lat, lon, datoMs)
  return Castifeh2Tempo(
    shaqe = 0L,
    sqe = 0L,
    she = 0L,
    haqe = suna.haqe,
    qe = suna.qe,
    he = suna.he
  )
}

fun castifeh2(datoMs: Long): Castifeh2Tempo = castifeh2(47.48, -122.21, datoMs)

// ⟪ Distancoj & Dimensioj 📏 ⟫

const val GESEHENI = 299792458.0 // m/s
val P0 = 149896229.0 / 9192631770.0 // ≈ 0.01630612767 metroj ( 1.6306 cm / 16.3061 mm )
const val C2TA_L6XA3ENI = 0.0002645833 // m ( 1 px ĉe 96 DPI )

fun metrojAlPeu(metroj: Double): Double = metroj / P0
fun peuAlMetroj(peu: Double): Double = peu * P0
fun metrojAlC2ta(metroj: Double): Double = metroj / C2TA_L6XA3ENI
fun c2taAlMetroj(c2ta: Double): Double = c2ta * C2TA_L6XA3ENI
fun peuAlC2ta(peu: Double): Double = peu * (P0 / C2TA_L6XA3ENI)

fun kalkuliDistancoMetroj(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
  val r = 6371000.0 // Tera radiuso en metroj
  val dLat = Math.toRadians(lat2 - lat1)
  val dLon = Math.toRadians(lon2 - lon1)
  val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
  val c = 2 * atan2(sqrt(a), sqrt(1 - a))
  return r * c
}

// ⟪ Temperaturo 🌡️ ⟫

const val SACA = 1.602176634e-19
const val K_BOLTZMANN = 1.380649e-23
val HI_L6RAK2K2H2 = (SACA / 4096.0) / K_BOLTZMANN // ≈ 2.83359744 Kelvin per Hia

fun kelvinoAlHia(k: Double): Double = k / HI_L6RAK2K2H2
fun hiaAlKelvino(hi: Double): Double = hi * HI_L6RAK2K2H2
fun celsiusAlHia(c: Double): Double = kelvinoAlHia(c + 273.15)
fun hiaAlCelsius(hi: Double): Double = hiaAlKelvino(hi) - 273.15
fun celsiusAlKelvino(c: Double): Double = c + 273.15
fun kelvinoAlCelsius(k: Double): Double = k - 273.15
fun fahrenheitAlCelsius(f: Double): Double = (f - 32.0) * 5.0 / 9.0
fun celsiusAlFahrenheit(c: Double): Double = (c * 9.0 / 5.0) + 32.0
fun fahrenheitAlHia(f: Double): Double = celsiusAlHia(fahrenheitAlCelsius(f))
fun hiaAlFahrenheit(hi: Double): Double = celsiusAlFahrenheit(hiaAlCelsius(hi))
fun fahrenheitAlKelvino(f: Double): Double = celsiusAlKelvino(fahrenheitAlCelsius(f))
fun kelvinoAlFahrenheit(k: Double): Double = celsiusAlFahrenheit(kelvinoAlCelsius(k))

// ⟪ Ksaka Koordinata Sistemo 🌐 ⟫

const val KADRA_DEKALO = 11.62354

val KSAKA_V = listOf(
  "ᶅſ", "ſן", "ſȷ", "ŋᷠ", "ʃ", "ɽ͑ʃ'", "j͑ʃ'", "ſᶘ", "ɭ(", "ɭʃ",
  "j͑ʃ", "}ʃ", "j͐ʃ", "ſ̀ȷ", "ſɭ,", "ſɭˬ", "ɭl̀", "ſɟ", "ı],", "ſ͕ȷ",
  "ſ͔ɭ", "ſɭ", "֭ſɭ", "ſ͕ɭ", "j͑ʃɘ", "j͑ʃƨ", "j͑ʃᴜ̭", "j͑ʃƽ", "ſןᴜ̭", "ɭʃƽ",
  "ſɟɘ", "ſɭƨ"
)

val LATIN_V = listOf(
  "w", "p", "f", "m", "b", "r", "v", "ts", "d", "t",
  "s", "n", "l", "tl", "z", "kz", "j", "c", "x", "y",
  "g", "k", "h", "q", "sp", "st", "sc", "sk", "pc", "tk",
  "cp", "kt"
)

val CHMUAH_V = listOf(
  "វ", "ព", "ប", "ម", "រ", "ត", "ដ", "ន", "យ", "ច",
  "ឆ", "ញ", "ហ", "ក", "ខ", "ង", "អ", "ផ", "ថ", "ល",
  "ប្រ", "ត្រ", "ច្រ", "ក្រ", "ផ្ល", "ថ្ល", "ឆ្ល", "ខ្ល", "ផ្ច", "ថ្ក",
  "ឆ្ប", "ខ្ត"
)

val H_PREFIX = listOf("ꞇ", "ɹ", "ɔ", "ᴜ", "w", "ɜ", "э", "эⅎ")
val H_SUFFIX = listOf("ʞ", "ⰱ", "ɔ˞", "ͷ̗", "ƴ", "ᶗ‹", "ƽ", "ȝ")

val LATIN_H_PREFIX = listOf("i", "ii", "e", "a", "u", "o", "aa", "au")
val LATIN_H_SUFFIX = listOf("f", "v", "s", "l", "z", "x", "k", "q")

val CHMUAH_H_PREFIX = listOf("ី", "ិ", "េ", "ា", "ើ", "ុ", "ូ", "")
val CHMUAH_H_SUFFIX = listOf("ប", "ត", "ស", "ក", "ម", "ន", "ល", "ង")

data class KadrajKoordinatoj(
  val v1: Int, val h1: Int,
  val v2: Int, val h2: Int,
  val v3: Int, val h3: Int,
  val v4: Int, val h4: Int
) {
  fun alKsakaNomo(): String = akiriNomojn(this).ksaka
  fun alLatinaNomo(): String = akiriNomojn(this).latina
  fun alChmuahNomo(): String = akiriNomojn(this).chmuah

  fun alOksalaTeksto(): String {
    return "${vab6caja((v1 + 1).toLong())} ${vab6caja((h1 + 1).toLong())} - " +
      "${vab6caja((v2 + 1).toLong())} ${vab6caja((h2 + 1).toLong())} - " +
      "${vab6caja((v3 + 1).toLong())} ${vab6caja((h3 + 1).toLong())} - " +
      "${vab6caja((v4 + 1).toLong())} ${vab6caja((h4 + 1).toLong())}"
  }

  fun alDekumaTeksto(lingvo: Lingvo = Lingvo.ESPERANTO): String {
    val kruda = "${v1 + 1} ${h1 + 1} - ${v2 + 1} ${h2 + 1} - ${v3 + 1} ${h3 + 1} - ${v4 + 1} ${h4 + 1}"
    return tradukiCiferojn(kruda, lingvo)
  }
}

data class KsakaNomoj(
  val ksaka: String,
  val latina: String,
  val chmuah: String
)

fun kalkuliKadronivelojn(valoro: Double, total: Double, dividoj: List<Int>): List<Int> {
  var kruda = (valoro / total) * dividoj[0]
  if (kruda >= dividoj[0]) kruda = dividoj[0] - 0.000001
  if (kruda < 0) kruda = 0.0
  val n1 = floor(kruda).toInt()
  var resto = kruda - n1
  val niveloj = mutableListOf(n1)
  for (i in 1 until 4) {
    val k = resto * dividoj[i]
    val ni = floor(k).toInt()
    niveloj.add(ni)
    resto = k - ni
  }
  return niveloj
}

fun akiriKadrajnKoordinatojn(lat: Double, lon: Double): KadrajKoordinatoj {
  var bazo = if (lon <= 0) -lon else (360.0 - lon)
  if (lon == 0.0) bazo = 0.0
  val grad = (bazo + KADRA_DEKALO) % 360.0
  val h = kalkuliKadronivelojn(grad, 360.0, listOf(64, 32, 32, 32))
  val v = kalkuliKadronivelojn(90.0 - lat, 180.0, listOf(32, 32, 32, 32))
  return KadrajKoordinatoj(
    v[0], h[0],
    v[1], h[1],
    v[2], h[2],
    v[3], h[3]
  )
}

fun akiriUnuopanNomon(v: Int, h: Int, sistemo: String = "ksaka"): String {
  val vIndex = v.coerceIn(0, 31)
  val p = (h / 8).coerceIn(0, 7)
  val s = (h % 8).coerceIn(0, 7)
  return when (sistemo) {
    "latin" -> {
      val vNom = LATIN_V.getOrElse(vIndex) { "?" }
      val pref = LATIN_H_PREFIX.getOrElse(p) { "" }
      val suf = LATIN_H_SUFFIX.getOrElse(s) { "" }
      (vNom + pref + suf).replaceFirstChar { it.uppercase() }
    }
    "chmuah" -> {
      val vNom = CHMUAH_V.getOrElse(vIndex) { "?" }
      val pref = CHMUAH_H_PREFIX.getOrElse(p) { "" }
      val suf = CHMUAH_H_SUFFIX.getOrElse(s) { "" }
      vNom + pref + suf
    }
    else -> {
      val vNom = KSAKA_V.getOrElse(vIndex) { "?" }
      val pref = H_PREFIX.getOrElse(p) { "" }
      val suf = H_SUFFIX.getOrElse(s) { "" }
      vNom + pref + suf
    }
  }
}

fun akiriNomojn(k: KadrajKoordinatoj): KsakaNomoj {
  val vArr = listOf(k.v1, k.v2, k.v3, k.v4)
  val hArr = listOf(k.h1, k.h2, k.h3, k.h4)
  val ksaka = vArr.indices.joinToString(" ") { akiriUnuopanNomon(vArr[it], hArr[it], "ksaka") }
  val latina = vArr.indices.joinToString(" ") { akiriUnuopanNomon(vArr[it], hArr[it], "latin") }
  val chmuah = vArr.indices.joinToString(" ") { akiriUnuopanNomon(vArr[it], hArr[it], "chmuah") }
  return KsakaNomoj(ksaka, latina, chmuah)
}

fun malakiriUnuopanNomon(vorto: String): Pair<Int, Int>? {
  val v = vorto.trim()
  if (v.isEmpty()) return null

  // 1. Ksaka formo
  var restanta = v
  var sIdx = -1
  val ordigitajSufiksoj = H_SUFFIX.mapIndexed { idx, suf -> idx to suf }.sortedByDescending { it.second.length }
  for ((idx, suf) in ordigitajSufiksoj) {
    if (restanta.endsWith(suf)) {
      sIdx = idx
      restanta = restanta.substring(0, restanta.length - suf.length)
      break
    }
  }

  var pIdx = -1
  val ordigitajPrefiksoj = H_PREFIX.mapIndexed { idx, pref -> idx to pref }.sortedByDescending { it.second.length }
  for ((idx, pref) in ordigitajPrefiksoj) {
    if (restanta.endsWith(pref)) {
      pIdx = idx
      restanta = restanta.substring(0, restanta.length - pref.length)
      break
    }
  }

  val vIdx = KSAKA_V.indexOf(restanta)
  if (vIdx >= 0 && pIdx >= 0 && sIdx >= 0) {
    val h = pIdx * 8 + sIdx
    return Pair(vIdx, h)
  }

  // 2. Latina formo
  val lowerVorto = v.lowercase()
  for (i in LATIN_V.indices) {
    val lv = LATIN_V[i]
    if (lowerVorto.startsWith(lv)) {
      val postV = lowerVorto.substring(lv.length)
      for (p in LATIN_H_PREFIX.indices) {
        val lp = LATIN_H_PREFIX[p]
        if (postV.startsWith(lp)) {
          val postP = postV.substring(lp.length)
          val s = LATIN_H_SUFFIX.indexOf(postP)
          if (s >= 0) {
            return Pair(i, p * 8 + s)
          }
        }
      }
    }
  }

  return null
}

fun malakiriKsakaNomon(teksto: String): KadrajKoordinatoj? {
  val vortoj = teksto.trim().split(Regex("[\\s-]+")).filter { it.isNotEmpty() }
  if (vortoj.size >= 4) {
    val p1 = malakiriUnuopanNomon(vortoj[0])
    val p2 = malakiriUnuopanNomon(vortoj[1])
    val p3 = malakiriUnuopanNomon(vortoj[2])
    val p4 = malakiriUnuopanNomon(vortoj[3])
    if (p1 != null && p2 != null && p3 != null && p4 != null) {
      return KadrajKoordinatoj(p1.first, p1.second, p2.first, p2.second, p3.first, p3.second, p4.first, p4.second)
    }
  }
  return null
}

fun kalkuliDatoMsElKtash(dato: Cax2lDato, tempo: Castifeh2Tempo): Long {
  var tagoj = 0L
  if (dato.stibix >= 1) {
    for (y in 1 until dato.stibix) {
      tagoj += quqalJ6stibixStafl2(y)
    }
    for (m in 1 until dato.pal2stif) {
      tagoj += quqalJ6pal2stifStafl2(dato.stibix, m)
    }
    tagoj += (dato.stafl2 - 1)
  }
  val sek = tempo.haqe * HAQE_L6VEM2 + tempo.qe * QE_L6VEM2 + tempo.he * HE_L6VEM2
  return SAXENICAX2L_MS + tagoj * J6STAFL2_BAR6Q_MS + (sek * 1000.0).toLong()
}

fun nivelojAlNormaligitaj(niveloj: List<Int>, dividantoj: List<Int>): Double {
  var tuta = 0.0
  for (i in niveloj.indices) {
    var dividanto = 1.0
    for (j in 0..i) {
      dividanto *= dividantoj[j]
    }
    tuta += niveloj[i] / dividanto
  }
  return tuta
}

fun kadroAlLatLon(v1: Int, h1: Int, v2: Int, h2: Int, v3: Int, h3: Int, v4: Int, h4: Int): Pair<Double, Double> {
  val vNiveloj = listOf(v1, v2, v3, v4).map { max(0, it - 1) }
  val hNiveloj = listOf(h1, h2, h3, h4).map { max(0, it - 1) }

  val vTuta = nivelojAlNormaligitaj(vNiveloj, listOf(32, 32, 32, 32))
  val hTuta = nivelojAlNormaligitaj(hNiveloj, listOf(64, 32, 32, 32))

  val lat = 90.0 - (vTuta * 180.0)

  val gradOkcidenten = hTuta * 360.0
  var bazaGradOkcidenten = gradOkcidenten - KADRA_DEKALO
  while (bazaGradOkcidenten < 0.0) bazaGradOkcidenten += 360.0
  bazaGradOkcidenten %= 360.0

  val lon = if (bazaGradOkcidenten <= 180.0) -bazaGradOkcidenten else 360.0 - bazaGradOkcidenten
  return Pair(lat, lon)
}

// ⟪ Suna Tago, Taglongo & Sunleviĝo ☀️ ⟫

data class SunaInformo(
  val sunlevigoMs: Long,
  val sunsubiroMs: Long,
  val sekvaSunlevigoMs: Long,
  val tagoLongoSekundoj: Double,
  val lumoLongoSekundoj: Double,
  val pasintajSekundoj: Double,
  val bazo64Horlogo: Triple<Int, Int, Int>,
  val haqe: Long,
  val qe: Long,
  val he: Double,
  val bazo64Sunsubiro: Triple<Int, Int, Int>,
  val haqeSunsubiro: Long,
  val qeSunsubiro: Long,
  val heSunsubiro: Double,
  val bazo64Taglongo: Triple<Int, Int, Int>,
  val haqeTaglongo: Long,
  val qeTaglongo: Long,
  val heTaglongo: Double,
  val bazo64TutaTago: Triple<Int, Int, Int>,
  val haqeTutaTago: Long,
  val qeTutaTago: Long,
  val heTutaTago: Double,
  val taglumoProgreso: Double, // 0.0 to 1.0
  val deklinoRad: Double = 0.0,
  val azimutoRad: Double = 0.0,
  val altoRad: Double = 0.0,
  val bazo64Sunlevigo: Triple<Int, Int, Int> = Triple(0, 0, 0),
  val haqeSunlevigo: Long = 0L,
  val qeSunlevigo: Long = 0L,
  val heSunlevigo: Double = 0.0
)

fun konvertiSekundojnAlBazo64(sekundoj: Double): Triple<Int, Int, Int> {
  val tagoLongoSek = 86400.0
  val n1 = tagoLongoSek / 64.0
  val n2 = tagoLongoSek / 4096.0
  val n3 = tagoLongoSek / 262144.0
  val s = sekundoj.coerceAtLeast(0.0)
  val k1 = floor(s / n1).toInt()
  val r1 = s % n1
  val k2 = floor(r1 / n2).toInt()
  val r2 = r1 % n2
  val k3 = floor(r2 / n3).toInt()
  return Triple(k1, k2, k3)
}

fun konvertiSekundojnAlHaqeQeHe(sekundoj: Double): Triple<Long, Long, Double> {
  val s = sekundoj.coerceAtLeast(0.0)
  val haqe = floor(s / HAQE_L6VEM2).toLong()
  val restoHaqe = s % HAQE_L6VEM2
  val qe = floor(restoHaqe / QE_L6VEM2).toLong()
  val restoQe = restoHaqe % QE_L6VEM2
  val he = restoQe / HE_L6VEM2
  return Triple(haqe, qe, he)
}

fun formatiBazo64Horlogo(
  k: Triple<Int, Int, Int>,
  uzuBazo10: Boolean,
  lingvo: Lingvo = Lingvo.ESPERANTO
): String {
  val k1Val = if (uzuBazo10) tradukiCiferojn(k.first.toString(), lingvo) else vab6caja(k.first.toLong())
  val k2Val = if (uzuBazo10) tradukiCiferojn(k.second.toString(), lingvo) else vab6caja(k.second.toLong())
  val k3Val = if (uzuBazo10) tradukiCiferojn(k.third.toString(), lingvo) else vab6caja(k.third.toLong())
  return "$k1Val • $k2Val • $k3Val"
}

fun formatiHaqeQeHeHorlogo(
  haqe: Long,
  qe: Long,
  he: Double,
  uzuBazo10: Boolean,
  lingvo: Lingvo = Lingvo.ESPERANTO
): String {
  val hVal = if (uzuBazo10) tradukiCiferojn(haqe.toString(), lingvo) else vab6caja(haqe)
  val qVal = if (uzuBazo10) tradukiCiferojn(qe.toString(), lingvo) else vab6caja(qe)
  val heVal = if (uzuBazo10) tradukiCiferojn(he.toInt().toString(), lingvo) else vab6caja(he.toLong())
  return "$hVal • $qVal • $heVal"
}

fun kalkuliSunon(lat: Double, lon: Double, datoMs: Long = System.currentTimeMillis()): SunaInformo {
  val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = datoMs }
  val tagaJaro = cal.get(Calendar.DAY_OF_YEAR)
  val horoZonoOfseto = (lon / 15.0)
  val lokaHoro = ((cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE) / 60.0 + cal.get(Calendar.SECOND) / 3600.0 + horoZonoOfseto) % 24.0 + 24.0) % 24.0

  // Sun-deklinacio (rad)
  val dDeg = 23.45 * sin(Math.toRadians((360.0 / 365.0) * (tagaJaro - 81)))
  val dRad = Math.toRadians(dDeg)
  val latRad = Math.toRadians(lat)

  // Sunhora angulo (rad)
  val horaAnguloRad = Math.toRadians((lokaHoro - 12.0) * 15.0)

  // Suna alto (elevation) en radianoj
  val sinAlto = (sin(latRad) * sin(dRad) + cos(latRad) * cos(dRad) * cos(horaAnguloRad)).coerceIn(-1.0, 1.0)
  val altoRad = asin(sinAlto)

  // Suna azimuto (azimuth) en radianoj
  val cosAzimuto = if (cos(latRad) * cos(altoRad) != 0.0) {
    ((sin(dRad) - sin(latRad) * sinAlto) / (cos(latRad) * cos(altoRad))).coerceIn(-1.0, 1.0)
  } else 1.0
  val krudaAzimutoRad = acos(cosAzimuto)
  val azimutoRad = if (sin(horaAnguloRad) > 0) (2.0 * Math.PI - krudaAzimutoRad) else krudaAzimutoRad

  // Sunhora angulo por sunleviĝo
  val cosH0 = -tan(latRad) * tan(dRad)
  val h0 = when {
    cosH0 >= 1.0 -> 0.0 // Polusa nokto
    cosH0 <= -1.0 -> 180.0 // Polusa tago
    else -> Math.toDegrees(acos(cosH0.coerceIn(-1.0, 1.0)))
  }

  val tagaLumoHoroj = (h0 / 180.0) * 12.0 * 2.0
  val tagmezoUTC = 12.0 - horoZonoOfseto
  val sunlevigoUTC = (tagmezoUTC - tagaLumoHoroj / 2.0).let { (it + 24.0) % 24.0 }
  val sunsubiroUTC = (tagmezoUTC + tagaLumoHoroj / 2.0).let { (it + 24.0) % 24.0 }

  val hodiauaKomenco = cal.apply {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
  }.timeInMillis

  val sunlevigoMs = hodiauaKomenco + (sunlevigoUTC * 3600.0 * 1000.0).toLong()
  val sunsubiroMs = hodiauaKomenco + (sunsubiroUTC * 3600.0 * 1000.0).toLong()
  val sekvaSunlevigoMs = sunlevigoMs + 86400000L

  val tagoLongoSek = 86400.0
  val lumoLongoSek = (tagaLumoHoroj * 3600.0).coerceAtLeast(1.0)

  var pasis = (datoMs - sunlevigoMs) / 1000.0
  if (pasis < 0) pasis += tagoLongoSek
  pasis %= tagoLongoSek

  // Bazo-64 horloĝo (3 niveloj de 64 divido de la suna tago) ekde sunleviĝo (0)
  val (k1, k2, k3) = konvertiSekundojnAlBazo64(pasis)

  // Ekzaktaj tempounuoj ( He, Qe, Haqe ) ekde sunleviĝo (0)
  val (haqe, qe, he) = konvertiSekundojnAlHaqeQeHe(pasis)

  // Sunsubiro (ekde sunleviĝo 0, do je elpasinta lumo-longo)
  val bazo64Sub = konvertiSekundojnAlBazo64(lumoLongoSek)
  val (haqeSub, qeSub, heSub) = konvertiSekundojnAlHaqeQeHe(lumoLongoSek)

  // Tuta suna tago (86400 sekundoj - longo de la tuta tago)
  val bazo64Tuta = Triple(64, 0, 0)
  val (haqeTuta, qeTuta, heTuta) = konvertiSekundojnAlHaqeQeHe(tagoLongoSek)

  // Taglongo (longo de la tuta tago)
  val bazo64Tag = bazo64Tuta
  val haqeTag = haqeTuta
  val qeTag = qeTuta
  val heTag = heTuta

  val lumoPasis = (datoMs - sunlevigoMs) / 1000.0
  val progreso = (lumoPasis / lumoLongoSek).coerceIn(0.0, 1.0)

  return SunaInformo(
    sunlevigoMs = sunlevigoMs,
    sunsubiroMs = sunsubiroMs,
    sekvaSunlevigoMs = sekvaSunlevigoMs,
    tagoLongoSekundoj = tagoLongoSek,
    lumoLongoSekundoj = lumoLongoSek,
    pasintajSekundoj = pasis,
    bazo64Horlogo = Triple(k1, k2, k3),
    haqe = haqe,
    qe = qe,
    he = he,
    bazo64Sunsubiro = bazo64Sub,
    haqeSunsubiro = haqeSub,
    qeSunsubiro = qeSub,
    heSunsubiro = heSub,
    bazo64Taglongo = bazo64Tag,
    haqeTaglongo = haqeTag,
    qeTaglongo = qeTag,
    heTaglongo = heTag,
    bazo64TutaTago = bazo64Tuta,
    haqeTutaTago = haqeTuta,
    qeTutaTago = qeTuta,
    heTutaTago = heTuta,
    taglumoProgreso = progreso,
    deklinoRad = dRad,
    azimutoRad = azimutoRad,
    altoRad = altoRad
  )
}

// ⟪ Senreta Klimata & Temperatura Modelo 🌡️ ⟫
fun kalkuliProksimumanTemperaturonCelsius(lat: Double, lon: Double, datoMs: Long = System.currentTimeMillis()): Double {
  val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = datoMs }
  val tagaJaro = cal.get(Calendar.DAY_OF_YEAR)
  val horoZonoOfseto = (lon / 15.0)
  val lokaHoro = ((cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE) / 60.0 + horoZonoOfseto) % 24.0 + 24.0) % 24.0

  // Sezono ( norda vs suda hemisfero )
  val sezonaFaz = if (lat >= 0) (tagaJaro - 195.0) / 365.25 * 2.0 * Math.PI else (tagaJaro - 15.0) / 365.25 * 2.0 * Math.PI
  val bazaLatT = 30.0 * kotlin.math.cos(Math.toRadians(lat.coerceIn(-90.0, 90.0))) - 5.0
  val sezonaAmplekso = 12.0 * (kotlin.math.abs(lat) / 90.0)
  val sezonaT = bazaLatT + sezonaAmplekso * kotlin.math.cos(sezonaFaz)

  // Diurna taga/nokta ciklo
  val diurnaFaz = (lokaHoro - 14.0) / 24.0 * 2.0 * Math.PI
  val diurnaAmplekso = 4.0 + 3.0 * kotlin.math.cos(Math.toRadians(lat))
  val finaT = sezonaT + diurnaAmplekso * kotlin.math.cos(diurnaFaz)

  return finaT
}
