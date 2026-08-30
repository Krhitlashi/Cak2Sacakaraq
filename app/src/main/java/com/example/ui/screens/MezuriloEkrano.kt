package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import com.example.ktash.*
import com.example.ui.KtashViewModel
import com.example.ui.components.KtashKarto
import com.example.ui.components.KtashSubKarto
import com.example.ui.components.N2taseButono
import com.example.ui.components.N2taseKarto
import com.example.ui.components.StatInsigno
import com.example.ui.components.animaciaButonFormo
import com.example.ui.components.n2taseButonKoloro
import com.example.ui.components.n2taseFono
import com.example.ui.theme.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

// ≺⧼ Mezurilo & Distancaj Kalkuloj 📏 ⧽≻

enum class AlgluoElekto(val frakcio: Double?) {
  LIBERA(null),
  PEU_1(1.0),
  PEU_8(1.0 / 8.0),
  PEU_64(1.0 / 64.0)
}

fun aplikiAlgluonPx(valoroPx: Float, pxPerPeu: Float, elekto: AlgluoElekto): Float {
  val frakcio = elekto.frakcio ?: return valoroPx
  val pasoPx = (pxPerPeu * frakcio).toFloat()
  if (pasoPx <= 0.5f) return valoroPx
  return round(valoroPx / pasoPx) * pasoPx
}

@Composable
fun MezuriloEkrano(
  viewModel: KtashViewModel,
  modifier: Modifier = Modifier
) {
  val uzuBazo10 by viewModel.uzuBazo10.collectAsState()
  val tradukoj by viewModel.tradukoj.collectAsState()
  val lingvo by viewModel.elektitaLingvo.collectAsState()
  val ĉuPlenaEkranaMezurilo by viewModel.plenaEkranaMezurilo.collectAsState()

  var montruKradon by remember { mutableStateOf(true) }
  var elektitaAlgluo by remember { mutableStateOf(AlgluoElekto.LIBERA) }

  val context = LocalContext.current
  val metrics = context.resources.displayMetrics
  val composeDensity = LocalDensity.current.density
  val realMetrics = remember {
    val dm = DisplayMetrics()
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        context.display?.getRealMetrics(dm)
      } else {
        @Suppress("DEPRECATION")
        (context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager)?.defaultDisplay?.getRealMetrics(dm)
      }
    } catch (_: Exception) {}
    dm
  }

  // Precizaj pikseloj per milimetro kaj per peu laŭ la reala fizika ekrana denseco ( X kaj Y )
  // 1 colo = 25.4 mm
  val xdpi = when {
    realMetrics.xdpi > 10f -> realMetrics.xdpi
    metrics.xdpi > 10f -> metrics.xdpi
    else -> metrics.densityDpi.toFloat()
  }
  val ydpi = when {
    realMetrics.ydpi > 10f -> realMetrics.ydpi
    metrics.ydpi > 10f -> metrics.ydpi
    else -> metrics.densityDpi.toFloat()
  }

  val pxPerMmX = xdpi / 25.4f
  val pxPerMmY = ydpi / 25.4f
  val pxPerMm = pxPerMmX

  // Preciza fizika mezuro de peu en milimetroj bazita sur lumrapido ( P0 = 149896229/9192631770 m ≈ 16.3061 mm )
  val mmPerPeu = (P0 * 1000.0).toFloat()
  val pxPerPeuX = mmPerPeu * pxPerMmX
  val pxPerPeuY = mmPerPeu * pxPerMmY
  val pxPerPeu = pxPerPeuX

  // Caliper-pozicioj en pikseloj ( por norma reĝimo, komence 1 peu )
  var krudaKaliproKomencoPx by remember { mutableFloatStateOf(24f * composeDensity) }
  var krudaKaliproFinoPx by remember { mutableFloatStateOf(24f * composeDensity + pxPerPeuX) }
  var kaliproKomencoPx by remember { mutableFloatStateOf(24f * composeDensity) }
  var kaliproFinoPx by remember { mutableFloatStateOf(24f * composeDensity + pxPerPeuX) }

  // Caliper-pozicioj por plena ekrano ( komence 2 peu X, 3 peu Y )
  var krudaPlenaX1 by remember { mutableFloatStateOf(32f * composeDensity) }
  var krudaPlenaX2 by remember { mutableFloatStateOf(32f * composeDensity + (pxPerPeuX * 2f)) }
  var krudaPlenaY1 by remember { mutableFloatStateOf(48f * composeDensity) }
  var krudaPlenaY2 by remember { mutableFloatStateOf(48f * composeDensity + (pxPerPeuY * 3f)) }

  var plenaKaliproX1 by remember { mutableFloatStateOf(32f * composeDensity) }
  var plenaKaliproX2 by remember { mutableFloatStateOf(32f * composeDensity + (pxPerPeuX * 2f)) }
  var plenaKaliproY1 by remember { mutableFloatStateOf(48f * composeDensity) }
  var plenaKaliproY2 by remember { mutableFloatStateOf(48f * composeDensity + (pxPerPeuY * 3f)) }
  var flosantaPaneloMalfaldita by remember { mutableStateOf(true) }

  // Distanco kalkulita en norma reĝimo
  val distancoPx = abs(kaliproFinoPx - kaliproKomencoPx)
  val distancoMm = (distancoPx / pxPerMmX).toDouble()
  val distancoMetroj = distancoMm / 1000.0
  val distancoPeu = distancoMetroj / P0
  val distancoC2ta = metrojAlC2ta(distancoMetroj)

  // Distanco en plena ekrana reĝimo
  val plenaDistancoX_Px = abs(plenaKaliproX2 - plenaKaliproX1)
  val plenaDistancoX_Mm = (plenaDistancoX_Px / pxPerMmX).toDouble()
  val plenaDistancoX_Metroj = plenaDistancoX_Mm / 1000.0
  val plenaDistancoX_Peu = plenaDistancoX_Metroj / P0

  val plenaDistancoY_Px = abs(plenaKaliproY2 - plenaKaliproY1)
  val plenaDistancoY_Mm = (plenaDistancoY_Px / pxPerMmY).toDouble()
  val plenaDistancoY_Metroj = plenaDistancoY_Mm / 1000.0
  val plenaDistancoY_Peu = plenaDistancoY_Metroj / P0

  val diagonaloMetroj = sqrt(plenaDistancoX_Metroj.pow(2.0) + plenaDistancoY_Metroj.pow(2.0))
  val diagonaloPeu = metrojAlPeu(diagonaloMetroj)

  // Konvertilo stato
  var enigoValoro by remember { mutableStateOf("1.0") }
  var enigoUnuo by remember { mutableStateOf("peu") }

  val skroloStato = rememberScrollState()

  val primaraKoloro = MaterialTheme.colorScheme.primary
  val tekstoDuaKoloro = MaterialTheme.colorScheme.onSurfaceVariant
  val bordoKoloro = MaterialTheme.colorScheme.outline

  // ⟪ Plena Ekrana Mezurilo ( Ekran-Randoj & Duaksaj Kaliproj - Plena Ekrana Dialogo ) ⟫
  if (ĉuPlenaEkranaMezurilo) {
    val view = LocalView.current
    Dialog(
      onDismissRequest = { viewModel.agordiPlenanEkrananMezurilon(false) },
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
          .background(MaterialTheme.colorScheme.background)
      ) {
        Canvas(
          modifier = Modifier
            .fillMaxSize()
            .pointerInput(elektitaAlgluo, pxPerPeuX, pxPerPeuY) {
              var aktivaTenilo = 0
              detectDragGestures(
                onDragStart = { pozicio ->
                  val dX1 = abs(pozicio.x - plenaKaliproX1)
                  val dX2 = abs(pozicio.x - plenaKaliproX2)
                  val dY1 = abs(pozicio.y - plenaKaliproY1)
                  val dY2 = abs(pozicio.y - plenaKaliproY2)
                  val minD = minOf(dX1, dX2, dY1, dY2)
                  aktivaTenilo = when (minD) {
                    dX1 -> 1
                    dX2 -> 2
                    dY1 -> 3
                    else -> 4
                  }
                  krudaPlenaX1 = plenaKaliproX1
                  krudaPlenaX2 = plenaKaliproX2
                  krudaPlenaY1 = plenaKaliproY1
                  krudaPlenaY2 = plenaKaliproY2
                },
                onDrag = { change, dragAmount ->
                  change.consume()
                  val maxW = (size.width - 10).toFloat().coerceAtLeast(10f)
                  val maxH = (size.height - 10).toFloat().coerceAtLeast(10f)
                  when (aktivaTenilo) {
                    1 -> {
                      krudaPlenaX1 = (krudaPlenaX1 + dragAmount.x).coerceIn(10f, maxW)
                      plenaKaliproX1 = aplikiAlgluonPx(krudaPlenaX1, pxPerPeuX, elektitaAlgluo)
                    }
                    2 -> {
                      krudaPlenaX2 = (krudaPlenaX2 + dragAmount.x).coerceIn(10f, maxW)
                      plenaKaliproX2 = aplikiAlgluonPx(krudaPlenaX2, pxPerPeuX, elektitaAlgluo)
                    }
                    3 -> {
                      krudaPlenaY1 = (krudaPlenaY1 + dragAmount.y).coerceIn(10f, maxH)
                      plenaKaliproY1 = aplikiAlgluonPx(krudaPlenaY1, pxPerPeuY, elektitaAlgluo)
                    }
                    4 -> {
                      krudaPlenaY2 = (krudaPlenaY2 + dragAmount.y).coerceIn(10f, maxH)
                      plenaKaliproY2 = aplikiAlgluonPx(krudaPlenaY2, pxPerPeuY, elektitaAlgluo)
                    }
                  }
                }
              )
            }
        ) {
          val w = size.width
          val h = size.height

          // Fona Krado se aktivigita
          if (montruKradon) {
            var gx = 0f
            while (gx <= w) {
              drawLine(
                color = bordoKoloro.copy(alpha = 0.25f),
                start = Offset(gx, 0f),
                end = Offset(gx, h),
                strokeWidth = 0.5.dp.toPx()
              )
              gx += pxPerPeuX
            }
            var gy = 0f
            while (gy <= h) {
              drawLine(
                color = bordoKoloro.copy(alpha = 0.25f),
                start = Offset(0f, gy),
                end = Offset(w, gy),
                strokeWidth = 0.5.dp.toPx()
              )
              gy += pxPerPeuY
            }
          }

          // Vertikala Skalo ( 32 subskaloj por 1 peu en bazo 8 - sen 1/64 markoj )
          val subPxY = pxPerPeuY / 32f
          var yPx = 0f
          var tickIndexY = 0
          while (yPx <= h) {
            val isPeu = tickIndexY % 32 == 0
            val isHalf = tickIndexY % 16 == 0
            val isQuarter = tickIndexY % 8 == 0
            val isEighth = tickIndexY % 4 == 0
            val isSixteenth = tickIndexY % 2 == 0

            val tickLen = when {
              isPeu -> 36.dp.toPx()
              isHalf -> 26.dp.toPx()
              isQuarter -> 20.dp.toPx()
              isEighth -> 15.dp.toPx()
              isSixteenth -> 10.dp.toPx()
              else -> 6.dp.toPx()
            }
            val tickStroke = when {
              isPeu -> 2.5.dp.toPx()
              isHalf -> 2.0.dp.toPx()
              isQuarter -> 1.5.dp.toPx()
              isEighth -> 1.2.dp.toPx()
              isSixteenth -> 1.0.dp.toPx()
              else -> 0.7.dp.toPx()
            }
            val tickColor = when {
              isPeu -> primaraKoloro
              isHalf || isQuarter -> primaraKoloro.copy(alpha = 0.85f)
              isEighth -> bordoKoloro.copy(alpha = 0.95f)
              else -> bordoKoloro.copy(alpha = 0.55f)
            }

            drawLine(
              color = tickColor,
              start = Offset(0f, yPx),
              end = Offset(tickLen, yPx),
              strokeWidth = tickStroke
            )

            drawLine(
              color = tickColor,
              start = Offset(w, yPx),
              end = Offset(w - tickLen, yPx),
              strokeWidth = tickStroke
            )

            yPx += subPxY
            tickIndexY++
          }

          // Horizontala Skalo ( 32 subskaloj por 1 peu en bazo 8 - sen 1/64 markoj )
          val subPxX = pxPerPeuX / 32f
          var xPx = 0f
          var tickIndexX = 0
          while (xPx <= w) {
            val isPeu = tickIndexX % 32 == 0
            val isHalf = tickIndexX % 16 == 0
            val isQuarter = tickIndexX % 8 == 0
            val isEighth = tickIndexX % 4 == 0
            val isSixteenth = tickIndexX % 2 == 0

            val tickLen = when {
              isPeu -> 36.dp.toPx()
              isHalf -> 26.dp.toPx()
              isQuarter -> 20.dp.toPx()
              isEighth -> 15.dp.toPx()
              isSixteenth -> 10.dp.toPx()
              else -> 6.dp.toPx()
            }
            val tickStroke = when {
              isPeu -> 2.5.dp.toPx()
              isHalf -> 2.0.dp.toPx()
              isQuarter -> 1.5.dp.toPx()
              isEighth -> 1.2.dp.toPx()
              isSixteenth -> 1.0.dp.toPx()
              else -> 0.7.dp.toPx()
            }
            val tickColor = when {
              isPeu -> primaraKoloro
              isHalf || isQuarter -> primaraKoloro.copy(alpha = 0.85f)
              isEighth -> bordoKoloro.copy(alpha = 0.95f)
              else -> bordoKoloro.copy(alpha = 0.55f)
            }

            drawLine(
              color = tickColor,
              start = Offset(xPx, 0f),
              end = Offset(xPx, tickLen),
              strokeWidth = tickStroke
            )

            drawLine(
              color = tickColor,
              start = Offset(xPx, h),
              end = Offset(xPx, h - tickLen),
              strokeWidth = tickStroke
            )

            xPx += subPxX
            tickIndexX++
          }

          // Aktivaj Kalipraj Gvidlinioj & Mezura Rektangulo
          val leftX = min(plenaKaliproX1, plenaKaliproX2)
          val topY = min(plenaKaliproY1, plenaKaliproY2)
          val rectW = abs(plenaKaliproX2 - plenaKaliproX1)
          val rectH = abs(plenaKaliproY2 - plenaKaliproY1)

          drawRect(
            color = primaraKoloro.copy(alpha = 0.12f),
            topLeft = Offset(leftX, topY),
            size = Size(rectW, rectH)
          )

          // X Kaliproj
          drawLine(
            color = primaraKoloro,
            start = Offset(plenaKaliproX1, 0f),
            end = Offset(plenaKaliproX1, h),
            strokeWidth = 2.dp.toPx()
          )
          drawLine(
            color = primaraKoloro,
            start = Offset(plenaKaliproX2, 0f),
            end = Offset(plenaKaliproX2, h),
            strokeWidth = 2.dp.toPx()
          )

          // Y Kaliproj
          drawLine(
            color = primaraKoloro,
            start = Offset(0f, plenaKaliproY1),
            end = Offset(w, plenaKaliproY1),
            strokeWidth = 2.dp.toPx()
          )
          drawLine(
            color = primaraKoloro,
            start = Offset(0f, plenaKaliproY2),
            end = Offset(w, plenaKaliproY2),
            strokeWidth = 2.dp.toPx()
          )

          // Diagonala Mezurlinio
          drawLine(
            color = primaraKoloro.copy(alpha = 0.5f),
            start = Offset(leftX, topY),
            end = Offset(leftX + rectW, topY + rectH),
            strokeWidth = 1.dp.toPx()
          )
        }

        // Rapida flosanta butono por fermi en supra dekstra angulo ( n2tase )
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(14.dp),
          contentAlignment = Alignment.TopEnd
        ) {
          N2taseButono(
            onClick = { viewModel.agordiPlenanEkrananMezurilon(false) }
          ) {
            Icon(
              Icons.Default.CloseFullscreen,
              contentDescription = tradukoj.fermi,
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = tradukoj.fermi,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        // ⟪ Flosanta Kontrolpanelo ( n2tase HUD - Sendependa & Flosanta super Mezurilo ) ⟫
        Box(
          modifier = Modifier
            .align(Alignment.BottomCenter)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 14.dp)
            .padding(bottom = 76.dp)
            .widthIn(max = 480.dp)
            .fillMaxWidth()
            .n2taseFono(shape = FormoKartoGranda, hazeMalklareco = 24.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Text(
                  text = tradukoj.ekranaMezurilo,
                  color = MaterialTheme.colorScheme.primary,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold
                )

                val kradoInterago = remember { MutableInteractionSource() }
                val kradoPremita by kradoInterago.collectIsPressedAsState()
                val kradoFormo = animaciaButonFormo(kradoPremita || montruKradon, bazaStart = 16.dp, bazaEnd = 4.dp)
                val animKradoBordo by animateColorAsState(
                  targetValue = if (montruKradon) MaterialTheme.colorScheme.primary else if (kradoPremita) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.outline,
                  animationSpec = tween(150),
                  label = "kradoBordo"
                )
                val animKradoFono by animateColorAsState(
                  targetValue = if (montruKradon) MaterialTheme.colorScheme.primary else if (kradoPremita) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f) else n2taseButonKoloro(),
                  animationSpec = tween(150),
                  label = "kradoFono"
                )

                Surface(
                  onClick = { montruKradon = !montruKradon },
                  interactionSource = kradoInterago,
                  shape = kradoFormo,
                  color = animKradoFono,
                  border = androidx.compose.foundation.BorderStroke(1.dp, animKradoBordo)
                ) {
                  Text(
                    text = tradukoj.krado,
                    color = if (montruKradon) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }

              // Butono por malfaldi / faldi ( Minimize / Maximize )
              N2taseButono(
                onClick = { flosantaPaneloMalfaldita = !flosantaPaneloMalfaldita }
              ) {
                Icon(
                  imageVector = if (flosantaPaneloMalfaldita) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                  contentDescription = if (flosantaPaneloMalfaldita) "Minimize" else "Maximize",
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = if (flosantaPaneloMalfaldita) "—" else "+",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            if (!flosantaPaneloMalfaldita) {
              // Minimumigita resumo ( Sen duoblaj punktoj, uzo de tradukeblaj Y, X, Diag )
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "${tradukoj.simboloY} - ${formatiOksaleAuxDekume(plenaDistancoY_Peu, uzuBazo10, 2, lingvo)} ${tradukoj.unuoPeu}  •  ${tradukoj.simboloX} - ${formatiOksaleAuxDekume(plenaDistancoX_Peu, uzuBazo10, 2, lingvo)} ${tradukoj.unuoPeu}",
                  color = MaterialTheme.colorScheme.onSurface,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold
                )
                Text(
                  text = "${tradukoj.simboloDiag} - ${formatiOksaleAuxDekume(diagonaloPeu, uzuBazo10, 2, lingvo)} ${tradukoj.unuoPeu}",
                  color = MaterialTheme.colorScheme.primary,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            } else {
              // Maksimumigitaj detaloj
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .heightIn(max = 240.dp)
                  .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(5.dp)
              ) {
                // ⟪ Algluo Opcioj ( Snap to Peu ) ⟫
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(4.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "${tradukoj.algluoReĝimoEtikedo} -",
                    fontSize = 10.sp,
                    color = tekstoDuaKoloro,
                    modifier = Modifier.padding(end = 4.dp)
                  )

                  listOf(
                    AlgluoElekto.LIBERA to tradukoj.algluoLibera,
                    AlgluoElekto.PEU_1 to tradukoj.algluoPeu1,
                    AlgluoElekto.PEU_8 to tradukoj.algluoPeu8,
                    AlgluoElekto.PEU_64 to tradukoj.algluoPeu64
                  ).forEach { (elekto, etikedo) ->
                    val elektita = elektitaAlgluo == elekto
                    val interago = remember { MutableInteractionSource() }
                    val premita by interago.collectIsPressedAsState()
                    val formo = animaciaButonFormo(premita || elektita, bazaStart = 16.dp, bazaEnd = 4.dp)
                    val animBordo by animateColorAsState(
                      targetValue = if (elektita) MaterialTheme.colorScheme.primary else if (premita) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.outline,
                      animationSpec = tween(150),
                      label = "hudBordo"
                    )
                    val animFono by animateColorAsState(
                      targetValue = if (elektita) MaterialTheme.colorScheme.primary else if (premita) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f) else n2taseButonKoloro(),
                      animationSpec = tween(150),
                      label = "hudFono"
                    )

                    Surface(
                      onClick = {
                        elektitaAlgluo = elekto
                        if (elekto != AlgluoElekto.LIBERA) {
                          plenaKaliproX1 = aplikiAlgluonPx(plenaKaliproX1, pxPerPeuX, elekto)
                          plenaKaliproX2 = aplikiAlgluonPx(plenaKaliproX2, pxPerPeuX, elekto)
                          plenaKaliproY1 = aplikiAlgluonPx(plenaKaliproY1, pxPerPeuY, elekto)
                          plenaKaliproY2 = aplikiAlgluonPx(plenaKaliproY2, pxPerPeuY, elekto)
                          krudaPlenaX1 = plenaKaliproX1
                          krudaPlenaX2 = plenaKaliproX2
                          krudaPlenaY1 = plenaKaliproY1
                          krudaPlenaY2 = plenaKaliproY2
                        }
                      },
                      interactionSource = interago,
                      shape = formo,
                      color = animFono,
                      border = androidx.compose.foundation.BorderStroke(1.dp, animBordo),
                      modifier = Modifier.weight(1f)
                    ) {
                      Text(
                        text = etikedo,
                        color = if (elektita) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        fontSize = 9.sp,
                        fontWeight = if (elektita) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 4.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                      )
                    }
                  }
                }

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  StatInsigno(
                    etikedo = "${tradukoj.vertikalaY} ( ${tradukoj.unuoPeu} )",
                    valoro = formatiOksaleAuxDekume(plenaDistancoY_Peu, uzuBazo10, 3, lingvo),
                    unuo = tradukoj.unuoPeu,
                    modifier = Modifier.weight(1f)
                  )
                  StatInsigno(
                    etikedo = "${tradukoj.horizontalaX} ( ${tradukoj.unuoPeu} )",
                    valoro = formatiOksaleAuxDekume(plenaDistancoX_Peu, uzuBazo10, 3, lingvo),
                    unuo = tradukoj.unuoPeu,
                    modifier = Modifier.weight(1f)
                  )
                }

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  StatInsigno(
                    etikedo = "${tradukoj.simboloY} ( ${tradukoj.unuoMilimetroSimbolo} )",
                    valoro = formatiOksaleAuxDekume(plenaDistancoY_Metroj * 1000.0, uzuBazo10, 1, lingvo),
                    unuo = tradukoj.unuoMilimetroSimbolo,
                    modifier = Modifier.weight(1f)
                  )
                  StatInsigno(
                    etikedo = "${tradukoj.simboloX} ( ${tradukoj.unuoMilimetroSimbolo} )",
                    valoro = formatiOksaleAuxDekume(plenaDistancoX_Metroj * 1000.0, uzuBazo10, 1, lingvo),
                    unuo = tradukoj.unuoMilimetroSimbolo,
                    modifier = Modifier.weight(1f)
                  )
                  StatInsigno(
                    etikedo = tradukoj.diagonalo,
                    valoro = formatiOksaleAuxDekume(diagonaloPeu, uzuBazo10, 2, lingvo),
                    unuo = tradukoj.unuoPeu,
                    modifier = Modifier.weight(1f)
                  )
                }
              }
            }
          }
        }
      }
    }
  }

  // ⟪ Norma Mezurila Ekrano ⟫
  Box(modifier = modifier.fillMaxSize()) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(horizontal = 14.dp)
        .clip(FormoSkroloMaska)
        .verticalScroll(skroloStato)
        .padding(vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // ⟪ Titolo de Mezurilo & Plenekrana Butono ⟫
      KtashKarto {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = tradukoj.mezuriloTitolo,
              color = MaterialTheme.colorScheme.primary,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = tradukoj.mezuriloPriskribo,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 12.sp
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          val plenekranoInterago = remember { MutableInteractionSource() }
          val plenekranoPremita by plenekranoInterago.collectIsPressedAsState()
          val plenekranoFormo = animaciaButonFormo(plenekranoPremita, bazaStart = 20.dp, bazaEnd = 8.dp)
          val animPlenekranoBordo by animateColorAsState(
            targetValue = if (plenekranoPremita) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.outline,
            animationSpec = tween(150),
            label = "plenekranoBordo"
          )
          val animPlenekranoFono by animateColorAsState(
            targetValue = if (plenekranoPremita) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.primary,
            animationSpec = tween(150),
            label = "plenekranoFono"
          )

          Surface(
            onClick = { viewModel.agordiPlenanEkrananMezurilon(true) },
            interactionSource = plenekranoInterago,
            shape = plenekranoFormo,
            color = animPlenekranoFono,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            border = androidx.compose.foundation.BorderStroke(1.dp, animPlenekranoBordo)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
              Icon(Icons.Default.OpenInFull, contentDescription = null, modifier = Modifier.size(16.dp))
              Text(
                text = tradukoj.ekranaMezurilo,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

    // ⟪ Interaga Caliper Mezurilo ⟫
    KtashKarto {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(
          text = tradukoj.standardaMezurilo,
          color = MaterialTheme.colorScheme.primary,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )

        // Algluo por norma reĝimo kun 1/64 peu butono rekte unu apud la alia
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          listOf(
            AlgluoElekto.LIBERA to tradukoj.algluoLibera,
            AlgluoElekto.PEU_1 to tradukoj.algluoPeu1,
            AlgluoElekto.PEU_8 to tradukoj.algluoPeu8,
            AlgluoElekto.PEU_64 to tradukoj.algluoPeu64
          ).forEach { (elekto, etikedo) ->
            val elektita = elektitaAlgluo == elekto
            val interago = remember { MutableInteractionSource() }
            val premita by interago.collectIsPressedAsState()
            val formo = animaciaButonFormo(premita || elektita, bazaStart = 16.dp, bazaEnd = 6.dp)
            val animBordo by animateColorAsState(
              targetValue = if (elektita) MaterialTheme.colorScheme.primary else if (premita) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.outline,
              animationSpec = tween(150),
              label = "algluoBordo"
            )
            val animFono by animateColorAsState(
              targetValue = if (elektita) MaterialTheme.colorScheme.primary else if (premita) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f) else n2taseButonKoloro(),
              animationSpec = tween(150),
              label = "algluoFono"
            )

            Surface(
              onClick = {
                elektitaAlgluo = elekto
                if (elekto != AlgluoElekto.LIBERA) {
                  kaliproKomencoPx = aplikiAlgluonPx(kaliproKomencoPx, pxPerPeuX, elekto)
                  kaliproFinoPx = aplikiAlgluonPx(kaliproFinoPx, pxPerPeuX, elekto)
                  krudaKaliproKomencoPx = kaliproKomencoPx
                  krudaKaliproFinoPx = kaliproFinoPx
                }
              },
              interactionSource = interago,
              shape = formo,
              color = animFono,
              border = androidx.compose.foundation.BorderStroke(1.dp, animBordo)
            ) {
              Text(
                text = etikedo,
                color = if (elektita) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                fontSize = 10.sp,
                fontWeight = if (elektita) FontWeight.Bold else FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Mezurila Zono
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(130.dp)
          .clip(FormoInsigno)
          .background(MaterialTheme.colorScheme.surfaceVariant)
          .border(1.dp, bordoKoloro, FormoInsigno)
          .pointerInput(elektitaAlgluo, pxPerPeuX) {
            var aktivaTenilo = 0
            detectDragGestures(
              onDragStart = { pozicio ->
                val x = pozicio.x
                val d1 = abs(x - kaliproKomencoPx)
                val d2 = abs(x - kaliproFinoPx)
                aktivaTenilo = if (d1 < d2) 1 else 2
                krudaKaliproKomencoPx = kaliproKomencoPx
                krudaKaliproFinoPx = kaliproFinoPx
              },
              onDrag = { change, dragAmount ->
                change.consume()
                val maxW = (size.width - 10).toFloat().coerceAtLeast(10f)
                if (aktivaTenilo == 1) {
                  krudaKaliproKomencoPx = (krudaKaliproKomencoPx + dragAmount.x).coerceIn(10f, maxW)
                  kaliproKomencoPx = aplikiAlgluonPx(krudaKaliproKomencoPx, pxPerPeuX, elektitaAlgluo)
                } else {
                  krudaKaliproFinoPx = (krudaKaliproFinoPx + dragAmount.x).coerceIn(10f, maxW)
                  kaliproFinoPx = aplikiAlgluonPx(krudaKaliproFinoPx, pxPerPeuX, elektitaAlgluo)
                }
              }
            )
          }
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val w = size.width
          val h = size.height

          // Subskaloj por 1 peu en bazo 8 ( 32 dividoj sen 1/64 markoj )
          val subPx = pxPerPeuX / 32f
          var currX = 0f
          var tickIdx = 0
          while (currX <= w) {
            val isPeu = tickIdx % 32 == 0
            val isHalf = tickIdx % 16 == 0
            val isQuarter = tickIdx % 8 == 0
            val isEighth = tickIdx % 4 == 0
            val isSixteenth = tickIdx % 2 == 0

            val tickH = when {
              isPeu -> 28.dp.toPx()
              isHalf -> 20.dp.toPx()
              isQuarter -> 15.dp.toPx()
              isEighth -> 11.dp.toPx()
              isSixteenth -> 8.dp.toPx()
              else -> 5.dp.toPx()
            }

            val tickColor = when {
              isPeu -> primaraKoloro
              isHalf || isQuarter -> primaraKoloro.copy(alpha = 0.85f)
              isEighth -> bordoKoloro.copy(alpha = 0.95f)
              else -> bordoKoloro.copy(alpha = 0.55f)
            }

            drawLine(
              color = tickColor,
              start = Offset(currX, 0f),
              end = Offset(currX, tickH),
              strokeWidth = if (isPeu) 2.dp.toPx() else 1.dp.toPx()
            )

            drawLine(
              color = tickColor,
              start = Offset(currX, h),
              end = Offset(currX, h - tickH),
              strokeWidth = if (isPeu) 2.dp.toPx() else 1.dp.toPx()
            )

            currX += subPx
            tickIdx++
          }

          val left = min(kaliproKomencoPx, kaliproFinoPx)
          val right = max(kaliproKomencoPx, kaliproFinoPx)

          drawRect(
            color = primaraKoloro.copy(alpha = 0.12f),
            topLeft = Offset(left, 0f),
            size = Size(right - left, h)
          )

          drawLine(
            color = primaraKoloro,
            start = Offset(kaliproKomencoPx, 0f),
            end = Offset(kaliproKomencoPx, h),
            strokeWidth = 3.dp.toPx()
          )

          drawLine(
            color = primaraKoloro,
            start = Offset(kaliproFinoPx, 0f),
            end = Offset(kaliproFinoPx, h),
            strokeWidth = 3.dp.toPx()
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        StatInsigno(
          etikedo = tradukoj.unuoPeu,
          valoro = formatiOksaleAuxDekume(distancoPeu, uzuBazo10, 3, lingvo),
          unuo = tradukoj.unuoPeu,
          modifier = Modifier.weight(1f)
        )
        StatInsigno(
          etikedo = tradukoj.milimetroj,
          valoro = formatiOksaleAuxDekume(distancoMm, uzuBazo10, 1, lingvo),
          unuo = tradukoj.unuoMilimetroSimbolo,
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        StatInsigno(
          etikedo = tradukoj.centimetroj,
          valoro = formatiOksaleAuxDekume(distancoMm / 10.0, uzuBazo10, 2, lingvo),
          unuo = tradukoj.unuoCentimetroSimbolo,
          modifier = Modifier.weight(1f)
        )
        StatInsigno(
          etikedo = tradukoj.c2taPikseloj,
          valoro = formatiOksaleAuxDekume(distancoC2ta, uzuBazo10, 1, lingvo),
          unuo = tradukoj.unuoC2ta,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // ⟪ Universala Distanca & Longa Konvertilo ⟫
    KtashKarto {
      Text(
        text = tradukoj.universalaKonvertilo,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = tradukoj.eniguDistancon,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 12.sp
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = enigoValoro,
        onValueChange = { enigoValoro = it },
        shape = FormoButono,
        label = { Text(tradukoj.eniguDistancon, color = MaterialTheme.colorScheme.onSurfaceVariant) },
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
      com.example.ui.components.KtashCiferoKlavaro(
        nunaValoro = enigoValoro,
        onValoroSanĝita = { enigoValoro = it }
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        listOf(
          "peu" to tradukoj.unuoPeu,
          "c2ta" to tradukoj.c2taPikseloj,
          "m" to tradukoj.metroj,
          "km" to tradukoj.kilometroj
        ).forEach { (unuoKodo, unuoEtikedo) ->
          val elektita = enigoUnuo == unuoKodo
          val interago = remember { MutableInteractionSource() }
          val premita by interago.collectIsPressedAsState()
          val formo = animaciaButonFormo(premita || elektita, bazaStart = 16.dp, bazaEnd = 4.dp)
          val animBordo by animateColorAsState(
            targetValue = if (elektita) MaterialTheme.colorScheme.primary else if (premita) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.outline,
            animationSpec = tween(150),
            label = "unuoBordo"
          )
          val animFono by animateColorAsState(
            targetValue = if (elektita) MaterialTheme.colorScheme.primary else if (premita) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f) else n2taseButonKoloro(),
            animationSpec = tween(150),
            label = "unuoFono"
          )

          Surface(
            onClick = { enigoUnuo = unuoKodo },
            interactionSource = interago,
            shape = formo,
            color = animFono,
            border = androidx.compose.foundation.BorderStroke(1.dp, animBordo),
            modifier = Modifier.weight(1f)
          ) {
            Text(
              text = unuoEtikedo,
              color = if (elektita) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
              fontSize = 10.sp,
              fontWeight = if (elektita) FontWeight.Bold else FontWeight.Medium,
              modifier = Modifier.padding(vertical = 8.dp),
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      val krudaEnigo = analiziEnigonNombro(enigoValoro, uzuBazo10).let { if (it == 0.0 && enigoValoro.isBlank()) 1.0 else it }

      val valoroMetroj = when (enigoUnuo) {
        "peu" -> peuAlMetroj(krudaEnigo)
        "c2ta" -> c2taAlMetroj(krudaEnigo)
        "m" -> krudaEnigo
        "km" -> krudaEnigo * 1000.0
        else -> krudaEnigo
      }

      Spacer(modifier = Modifier.height(12.dp))

      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text(tradukoj.unuoPeu, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
          Text(
            text = "${formatiOksaleAuxDekume(metrojAlPeu(valoroMetroj), uzuBazo10, 4, lingvo)} ${tradukoj.unuoPeu}",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text(tradukoj.c2taPikseloj, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
          Text(
            text = "${formatiOksaleAuxDekume(metrojAlC2ta(valoroMetroj), uzuBazo10, 2, lingvo)} ${tradukoj.unuoC2ta}",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text(tradukoj.metroj, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
          Text(
            text = "${formatiOksaleAuxDekume(valoroMetroj, uzuBazo10, 4, lingvo)} ${tradukoj.unuoMetroSimbolo}",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp
          )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text(tradukoj.kilometroj, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
          Text(
            text = "${formatiOksaleAuxDekume(valoroMetroj / 1000.0, uzuBazo10, 6, lingvo)} ${tradukoj.unuoKilometroSimbolo}",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(100.dp))
  }
}
}
