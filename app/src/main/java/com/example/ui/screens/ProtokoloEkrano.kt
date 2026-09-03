package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.KtashTagoIdentigilo
import com.example.data.LokoLogEntity
import com.example.ktash.*
import com.example.ui.KtashViewModel
import com.example.ui.components.KtashFlosantaDialogo
import com.example.ui.components.KtashFlosantaKonfirmDialogo
import com.example.ui.components.KtashKarto
import com.example.ui.components.KtashSubKarto
import com.example.ui.components.N2taseButono
import com.example.ui.components.StatInsigno
import com.example.ui.components.animaciaButonFormo
import com.example.ui.components.n2taseButonKoloro
import com.example.ui.i18n.Lingvo
import com.example.ui.theme.*

// ≺⧼ Protokolo & Historio Ekrano 📜 ⧽≻

@Composable
fun ProtokoloEkrano(
  viewModel: KtashViewModel,
  modifier: Modifier = Modifier
) {
  val kunteksto = LocalContext.current
  val tondilaro = remember {
    try {
      kunteksto.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    } catch (_: Throwable) {
      null
    }
  }

  val filtritajProtokoloj by viewModel.filtritajProtokoloj.collectAsState()
  val distinctTagoj by viewModel.distinctTagoj.collectAsState()
  val elektitaTago by viewModel.elektitaTago.collectAsState()
  val uzuBazo10 by viewModel.uzuBazo10.collectAsState()
  val tradukoj by viewModel.tradukoj.collectAsState()
  val lingvo by viewModel.elektitaLingvo.collectAsState()

  var montruVakigiDialogon by remember { mutableStateOf(false) }
  var montruEksportDialogon by remember { mutableStateOf(false) }
  var montruImportDialogon by remember { mutableStateOf(false) }
  var eksportitaDosierNomo by remember { mutableStateOf("") }
  var eksportitaTeksto by remember { mutableStateOf("") }
  var importTeksto by remember { mutableStateOf("") }

  // ⟨ Dosiera Konservado Launcher ⟩
  val konserviDosieronLanĉilo = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.CreateDocument("text/plain")
  ) { uri: Uri? ->
    if (uri != null) {
      try {
        kunteksto.contentResolver.openOutputStream(uri)?.use { stream ->
          stream.write(eksportitaTeksto.toByteArray(Charsets.UTF_8))
        }
        Toast.makeText(kunteksto, tradukoj.sukceseEksportita, Toast.LENGTH_SHORT).show()
      } catch (_: Throwable) {
        Toast.makeText(kunteksto, tradukoj.eraroImportado, Toast.LENGTH_SHORT).show()
      }
    }
  }

  // ⟨ Dosiera Importado Launcher ⟩
  val malfermiDosieronLanĉilo = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.OpenDocument()
  ) { uri: Uri? ->
    if (uri != null) {
      try {
        val enhavo = kunteksto.contentResolver.openInputStream(uri)?.use { stream ->
          stream.bufferedReader().use { it.readText() }
        } ?: ""
        if (enhavo.isNotBlank()) {
          importTeksto = enhavo
          viewModel.importiProtokolojnTXT(
            teksto = enhavo,
            onSukceso = { nombro ->
              val nStr = if (uzuBazo10) tradukiCiferojn(nombro.toString(), lingvo) else vab6caja(nombro.toLong())
              Toast.makeText(kunteksto, "${tradukoj.sukceseImportita} ( $nStr )", Toast.LENGTH_SHORT).show()
              montruImportDialogon = false
            },
            onEraro = {
              Toast.makeText(kunteksto, tradukoj.eraroImportado, Toast.LENGTH_SHORT).show()
            }
          )
        }
      } catch (_: Throwable) {
        Toast.makeText(kunteksto, tradukoj.eraroImportado, Toast.LENGTH_SHORT).show()
      }
    }
  }

  val rubujoInterago = remember { MutableInteractionSource() }
  val rubujoPremita by rubujoInterago.collectIsPressedAsState()
  val eksportInterago = remember { MutableInteractionSource() }
  val eksportPremita by eksportInterago.collectIsPressedAsState()
  val importInterago = remember { MutableInteractionSource() }
  val importPremita by importInterago.collectIsPressedAsState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // ⟪ Kapo, Eksporto, Importo & Vakigo ⟫
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = tradukoj.protokoloTitolo,
          color = MaterialTheme.colorScheme.primary,
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "${if (uzuBazo10) tradukiCiferojn(filtritajProtokoloj.size.toString(), lingvo) else vab6caja(filtritajProtokoloj.size.toLong())} ${tradukoj.registritajPunktoj}",
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 12.sp
        )
      }

      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        val eksportBordo by animateColorAsState(
          targetValue = if (eksportPremita) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.outline,
          animationSpec = tween(150),
          label = "eksportBordo"
        )
        val eksportFono by animateColorAsState(
          targetValue = if (eksportPremita) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f) else n2taseButonKoloro(),
          animationSpec = tween(150),
          label = "eksportFono"
        )

        // ⟨ Eksporti Butono ⟩
        Surface(
          onClick = {
            viewModel.eksportiProtokolojnTXT { dosierNomo, teksto ->
              eksportitaDosierNomo = dosierNomo
              eksportitaTeksto = teksto
              try {
                val tranĉo = ClipData.newPlainText(dosierNomo, teksto)
                tondilaro?.setPrimaryClip(tranĉo)
                Toast.makeText(kunteksto, tradukoj.sukceseEksportita, Toast.LENGTH_SHORT).show()
              } catch (_: Throwable) {}
              montruEksportDialogon = true
            }
          },
          interactionSource = eksportInterago,
          shape = animaciaButonFormo(eksportPremita),
          color = eksportFono,
          border = androidx.compose.foundation.BorderStroke(1.dp, eksportBordo)
        ) {
          Box(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp), contentAlignment = Alignment.Center) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              Icon(Icons.Default.FileDownload, contentDescription = tradukoj.eksportiProtokolojn, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
              Text(tradukoj.txt, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
          }
        }

        val importBordo by animateColorAsState(
          targetValue = if (importPremita) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.outline,
          animationSpec = tween(150),
          label = "importBordo"
        )
        val importFono by animateColorAsState(
          targetValue = if (importPremita) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f) else n2taseButonKoloro(),
          animationSpec = tween(150),
          label = "importFono"
        )

        // ⟨ Importi Butono ⟩
        Surface(
          onClick = {
            importTeksto = ""
            montruImportDialogon = true
          },
          interactionSource = importInterago,
          shape = animaciaButonFormo(importPremita),
          color = importFono,
          border = androidx.compose.foundation.BorderStroke(1.dp, importBordo)
        ) {
          Box(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp), contentAlignment = Alignment.Center) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              Icon(Icons.Default.FileUpload, contentDescription = tradukoj.importiProtokolojn, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
              Text(tradukoj.txt, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
            }
          }
        }

        // ⟨ Vakigi Butono ⟩
        if (filtritajProtokoloj.isNotEmpty()) {
          val rubujoBordo by animateColorAsState(
            targetValue = if (rubujoPremita) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.outline,
            animationSpec = tween(150),
            label = "rubujoBordo"
          )
          val rubujoFono by animateColorAsState(
            targetValue = if (rubujoPremita) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f) else n2taseButonKoloro(),
            animationSpec = tween(150),
            label = "rubujoFono"
          )

          Surface(
            onClick = { montruVakigiDialogon = true },
            interactionSource = rubujoInterago,
            shape = animaciaButonFormo(rubujoPremita, bazaStart = 18.dp, bazaEnd = 10.dp, piloRadius = 26.dp),
            color = rubujoFono,
            border = androidx.compose.foundation.BorderStroke(1.dp, rubujoBordo)
          ) {
            Box(modifier = Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
              Icon(Icons.Default.DeleteSweep, contentDescription = tradukoj.vakigiProtokolojn, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(22.dp))
            }
          }
        }
      }
    }

    // ⟪ Taga Filtril-stango ⟫
    LazyRow(
      modifier = Modifier
        .fillMaxWidth()
        .clip(FormoSkroloMaskaHorizontala),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      item {
        val elektita = elektitaTago == null
        val eroInterago = remember { MutableInteractionSource() }
        val eroPremita by eroInterago.collectIsPressedAsState()
        val animBordo by animateColorAsState(
          targetValue = if (elektita) MaterialTheme.colorScheme.primary else if (eroPremita) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.outline,
          animationSpec = tween(150),
          label = "eroBordo"
        )
        val animFono by animateColorAsState(
          targetValue = if (elektita) MaterialTheme.colorScheme.primary else if (eroPremita) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f) else n2taseButonKoloro(),
          animationSpec = tween(150),
          label = "eroFono"
        )
        Surface(
          onClick = { viewModel.elektiTagonPorFiltro(null) },
          interactionSource = eroInterago,
          shape = animaciaButonFormo(eroPremita || elektita, bazaStart = 16.dp, bazaEnd = 6.dp),
          color = animFono,
          border = androidx.compose.foundation.BorderStroke(1.dp, animBordo)
        ) {
          Text(
            text = tradukoj.ciujTagoj,
            color = if (elektita) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
          )
        }
      }

      items(distinctTagoj) { tago ->
        val elektita = elektitaTago == tago
        val eroInterago = remember { MutableInteractionSource() }
        val eroPremita by eroInterago.collectIsPressedAsState()
        val animBordo by animateColorAsState(
          targetValue = if (elektita) MaterialTheme.colorScheme.primary else if (eroPremita) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.outline,
          animationSpec = tween(150),
          label = "tagoBordo"
        )
        val animFono by animateColorAsState(
          targetValue = if (elektita) MaterialTheme.colorScheme.primary else if (eroPremita) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f) else n2taseButonKoloro(),
          animationSpec = tween(150),
          label = "tagoFono"
        )
        Surface(
          onClick = { viewModel.elektiTagonPorFiltro(tago) },
          interactionSource = eroInterago,
          shape = animaciaButonFormo(eroPremita || elektita, bazaStart = 16.dp, bazaEnd = 6.dp),
          color = animFono,
          border = androidx.compose.foundation.BorderStroke(1.dp, animBordo)
        ) {
          Text(
            text = tago.alTeksto(uzuBazo10, lingvo),
            color = if (elektita) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
          )
        }
      }
    }

    // ⟪ Protokoloj Listo ⟫
    if (filtritajProtokoloj.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            Icons.Default.ExploreOff,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(48.dp)
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = tradukoj.neniuProtokolo,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
          )
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .clip(FormoSkroloMaska),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        itemsIndexed(filtritajProtokoloj, key = { _, ero -> ero.id }) { indekso, ero ->
          val antauxaEro = filtritajProtokoloj.getOrNull(indekso + 1)
          ProtokolEroKarto(
            ero = ero,
            antauxaEro = antauxaEro,
            uzuBazo10 = uzuBazo10,
            tradukoj = tradukoj,
            lingvo = lingvo,
            onForigi = { viewModel.forigiProtokolon(ero.id) }
          )
        }
      }
    }
  }

  // ⟪ Flosanta Dialogo por Eksporto ⟫
  if (montruEksportDialogon) {
    KtashFlosantaDialogo(
      titolo = tradukoj.eksportiProtokolojn,
      onFermi = { montruEksportDialogon = false }
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (eksportitaDosierNomo.isNotEmpty()) {
          Text(
            text = eksportitaDosierNomo,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
        }
        Text(
          text = tradukoj.sukceseEksportita,
          color = MaterialTheme.colorScheme.onSurface,
          fontSize = 12.sp
        )
        OutlinedTextField(
          value = eksportitaTeksto,
          onValueChange = {},
          readOnly = true,
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 220.dp),
          textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
          shape = FormoKarto
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedButton(
            onClick = {
              val dosierNomo = if (eksportitaDosierNomo.isNotBlank()) eksportitaDosierNomo else "ktash_protokolo.txt"
              konserviDosieronLanĉilo.launch(dosierNomo)
            },
            shape = FormoButono
          ) {
            Icon(Icons.Default.SaveAlt, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Dosiero", fontWeight = FontWeight.Bold)
          }

          Spacer(modifier = Modifier.width(8.dp))

          Surface(
            onClick = { montruEksportDialogon = false },
            shape = FormoButono,
            color = MaterialTheme.colorScheme.primary,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
          ) {
            Text(
              text = tradukoj.fermi,
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

  // ⟪ Flosanta Dialogo por Importo ⟫
  if (montruImportDialogon) {
    KtashFlosantaDialogo(
      titolo = tradukoj.importiProtokolojn,
      onFermi = { montruImportDialogon = false }
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = tradukoj.eniguJsonPayload,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
          )
          Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            TextButton(
              onClick = { malfermiDosieronLanĉilo.launch(arrayOf("text/plain", "*/*")) }
            ) {
              Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(2.dp))
              Text("Dosiero", fontSize = 12.sp)
            }
            TextButton(
              onClick = {
                try {
                  val algluo = tondilaro?.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
                  if (algluo.isNotBlank()) {
                    importTeksto = algluo
                  }
                } catch (_: Throwable) {}
              }
            ) {
              Text(tradukoj.alglui, fontSize = 12.sp)
            }
          }
        }
        OutlinedTextField(
          value = importTeksto,
          onValueChange = { importTeksto = it },
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp, max = 220.dp),
          placeholder = { Text(tradukoj.eniguJsonPayload, fontSize = 11.sp) },
          textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
          shape = FormoKarto
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          N2taseButono(onClick = { montruImportDialogon = false }) {
            Text(tradukoj.nuligi, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }

          Spacer(modifier = Modifier.width(8.dp))

          Surface(
            onClick = {
              viewModel.importiProtokolojnTXT(
                teksto = importTeksto,
                onSukceso = { nombro ->
                  val nStr = if (uzuBazo10) tradukiCiferojn(nombro.toString(), lingvo) else vab6caja(nombro.toLong())
                  Toast.makeText(kunteksto, "${tradukoj.sukceseImportita} ( $nStr )", Toast.LENGTH_SHORT).show()
                  montruImportDialogon = false
                },
                onEraro = {
                  Toast.makeText(kunteksto, tradukoj.eraroImportado, Toast.LENGTH_SHORT).show()
                }
              )
            },
            shape = FormoButono,
            color = MaterialTheme.colorScheme.primary,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
          ) {
            Text(
              text = tradukoj.importiProtokolojn,
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

  // ⟪ Flosanta Dialogo por Vakigi Protokolojn ⟫
  if (montruVakigiDialogon) {
    KtashFlosantaKonfirmDialogo(
      titolo = tradukoj.vakigiProtokolojn,
      mesagxo = tradukoj.konfirmiVakigon,
      konfirmiTeksto = tradukoj.forigi,
      nuligiTeksto = tradukoj.nuligi,
      ĉuForigo = true,
      onKonfirmi = {
        viewModel.vakigiCiujnProtokolojn()
        montruVakigiDialogon = false
      },
      onNuligi = { montruVakigiDialogon = false }
    )
  }
}

@Composable
fun ProtokolEroKarto(
  ero: LokoLogEntity,
  antauxaEro: LokoLogEntity? = null,
  uzuBazo10: Boolean,
  tradukoj: com.example.ui.i18n.TradukTekstoj,
  lingvo: Lingvo = Lingvo.ESPERANTO,
  onForigi: () -> Unit
) {
  val dato = remember(ero.tempoMilisekundoj) { cax2lStafl2(ero.tempoMilisekundoj) }
  val tempo = remember(ero.tempoMilisekundoj) { castifeh2(ero.tempoMilisekundoj) }
  val forigiInterago = remember { MutableInteractionSource() }
  val forigiPremita by forigiInterago.collectIsPressedAsState()

  // ⟨ Mezuri kiom oni moviĝis ekde la lasta registrita protokolo ⟩
  val movitaPeu = remember(ero, antauxaEro) {
    if (antauxaEro != null) {
      val distMetroj = kalkuliDistancoMetroj(
        antauxaEro.latitudo,
        antauxaEro.longitudo,
        ero.latitudo,
        ero.longitudo
      )
      metrojAlPeu(distMetroj)
    } else if (ero.distancoDeAntauaPeu > 0.0) {
      ero.distancoDeAntauaPeu
    } else {
      0.0
    }
  }

  KtashKarto {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.Top
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = ero.ksakaNomo,
          color = MaterialTheme.colorScheme.onSurface,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "${tradukoj.latina} - ${ero.latinaNomo}",
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 12.sp
        )
      }

      val animForigiBordo by animateColorAsState(
        targetValue = if (forigiPremita) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.outline,
        animationSpec = tween(150),
        label = "forigiBordo"
      )
      val animForigiFono by animateColorAsState(
        targetValue = if (forigiPremita) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f) else n2taseButonKoloro(),
        animationSpec = tween(150),
        label = "forigiFono"
      )

      Surface(
        onClick = onForigi,
        interactionSource = forigiInterago,
        shape = animaciaButonFormo(forigiPremita, bazaStart = 16.dp, bazaEnd = 8.dp, piloRadius = 22.dp),
        color = animForigiFono,
        border = androidx.compose.foundation.BorderStroke(1.dp, animForigiBordo)
      ) {
        Box(modifier = Modifier.padding(6.dp), contentAlignment = Alignment.Center) {
          Icon(Icons.Default.DeleteOutline, contentDescription = tradukoj.forigi, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = "${dato.alTeksto(uzuBazo10, lingvo)}  •  ${tempo.alTeksto(uzuBazo10, lingvo)}",
      color = MaterialTheme.colorScheme.primary,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold
    )

    val vertikalo = remember(ero) { ero.akiriVertikalan() }
    val vertTeksto = if (uzuBazo10) vertikalo.alDekumaTeksto(lingvo) else vertikalo.alOksalaTeksto()
    Text(
      text = "${tradukoj.vertikalaLokoTitolo} — $vertTeksto",
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
        etikedo = tradukoj.unuoPeu,
        valoro = formatiOksaleAuxDekume(movitaPeu, uzuBazo10, 2, lingvo),
        unuo = tradukoj.unuoPeu,
        modifier = Modifier.weight(1f)
      )
      ero.temperaturoKelvino?.let { kelv ->
        StatInsigno(
          etikedo = tradukoj.unuoHia,
          valoro = formatiOksaleAuxDekume(kelvinoAlHia(kelv), uzuBazo10, 1, lingvo),
          unuo = tradukoj.unuoHia,
          modifier = Modifier.weight(1f)
        )
      }
    }

    ero.noto?.let { noto ->
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "${tradukoj.notoEkzemplo} - $noto",
        color = MaterialTheme.colorScheme.primary,
        fontSize = 12.sp
      )
    }
  }
}

