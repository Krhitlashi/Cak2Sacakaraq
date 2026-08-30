package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// ≺⧼ Formoj & Angulaj Radiusoj 📐 ⧽≻

// ⟪ CSS Corner Radius Variabloj (--អិត្ភេពឺ, --អិត្ភេ, --អិត្ភេចិ, --អិត្ភេមិ, --ចិង) ⟫

// CSS --អិត្ភេពឺ ( 32px 12px ) - Grandaj thala kartoj & paneloj
val FormoItphePue = RoundedCornerShape(
  topStart = 32.dp,
  topEnd = 12.dp,
  bottomEnd = 32.dp,
  bottomStart = 12.dp
)

// CSS --អិត្ភេ ( 24px 8px ) - Normaj ciihii kartoj, sozanu, c2w2qkuba
val FormoItphe = RoundedCornerShape(
  topStart = 24.dp,
  topEnd = 8.dp,
  bottomEnd = 24.dp,
  bottomStart = 8.dp
)

// CSS --អិត្ភេចិ ( 20px 8px ) - Butonoj, flak, enigoj / input, select, td
val FormoItpheCi = RoundedCornerShape(
  topStart = 20.dp,
  topEnd = 8.dp,
  bottomEnd = 20.dp,
  bottomStart = 8.dp
)

// CSS --អិត្ភេមិ ( 16px 4px ) - file-selector-button, etaj insignoj
val FormoItpheMi = RoundedCornerShape(
  topStart = 16.dp,
  topEnd = 4.dp,
  bottomEnd = 16.dp,
  bottomStart = 4.dp
)

// CSS --ចិង ( 64px ) - ksaka naviga stango, aktivaj butonoj, piloj
val FormoCing = RoundedCornerShape(64.dp)

// ⟪ Semantikaj Komponant-Formoj kongruaj kun la ekstera CSS ⟫
val FormoKartoGranda = FormoItphePue
val FormoKarto = FormoItphePue
val FormoSkroloMaska = FormoItphePue
val FormoSkroloMaskaDialogo = FormoItpheCi
val FormoSkroloMaskaHorizontala = FormoItphe
val FormoSubKarto = FormoItphe
val FormoNavStango = FormoItphePue
val FormoKartoEtulo = FormoItphe
val FormoInsigno = FormoItphe
val FormoButono = FormoItpheCi
val FormoButonoKompakta = FormoItpheMi
val FormoInsignoKompakta = FormoItpheMi
val FormoPilo = FormoCing

val KtashShapes = Shapes(
  extraSmall = FormoItpheMi,
  small = FormoItpheCi,
  medium = FormoItphe,
  large = FormoItphePue,
  extraLarge = FormoCing
)


