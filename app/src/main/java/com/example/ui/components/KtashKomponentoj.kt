package com.example.ui.components

import android.os.Build
import android.view.WindowManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ktash.*
import com.example.ui.NavigaLangeto
import com.example.ui.i18n.Lingvo
import com.example.ui.i18n.TradukTekstoj
import com.example.ui.theme.*

// ≺⧼ Komponantoj 🧩 ⧽≻

// ⟪ Animacia Buton-Formo al Pilo ( Circlo / Pilo ) dum Premado ⟫
@Composable
fun animaciaButonFormo(
  premita: Boolean,
  bazaStart: Dp = 20.dp,
  bazaEnd: Dp = 8.dp,
  piloRadius: Dp = 64.dp
): Shape {
  val topStart by animateDpAsState(
    targetValue = if (premita) piloRadius else bazaStart,
    animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium),
    label = "ts"
  )
  val topEnd by animateDpAsState(
    targetValue = if (premita) piloRadius else bazaEnd,
    animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium),
    label = "te"
  )
  val bottomEnd by animateDpAsState(
    targetValue = if (premita) piloRadius else bazaStart,
    animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium),
    label = "be"
  )
  val bottomStart by animateDpAsState(
    targetValue = if (premita) piloRadius else bazaEnd,
    animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium),
    label = "bs"
  )
  return RoundedCornerShape(
    topStart = topStart,
    topEnd = topEnd,
    bottomEnd = bottomEnd,
    bottomStart = bottomStart
  )
}

// ⟪ n2tase Malklara Sistemo & Tavoligita Fono sen Gradiento ( CSS --ច្ហិនី blur(32px) ) ⟫
@Composable
fun n2taseFonKoloroj(): Pair<Color, Color> {
  val fono = MaterialTheme.colorScheme.background
  val estasMalhela = fono == KoloroFonoMalhela || fono.red < 0.5f
  // CSS: --ខេលេសៃច្ហិ: #000000a0 (malhela) / #ffffffa0 (hela)
  val cjhini = if (estasMalhela) Color(0xA0000000) else Color(0xA0FFFFFF)
  // CSS: --តានេកខេលេ: #ffffff10 (malhela) / #00000008 (hela)
  val tanekKele = if (estasMalhela) Color(0x10FFFFFF) else Color(0x08000000)
  return Pair(cjhini, tanekKele)
}

@Composable
fun n2taseBordaKoloro(): Color {
  val fono = MaterialTheme.colorScheme.background
  val estasMalhela = fono == KoloroFonoMalhela || fono.red < 0.5f
  // CSS: --សាកព៏: 1px solid var(--តានេក) kie --តានេក estas #ffffff18 (malhela) / #00000010 (hela)
  return if (estasMalhela) Color(0x18FFFFFF) else Color(0x10000000)
}

@Composable
fun n2taseButonKoloro(): Color {
  val fono = MaterialTheme.colorScheme.background
  val estasMalhela = fono == KoloroFonoMalhela || fono.red < 0.5f
  return if (estasMalhela) KoloroKartoMalhela else KoloroKartoHela
}

@Composable
fun n2taseSolidaKoloro(): Color {
  val (cjhini, tanekKele) = n2taseFonKoloroj()
  return tanekKele.compositeOver(cjhini)
}

@Composable
fun n2taseFonKoloro(): Color {
  return n2taseSolidaKoloro()
}

@Composable
fun Modifier.n2taseFono(
  shape: Shape = FormoKartoGranda,
  bordaKoloro: Color? = null,
  hazeMalklareco: Dp = 24.dp
): Modifier {
  val uzataBordo = bordaKoloro ?: n2taseBordaKoloro()
  val (cjhini, tanekKele) = n2taseFonKoloroj()

  return this
    .clip(shape)
    .background(cjhini)
    .background(tanekKele)
    .border(1.dp, uzataBordo, shape)
}

// ⟪ n2tase Flosanta Karto kun Malklareco ( Blur Backdrop ) ⟫
@Composable
fun N2taseKarto(
  modifier: Modifier = Modifier,
  shape: Shape = FormoKartoGranda,
  bordaKoloro: Color? = null,
  hazeMalklareco: Dp = 24.dp,
  enhavo: @Composable ColumnScope.() -> Unit
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .n2taseFono(shape = shape, bordaKoloro = bordaKoloro, hazeMalklareco = hazeMalklareco)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 12.dp),
      content = enhavo
    )
  }
}

// ⟪ Standarta Karto sur Fono ( --តានេកខេលេ ) ⟫
@Composable
fun KtashKarto(
  modifier: Modifier = Modifier,
  shape: Shape = FormoKarto,
  fonKoloro: Color? = null,
  bordaKoloro: Color? = null,
  enhavo: @Composable ColumnScope.() -> Unit
) {
  val uzataFono = fonKoloro ?: MaterialTheme.colorScheme.surface
  val uzataBordo = bordaKoloro ?: MaterialTheme.colorScheme.outline

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .border(1.dp, uzataBordo, shape),
    shape = shape,
    color = uzataFono,
    contentColor = MaterialTheme.colorScheme.onSurface
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      content = enhavo
    )
  }
}

// ⟪ Subkarto / Interna Karto ( --តានេកខេលេ ) ⟫
@Composable
fun KtashSubKarto(
  modifier: Modifier = Modifier,
  shape: Shape = FormoSubKarto,
  enhavo: @Composable ColumnScope.() -> Unit
) {
  Surface(
    modifier = modifier
      .fillMaxWidth()
      .border(1.dp, MaterialTheme.colorScheme.outline, shape),
    shape = shape,
    color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor = MaterialTheme.colorScheme.onSurface
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      content = enhavo
    )
  }
}

// ⟪ Norma n2tase Butono ( Asimetria Angulo, Animacias al Pilo ចិង, Prem-Transira Koloro & Bordo ) ⟫
@Composable
fun N2taseButono(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  shape: Shape? = null,
  fonKoloro: Color? = null,
  bordaKoloro: Color? = null,
  contentPadding: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
  enhavo: @Composable RowScope.() -> Unit
) {
  val fontoInterago = remember { MutableInteractionSource() }
  val premita by fontoInterago.collectIsPressedAsState()
  val uzataFormo = shape ?: animaciaButonFormo(premita)

  val celBordo = if (premita) {
    MaterialTheme.colorScheme.outlineVariant
  } else {
    bordaKoloro ?: n2taseBordaKoloro()
  }
  val animBordo by animateColorAsState(
    targetValue = celBordo,
    animationSpec = tween(durationMillis = 150),
    label = "butonBordo"
  )

  val celFono = when {
    fonKoloro != null -> if (premita) fonKoloro.copy(alpha = 0.8f) else fonKoloro
    premita -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
    else -> Color.Transparent
  }
  val animFono by animateColorAsState(
    targetValue = celFono,
    animationSpec = tween(durationMillis = 150),
    label = "butonFono"
  )

  Box(
    modifier = modifier
      .clip(uzataFormo)
      .then(
        if (fonKoloro != null) {
          Modifier
            .background(animFono)
            .border(1.dp, animBordo, uzataFormo)
        } else {
          Modifier
            .n2taseFono(shape = uzataFormo, bordaKoloro = animBordo)
            .background(animFono)
        }
      )
      .clickable(
        interactionSource = fontoInterago,
        indication = ripple(),
        onClick = onClick
      ),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      modifier = Modifier.padding(contentPadding),
      content = enhavo
    )
  }
}

// ⟪ Flosanta n2tase Naviga Stango kun Interspacoj & Malklareco ⟫
@Composable
fun N2taseNavigaStango(
  aktivaLangeto: NavigaLangeto,
  tradukoj: TradukTekstoj,
  onElektiLangeton: (NavigaLangeto) -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .n2taseFono(shape = FormoNavStango, hazeMalklareco = 24.dp)
      .padding(horizontal = 6.dp, vertical = 6.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      val eroj = listOf(
        Triple(NavigaLangeto.SPURILO, Icons.Default.LocationSearching, tradukoj.navSpurilo),
        Triple(NavigaLangeto.MAPO, Icons.Default.Map, tradukoj.navMapo),
        Triple(NavigaLangeto.MEZURILO, Icons.Default.Straighten, tradukoj.navMezurilo),
        Triple(NavigaLangeto.SUNO_VETERO, Icons.Default.WbSunny, tradukoj.navSuno),
        Triple(NavigaLangeto.PROTOKOLO, Icons.Default.History, tradukoj.navProtokolo)
      )

      eroj.forEach { (langeto, ikono, teksto) ->
        val elektita = aktivaLangeto == langeto
        val interago = remember { MutableInteractionSource() }
        val premita by interago.collectIsPressedAsState()
        val butonFormo = animaciaButonFormo(
          premita = premita || elektita,
          bazaStart = 20.dp,
          bazaEnd = 10.dp,
          piloRadius = 100.dp
        )

        val fonoKoloro = when {
          elektita -> MaterialTheme.colorScheme.primary
          premita -> MaterialTheme.colorScheme.surfaceVariant
          else -> Color.Transparent
        }

        val tekstoKoloro = when {
          elektita -> MaterialTheme.colorScheme.onPrimary
          else -> MaterialTheme.colorScheme.onSurfaceVariant
        }

        Surface(
          onClick = { onElektiLangeton(langeto) },
          interactionSource = interago,
          shape = butonFormo,
          color = fonoKoloro,
          border = if (elektita) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null,
          modifier = Modifier.weight(1f)
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp)
          ) {
            Icon(
              imageVector = ikono,
              contentDescription = teksto,
              tint = tekstoKoloro,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = teksto,
              color = tekstoKoloro,
              fontSize = 9.sp,
              fontWeight = if (elektita) FontWeight.Bold else FontWeight.Normal,
              textAlign = TextAlign.Center,
              maxLines = 1
            )
          }
        }
      }
    }
  }
}

// ⟪ Supra Ciela Kapo ( Travidebla Fono & Pli Malgrandaj Radiusoj ) ⟫
@Composable
fun CielaKapo(
  progreso: Double,
  cax2lDato: Cax2lDato,
  castifeh2Tempo: Castifeh2Tempo,
  sunaInformo: SunaInformo? = null,
  uzuBazo10: Boolean,
  tradukoj: TradukTekstoj,
  lingvo: Lingvo = Lingvo.ESPERANTO,
  onBaskuliBazon: () -> Unit,
  onMalfermiAgordojn: () -> Unit,
  modifier: Modifier = Modifier
) {
  val agordojInterago = remember { MutableInteractionSource() }
  val agordojPremita by agordojInterago.collectIsPressedAsState()

  val bazoInterago = remember { MutableInteractionSource() }
  val bazoPremita by bazoInterago.collectIsPressedAsState()

  val haqeVal = if (uzuBazo10) tradukiCiferojn(castifeh2Tempo.haqe.toString(), lingvo) else vab6caja(castifeh2Tempo.haqe)
  val qeVal = if (uzuBazo10) tradukiCiferojn(castifeh2Tempo.qe.toString(), lingvo) else vab6caja(castifeh2Tempo.qe)
  val heVal = if (uzuBazo10) tradukiCiferojn(castifeh2Tempo.he.toInt().toString(), lingvo) else vab6caja(castifeh2Tempo.he.toLong())

  val b64Teksto = sunaInformo?.let {
    val (k1, k2, k3) = it.bazo64Horlogo
    val k1Val = if (uzuBazo10) tradukiCiferojn(k1.toString(), lingvo) else vab6caja(k1.toLong())
    val k2Val = if (uzuBazo10) tradukiCiferojn(k2.toString(), lingvo) else vab6caja(k2.toLong())
    val k3Val = if (uzuBazo10) tradukiCiferojn(k3.toString(), lingvo) else vab6caja(k3.toLong())
    "$k1Val • $k2Val • $k3Val"
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .background(Color.Transparent)
      .padding(horizontal = 16.dp, vertical = 6.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Surface(
          modifier = Modifier.size(42.dp),
          shape = RoundedCornerShape(topStart = 24.dp, topEnd = 10.dp, bottomEnd = 24.dp, bottomStart = 10.dp),
          color = n2taseButonKoloro(),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.Explore,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.size(20.dp)
            )
          }
        }

        Column {
          // Dato sen "Ktash" teksto
          Text(
            text = cax2lDato.alTeksto(uzuBazo10, lingvo),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
          // Horloĝo sur la supra stango ( sen haqe qe he vortoj )
          Text(
            text = "$haqeVal • $qeVal • $heVal",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
          )
          if (b64Teksto != null) {
            Text(
              text = b64Teksto,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 10.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Surface(
          onClick = onBaskuliBazon,
          interactionSource = bazoInterago,
          shape = animaciaButonFormo(bazoPremita, bazaStart = 18.dp, bazaEnd = 10.dp, piloRadius = 26.dp),
          color = n2taseButonKoloro(),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
          Text(
            text = if (uzuBazo10) tradukoj.bazo10 else tradukoj.bazo8,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          )
        }

        Surface(
          onClick = onMalfermiAgordojn,
          interactionSource = agordojInterago,
          shape = animaciaButonFormo(agordojPremita, bazaStart = 18.dp, bazaEnd = 10.dp, piloRadius = 26.dp),
          color = n2taseButonKoloro(),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
          modifier = Modifier.size(40.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.Palette,
              contentDescription = tradukoj.aspektoAgordoj,
              tint = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }
    }
  }
}

// ⟪ Ksaka Glifo Insigno ⟫
@Composable
fun KsakaGlifoInsigno(
  ksakaNomo: String,
  latinaNomo: String,
  chmuahNomo: String,
  vivaTeksto: String,
  latinaEtikedo: String = "Latina",
  chmuahEtikedo: String = "Chmuah",
  ksakaEtikedo: String = "Ksaka Koordinato",
  modifier: Modifier = Modifier
) {
  KtashKarto(
    modifier = modifier,
    fonKoloro = MaterialTheme.colorScheme.surface,
    bordaKoloro = MaterialTheme.colorScheme.outline
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = ksakaEtikedo,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
      )

      Surface(
        shape = FormoInsignoKompakta,
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
      ) {
        Text(
          text = vivaTeksto,
          color = MaterialTheme.colorScheme.onSurface,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = ksakaNomo,
      color = MaterialTheme.colorScheme.onSurface,
      fontSize = 22.sp,
      fontWeight = FontWeight.Bold,
      lineHeight = 28.sp
    )

    Spacer(modifier = Modifier.height(6.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = "$latinaEtikedo - $latinaNomo",
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium
      )
      Text(
        text = "$chmuahEtikedo - $chmuahNomo",
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium
      )
    }
  }
}

// ⟪ Stat Insigno ( Plena Teksto sen Trunko ... ) ⟫
@Composable
fun StatInsigno(
  etikedo: String,
  valoro: String,
  unuo: String = "",
  fonKoloro: Color? = null,
  modifier: Modifier = Modifier
) {
  val uzataFono = fonKoloro ?: MaterialTheme.colorScheme.surfaceVariant

  Box(
    modifier = modifier
      .clip(FormoInsigno)
      .background(uzataFono)
      .border(1.dp, MaterialTheme.colorScheme.outline, FormoInsigno)
      .padding(horizontal = 8.dp, vertical = 6.dp)
  ) {
    Column {
      Text(
        text = etikedo,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = valoro,
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
      )
      if (unuo.isNotEmpty()) {
        Spacer(modifier = Modifier.height(1.dp))
        Text(
          text = unuo,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 9.sp,
          fontWeight = FontWeight.SemiBold
        )
      }
    }
  }
}

// ⟪ Flosanta n2tase Agordoj Dialogo kun Rulumebla Lingvo-Listo ⟫
@Composable
fun AgordojFlosantaDialogo(
  temoModo: TemoModo,
  uzuMaterialYou: Boolean,
  uzuPropraTiparo: Boolean,
  uzuBazo10: Boolean,
  elektitaLingvo: Lingvo,
  tradukoj: TradukTekstoj,
  onAgordiTemoModon: (TemoModo) -> Unit,
  onBaskuliMaterialYou: () -> Unit,
  onBaskuliTiparon: () -> Unit,
  onBaskuliBazon: () -> Unit,
  onAgordiLingvon: (Lingvo) -> Unit,
  onFermi: () -> Unit
) {
  val subtenasMaterialYou = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
  val skroloStato = rememberScrollState()
  val view = LocalView.current

  val fermiInterago = remember { MutableInteractionSource() }
  val fermiPremita by fermiInterago.collectIsPressedAsState()

  Dialog(
    onDismissRequest = onFermi,
    properties = DialogProperties(
      usePlatformDefaultWidth = false,
      decorFitsSystemWindows = false
    )
  ) {
    DisposableEffect(Unit) {
      val fenestro = (view.parent as? DialogWindowProvider)?.window
      fenestro?.setDimAmount(0f)
      fenestro?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
      onDispose {}
    }

    Box(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(vertical = 24.dp)
        .n2taseFono(shape = FormoKartoGranda)
        .padding(18.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(FormoSkroloMaskaDialogo)
          .verticalScroll(skroloStato),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // ⟪ Titolo & Fermi ⟫
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = tradukoj.aspektoAgordoj,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
          )
          Surface(
            onClick = onFermi,
            interactionSource = fermiInterago,
            shape = animaciaButonFormo(fermiPremita),
            color = n2taseButonKoloro(),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier.size(44.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = tradukoj.fermi,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }

        // ⟪ Lingvo Elekto ( Ruluma LazyRow anstataŭ Premita Linio ) ⟫
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            text = tradukoj.lingvo,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
          LazyRow(
            modifier = Modifier
              .fillMaxWidth()
              .clip(FormoSkroloMaskaHorizontala),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
          ) {
            items(Lingvo.values()) { lingvo ->
              val elektita = elektitaLingvo == lingvo
              val interago = remember { MutableInteractionSource() }
              val premita by interago.collectIsPressedAsState()
              val formo = animaciaButonFormo(premita = premita || elektita)

              Surface(
                onClick = { onAgordiLingvon(lingvo) },
                interactionSource = interago,
                shape = formo,
                color = if (elektita) MaterialTheme.colorScheme.primary else n2taseButonKoloro(),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
              ) {
                Text(
                  text = lingvo.nomo,
                  color = if (elektita) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                  fontWeight = if (elektita) FontWeight.Bold else FontWeight.Medium,
                  fontSize = 11.sp,
                  textAlign = TextAlign.Center,
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
              }
            }
          }
        }

        // ⟪ Temo Elekto ⟫
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            text = tradukoj.temoModo,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf(
              TemoModo.SISTEMA to tradukoj.sistema,
              TemoModo.HELA to tradukoj.hela,
              TemoModo.MALHELA to tradukoj.malhela
            ).forEach { (modo, nomo) ->
              val elektita = temoModo == modo
              val interago = remember { MutableInteractionSource() }
              val premita by interago.collectIsPressedAsState()
              val formo = animaciaButonFormo(premita = premita || elektita)

              Surface(
                onClick = { onAgordiTemoModon(modo) },
                interactionSource = interago,
                shape = formo,
                color = if (elektita) MaterialTheme.colorScheme.primary else n2taseButonKoloro(),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.weight(1f)
              ) {
                Text(
                  text = nomo,
                  color = if (elektita) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                  fontWeight = if (elektita) FontWeight.Bold else FontWeight.Medium,
                  fontSize = 11.sp,
                  textAlign = TextAlign.Center,
                  modifier = Modifier.padding(vertical = 8.dp)
                )
              }
            }
          }
        }

        // ⟪ Material You Ŝaltilo ⟫
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = subtenasMaterialYou) {
              if (subtenasMaterialYou) onBaskuliMaterialYou()
            },
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = tradukoj.materialYou,
              color = MaterialTheme.colorScheme.onSurface,
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = if (subtenasMaterialYou) tradukoj.materialYouPriskribo else "Android 12+",
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 10.sp
            )
          }
          KtashŜaltilo(
            checked = uzuMaterialYou && subtenasMaterialYou,
            onCheckedChange = { if (subtenasMaterialYou) onBaskuliMaterialYou() },
            enabled = subtenasMaterialYou
          )
        }

        // ⟪ Tiparo Ŝaltilo ⟫
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onBaskuliTiparon() },
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = tradukoj.propraTiparo,
              color = MaterialTheme.colorScheme.onSurface,
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = if (uzuPropraTiparo) tradukoj.propraTiparoPriskribo else tradukoj.sistema,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 10.sp
            )
          }
          KtashŜaltilo(
            checked = uzuPropraTiparo,
            onCheckedChange = { onBaskuliTiparon() }
          )
        }

        // ⟪ Nombra Bazo Ŝaltilo ⟫
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onBaskuliBazon() },
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = tradukoj.nombraBazo,
              color = MaterialTheme.colorScheme.onSurface,
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = if (uzuBazo10) tradukoj.bazo10 else tradukoj.bazo8,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 10.sp
            )
          }
          KtashŜaltilo(
            checked = uzuBazo10,
            onCheckedChange = { onBaskuliBazon() }
          )
        }
      }
    }
  }
}

// ⟪ Ktash Rondo-Romba Baskulo / Ŝaltilo ( Rounded Diamond Toggle / Checkbox ) ⟫
@Composable
fun KtashŜaltilo(
  checked: Boolean,
  onCheckedChange: ((Boolean) -> Unit)?,
  modifier: Modifier = Modifier,
  enabled: Boolean = true
) {
  val interago = remember { MutableInteractionSource() }
  val premita by interago.collectIsPressedAsState()

  val animScala by animateFloatAsState(
    targetValue = if (premita) 0.88f else 1.0f,
    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
    label = "diamondScale"
  )

  val animInnerScale by animateFloatAsState(
    targetValue = if (checked) 1.0f else 0.0f,
    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
    label = "innerDiamondScale"
  )

  val fonaKoloro = if (checked) {
    MaterialTheme.colorScheme.primary
  } else {
    n2taseButonKoloro()
  }

  val bordaKoloro = if (checked) {
    MaterialTheme.colorScheme.primary
  } else {
    MaterialTheme.colorScheme.outline
  }

  val klakeblaModifier = if (onCheckedChange != null && enabled) {
    Modifier.toggleable(
      value = checked,
      onValueChange = onCheckedChange,
      role = Role.Switch,
      interactionSource = interago,
      indication = null
    )
  } else {
    Modifier
  }

  Box(
    modifier = modifier
      .size(48.dp)
      .then(klakeblaModifier),
    contentAlignment = Alignment.Center
  ) {
    Box(
      modifier = Modifier
        .size(24.dp)
        .graphicsLayer {
          rotationZ = 45f
          scaleX = animScala
          scaleY = animScala
        }
        .clip(RoundedCornerShape(6.dp))
        .background(if (enabled) fonaKoloro else fonaKoloro.copy(alpha = 0.4f))
        .border(1.5.dp, if (enabled) bordaKoloro else bordaKoloro.copy(alpha = 0.4f), RoundedCornerShape(6.dp)),
      contentAlignment = Alignment.Center
    ) {
      if (animInnerScale > 0.01f) {
        Box(
          modifier = Modifier
            .size(10.dp)
            .graphicsLayer {
              scaleX = animInnerScale
              scaleY = animInnerScale
            }
            .clip(RoundedCornerShape(2.dp))
            .background(MaterialTheme.colorScheme.onPrimary)
        )
      }
    }
  }
}

// ⟪ Ktash Skribsistemo Nombra Enig-Klavaro ⌨️ ⟫
@Composable
fun KtashCiferoKlavaro(
  nunaValoro: String,
  onValoroSanĝita: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val glifoj = listOf(
    Pair("ɔ", "0"),
    Pair("ı", "1"),
    Pair("ɿ", "2"),
    Pair("ц", "3"),
    Pair("э", "4"),
    Pair("ꞟ", "5"),
    Pair("ɩ", "6"),
    Pair("ƨ", "7")
  )

  Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
    // Unua vico: Glifoj ɔ ĝis ƨ
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      glifoj.forEach { (glifo, cifero) ->
        val interago = remember { MutableInteractionSource() }
        val premita by interago.collectIsPressedAsState()
        val formo = animaciaButonFormo(premita, bazaStart = 16.dp, bazaEnd = 4.dp, piloRadius = 32.dp)
        val animBordo by animateColorAsState(
          targetValue = if (premita) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
          animationSpec = tween(durationMillis = 150),
          label = "klavBordo"
        )
        val animFono by animateColorAsState(
          targetValue = if (premita) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f) else n2taseButonKoloro(),
          animationSpec = tween(durationMillis = 150),
          label = "klavFono"
        )

        Surface(
          onClick = { onValoroSanĝita(nunaValoro + glifo) },
          interactionSource = interago,
          shape = formo,
          color = animFono,
          border = androidx.compose.foundation.BorderStroke(1.dp, animBordo),
          modifier = Modifier.weight(1f)
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 4.dp)
          ) {
            Text(
              text = glifo,
              color = MaterialTheme.colorScheme.primary,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = cifero,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 8.sp
            )
          }
        }
      }
    }

    // Dua vico: Punkto, Spaco, Forigi, Vakigi
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      listOf(
        Triple(".", { onValoroSanĝita(nunaValoro + ".") }, null),
        Triple("␣", { onValoroSanĝita(nunaValoro + " ") }, null),
        Triple("←", { if (nunaValoro.isNotEmpty()) onValoroSanĝita(nunaValoro.dropLast(1)) }, Icons.AutoMirrored.Filled.Backspace),
        Triple("C", { onValoroSanĝita("") }, null)
      ).forEach { (teksto, ago, ikono) ->
        val interago = remember { MutableInteractionSource() }
        val premita by interago.collectIsPressedAsState()
        val formo = animaciaButonFormo(premita, bazaStart = 16.dp, bazaEnd = 4.dp, piloRadius = 32.dp)
        val animBordo by animateColorAsState(
          targetValue = if (premita) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
          animationSpec = tween(durationMillis = 150),
          label = "klavBordo"
        )
        val animFono by animateColorAsState(
          targetValue = if (premita) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f) else n2taseButonKoloro(),
          animationSpec = tween(durationMillis = 150),
          label = "klavFono"
        )

        Surface(
          onClick = ago,
          interactionSource = interago,
          shape = formo,
          color = animFono,
          border = androidx.compose.foundation.BorderStroke(1.dp, animBordo),
          modifier = Modifier.weight(1f)
        ) {
          Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 6.dp)) {
            if (ikono != null) {
              Icon(
                ikono,
                contentDescription = teksto,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurface
              )
            } else {
              Text(teksto, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}

// ⟪ Flosanta Konfirm-Dialogo ( n2tase Stilo, Sen Mallumo, Plena Larĝo, Propra Tiparo ) ⟫
@Composable
fun KtashFlosantaKonfirmDialogo(
  titolo: String,
  mesagxo: String,
  konfirmiTeksto: String,
  nuligiTeksto: String,
  ĉuForigo: Boolean = true,
  onKonfirmi: () -> Unit,
  onNuligi: () -> Unit
) {
  val view = LocalView.current
  Dialog(
    onDismissRequest = onNuligi,
    properties = DialogProperties(
      usePlatformDefaultWidth = false,
      decorFitsSystemWindows = false
    )
  ) {
    DisposableEffect(Unit) {
      val fenestro = (view.parent as? DialogWindowProvider)?.window
      fenestro?.setDimAmount(0f)
      fenestro?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
      onDispose {}
    }

    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 14.dp),
      contentAlignment = Alignment.Center
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(max = 440.dp)
          .n2taseFono(shape = FormoKartoGranda, hazeMalklareco = 24.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Text(
            text = titolo,
            color = if (ĉuForigo) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = KtashFontFamilio
          )

          Text(
            text = mesagxo,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 13.sp,
            fontFamily = KtashFontFamilio
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
          ) {
            val nuligiInterago = remember { MutableInteractionSource() }
            val nuligiPremita by nuligiInterago.collectIsPressedAsState()
            Surface(
              onClick = onNuligi,
              interactionSource = nuligiInterago,
              shape = animaciaButonFormo(nuligiPremita),
              color = n2taseButonKoloro(),
              border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
              Text(
                text = nuligiTeksto,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
              )
            }

            Spacer(modifier = Modifier.width(10.dp))

            N2taseButono(
              onClick = onKonfirmi,
              fonKoloro = if (ĉuForigo) MaterialTheme.colorScheme.error.copy(alpha = 0.20f) else MaterialTheme.colorScheme.primary,
              bordaKoloro = if (ĉuForigo) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
            ) {
              if (ĉuForigo) {
                Icon(
                  Icons.Default.Delete,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp),
                  tint = MaterialTheme.colorScheme.error
                )
              }
              Text(
                text = konfirmiTeksto,
                color = if (ĉuForigo) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }
  }
}

// ⟪ Universala Flosanta Dialogo ( n2tase Stilo, Sen Mallumo, Plena Larĝo ) ⟫
@Composable
fun KtashFlosantaDialogo(
  titolo: String,
  onFermi: () -> Unit,
  enhavo: @Composable ColumnScope.() -> Unit
) {
  val view = LocalView.current
  Dialog(
    onDismissRequest = onFermi,
    properties = DialogProperties(
      usePlatformDefaultWidth = false,
      decorFitsSystemWindows = false
    )
  ) {
    DisposableEffect(Unit) {
      val fenestro = (view.parent as? DialogWindowProvider)?.window
      fenestro?.setDimAmount(0f)
      fenestro?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
      onDispose {}
    }

    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 14.dp),
      contentAlignment = Alignment.Center
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(max = 460.dp)
          .n2taseFono(shape = FormoKartoGranda, hazeMalklareco = 24.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = titolo,
              color = MaterialTheme.colorScheme.primary,
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = KtashFontFamilio
            )
            val fermiInterago = remember { MutableInteractionSource() }
            val fermiPremita by fermiInterago.collectIsPressedAsState()
            Surface(
              onClick = onFermi,
              interactionSource = fermiInterago,
              shape = animaciaButonFormo(fermiPremita),
              color = MaterialTheme.colorScheme.surfaceVariant,
              border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
              modifier = Modifier.size(44.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  Icons.Default.Close,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          }

          enhavo()
        }
      }
    }
  }
}


