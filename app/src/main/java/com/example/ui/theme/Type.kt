package com.example.ui.theme

import android.content.Context
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.example.R

// ≺⧼ Tiparo j͑ʃꞇȝ 🔤 ⧽≻

private var konservitaFontFamilio: FontFamily? = null

fun akiriJsht3FontFamilion(kunteksto: Context? = null): FontFamily {
  konservitaFontFamilio?.let { return it }
  if (kunteksto != null) {
    try {
      val androidTypeface = ResourcesCompat.getFont(kunteksto, R.font.ktash_font)
      if (androidTypeface != null) {
        val familio = FontFamily(androidx.compose.ui.text.font.Typeface(androidTypeface))
        konservitaFontFamilio = familio
        return familio
      }
    } catch (_: Throwable) {
      // Sekura rezervo
    }
  }
  return try {
    val f = FontFamily(Font(R.font.ktash_font))
    konservitaFontFamilio = f
    f
  } catch (_: Throwable) {
    FontFamily.Default
  }
}

val Jsht3FontFamilio: FontFamily
  get() = konservitaFontFamilio ?: FontFamily.Default

val KtashFontFamilio: FontFamily
  get() = Jsht3FontFamilio

fun kreuTiparon(uzuPropraTiparo: Boolean, kunteksto: Context? = null): Typography {
  val familio = if (uzuPropraTiparo) {
    try {
      akiriJsht3FontFamilion(kunteksto)
    } catch (_: Throwable) {
      FontFamily.Default
    }
  } else {
    FontFamily.Default
  }

  return Typography(
    displayLarge = TextStyle(
      fontFamily = familio,
      fontWeight = FontWeight.Bold,
      fontSize = 40.sp,
      lineHeight = 48.sp
    ),
    displayMedium = TextStyle(
      fontFamily = familio,
      fontWeight = FontWeight.Bold,
      fontSize = 32.sp,
      lineHeight = 38.sp
    ),
    headlineLarge = TextStyle(
      fontFamily = familio,
      fontWeight = FontWeight.Bold,
      fontSize = 24.sp,
      lineHeight = 30.sp
    ),
    headlineMedium = TextStyle(
      fontFamily = familio,
      fontWeight = FontWeight.SemiBold,
      fontSize = 20.sp,
      lineHeight = 26.sp
    ),
    titleLarge = TextStyle(
      fontFamily = familio,
      fontWeight = FontWeight.Bold,
      fontSize = 18.sp,
      lineHeight = 24.sp
    ),
    titleMedium = TextStyle(
      fontFamily = familio,
      fontWeight = FontWeight.SemiBold,
      fontSize = 15.sp,
      lineHeight = 20.sp
    ),
    titleSmall = TextStyle(
      fontFamily = familio,
      fontWeight = FontWeight.Medium,
      fontSize = 13.sp,
      lineHeight = 18.sp
    ),
    bodyLarge = TextStyle(
      fontFamily = familio,
      fontWeight = FontWeight.Normal,
      fontSize = 15.sp,
      lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
      fontFamily = familio,
      fontWeight = FontWeight.Normal,
      fontSize = 13.sp,
      lineHeight = 18.sp
    ),
    bodySmall = TextStyle(
      fontFamily = familio,
      fontWeight = FontWeight.Normal,
      fontSize = 11.sp,
      lineHeight = 15.sp
    ),
    labelLarge = TextStyle(
      fontFamily = familio,
      fontWeight = FontWeight.SemiBold,
      fontSize = 13.sp,
      lineHeight = 16.sp
    ),
    labelMedium = TextStyle(
      fontFamily = familio,
      fontWeight = FontWeight.Medium,
      fontSize = 11.sp,
      lineHeight = 14.sp
    ),
    labelSmall = TextStyle(
      fontFamily = familio,
      fontWeight = FontWeight.Medium,
      fontSize = 9.sp,
      lineHeight = 12.sp
    )
  )
}

