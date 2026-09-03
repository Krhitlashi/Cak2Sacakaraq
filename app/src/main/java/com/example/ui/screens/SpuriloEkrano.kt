package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktash.*
import com.example.ui.KtashViewModel
import com.example.ui.components.KsakaGlifoInsigno
import com.example.ui.components.KtashFlosantaDialogo
import com.example.ui.components.KtashKarto
import com.example.ui.components.KtashSubKarto
import com.example.ui.components.KtashŜaltilo
import com.example.ui.components.N2taseButono
import com.example.ui.components.StatInsigno
import com.example.ui.components.animaciaButonFormo
import com.example.ui.components.n2taseButonKoloro
import com.example.ui.theme.*

// ≺⧼ Spurilo Ekrano 🧭 ⧽≻

@Composable
fun SpuriloEkrano(
  viewModel: KtashViewModel,
  modifier: Modifier = Modifier
) {
  val nunaLoko by viewModel.lokoManagero.nunaLoko.collectAsState()
  val autoSpuradoAktiva by viewModel.lokoManagero.autoSpuradoAktiva.collectAsState()
  val spuraIntervaloMs by viewModel.lokoManagero.spuraIntervaloMs.collectAsState()
  val lastaRegistrita by viewModel.lokoManagero.lastaRegistrita.collectAsState()
  val uzuBazo10 by viewModel.uzuBazo10.collectAsState()
  val tradukoj by viewModel.tradukoj.collectAsState()
  val lingvo by viewModel.elektitaLingvo.collectAsState()
  val nunaTempoMs by viewModel.nunaTempoMs.collectAsState()
  val nunaTemperaturoCelsius by viewModel.nunaTemperaturoCelsius.collectAsState()
  val tutaPasoj by viewModel.tutaPasoj.collectAsState()
  val hodiauajPasoj by viewModel.hodiauajPasoj.collectAsState()
  val personDistancoPeu by viewModel.personDistancoPeu.collectAsState()
  val pasRapido by viewModel.pasRapido.collectAsState()
  val estasMoviĝanta by viewModel.estasMoviĝanta.collectAsState()

  val kadro = remember(nunaLoko.latitudo, nunaLoko.longitudo) {
    akiriKadrajnKoordinatojn(nunaLoko.latitudo, nunaLoko.longitudo)
  }
  val nomoj = remember(kadro) {
    akiriNomojn(kadro)
  }
  val sunaInformo = remember(nunaLoko.latitudo, nunaLoko.longitudo, nunaTempoMs) {
    kalkuliSunon(nunaLoko.latitudo, nunaLoko.longitudo, nunaTempoMs)
  }

  var montruNotoDialogon by remember { mutableStateOf(false) }
  var notoTeksto by remember { mutableStateOf("") }

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
    // ⟪ Ksaka Nomo Insigno ( Uzas Proprajn Tiparojn Sen Monospace ) ⟫
    KsakaGlifoInsigno(
      ksakaNomo = nomoj.ksaka,
      latinaNomo = nomoj.latina,
      chmuahNomo = nomoj.chmuah,
      vivaTeksto = tradukoj.vivaGps,
      latinaEtikedo = tradukoj.latina,
      chmuahEtikedo = tradukoj.chmuah,
      ksakaEtikedo = tradukoj.ksakaKoordinato
    )

    // ⟪ Kadraj Koordinatoj Karto ⟫
    KtashKarto {
      Text(
        text = tradukoj.kadrajKoordinatoj,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = if (uzuBazo10) kadro.alDekumaTeksto(lingvo) else kadro.alOksalaTeksto(),
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 26.sp
      )

      Spacer(modifier = Modifier.height(12.dp))

      val vertikalaLoko = remember(nunaLoko.alteco) {
        akiriVertikalanLokon(nunaLoko.alteco)
      }

      Text(
        text = tradukoj.vertikalaLokoTitolo,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = if (uzuBazo10) vertikalaLoko.alDekumaTeksto(lingvo) else vertikalaLoko.alOksalaTeksto(),
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 26.sp
      )

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text(tradukoj.latitudo, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
          Text(
            text = "${formatiOksaleAuxDekume(nunaLoko.latitudo, uzuBazo10, 5, lingvo)}°",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
        Column {
          Text(tradukoj.longitudo, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
          Text(
            text = "${formatiOksaleAuxDekume(nunaLoko.longitudo, uzuBazo10, 5, lingvo)}°",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
        Column {
          Text(tradukoj.alteco, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
          val altecoPeu = remember(nunaLoko.alteco) { metrojAlPeu(nunaLoko.alteco) }
          Text(
            text = "${formatiOksaleAuxDekume(altecoPeu, uzuBazo10, 1, lingvo)} ${tradukoj.unuoPeu}",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
        Column {
          Text(tradukoj.fonto, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
          val fontoTeksto = if (nunaLoko.fonto.equals("GPS", ignoreCase = true)) {
            tradukoj.gps
          } else if (nunaLoko.fonto.equals("Defaŭlta", ignoreCase = true) || nunaLoko.fonto.equals("Default", ignoreCase = true)) {
            tradukoj.defaultaLoko
          } else {
            nunaLoko.fonto
          }
          Text(
            text = fontoTeksto,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    }

    // ⟪ Aŭtomata Spurado ⟫
    KtashKarto {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = tradukoj.kontinuaSpurado,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
          val fojojTage = 86400000.0 / spuraIntervaloMs
          val qeValoro = (spuraIntervaloMs / 1000.0) / com.example.ktash.QE_L6VEM2
          val fojojStr = formatiOksaleAuxDekume(fojojTage, uzuBazo10, 0, lingvo)
          val qeStr = formatiOksaleAuxDekume(qeValoro, uzuBazo10, 0, lingvo)
          Text(
            text = "${tradukoj.kontinuaSpuradoPriskribo} ( ~$fojojStr / ${tradukoj.tago} • ~$qeStr ${tradukoj.unuoQe} )",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
          )
        }

        KtashŜaltilo(
          checked = autoSpuradoAktiva,
          onCheckedChange = { viewModel.baskuliAutoSpuradon(it) }
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Intervalo-elektilo kun animaciaj n2tase butonoj
      Text(
        text = tradukoj.specifitaIntervalo,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium
      )
      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        val opcioj = listOf(5400000L, 2700000L, 1350000L, 675000L, 337500L)

        opcioj.forEach { ms ->
          val elektita = spuraIntervaloMs == ms
          val fojoj = 86400000.0 / ms
          val qe = (ms / 1000.0) / com.example.ktash.QE_L6VEM2
          val fojojTeksto = formatiOksaleAuxDekume(fojoj, uzuBazo10, 0, lingvo)
          val qeTeksto = formatiOksaleAuxDekume(qe, uzuBazo10, 0, lingvo)
          val nomo = "${fojojTeksto}x\n( ${qeTeksto} ${tradukoj.unuoQe} )"

          val interago = remember { MutableInteractionSource() }
          val premita by interago.collectIsPressedAsState()
          val formo = animaciaButonFormo(premita || elektita, bazaStart = 16.dp, bazaEnd = 6.dp)
          val animBordo by animateColorAsState(
            targetValue = if (elektita) MaterialTheme.colorScheme.primary else if (premita) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.outline,
            animationSpec = tween(150),
            label = "intervaloBordo"
          )
          val animFono by animateColorAsState(
            targetValue = if (elektita) MaterialTheme.colorScheme.primary else if (premita) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f) else n2taseButonKoloro(),
            animationSpec = tween(150),
            label = "intervaloFono"
          )

          Surface(
            onClick = { viewModel.agordiSpuranIntervalon(ms) },
            interactionSource = interago,
            shape = formo,
            color = animFono,
            border = androidx.compose.foundation.BorderStroke(1.dp, animBordo),
            modifier = Modifier.weight(1f)
          ) {
            Text(
              text = nomo,
              color = if (elektita) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
              fontSize = 9.sp,
              fontWeight = if (elektita) FontWeight.Bold else FontWeight.Medium,
              modifier = Modifier.padding(vertical = 6.dp, horizontal = 1.dp),
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Butono por manaĝe registri kun glata animacio
      val registriInterago = remember { MutableInteractionSource() }
      val registriPremita by registriInterago.collectIsPressedAsState()
      val registriFormo = animaciaButonFormo(registriPremita, bazaStart = 24.dp, bazaEnd = 8.dp, piloRadius = 32.dp)
      val animRegistriBordo by animateColorAsState(
        targetValue = if (registriPremita) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.outline,
        animationSpec = tween(150),
        label = "registriBordo"
      )
      val animRegistriFono by animateColorAsState(
        targetValue = if (registriPremita) MaterialTheme.colorScheme.primary.copy(alpha = 0.85f) else MaterialTheme.colorScheme.primary,
        animationSpec = tween(150),
        label = "registriFono"
      )

      Surface(
        onClick = { montruNotoDialogon = true },
        interactionSource = registriInterago,
        modifier = Modifier.fillMaxWidth(),
        shape = registriFormo,
        color = animRegistriFono,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        border = androidx.compose.foundation.BorderStroke(1.dp, animRegistriBordo)
      ) {
        Row(
          modifier = Modifier.padding(vertical = 12.dp),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.AddLocationAlt, contentDescription = null)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = tradukoj.registriNunanLokon,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    // ⟪ Lasta Registrita Punkto ( Preferante Proprajn Sistemojn ) ⟫
    lastaRegistrita?.let { lasta ->
      KtashKarto {
        Text(
          text = tradukoj.lastaRegistritaPozicio,
          color = MaterialTheme.colorScheme.primary,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = lasta.ksakaNomo,
          color = MaterialTheme.colorScheme.onSurface,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )

        val lastaVert = remember(lasta) { lasta.akiriVertikalan() }
        Text(
          text = "${tradukoj.vertikalaLokoTitolo} — ${if (uzuBazo10) lastaVert.alDekumaTeksto(lingvo) else lastaVert.alOksalaTeksto()}",
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          StatInsigno(
            etikedo = tradukoj.distanco,
            valoro = formatiOksaleAuxDekume(lasta.distancoDeAntauaPeu, uzuBazo10, 2, lingvo),
            unuo = "P0 ( ${tradukoj.unuoPeu} )",
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // ⟪ Person-Distanca & Paŝa Spurilo Karto ( Kiel Telefono Spuras Paŝojn ) ⟫
    KtashKarto {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.DirectionsWalk,
            contentDescription = null,
            tint = if (estasMoviĝanta) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = tradukoj.personDistanco,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
        }

        if (estasMoviĝanta) {
          Text(
            text = "● ${tradukoj.vivaGps}",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Ĉefa Distanco en Peu
      Text(
        text = "${formatiOksaleAuxDekume(personDistancoPeu, uzuBazo10, 2, lingvo)} ${tradukoj.unuoPeu}",
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Paŝoj kaj Rapido laŭ qe
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        StatInsigno(
          etikedo = tradukoj.pasoj,
          valoro = if (uzuBazo10) tradukiCiferojn(tutaPasoj.toString(), lingvo) else vab6caja(tutaPasoj),
          unuo = tradukoj.pasoj,
          modifier = Modifier.weight(1f)
        )
        StatInsigno(
          etikedo = tradukoj.pasRapido,
          valoro = formatiOksaleAuxDekume(pasRapido, uzuBazo10, 1, lingvo),
          unuo = "/ ${tradukoj.unuoQe}",
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Butono por Restarigi ( Kun Norma n2tase Butona Stilo )
      N2taseButono(
        onClick = { viewModel.restarigiPasojn() },
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(
          imageVector = Icons.Default.Refresh,
          contentDescription = null,
          modifier = Modifier.size(16.dp),
          tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = tradukoj.restarigiPasojn,
          color = MaterialTheme.colorScheme.onSurface,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold
        )
      }
    }

    // ⟪ Rapida Vetero & Suno ⟫
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      val cels = nunaTemperaturoCelsius
      val hia = celsiusAlHia(cels)
      val kelv = celsiusAlKelvino(cels)

      KtashKarto(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Thermostat, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
          Spacer(modifier = Modifier.width(6.dp))
          Text(tradukoj.temperaturo, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "${formatiOksaleAuxDekume(hia, uzuBazo10, 2, lingvo)} ${tradukoj.unuoHia}",
          color = MaterialTheme.colorScheme.onSurface,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "${formatiOksaleAuxDekume(cels, uzuBazo10, 1, lingvo)} ${tradukoj.unuoCelsius} ( ${formatiOksaleAuxDekume(kelv, uzuBazo10, 1, lingvo)} ${tradukoj.unuoKelvin} )",
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 11.sp
        )
      }

      KtashKarto(modifier = Modifier.weight(1f)) {
        val (k1, k2, k3) = sunaInformo.bazo64Horlogo
        val k1Val = if (uzuBazo10) tradukiCiferojn(k1.toString(), lingvo) else vab6caja(k1.toLong())
        val k2Val = if (uzuBazo10) tradukiCiferojn(k2.toString(), lingvo) else vab6caja(k2.toLong())
        val k3Val = if (uzuBazo10) tradukiCiferojn(k3.toString(), lingvo) else vab6caja(k3.toLong())

        val b64Sub = formatiBazo64Horlogo(sunaInformo.bazo64Sunsubiro, uzuBazo10, lingvo)

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.WbSunny, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
          Spacer(modifier = Modifier.width(6.dp))
          Text(tradukoj.taglumo, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "$k1Val • $k2Val • $k3Val",
          color = MaterialTheme.colorScheme.onSurface,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "${tradukoj.sunsubiro} $b64Sub ( ${formatiTaglumon(sunaInformo.taglumoProgreso, uzuBazo10, lingvo)} )",
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 11.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(100.dp))
  }

  // ⟪ Flosanta Dialogo por aldoni noton ⟫
  if (montruNotoDialogon) {
    KtashFlosantaDialogo(
      titolo = tradukoj.registriNunanLokon,
      onFermi = { montruNotoDialogon = false }
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(tradukoj.notoMalnepra, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        OutlinedTextField(
          value = notoTeksto,
          onValueChange = { notoTeksto = it },
          shape = FormoButono,
          placeholder = { Text(tradukoj.notoEkzemplo, color = MaterialTheme.colorScheme.onSurfaceVariant) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedBorderColor = MaterialTheme.colorScheme.outline,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
          ),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            onClick = { montruNotoDialogon = false },
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
              viewModel.registriNunanLokon(notoTeksto.ifBlank { null })
              notoTeksto = ""
              montruNotoDialogon = false
            },
            shape = FormoButono,
            color = MaterialTheme.colorScheme.primary,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
          ) {
            Text(
              text = tradukoj.registriNunanLokon,
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
}
