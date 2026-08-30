package com.example

import android.app.Application
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.example.ui.KtashViewModel
import com.example.ui.NavigaLangeto
import com.example.ui.i18n.Lingvo
import com.example.ui.theme.KtashTrackerTheme
import com.example.ui.theme.TemoModo
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun testAppRenderCiujLingvojKajEkranoj() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = KtashViewModel(app)

    composeTestRule.setContent {
      val temoModo by viewModel.temoModo.collectAsState()
      val uzuMaterialYou by viewModel.uzuMaterialYou.collectAsState()
      val uzuPropraTiparo by viewModel.uzuPropraTiparo.collectAsState()

      KtashTrackerTheme(
        temoModo = temoModo,
        uzuMaterialYou = uzuMaterialYou,
        uzuPropraTiparo = uzuPropraTiparo
      ) {
        KtashApp(viewModel = viewModel)
      }
    }

    composeTestRule.waitForIdle()

    Lingvo.values().forEach { lingvo ->
      viewModel.agordiLingvon(lingvo)
      NavigaLangeto.values().forEach { langeto ->
        viewModel.ŝanĝiLangeton(langeto)
        composeTestRule.waitForIdle()
      }
    }
  }
}


