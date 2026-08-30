package com.example.ui.screens

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

// ≺⧼ Mondaj Teraj Konturoj 🌍 ⧽≻

data class KontinentoPoligono(
  val punktoj: List<Pair<Double, Double>> // Pair(lat, lon)
)

val MONDO_POLIGONOJ: List<KontinentoPoligono> = listOf(
  // ⟪ Nord-Ameriko ⟫
  KontinentoPoligono(listOf(
    71.0 to -156.0, 70.0 to -130.0, 68.0 to -100.0, 60.0 to -80.0, 58.0 to -65.0,
    47.0 to -53.0, 44.0 to -64.0, 41.0 to -70.0, 35.0 to -75.0, 25.0 to -80.0,
    30.0 to -85.0, 29.0 to -95.0, 26.0 to -97.0, 20.0 to -97.0, 16.0 to -93.0,
    15.0 to -88.0, 9.0 to -79.0, 8.5 to -83.5, 14.0 to -92.0, 20.0 to -105.0,
    23.0 to -110.0, 32.0 to -117.0, 38.0 to -123.0, 48.0 to -124.0, 55.0 to -132.0,
    60.0 to -145.0, 65.0 to -168.0, 71.0 to -156.0
  )),
  // ⟪ Sud-Ameriko ⟫
  KontinentoPoligono(listOf(
    12.0 to -72.0, 10.5 to -62.0, 5.0 to -52.0, -2.0 to -44.0, -5.0 to -35.0,
    -13.0 to -38.0, -23.0 to -43.0, -34.0 to -53.0, -40.0 to -62.0, -53.0 to -68.0,
    -55.0 to -66.0, -52.0 to -74.0, -42.0 to -74.0, -33.0 to -72.0, -18.0 to -70.0,
    -5.0 to -81.0, 1.0 to -80.0, 8.0 to -77.0, 12.0 to -72.0
  )),
  // ⟪ Eŭrazio & Norda Afriko Ligo ⟫
  KontinentoPoligono(listOf(
    36.0 to -6.0, 43.0 to -9.0, 48.0 to -4.0, 53.0 to 5.0, 58.0 to 6.0,
    62.0 to 5.0, 71.0 to 28.0, 69.0 to 60.0, 73.0 to 80.0, 76.0 to 110.0,
    72.0 to 140.0, 66.0 to 170.0, 60.0 to 162.0, 53.0 to 142.0, 43.0 to 132.0,
    38.0 to 119.0, 30.0 to 122.0, 22.0 to 114.0, 21.0 to 108.0, 10.0 to 104.0,
    1.0 to 104.0, 13.0 to 100.0, 22.0 to 90.0, 13.0 to 80.0, 8.0 to 77.0,
    20.0 to 73.0, 25.0 to 67.0, 25.0 to 57.0, 30.0 to 48.0, 31.0 to 35.0,
    37.0 to 36.0, 41.0 to 29.0, 38.0 to 23.0, 40.0 to 18.0, 44.0 to 12.0,
    43.0 to 6.0, 36.0 to -6.0
  )),
  // ⟪ Afriko ⟫
  KontinentoPoligono(listOf(
    35.0 to -6.0, 37.0 to 10.0, 32.0 to 24.0, 31.0 to 32.0, 22.0 to 37.0,
    12.0 to 44.0, 12.0 to 51.0, -4.0 to 39.0, -15.0 to 40.0, -26.0 to 33.0,
    -34.0 to 26.0, -34.0 to 18.0, -23.0 to 14.0, -12.0 to 13.0, 4.0 to 9.0,
    5.0 to 1.0, 5.0 to -7.0, 10.0 to -13.0, 15.0 to -17.0, 21.0 to -17.0,
    28.0 to -13.0, 35.0 to -6.0
  )),
  // ⟪ Aŭstralio ⟫
  KontinentoPoligono(listOf(
    -12.0 to 136.0, -11.0 to 142.0, -18.0 to 146.0, -25.0 to 153.0, -33.0 to 152.0,
    -38.0 to 146.0, -38.0 to 140.0, -32.0 to 132.0, -35.0 to 117.0, -32.0 to 115.0,
    -22.0 to 114.0, -17.0 to 122.0, -14.0 to 129.0, -12.0 to 136.0
  )),
  // ⟪ Gronlando ⟫
  KontinentoPoligono(listOf(
    77.0 to -19.0, 83.0 to -30.0, 81.0 to -60.0, 76.0 to -68.0, 65.0 to -52.0,
    60.0 to -44.0, 65.0 to -37.0, 71.0 to -22.0, 77.0 to -19.0
  )),
  // ⟪ Britio & Irlando ⟫
  KontinentoPoligono(listOf(
    58.0 to -5.0, 58.0 to -2.0, 53.0 to 0.0, 51.0 to 1.0, 50.0 to -5.0,
    54.0 to -3.0, 58.0 to -5.0
  )),
  // ⟪ Madagaskaro ⟫
  KontinentoPoligono(listOf(
    -12.0 to 49.0, -16.0 to 50.0, -25.0 to 47.0, -25.0 to 44.0, -16.0 to 44.0,
    -12.0 to 49.0
  )),
  // ⟪ Japanio ⟫
  KontinentoPoligono(listOf(
    45.0 to 142.0, 43.0 to 145.0, 38.0 to 141.0, 35.0 to 140.0, 33.0 to 132.0,
    36.0 to 136.0, 41.0 to 140.0, 45.0 to 142.0
  )),
  // ⟪ Antarkto ( Rimarkinde grava por Ktash stacioj ) ⟫
  KontinentoPoligono(listOf(
    -63.0 to -57.0, -68.0 to -67.0, -74.0 to -80.0, -76.0 to -120.0, -75.0 to -150.0,
    -78.0 to 166.0, -72.0 to 170.0, -66.0 to 140.0, -66.0 to 110.0, -68.0 to 80.0,
    -69.0 to 40.0, -70.0 to 10.0, -72.0 to -20.0, -75.0 to -40.0, -63.0 to -57.0
  ))
)

fun DrawScope.desegniMondMapon(
  larĝo: Float,
  alto: Float,
  centroLat: Double,
  centroLon: Double,
  pikselojPorGrado: Float,
  teroFonoKoloro: Color,
  teroBordoKoloro: Color
) {
  val centroX = larĝo / 2f
  val centroY = alto / 2f

  fun latLonAlOffset(lat: Double, lon: Double): Offset {
    var dLon = lon - centroLon
    while (dLon > 180.0) dLon -= 360.0
    while (dLon < -180.0) dLon += 360.0
    val x = centroX + (dLon.toFloat() * pikselojPorGrado)
    val y = centroY - ((lat - centroLat).toFloat() * pikselojPorGrado)
    return Offset(x, y)
  }

  MONDO_POLIGONOJ.forEach { poligono ->
    if (poligono.punktoj.isNotEmpty()) {
      val path = Path()
      val unua = latLonAlOffset(poligono.punktoj[0].first, poligono.punktoj[0].second)
      path.moveTo(unua.x, unua.y)
      for (i in 1 until poligono.punktoj.size) {
        val p = latLonAlOffset(poligono.punktoj[i].first, poligono.punktoj[i].second)
        path.lineTo(p.x, p.y)
      }
      path.close()

      // Fono de tero
      drawPath(
        path = path,
        color = teroFonoKoloro,
        style = Fill
      )
      // Bordo de tero
      drawPath(
        path = path,
        color = teroBordoKoloro,
        style = Stroke(width = 1.2.dp.toPx())
      )
    }
  }
}
