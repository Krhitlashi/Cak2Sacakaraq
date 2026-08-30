package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.data.KtashTagoIdentigilo
import com.example.data.LokoLogEntity
import com.example.ktash.*
import com.example.ui.ANTARKTAJ_STACIOJ
import com.example.ui.EsplorStacio
import com.example.ui.KtashViewModel
import com.example.ui.components.N2taseButono
import com.example.ui.components.N2taseKarto
import com.example.ui.components.StatInsigno
import com.example.ui.components.animaciaButonFormo
import com.example.ui.components.n2taseBordaKoloro
import com.example.ui.components.n2taseButonKoloro
import com.example.ui.components.n2taseFonKoloro
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.math.*

// ≺⧼ Mapo & Reala Map-Ekrano 🗺️ ⧽≻

@Composable
fun MapoEkrano(
  viewModel: KtashViewModel,
  modifier: Modifier = Modifier
) {
  val nunaLoko by viewModel.lokoManagero.nunaLoko.collectAsState()
  val distinctTagoj by viewModel.distinctTagoj.collectAsState()
  val elektitaTago by viewModel.elektitaTago.collectAsState()
  val filtritajProtokoloj by viewModel.filtritajProtokoloj.collectAsState()
  val elektitaPunkto by viewModel.elektitaPunkto.collectAsState()
  val konservitajLokoj by viewModel.konservitajLokoj.collectAsState()
  val uzuBazo10 by viewModel.uzuBazo10.collectAsState()
  val tradukoj by viewModel.tradukoj.collectAsState()
  val lingvo by viewModel.elektitaLingvo.collectAsState()

  val coroutineScope = rememberCoroutineScope()

  // Glataj Animacieblaj Pozicioj por Movado & Zomo
  val animLat = remember { Animatable(nunaLoko.latitudo.toFloat()) }
  val animLon = remember { Animatable(nunaLoko.longitudo.toFloat()) }
  val animZomo = remember { Animatable(4.0f) }

  fun glateMoviAl(celLat: Double, celLon: Double, celZomo: Float? = null) {
    coroutineScope.launch {
      launch {
        animLat.animateTo(
          targetValue = celLat.toFloat(),
          animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
        )
      }
      launch {
        animLon.animateTo(
          targetValue = celLon.toFloat(),
          animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
        )
      }
      if (celZomo != null) {
        launch {
          animZomo.animateTo(
            targetValue = celZomo.coerceIn(1.0f, 18.0f),
            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
          )
        }
      }
    }
  }

  // Ĝisdatigi centron se unuafoje ŝarĝita kun reala GPS
  LaunchedEffect(nunaLoko.latitudo, nunaLoko.longitudo) {
    if (animLat.value == 47.48f && animLon.value == -122.21f && nunaLoko.latitudo != 47.48) {
      glateMoviAl(nunaLoko.latitudo, nunaLoko.longitudo)
    }
  }

  val sumaDistancoPeu = remember(filtritajProtokoloj) {
    filtritajProtokoloj.sumOf { it.distancoDeAntauaPeu }
  }

  val context = LocalContext.current
  val subdomajnoj = remember { listOf("a", "b", "c") }

  val centroLat = animLat.value.toDouble()
  val centroLon = animLon.value.toDouble()
  val zomoNivelo = animZomo.value

  // Mapo bazo fona koloro por eviti nigrajn truojn dum ŝarĝado de kaheloj
  val estasMalhela = MaterialTheme.colorScheme.background == KoloroFonoMalhela || MaterialTheme.colorScheme.background.red < 0.5f
  val fonoMapKahelo = if (estasMalhela) Color(0xFF161A20) else Color(0xFFE4E8EB)

  BoxWithConstraints(
    modifier = modifier
      .fillMaxSize()
      .background(fonoMapKahelo)
  ) {
    val screenWidthPx = constraints.maxWidth.toFloat()
    val screenHeightPx = constraints.maxHeight.toFloat()
    val density = LocalDensity.current

    val intZoom = zomoNivelo.toInt().coerceIn(1, 18)
    val tileScale = 2.0.pow((zomoNivelo - intZoom).toDouble()).toFloat()
    val baseTileSize = 256f * density.density
    val scaledTileSize = (baseTileSize * tileScale).coerceAtLeast(16f)

    val totalTiles = 1 shl intZoom

    // Mercator koordinatoj de centro ĉe intZoom
    val centerMercX = ((centroLon + 180.0) / 360.0) * totalTiles
    val latRad = Math.toRadians(centroLat.coerceIn(-85.0511, 85.0511))
    val secVal = 1.0 / cos(latRad)
    val tanVal = tan(latRad)
    val sumVal = tanVal + secVal
    val centerMercY = if (sumVal > 0.0) {
      ((1.0 - ln(sumVal) / Math.PI) / 2.0 * totalTiles).let { if (it.isNaN() || it.isInfinite()) 0.0 else it }
    } else {
      0.0
    }

    // Kalkuli videblajn kahelajn indeksojn kun sekura bufro
    val minTileX = floor(centerMercX - (screenWidthPx / 2f) / scaledTileSize).toInt() - 2
    val maxTileX = ceil(centerMercX + (screenWidthPx / 2f) / scaledTileSize).toInt() + 2

    val minTileY = (floor(centerMercY - (screenHeightPx / 2f) / scaledTileSize).toInt() - 2).coerceIn(0, totalTiles - 1)
    val maxTileY = (ceil(centerMercY + (screenHeightPx / 2f) / scaledTileSize).toInt() + 2).coerceIn(minTileY, totalTiles - 1)

    // ⟪ Gestoj por Zomo & Movado ( Sen Distorda Trenado Dum Zomado & Preciza Fokuso ) ⟫
    Box(
      modifier = Modifier
        .fillMaxSize()
        .clipToBounds()
        .pointerInput(Unit) {
          detectTransformGestures { centroid, pan, zoom, _ ->
            val malnovaZomo = animZomo.value
            val novaZomo = (malnovaZomo + log2(zoom)).coerceIn(1.0f, 18.0f)

            val malnovaSkalo = 2.0.pow(malnovaZomo.toDouble()) * 256.0 * density.density
            val novaSkalo = 2.0.pow(novaZomo.toDouble()) * 256.0 * density.density

            val cx = centroid.x - (screenWidthPx / 2f)
            val cy = centroid.y - (screenHeightPx / 2f)

            val curLatRad = Math.toRadians(animLat.value.toDouble().coerceIn(-85.0, 85.0))
            val curSec = 1.0 / cos(curLatRad)
            val curTan = tan(curLatRad)
            val curSum = curTan + curSec
            val curMercY = if (curSum > 0.0) {
              ((1.0 - ln(curSum) / Math.PI) / 2.0).let { if (it.isNaN() || it.isInfinite()) 0.5 else it }
            } else {
              0.5
            }
            val curMercX = ((animLon.value.toDouble() + 180.0) / 360.0).let { ((it % 1.0) + 1.0) % 1.0 }

            val novaMercX = curMercX + (cx / malnovaSkalo) - ((cx + pan.x) / novaSkalo)
            val novaMercY = (curMercY + (cy / malnovaSkalo) - ((cy + pan.y) / novaSkalo)).coerceIn(0.0001, 0.9999)

            val sinhVal = Math.sinh(Math.PI * (1.0 - 2.0 * novaMercY))
            val newLat = Math.toDegrees(Math.atan(sinhVal)).coerceIn(-85.0, 85.0)

            var nextLon = (novaMercX * 360.0) - 180.0
            while (nextLon > 180.0) nextLon -= 360.0
            while (nextLon < -180.0) nextLon += 360.0

            coroutineScope.launch {
              if (!newLat.isNaN()) {
                animLat.snapTo(newLat.toFloat())
              }
              animLon.snapTo(nextLon.toFloat())
              animZomo.snapTo(novaZomo)
            }
          }
        }
    ) {
      // ⟪ Antaŭa / Patra Kahela Tavolo por Seninterrompa Zomado kaj Nul Nigraj Truoj ⟫
      if (intZoom > 1) {
        val parentZoom = intZoom - 1
        val parentTotalTiles = 1 shl parentZoom
        val parentScale = 2.0.pow((zomoNivelo - parentZoom).toDouble()).toFloat()
        val parentScaledTileSize = (baseTileSize * parentScale).coerceAtLeast(8f)
        val parentCenterMercX = ((centroLon + 180.0) / 360.0) * parentTotalTiles
        val parentCenterMercY = if (sumVal > 0.0) {
          ((1.0 - ln(sumVal) / Math.PI) / 2.0 * parentTotalTiles).let { if (it.isNaN() || it.isInfinite()) 0.0 else it }
        } else {
          0.0
        }

        val pMinX = floor(parentCenterMercX - (screenWidthPx / 2f) / parentScaledTileSize).toInt() - 1
        val pMaxX = ceil(parentCenterMercX + (screenWidthPx / 2f) / parentScaledTileSize).toInt() + 1
        val pMinY = (floor(parentCenterMercY - (screenHeightPx / 2f) / parentScaledTileSize).toInt() - 1).coerceIn(0, parentTotalTiles - 1)
        val pMaxY = (ceil(parentCenterMercY + (screenHeightPx / 2f) / parentScaledTileSize).toInt() + 1).coerceIn(pMinY, parentTotalTiles - 1)

        val parentTileW = ceil(parentScaledTileSize).toInt() + 2
        val parentTileH = ceil(parentScaledTileSize).toInt() + 2

        for (pY in pMinY..pMaxY) {
          for (pX in pMinX..pMaxX) {
            key("parent", parentZoom, pX, pY) {
              val wrappedX = ((pX % parentTotalTiles) + parentTotalTiles) % parentTotalTiles
              val sub = subdomajnoj[abs((pX + pY) % subdomajnoj.size)]
              val tileUrl = "https://$sub.tile.openstreetmap.org/$parentZoom/$wrappedX/$pY.png"

              val bildPetado = remember(tileUrl) {
                ImageRequest.Builder(context)
                  .data(tileUrl)
                  .setHeader("User-Agent", "KtashTrackerApp/1.0 (Android; OpenStreetMap)")
                  .setHeader("Referer", "https://krhitlashi.github.io/")
                  .memoryCachePolicy(CachePolicy.ENABLED)
                  .diskCachePolicy(CachePolicy.ENABLED)
                  .crossfade(false)
                  .build()
              }

              val leftPx = ((screenWidthPx / 2f) + ((pX - parentCenterMercX) * parentScaledTileSize)).roundToInt()
              val topPx = ((screenHeightPx / 2f) + ((pY - parentCenterMercY) * parentScaledTileSize)).roundToInt()

              AsyncImage(
                model = bildPetado,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                  .offset { androidx.compose.ui.unit.IntOffset(leftPx, topPx) }
                  .size(with(density) { parentTileW.toDp() }, with(density) { parentTileH.toDp() })
              )
            }
          }
        }
      }

      // ⟪ Kahela Map-Tegolo ( OpenStreetMap - Kun Kaŝmemoro & Sen Nigraj Truoj ) ⟫
      val tileW = ceil(scaledTileSize).toInt() + 2
      val tileH = ceil(scaledTileSize).toInt() + 2

      for (tileY in minTileY..maxTileY) {
        for (tileX in minTileX..maxTileX) {
          key("active", intZoom, tileX, tileY) {
            val wrappedX = ((tileX % totalTiles) + totalTiles) % totalTiles
            val sub = subdomajnoj[abs((tileX + tileY) % subdomajnoj.size)]
            val tileUrl = "https://$sub.tile.openstreetmap.org/$intZoom/$wrappedX/$tileY.png"

            val bildPetado = remember(tileUrl) {
              ImageRequest.Builder(context)
                .data(tileUrl)
                .setHeader("User-Agent", "KtashTrackerApp/1.0 (Android; OpenStreetMap)")
                .setHeader("Referer", "https://krhitlashi.github.io/")
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .crossfade(false)
                .build()
            }

            val leftPx = ((screenWidthPx / 2f) + ((tileX - centerMercX) * scaledTileSize)).roundToInt()
            val topPx = ((screenHeightPx / 2f) + ((tileY - centerMercY) * scaledTileSize)).roundToInt()

            AsyncImage(
              model = bildPetado,
              contentDescription = null,
              contentScale = ContentScale.Crop,
              modifier = Modifier
                .offset { androidx.compose.ui.unit.IntOffset(leftPx, topPx) }
                .size(with(density) { tileW.toDp() }, with(density) { tileH.toDp() })
            )
          }
        }
      }

      // ⟪ Kanvasa Supermeto por Voja Linio & Ktash Krado ⟫
      Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Funkcio por konverti Lat/Lon al Ekranaj Pikseloj
        fun latLonAlEkrano(lat: Double, lon: Double): Offset {
          val safeLat = lat.coerceIn(-85.0511, 85.0511)
          val safeLon = if (lon.isNaN()) 0.0 else lon
          val ptMercX = ((safeLon + 180.0) / 360.0) * totalTiles
          val ptLatRad = Math.toRadians(safeLat)
          val secP = 1.0 / cos(ptLatRad)
          val tanP = tan(ptLatRad)
          val sumP = tanP + secP
          val ptMercY = if (sumP > 0.0) {
            ((1.0 - ln(sumP) / Math.PI) / 2.0 * totalTiles).let { if (it.isNaN() || it.isInfinite()) 0.0 else it }
          } else {
            0.0
          }

          val px = ((w / 2f) + ((ptMercX - centerMercX) * scaledTileSize).toFloat()).let { if (it.isNaN() || it.isInfinite()) 0f else it }
          val py = ((h / 2f) + ((ptMercY - centerMercY) * scaledTileSize).toFloat()).let { if (it.isNaN() || it.isInfinite()) 0f else it }
          return Offset(px, py)
        }

        // 1. Desegni Ktash Latitudajn & Longitudajn Liniojn
        val kradoKoloro = Color(0x30888888)
        for (latG in -80..80 step 20) {
          val p1 = latLonAlEkrano(latG.toDouble(), -180.0)
          val p2 = latLonAlEkrano(latG.toDouble(), 180.0)
          drawLine(kradoKoloro, Offset(0f, p1.y), Offset(w, p2.y), strokeWidth = 1.dp.toPx())
        }
        for (lonG in -180..180 step 30) {
          val p1 = latLonAlEkrano(85.0, lonG.toDouble())
          val p2 = latLonAlEkrano(-85.0, lonG.toDouble())
          drawLine(kradoKoloro, Offset(p1.x, 0f), Offset(p2.x, h), strokeWidth = 1.dp.toPx())
        }

        // 2. Desegni Registritajn Spurpikojn kaj Voj-liniojn
        if (filtritajProtokoloj.isNotEmpty()) {
          val vojo = Path()
          var unua = true
          filtritajProtokoloj.forEach { ero ->
            val pos = latLonAlEkrano(ero.latitudo, ero.longitudo)
            if (unua) {
              vojo.moveTo(pos.x, pos.y)
              unua = false
            } else {
              vojo.lineTo(pos.x, pos.y)
            }
          }

          // Vojo Linio kun Glata Larĝo
          drawPath(
            path = vojo,
            color = Color(0xFF58A038),
            style = Stroke(width = 4.dp.toPx())
          )

          // Voj-punktoj
          filtritajProtokoloj.forEach { ero ->
            val pos = latLonAlEkrano(ero.latitudo, ero.longitudo)
            val estasElektita = elektitaPunkto?.id == ero.id
            drawCircle(
              color = if (estasElektita) Color(0xFFFFFFFF) else Color(0xFF58A038),
              radius = if (estasElektita) 7.dp.toPx() else 4.dp.toPx(),
              center = pos
            )
            drawCircle(
              color = Color(0xFF000000),
              radius = if (estasElektita) 7.dp.toPx() else 4.dp.toPx(),
              center = pos,
              style = Stroke(width = 1.5.dp.toPx())
            )
          }
        }

        // 3. Desegni Nunan Lokon
        val nunaPos = latLonAlEkrano(nunaLoko.latitudo, nunaLoko.longitudo)
        // Pulsanta ringo
        drawCircle(
          color = Color(0x4058A038),
          radius = 16.dp.toPx(),
          center = nunaPos
        )
        drawCircle(
          color = Color(0xFF58A038),
          radius = 6.dp.toPx(),
          center = nunaPos
        )
        drawCircle(
          color = Color(0xFFFFFFFF),
          radius = 2.dp.toPx(),
          center = nunaPos
        )

        // 4. Desegni Konservitajn Esplorajn Staciojn
        konservitajLokoj.forEach { stacio ->
          val pos = latLonAlEkrano(stacio.latitudo, stacio.longitudo)
          drawCircle(
            color = Color(0xFFE08838),
            radius = 5.dp.toPx(),
            center = pos
          )
        }
      }
    }

    // ⟪ Supraj Regiloj ( n2tase Flosanta Panelo por Tago-filtro ) ⟫
    Box(
      modifier = Modifier
        .align(Alignment.TopCenter)
        .padding(horizontal = 14.dp, vertical = 6.dp)
        .widthIn(max = 540.dp)
        .fillMaxWidth()
    ) {
      N2taseKarto(shape = FormoKartoGranda) {
        // Tago-elektilo
        Text(
          text = tradukoj.tago,
          color = MaterialTheme.colorScheme.primary,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))

        LazyRow(
          modifier = Modifier
            .fillMaxWidth()
            .clip(FormoSkroloMaskaHorizontala),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp)
        ) {
          item {
            val elektita = elektitaTago == null
            val interago = remember { MutableInteractionSource() }
            val premita by interago.collectIsPressedAsState()
            val formo = animaciaButonFormo(premita || elektita, bazaStart = 16.dp, bazaEnd = 4.dp)
            val animBordo by animateColorAsState(
              targetValue = if (elektita) MaterialTheme.colorScheme.primary else if (premita) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.outline,
              animationSpec = tween(150),
              label = "tagoBordo"
            )
            val animFono by animateColorAsState(
              targetValue = if (elektita) MaterialTheme.colorScheme.primary else if (premita) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f) else n2taseButonKoloro(),
              animationSpec = tween(150),
              label = "tagoFono"
            )

            Surface(
              onClick = { viewModel.elektiTagonPorFiltro(null) },
              interactionSource = interago,
              shape = formo,
              color = animFono,
              border = androidx.compose.foundation.BorderStroke(1.dp, animBordo)
            ) {
              Text(
                text = tradukoj.ciujTagoj,
                color = if (elektita) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }

          items(distinctTagoj) { tago ->
            val elektita = elektitaTago == tago
            val interago = remember { MutableInteractionSource() }
            val premita by interago.collectIsPressedAsState()
            val formo = animaciaButonFormo(premita || elektita, bazaStart = 16.dp, bazaEnd = 4.dp)
            val animBordo by animateColorAsState(
              targetValue = if (elektita) MaterialTheme.colorScheme.primary else if (premita) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.outline,
              animationSpec = tween(150),
              label = "tagoBordo"
            )
            val animFono by animateColorAsState(
              targetValue = if (elektita) MaterialTheme.colorScheme.primary else if (premita) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f) else n2taseButonKoloro(),
              animationSpec = tween(150),
              label = "tagoFono"
            )

            Surface(
              onClick = { viewModel.elektiTagonPorFiltro(tago) },
              interactionSource = interago,
              shape = formo,
              color = animFono,
              border = androidx.compose.foundation.BorderStroke(1.dp, animBordo)
            ) {
              Text(
                text = tago.alTeksto(uzuBazo10, lingvo),
                color = if (elektita) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Distanco & Punktoj resumo
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "${tradukoj.registritajPunktoj} - ${if (uzuBazo10) tradukiCiferojn(filtritajProtokoloj.size.toString(), lingvo) else vab6caja(filtritajProtokoloj.size.toLong())}",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
          )
          Text(
            text = "${tradukoj.distanco} - ${formatiOksaleAuxDekume(sumaDistancoPeu, uzuBazo10, 2, lingvo)} ${tradukoj.unuoPeu}",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    // ⟪ Flankaj Zom-Regiloj & Mia Loko ( Dekstre, Vera n2tase ) ⟫
    Column(
      modifier = Modifier
        .align(Alignment.CenterEnd)
        .padding(end = 12.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      N2taseButono(
        onClick = {
          val novaZ = (animZomo.value + 1.0f).coerceIn(1.0f, 18.0f)
          coroutineScope.launch {
            animZomo.animateTo(novaZ, animationSpec = tween(300, easing = FastOutSlowInEasing))
          }
        },
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.size(48.dp)
      ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = tradukoj.zomiEnen,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(22.dp)
          )
        }
      }

      N2taseButono(
        onClick = {
          val novaZ = (animZomo.value - 1.0f).coerceIn(1.0f, 18.0f)
          coroutineScope.launch {
            animZomo.animateTo(novaZ, animationSpec = tween(300, easing = FastOutSlowInEasing))
          }
        },
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.size(48.dp)
      ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.Remove,
            contentDescription = tradukoj.zomiEksteren,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(22.dp)
          )
        }
      }

      N2taseButono(
        onClick = {
          glateMoviAl(nunaLoko.latitudo, nunaLoko.longitudo, 15.0f)
        },
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.size(48.dp)
      ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.MyLocation,
            contentDescription = tradukoj.miaLoko,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(22.dp)
          )
        }
      }
    }

    // ⟪ Malsupra Panelo por Esploraj Stacioj & Elektita Punkto ( Rekte super la Flosanta Navigadstango kun Sekura Interspaco ) ⟫
    Column(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .navigationBarsPadding()
        .padding(bottom = 84.dp)
        .padding(horizontal = 14.dp)
        .fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Stacioj rapida elekto kun n2tase flosantaj butonoj
      LazyRow(
        modifier = Modifier
          .fillMaxWidth()
          .clip(FormoSkroloMaskaHorizontala),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp)
      ) {
        items(ANTARKTAJ_STACIOJ) { stacio ->
          N2taseButono(
            onClick = {
              glateMoviAl(stacio.latitudo, stacio.longitudo, 6.0f)
            },
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 7.dp)
          ) {
            Text(
              text = stacio.nomo,
              color = MaterialTheme.colorScheme.onSurface,
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }

      // Detaloj de elektita punkto
      elektitaPunkto?.let { punkto ->
        N2taseKarto {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = punkto.ksakaNomo,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "${tradukoj.latina} - ${punkto.latinaNomo}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
              )
            }
            Surface(
              onClick = { viewModel.elektiPunkton(null) },
              shape = FormoButono,
              color = n2taseButonKoloro(),
              border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
              Text(
                text = tradukoj.fermi,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }
      }
    }
  }
}

