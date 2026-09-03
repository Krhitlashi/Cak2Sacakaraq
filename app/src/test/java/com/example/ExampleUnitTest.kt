package com.example

import com.example.ktash.*
import com.example.ui.i18n.Lingvo
import com.example.ui.i18n.TradukTekstoj
import com.example.ui.i18n.preniTradukojn
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testCiujTradukojEkzistasKajFunkcias() {
    Lingvo.values().forEach { lingvo ->
      val tradukoj: TradukTekstoj = preniTradukojn(lingvo)
      assertNotNull(tradukoj.navSpurilo)
      assertNotNull(tradukoj.navMapo)
      assertNotNull(tradukoj.navMezurilo)
      assertNotNull(tradukoj.navSuno)
      assertNotNull(tradukoj.navProtokolo)
      assertNotNull(tradukoj.unuoPeu)
      assertNotNull(tradukoj.unuoHaqe)
      assertNotNull(tradukoj.unuoQe)
      assertNotNull(tradukoj.unuoHe)
      assertNotNull(tradukoj.unuoHia)
      assertNotNull(tradukoj.unuoCelsius)
      assertNotNull(tradukoj.bazo64Horlogo)
      assertNotNull(tradukoj.kadrajKoordinatoj)
    }
  }

  @Test
  fun testMatematikoKajKonvertoj() {
    assertEquals(1.0, peuAlMetroj(1.0) / (149896229.0 / 9192631770.0), 0.000001)
    assertEquals(1.0, c2taAlMetroj(1.0) / 0.000264583, 0.001)
    assertTrue(vab6caja(0L) == "ɔ")
    assertTrue(vab6caja(8L) == "ıɔ")
    // Test precision rounding: 0.999999999 must round to 1 ( "ı" ), not 0.77777 ( "ɔ ƨƨƨƨ" )
    assertEquals("ı", vab6cajaDomani(0.9999999999, 4))
    assertEquals("ɿ", vab6cajaDomani(1.9999999999, 4))
    assertEquals("ɔ ƨ", vab6cajaDomani(0.8749999999, 2))
    assertTrue(celsiusAlHia(0.0) > 0.0)
    assertTrue(hiaAlCelsius(100.0) != 0.0)
  }

  @Test
  fun testSunoKajDatoKalkuloj() {
    val dato = cax2lStafl2(System.currentTimeMillis())
    assertTrue(dato.stibix > 0)
    assertTrue(dato.pal2stif in 1..13)
    val tempo = castifeh2(System.currentTimeMillis())
    assertTrue(tempo.haqe >= 0)
    val suno = kalkuliSunon(-77.8419, 166.6863, System.currentTimeMillis())
    assertTrue(suno.taglumoProgreso in 0.0..1.0)
  }

  @Test
  fun testVertikalaLokoKalkuloj() {
    // ⟪ Spaco al Kerno ( 4 Niveloj po 64 ) 🌐 ⟫
    val spaco = akiriVertikalanLokon(SPACA_ALTECO_METROJ)
    assertEquals(0, spaco.z1)
    assertEquals(0, spaco.z2)
    assertEquals(0, spaco.z3)
    assertEquals(0, spaco.z4)

    val kerno = akiriVertikalanLokon(-TERA_RADIALA_PROFUNDECO_METROJ)
    assertEquals(63, kerno.z1)
    assertEquals(63, kerno.z2)
    assertEquals(63, kerno.z3)
    assertEquals(63, kerno.z4)

    val tekstoDekuma = kerno.alDekumaTeksto(Lingvo.ESPERANTO)
    assertEquals("64 64 64 64", tekstoDekuma)

    val maraNivelo = akiriVertikalanLokon(0.0)
    assertTrue(maraNivelo.z1 in 0..63)
    assertTrue(maraNivelo.z2 in 0..63)
    assertTrue(maraNivelo.z3 in 0..63)
    assertTrue(maraNivelo.z4 in 0..63)

    // Kontroli rekonstruon
    val reAlteco = vertikaloAlAlteco(maraNivelo.z1, maraNivelo.z2, maraNivelo.z3, maraNivelo.z4)
    assertEquals(0.0, reAlteco, 1.0)

    // Kontroli tekstan malakiron
    val parsita = malakiriVertikalanLokon("64 64 64 64")
    assertNotNull(parsita)
    assertEquals(63, parsita!!.z1)
    assertEquals(63, parsita.z2)
    assertEquals(63, parsita.z3)
    assertEquals(63, parsita.z4)
  }

  @Test
  fun testTradukojVertikalaLoko() {
    for (lingvo in Lingvo.values()) {
      val tradukoj = preniTradukojn(lingvo)
      if (lingvo == Lingvo.AIH) {
        assertEquals("", tradukoj.vertikalaLokoTitolo)
        assertEquals("", tradukoj.alteco)
        assertEquals("", tradukoj.spacoAlKerno)
      } else {
        assertTrue(tradukoj.vertikalaLokoTitolo.isNotEmpty())
        assertTrue(tradukoj.alteco.isNotEmpty())
        assertTrue(tradukoj.spacoAlKerno.isNotEmpty())
      }
    }
  }
}

