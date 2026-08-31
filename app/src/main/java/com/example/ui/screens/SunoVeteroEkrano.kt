package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktash.*
import com.example.ui.ANTARKTAJ_STACIOJ
import com.example.ui.EsplorStacio
import com.example.ui.KtashViewModel
import com.example.ui.components.KtashCiferoKlavaro
import com.example.ui.components.KtashFlosantaDialogo
import com.example.ui.components.KtashFlosantaKonfirmDialogo
import com.example.ui.components.KtashKarto
import com.example.ui.components.KtashSubKarto
import com.example.ui.components.N2taseButono
import com.example.ui.components.StatInsigno
import com.example.ui.components.animaciaButonFormo
import com.example.ui.components.n2taseButonKoloro
import com.example.ui.theme.*

// ≺⧼ Suno, Taglumo & Temperaturo ☀️ ⧽≻

enum class TemperaturoUnuo(val simbolo: String, val nomo: String) {
  HIA("Hi", "Hia"),
  KELVIN("K", "Kelvin"),
  CELSIUS("°C", "Celsius"),
  FAHRENHEIT("°F", "Fahrenheit")
}

@Composable
fun SunoVeteroEkrano(
  viewModel: KtashViewModel,
  modifier: Modifier = Modifier
) {
  val nunaLoko by viewModel.lokoManagero.nunaLoko.collectAsState()
  val esplorLoko by viewModel.esplorLoko.collectAsState()
  val konservitajLokoj by viewModel.konservitajLokoj.collectAsState()
  val esplorTemperaturoCelsius by viewModel.esplorTemperaturoCelsius.collectAsState()
  val uzuBazo10 by viewModel.uzuBazo10.collectAsState()
  val tradukoj by viewModel.tradukoj.collectAsState()
  val lingvo by viewModel.elektitaLingvo.collectAsState()
  val nunaTempoMs by viewModel.nunaTempoMs.collectAsState()

  var montruAldoniLokonDialogon by remember { mutableStateOf(false) }
  var novaLokoNomo by remember { mutableStateOf("") }
  var novaLokoLat by remember { mutableStateOf("") }
  var novaLokoLon by remember { mutableStateOf("") }
  var lokoForigenda by remember { mutableStateOf<EsplorStacio?>(null) }

  val sunaInformo = remember(esplorLoko.latitudo, esplorLoko.longitudo, nunaTempoMs) {
    kalkuliSunon(esplorLoko.latitudo, esplorLoko.longitudo, nunaTempoMs)
  }
  val castifeh2Tempo = remember(esplorLoko.latitudo, esplorLoko.longitudo, nunaTempoMs) {
    castifeh2(esplorLoko.latitudo, esplorLoko.longitudo, nunaTempoMs)
  }

  val cels = esplorTemperaturoCelsius
  val hia = celsiusAlHia(cels)
  val kelv = celsiusAlKelvino(cels)
  val fahr = celsiusAlFahrenheit(cels)

  var fontoUnuo by remember { mutableStateOf(TemperaturoUnuo.HIA) }
  var fontoValoroTeksto by remember { mutableStateOf("100") }

  val skroloStato = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 14.dp)
      .clip(FormoSkroloMaska)
      .verticalScroll(skroloStato)
      .padding(vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // ⟪ Loko-Elektilo kun aldoni / forigi ebloj ⟫
    LazyRow(
      modifier = Modifier
        .fillMaxWidth()
        .clip(FormoSkroloMaskaHorizontala),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      item {
        val ĉuMia = esplorLoko.id == "loc_cur" || esplorLoko.nomo == "Nuna Pozicio" || esplorLoko.nomo == tradukoj.nunaPozicio
        val miaInterago = remember { MutableInteractionSource() }
        val miaPremita by miaInterago.collectIsPressedAsState()
        val miaFormo = animaciaButonFormo(miaPremita || ĉuMia, bazaStart = 16.dp, bazaEnd = 6.dp)
        val animMiaBordo by animateColorAsState(
          targetValue = if (ĉuMia) MaterialTheme.colorScheme.primary else if (miaPremita) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.outline,
          animationSpec = tween(150),
          label = "miaBordo"
        )
        val animMiaFono by animateColorAsState(
          targetValue = if (ĉuMia) MaterialTheme.colorScheme.primary else if (miaPremita) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f) else n2taseButonKoloro(),
          animationSpec = tween(150),
          label = "miaFono"
        )

        Surface(
          onClick = {
            viewModel.elektiEsplorStacion(EsplorStacio("loc_cur", tradukoj.nunaPozicio, nunaLoko.latitudo, nunaLoko.longitudo))
          },
          interactionSource = miaInterago,
          shape = miaFormo,
          color = animMiaFono,
          border = androidx.compose.foundation.BorderStroke(1.dp, animMiaBordo)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Icon(
              Icons.Default.MyLocation,
              contentDescription = null,
              modifier = Modifier.size(16.dp),
              tint = if (ĉuMia) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = tradukoj.nunaPozicio,
              color = if (ĉuMia) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      items(konservitajLokoj, key = { it.id }) { stacio ->
        val elektita = esplorLoko.id == stacio.id || (esplorLoko.nomo == stacio.nomo && esplorLoko.latitudo == stacio.latitudo)
        val stacioInterago = remember { MutableInteractionSource() }
        val stacioPremita by stacioInterago.collectIsPressedAsState()
        val stacioFormo = animaciaButonFormo(stacioPremita || elektita, bazaStart = 16.dp, bazaEnd = 6.dp)
        val animStacioBordo by animateColorAsState(
          targetValue = if (elektita) MaterialTheme.colorScheme.primary else if (stacioPremita) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.outline,
          animationSpec = tween(150),
          label = "stacioBordo"
        )
        val animStacioFono by animateColorAsState(
          targetValue = if (elektita) MaterialTheme.colorScheme.primary else if (stacioPremita) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f) else n2taseButonKoloro(),
          animationSpec = tween(150),
          label = "stacioFono"
        )

        Surface(
          onClick = { viewModel.elektiEsplorStacion(stacio) },
          interactionSource = stacioInterago,
          shape = stacioFormo,
          color = animStacioFono,
          border = androidx.compose.foundation.BorderStroke(1.dp, animStacioBordo)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(start = 10.dp, end = 6.dp, top = 4.dp, bottom = 4.dp)
          ) {
            Text(
              text = stacio.nomo,
              color = if (elektita) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
              fontSize = 11.sp,
              fontWeight = if (elektita) FontWeight.Bold else FontWeight.Medium
            )
            IconButton(
              onClick = { lokoForigenda = stacio },
              modifier = Modifier.size(20.dp)
            ) {
              Icon(
                Icons.Default.Close,
                contentDescription = tradukoj.forigi,
                tint = if (elektita) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(13.dp)
              )
            }
          }
        }
      }

      item {
        val aldoniInterago = remember { MutableInteractionSource() }
        val aldoniPremita by aldoniInterago.collectIsPressedAsState()
        val aldoniFormo = animaciaButonFormo(aldoniPremita, bazaStart = 16.dp, bazaEnd = 6.dp)
        val animAldoniBordo by animateColorAsState(
          targetValue = if (aldoniPremita) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.outline,
          animationSpec = tween(150),
          label = "aldoniBordo"
        )
        val animAldoniFono by animateColorAsState(
          targetValue = if (aldoniPremita) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f) else n2taseButonKoloro(),
          animationSpec = tween(150),
          label = "aldoniFono"
        )

        Surface(
          onClick = {
            novaLokoNomo = ""
            novaLokoLat = ""
            novaLokoLon = ""
            montruAldoniLokonDialogon = true
          },
          interactionSource = aldoniInterago,
          shape = aldoniFormo,
          color = animAldoniFono,
          border = androidx.compose.foundation.BorderStroke(1.dp, animAldoniBordo)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Icon(
              Icons.Default.Add,
              contentDescription = null,
              modifier = Modifier.size(16.dp),
              tint = MaterialTheme.colorScheme.primary
            )
            Text(
              text = "+",
              color = MaterialTheme.colorScheme.primary,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }

    // ⟪ Dividita per 64 Suna Tago ( Bazo-64 Horloĝo ) ⟫
    val (k1, k2, k3) = sunaInformo.bazo64Horlogo
    val k1Val = if (uzuBazo10) tradukiCiferojn(k1.toString(), lingvo) else vab6caja(k1.toLong())
    val k2Val = if (uzuBazo10) tradukiCiferojn(k2.toString(), lingvo) else vab6caja(k2.toLong())
    val k3Val = if (uzuBazo10) tradukiCiferojn(k3.toString(), lingvo) else vab6caja(k3.toLong())

    val frac1Str = if (uzuBazo10) "${tradukiCiferojn("1", lingvo)} / ${tradukiCiferojn("64", lingvo)}" else "${vab6caja(1)} / ${vab6caja(64)}"
    val frac2Str = if (uzuBazo10) "${tradukiCiferojn("1", lingvo)} / ${tradukiCiferojn("4096", lingvo)}" else "${vab6caja(1)} / ${vab6caja(4096)}"
    val frac3Str = if (uzuBazo10) "${tradukiCiferojn("1", lingvo)} / ${tradukiCiferojn("262144", lingvo)}" else "${vab6caja(1)} / ${vab6caja(262144)}"

    val b64SunlevigoStr = formatiBazo64Horlogo(sunaInformo.bazo64Sunlevigo, uzuBazo10, lingvo)
    val b64SunsubiroStr = formatiBazo64Horlogo(sunaInformo.bazo64Sunsubiro, uzuBazo10, lingvo)
    val b64TaglongoStr = formatiBazo64Horlogo(sunaInformo.bazo64Taglongo, uzuBazo10, lingvo)

    KtashKarto {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = tradukoj.bazo64Horlogo,
          color = MaterialTheme.colorScheme.primary,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
          text = "${tradukoj.tutaSunaTago} ( $frac1Str • $frac2Str • $frac3Str )",
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 11.sp
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      LinearProgressIndicator(
        progress = { sunaInformo.taglumoProgreso.toFloat().coerceIn(0f, 1f) },
        modifier = Modifier
          .fillMaxWidth()
          .height(8.dp)
          .clip(FormoPilo),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
      )

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        StatInsigno(
          etikedo = tradukoj.bazo64Unuo1,
          valoro = k1Val,
          unuo = "${if (uzuBazo10) tradukiCiferojn("22.5", lingvo) else formatiOksaleAuxDekume(22.5, false, 1, lingvo)} ${tradukoj.unuoMinuto}",
          modifier = Modifier.weight(1f)
        )
        StatInsigno(
          etikedo = tradukoj.bazo64Unuo2,
          valoro = k2Val,
          unuo = "${if (uzuBazo10) tradukiCiferojn("21.1", lingvo) else formatiOksaleAuxDekume(21.1, false, 1, lingvo)} ${tradukoj.unuoSekundo}",
          modifier = Modifier.weight(1f)
        )
        StatInsigno(
          etikedo = tradukoj.bazo64Unuo3,
          valoro = k3Val,
          unuo = "${if (uzuBazo10) tradukiCiferojn("0.33", lingvo) else formatiOksaleAuxDekume(0.33, false, 2, lingvo)} ${tradukoj.unuoSekundo}",
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Suna Ciklo ( Sunleviĝo ekde 0, Sunsubiro & Taglongo por Bazo-64 )
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        StatInsigno(
          etikedo = tradukoj.sunlevigo,
          valoro = b64SunlevigoStr,
          unuo = if (uzuBazo10) tradukiCiferojn("0", lingvo) else vab6caja(0),
          modifier = Modifier.weight(1f)
        )
        StatInsigno(
          etikedo = tradukoj.sunsubiro,
          valoro = b64SunsubiroStr,
          unuo = tradukoj.taglumo,
          modifier = Modifier.weight(1f)
        )
        StatInsigno(
          etikedo = tradukoj.tutaSunaTago,
          valoro = b64TaglongoStr,
          unuo = if (uzuBazo10) "${tradukiCiferojn("64", lingvo)} • ${tradukiCiferojn("0", lingvo)}" else "${vab6caja(64)} • ${vab6caja(0)}",
          modifier = Modifier.weight(1f)
        )
      }
    }

    // ⟪ Nuna Haqe • Qe • He Tempo ⟫
    val haqeVal = if (uzuBazo10) tradukiCiferojn(castifeh2Tempo.haqe.toString(), lingvo) else vab6caja(castifeh2Tempo.haqe)
    val qeVal = if (uzuBazo10) tradukiCiferojn(castifeh2Tempo.qe.toString(), lingvo) else vab6caja(castifeh2Tempo.qe)
    val heVal = if (uzuBazo10) tradukiCiferojn(castifeh2Tempo.he.toInt().toString(), lingvo) else vab6caja(castifeh2Tempo.he.toLong())

    val hqhSunlevigoStr = formatiHaqeQeHeHorlogo(sunaInformo.haqeSunlevigo, sunaInformo.qeSunlevigo, sunaInformo.heSunlevigo, uzuBazo10, lingvo)
    val hqhSunsubiroStr = formatiHaqeQeHeHorlogo(sunaInformo.haqeSunsubiro, sunaInformo.qeSunsubiro, sunaInformo.heSunsubiro, uzuBazo10, lingvo)
    val hqhTaglongoStr = formatiHaqeQeHeHorlogo(sunaInformo.haqeTaglongo, sunaInformo.qeTaglongo, sunaInformo.heTaglongo, uzuBazo10, lingvo)

    val shaqeVal = if (uzuBazo10) tradukiCiferojn(castifeh2Tempo.shaqe.toString(), lingvo) else vab6caja(castifeh2Tempo.shaqe)
    val sqeVal = if (uzuBazo10) tradukiCiferojn(castifeh2Tempo.sqe.toString(), lingvo) else vab6caja(castifeh2Tempo.sqe)
    val sheVal = if (uzuBazo10) tradukiCiferojn(castifeh2Tempo.she.toString(), lingvo) else vab6caja(castifeh2Tempo.she)

    KtashKarto {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = tradukoj.castifeh2Horlogo,
          color = MaterialTheme.colorScheme.primary,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
          text = "${tradukoj.unuoHaqe} • ${tradukoj.unuoQe} • ${tradukoj.unuoHe}",
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 11.sp
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        StatInsigno(
          etikedo = tradukoj.unuoHaqe,
          valoro = haqeVal,
          unuo = "${if (uzuBazo10) tradukiCiferojn("31.89", lingvo) else formatiOksaleAuxDekume(31.89, false, 2, lingvo)} ${tradukoj.unuoMinuto}",
          modifier = Modifier.weight(1f)
        )
        StatInsigno(
          etikedo = tradukoj.unuoQe,
          valoro = qeVal,
          unuo = "${if (uzuBazo10) tradukiCiferojn("29.90", lingvo) else formatiOksaleAuxDekume(29.90, false, 2, lingvo)} ${tradukoj.unuoSekundo}",
          modifier = Modifier.weight(1f)
        )
        StatInsigno(
          etikedo = tradukoj.unuoHe,
          valoro = heVal,
          unuo = "${if (uzuBazo10) tradukiCiferojn("0.467", lingvo) else formatiOksaleAuxDekume(0.467, false, 3, lingvo)} ${tradukoj.unuoSekundo}",
          modifier = Modifier.weight(1f)
        )
      }

      // Superaj Castifeh2 Unuoj ( Shaqe, Sqe, She ) - montrataj nur se uzataj
      if (castifeh2Tempo.shaqe > 0L || castifeh2Tempo.sqe > 0L || castifeh2Tempo.she > 0L) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          StatInsigno(
            etikedo = tradukoj.unuoShaqe,
            valoro = shaqeVal,
            unuo = "x${if (uzuBazo10) tradukiCiferojn("1", lingvo) else vab6caja(1)} ( $frac1Str )",
            modifier = Modifier.weight(1f)
          )
          StatInsigno(
            etikedo = tradukoj.unuoSqe,
            valoro = sqeVal,
            unuo = "x${if (uzuBazo10) tradukiCiferojn("2", lingvo) else vab6caja(2)} ( $frac2Str )",
            modifier = Modifier.weight(1f)
          )
          StatInsigno(
            etikedo = tradukoj.unuoShe,
            valoro = sheVal,
            unuo = "x${if (uzuBazo10) tradukiCiferojn("3", lingvo) else vab6caja(3)} ( $frac3Str )",
            modifier = Modifier.weight(1f)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Suna Ciklo ( Sunleviĝo ekde 0, Sunsubiro & Taglongo por Haqe Qe He )
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        StatInsigno(
          etikedo = tradukoj.sunlevigo,
          valoro = hqhSunlevigoStr,
          unuo = if (uzuBazo10) tradukiCiferojn("0", lingvo) else vab6caja(0),
          modifier = Modifier.weight(1f)
        )
        StatInsigno(
          etikedo = tradukoj.sunsubiro,
          valoro = hqhSunsubiroStr,
          unuo = tradukoj.taglumo,
          modifier = Modifier.weight(1f)
        )
        StatInsigno(
          etikedo = tradukoj.tutaSunaTago,
          valoro = hqhTaglongoStr,
          unuo = "${tradukoj.unuoHaqe} • ${tradukoj.unuoQe}",
          modifier = Modifier.weight(1f)
        )
      }
    }

    // ⟪ Suna Pozicio ( Azimuto, Alto & Deklino en Radianoj ) ⟫
    KtashKarto {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = tradukoj.sunaPozicioTitolo,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = if (esplorLoko.id == "loc_cur" || esplorLoko.nomo == "Nuna Pozicio" || esplorLoko.nomo == tradukoj.nunaPozicio) tradukoj.nunaPozicio else esplorLoko.nomo,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        StatInsigno(
          etikedo = tradukoj.sunaAzimuto,
          valoro = formatiOksaleAuxDekume(sunaInformo.azimutoRad, uzuBazo10, 3, lingvo),
          unuo = tradukoj.unuoRadiano,
          modifier = Modifier.weight(1f)
        )
        StatInsigno(
          etikedo = tradukoj.sunaAlto,
          valoro = formatiOksaleAuxDekume(sunaInformo.altoRad, uzuBazo10, 3, lingvo),
          unuo = tradukoj.unuoRadiano,
          modifier = Modifier.weight(1f)
        )
        StatInsigno(
          etikedo = tradukoj.sunaDeklino,
          valoro = formatiOksaleAuxDekume(sunaInformo.deklinoRad, uzuBazo10, 3, lingvo),
          unuo = tradukoj.unuoRadiano,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // ⟪ Nuna Temperaturo ⟫
    KtashKarto {
      Text(
        text = tradukoj.temperaturo,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
      ) {
        Column {
          Text(
            text = "${formatiOksaleAuxDekume(hia, uzuBazo10, 2, lingvo)} ${tradukoj.unuoHia}",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "${tradukoj.unuoHia} ( ${if (uzuBazo10) tradukiCiferojn("0", lingvo) else vab6caja(0)} ${tradukoj.unuoHia} = ${if (uzuBazo10) tradukiCiferojn("-273.15", lingvo) else formatiOksaleAuxDekume(-273.15, false, 2, lingvo)} ${tradukoj.unuoCelsius} )",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
          )
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "${formatiOksaleAuxDekume(cels, uzuBazo10, 1, lingvo)} ${tradukoj.unuoCelsius}",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
          )
          Text(
            text = "${formatiOksaleAuxDekume(kelv, uzuBazo10, 1, lingvo)} ${tradukoj.unuoKelvin} ( ${formatiOksaleAuxDekume(fahr, uzuBazo10, 1, lingvo)} ${tradukoj.unuoFahrenheit} )",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
          )
        }
      }
    }

    // ⟪ Universala Temperatura Konvertilo ( Ĉiuj Direktoj ) ⟫
    KtashKarto {
      Text(
        text = tradukoj.konvertiTemperaturon,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(10.dp))

      OutlinedTextField(
        value = fontoValoroTeksto,
        onValueChange = { fontoValoroTeksto = it },
        shape = FormoButono,
        label = { Text(tradukoj.eniguTemperaturon, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = MaterialTheme.colorScheme.onSurface,
          unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
          focusedBorderColor = MaterialTheme.colorScheme.outline,
          unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Nombra enig-klavaro por la skribsistemo
      KtashCiferoKlavaro(
        nunaValoro = fontoValoroTeksto,
        onValoroSanĝita = { fontoValoroTeksto = it }
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        TemperaturoUnuo.values().forEach { unuo ->
          val elektita = fontoUnuo == unuo
          val unuoEtikedo = when (unuo) {
            TemperaturoUnuo.HIA -> tradukoj.unuoHia
            TemperaturoUnuo.KELVIN -> tradukoj.unuoKelvin
            TemperaturoUnuo.CELSIUS -> tradukoj.unuoCelsius
            TemperaturoUnuo.FAHRENHEIT -> tradukoj.unuoFahrenheit
          }
          val unuoInterago = remember { MutableInteractionSource() }
          val unuoPremita by unuoInterago.collectIsPressedAsState()
          val unuoFormo = animaciaButonFormo(unuoPremita || elektita, bazaStart = 16.dp, bazaEnd = 6.dp)
          val animUnuoBordo by animateColorAsState(
            targetValue = if (elektita) MaterialTheme.colorScheme.primary else if (unuoPremita) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.outline,
            animationSpec = tween(150),
            label = "unuoBordo"
          )
          val animUnuoFono by animateColorAsState(
            targetValue = if (elektita) MaterialTheme.colorScheme.primary else if (unuoPremita) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f) else n2taseButonKoloro(),
            animationSpec = tween(150),
            label = "unuoFono"
          )

          Surface(
            onClick = { fontoUnuo = unuo },
            interactionSource = unuoInterago,
            shape = unuoFormo,
            color = animUnuoFono,
            border = androidx.compose.foundation.BorderStroke(1.dp, animUnuoBordo),
            modifier = Modifier.weight(1f)
          ) {
            Text(
              text = unuoEtikedo,
              color = if (elektita) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
              fontSize = 11.sp,
              fontWeight = if (elektita) FontWeight.Bold else FontWeight.Medium,
              modifier = Modifier.padding(vertical = 8.dp),
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      val krudaValoro = analiziEnigonNombro(fontoValoroTeksto, uzuBazo10)
      val normaligitaKelvino = when (fontoUnuo) {
        TemperaturoUnuo.HIA -> hiaAlKelvino(krudaValoro)
        TemperaturoUnuo.KELVIN -> krudaValoro
        TemperaturoUnuo.CELSIUS -> celsiusAlKelvino(krudaValoro)
        TemperaturoUnuo.FAHRENHEIT -> celsiusAlKelvino((krudaValoro - 32.0) * 5.0 / 9.0)
      }

      val rezHia = kelvinoAlHia(normaligitaKelvino)
      val rezCels = kelvinoAlCelsius(normaligitaKelvino)
      val rezKelv = normaligitaKelvino
      val rezFahr = celsiusAlFahrenheit(rezCels)

      Spacer(modifier = Modifier.height(12.dp))

      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text(tradukoj.unuoHia, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
          Text(
            text = "${formatiOksaleAuxDekume(rezHia, uzuBazo10, 2, lingvo)} ${tradukoj.unuoHia}",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text(tradukoj.unuoKelvin, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
          Text(
            text = "${formatiOksaleAuxDekume(rezKelv, uzuBazo10, 2, lingvo)} ${tradukoj.unuoKelvin}",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp
          )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text(tradukoj.unuoCelsius, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
          Text(
            text = "${formatiOksaleAuxDekume(rezCels, uzuBazo10, 2, lingvo)} ${tradukoj.unuoCelsius}",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp
          )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text(tradukoj.unuoFahrenheit, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
          Text(
            text = "${formatiOksaleAuxDekume(rezFahr, uzuBazo10, 2, lingvo)} ${tradukoj.unuoFahrenheit}",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(100.dp))
  }

  // ⟪ Flosanta Dialogo por Aldoni Novan Lokon ( Sen Mallumo, Plena Larĝo ) ⟫
  if (montruAldoniLokonDialogon) {
    KtashFlosantaDialogo(
      titolo = "+ ${tradukoj.sercuLokon}",
      onFermi = { montruAldoniLokonDialogon = false }
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = novaLokoNomo,
          onValueChange = { novaLokoNomo = it },
          shape = FormoButono,
          label = { Text("Nomo / Label", color = MaterialTheme.colorScheme.onSurfaceVariant) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = novaLokoLat,
          onValueChange = { novaLokoLat = it },
          shape = FormoButono,
          label = { Text(tradukoj.latitudo, color = MaterialTheme.colorScheme.onSurfaceVariant) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = novaLokoLon,
          onValueChange = { novaLokoLon = it },
          shape = FormoButono,
          label = { Text(tradukoj.longitudo, color = MaterialTheme.colorScheme.onSurfaceVariant) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            onClick = { montruAldoniLokonDialogon = false },
            shape = FormoButono,
            color = n2taseButonKoloro(),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
          ) {
            Text(
              text = tradukoj.nuligi,
              color = MaterialTheme.colorScheme.onSurface,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Surface(
            onClick = {
              val lat = novaLokoLat.toDoubleOrNull() ?: 0.0
              val lon = novaLokoLon.toDoubleOrNull() ?: 0.0
              viewModel.aldoniLokon(novaLokoNomo, lat, lon)
              montruAldoniLokonDialogon = false
            },
            shape = FormoButono,
            color = MaterialTheme.colorScheme.primary,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
          ) {
            Text(
              text = tradukoj.iri,
              color = MaterialTheme.colorScheme.onPrimary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )
          }
        }
      }
    }
  }

  // ⟪ Flosanta Dialogo por Konfirmi Forigon de Loko ( Sen Mallumo, Plena Larĝo, Ruĝa Butono, Propra Tiparo ) ⟫
  lokoForigenda?.let { stacio ->
    KtashFlosantaKonfirmDialogo(
      titolo = tradukoj.forigi,
      mesagxo = "${tradukoj.konfirmiForigon}\n(${stacio.nomo})",
      konfirmiTeksto = tradukoj.forigi,
      nuligiTeksto = tradukoj.nuligi,
      ĉuForigo = true,
      onKonfirmi = {
        viewModel.forigiLokon(stacio)
        lokoForigenda = null
      },
      onNuligi = { lokoForigenda = null }
    )
  }
}
