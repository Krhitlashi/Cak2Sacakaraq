package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ktash.cax2lStafl2
import com.example.ktash.castifeh2
import com.example.ktash.kalkuliSunon
import com.example.ui.KtashViewModel
import com.example.ui.NavigaLangeto
import com.example.ui.components.AgordojFlosantaDialogo
import com.example.ui.components.CielaKapo
import com.example.ui.components.N2taseNavigaStango
import com.example.ui.screens.*
import com.example.ui.theme.*

// ≺⧼ Ĉefa Aktiveco 📱 ⧽≻

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val ĉefaViewModel: KtashViewModel = viewModel()
      val temoModo by ĉefaViewModel.temoModo.collectAsState()
      val uzuMaterialYou by ĉefaViewModel.uzuMaterialYou.collectAsState()
      val uzuPropraTiparo by ĉefaViewModel.uzuPropraTiparo.collectAsState()

      KtashTrackerTheme(
        temoModo = temoModo,
        uzuMaterialYou = uzuMaterialYou,
        uzuPropraTiparo = uzuPropraTiparo
      ) {
        KtashApp(viewModel = ĉefaViewModel)
      }
    }
  }
}

@Composable
fun KtashApp(
  viewModel: KtashViewModel = viewModel()
) {
  val context = LocalContext.current
  val aktivaLangeto by viewModel.aktivaLangeto.collectAsState()
  val uzuBazo10 by viewModel.uzuBazo10.collectAsState()
  val nunaTempoMs by viewModel.nunaTempoMs.collectAsState()
  val nunaLoko by viewModel.lokoManagero.nunaLoko.collectAsState()
  val sciigoTeksto by viewModel.sciigoTeksto.collectAsState()
  val elektitaLingvo by viewModel.elektitaLingvo.collectAsState()
  val tradukoj by viewModel.tradukoj.collectAsState()

  val temoModo by viewModel.temoModo.collectAsState()
  val uzuMaterialYou by viewModel.uzuMaterialYou.collectAsState()
  val uzuPropraTiparo by viewModel.uzuPropraTiparo.collectAsState()
  val plenaEkranaMezurilo by viewModel.plenaEkranaMezurilo.collectAsState()

  var montruAgordojn by remember { mutableStateOf(false) }

  val cax2lDato = remember(nunaTempoMs) { cax2lStafl2(nunaTempoMs) }
  val castifeh2Tempo = remember(nunaLoko.latitudo, nunaLoko.longitudo, nunaTempoMs) {
    castifeh2(nunaLoko.latitudo, nunaLoko.longitudo, nunaTempoMs)
  }
  val sunaInformo = remember(nunaLoko.latitudo, nunaLoko.longitudo, nunaTempoMs) {
    kalkuliSunon(nunaLoko.latitudo, nunaLoko.longitudo, nunaTempoMs)
  }

  val snackbarHostState = remember { SnackbarHostState() }

  // Permeso-peto por GPS kaj Paŝspurado
  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions()
  ) { permesoj ->
    val fineGranted = permesoj[Manifest.permission.ACCESS_FINE_LOCATION] == true
    val coarseGranted = permesoj[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    if (fineGranted || coarseGranted) {
      viewModel.lokoManagero.komenciVivajnGPSGisdatigojn()
    }
    viewModel.pasSpurilo.komenciSensorojn()
  }

  LaunchedEffect(Unit) {
    val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
    val activityPerm = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
      ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION)
    } else {
      PackageManager.PERMISSION_GRANTED
    }

    val petendaj = mutableListOf<String>()
    if (fine != PackageManager.PERMISSION_GRANTED) {
      petendaj.add(Manifest.permission.ACCESS_FINE_LOCATION)
      petendaj.add(Manifest.permission.ACCESS_COARSE_LOCATION)
    }
    if (activityPerm != PackageManager.PERMISSION_GRANTED && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
      petendaj.add(Manifest.permission.ACTIVITY_RECOGNITION)
    }

    if (petendaj.isNotEmpty()) {
      permissionLauncher.launch(petendaj.toTypedArray())
    }
  }

  LaunchedEffect(sciigoTeksto) {
    sciigoTeksto?.let { mesaĝo ->
      snackbarHostState.showSnackbar(mesaĝo)
      viewModel.purigiSciigon()
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = MaterialTheme.colorScheme.background,
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      if (!plenaEkranaMezurilo) {
        CielaKapo(
          progreso = sunaInformo.taglumoProgreso,
          cax2lDato = cax2lDato,
          castifeh2Tempo = castifeh2Tempo,
          sunaInformo = sunaInformo,
          uzuBazo10 = uzuBazo10,
          tradukoj = tradukoj,
          lingvo = elektitaLingvo,
          onBaskuliBazon = { viewModel.baskuliBazon() },
          onMalfermiAgordojn = { montruAgordojn = true },
          modifier = Modifier.statusBarsPadding()
        )
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(top = innerPadding.calculateTopPadding())
    ) {
      // ⟪ Ĉefa Ekrana Enhavo ( Plena Fono malantaŭ la Flosanta Stango ) ⟫
      Box(
        modifier = Modifier.fillMaxSize()
      ) {
        when (aktivaLangeto) {
          NavigaLangeto.SPURILO -> SpuriloEkrano(viewModel = viewModel)
          NavigaLangeto.MAPO -> MapoEkrano(viewModel = viewModel)
          NavigaLangeto.MEZURILO -> MezuriloEkrano(viewModel = viewModel)
          NavigaLangeto.SUNO_VETERO -> SunoVeteroEkrano(viewModel = viewModel)
          NavigaLangeto.PROTOKOLO -> ProtokoloEkrano(viewModel = viewModel)
        }
      }

      // ⟪ Flosanta n2tase Navigadstango super la Enhavo ⟫
      if (!plenaEkranaMezurilo) {
        N2taseNavigaStango(
          aktivaLangeto = aktivaLangeto,
          tradukoj = tradukoj,
          onElektiLangeton = { viewModel.ŝanĝiLangeton(it) },
          modifier = Modifier
            .align(Alignment.BottomCenter)
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 10.dp)
        )
      }

      if (montruAgordojn) {
        AgordojFlosantaDialogo(
          temoModo = temoModo,
          uzuMaterialYou = uzuMaterialYou,
          uzuPropraTiparo = uzuPropraTiparo,
          uzuBazo10 = uzuBazo10,
          elektitaLingvo = elektitaLingvo,
          tradukoj = tradukoj,
          onAgordiTemoModon = { viewModel.agordiTemoModon(it) },
          onBaskuliMaterialYou = { viewModel.baskuliMaterialYou() },
          onBaskuliTiparon = { viewModel.baskuliTiparon() },
          onBaskuliBazon = { viewModel.baskuliBazon() },
          onAgordiLingvon = { viewModel.agordiLingvon(it) },
          onFermi = { montruAgordojn = false }
        )
      }
    }
  }
}
