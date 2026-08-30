package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// ≺⧼ Temo 🎨 ⧽≻

enum class TemoModo {
  SISTEMA,
  HELA,
  MALHELA
}

private val MalhelaKolorSkemo = darkColorScheme(
  primary = KoloroTekstoMalhela,
  onPrimary = KoloroFonoMalhela,
  primaryContainer = KoloroKartoMalhelaKele,
  onPrimaryContainer = KoloroTekstoMalhela,
  secondary = KoloroTekstoDuaMalhela,
  onSecondary = KoloroFonoMalhela,
  secondaryContainer = KoloroKartoMalhela,
  onSecondaryContainer = KoloroTekstoMalhela,
  tertiary = KoloroTekstoDuaMalhela,
  background = KoloroFonoMalhela,
  onBackground = KoloroTekstoMalhela,
  surface = KoloroKartoMalhelaKele,
  onSurface = KoloroTekstoMalhela,
  surfaceVariant = KoloroKartoMalhelaKele,
  onSurfaceVariant = KoloroTekstoDuaMalhela,
  outline = KoloroBordoMalhela,
  outlineVariant = KoloroBordoMalhela2
)

private val HelaKolorSkemo = lightColorScheme(
  primary = KoloroTekstoHela,
  onPrimary = KoloroFonoHela,
  primaryContainer = KoloroKartoHelaKele,
  onPrimaryContainer = KoloroTekstoHela,
  secondary = KoloroTekstoDuaHela,
  onSecondary = KoloroFonoHela,
  secondaryContainer = KoloroKartoHela,
  onSecondaryContainer = KoloroTekstoHela,
  tertiary = KoloroTekstoDuaHela,
  background = KoloroFonoHela,
  onBackground = KoloroTekstoHela,
  surface = KoloroKartoHelaKele,
  onSurface = KoloroTekstoHela,
  surfaceVariant = KoloroKartoHelaKele,
  onSurfaceVariant = KoloroTekstoDuaHela,
  outline = KoloroBordoHela,
  outlineVariant = KoloroBordoHela2
)

@Composable
fun KtashTrackerTheme(
  temoModo: TemoModo = TemoModo.SISTEMA,
  uzuMaterialYou: Boolean = false,
  uzuPropraTiparo: Boolean = true,
  content: @Composable () -> Unit
) {
  val sistemaMalhela = isSystemInDarkTheme()
  val ĉuMalhela = when (temoModo) {
    TemoModo.SISTEMA -> sistemaMalhela
    TemoModo.HELA -> false
    TemoModo.MALHELA -> true
  }

  val context = LocalContext.current
  val ĉuSubtenasMaterialYou = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

  val kolorSkemo = when {
    uzuMaterialYou && ĉuSubtenasMaterialYou -> {
      val dinamika = if (ĉuMalhela) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      if (ĉuMalhela) {
        MalhelaKolorSkemo.copy(
          primary = dinamika.primary,
          onPrimary = dinamika.onPrimary,
          secondary = dinamika.secondary,
          onSecondary = dinamika.onSecondary,
          tertiary = dinamika.tertiary,
          onTertiary = dinamika.onTertiary,
          // Konservi la precizajn តានេក kaj fono valorojn
          surface = KoloroKartoMalhelaKele,
          onSurface = KoloroTekstoMalhela,
          surfaceVariant = KoloroKartoMalhelaKele,
          onSurfaceVariant = KoloroTekstoDuaMalhela,
          secondaryContainer = KoloroKartoMalhela,
          onSecondaryContainer = KoloroTekstoMalhela,
          background = KoloroFonoMalhela,
          onBackground = KoloroTekstoMalhela,
          outline = KoloroBordoMalhela,
          outlineVariant = KoloroBordoMalhela2
        )
      } else {
        HelaKolorSkemo.copy(
          primary = dinamika.primary,
          onPrimary = dinamika.onPrimary,
          secondary = dinamika.secondary,
          onSecondary = dinamika.onSecondary,
          tertiary = dinamika.tertiary,
          onTertiary = dinamika.onTertiary,
          // Konservi la precizajn តានេក kaj fono valorojn
          surface = KoloroKartoHelaKele,
          onSurface = KoloroTekstoHela,
          surfaceVariant = KoloroKartoHelaKele,
          onSurfaceVariant = KoloroTekstoDuaHela,
          secondaryContainer = KoloroKartoHela,
          onSecondaryContainer = KoloroTekstoHela,
          background = KoloroFonoHela,
          onBackground = KoloroTekstoHela,
          outline = KoloroBordoHela,
          outlineVariant = KoloroBordoHela2
        )
      }
    }
    ĉuMalhela -> MalhelaKolorSkemo
    else -> HelaKolorSkemo
  }

  val tiparo = kreuTiparon(uzuPropraTiparo = uzuPropraTiparo, kunteksto = context)

  MaterialTheme(
    colorScheme = kolorSkemo,
    typography = tiparo,
    shapes = KtashShapes,
    content = content
  )
}
