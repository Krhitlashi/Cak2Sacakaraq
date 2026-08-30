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
}

