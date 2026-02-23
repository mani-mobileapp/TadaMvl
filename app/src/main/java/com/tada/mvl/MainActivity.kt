package com.tada.mvl

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.tada.mvl.ui.navigation.MVLApp
import dagger.hilt.android.AndroidEntryPoint

import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.graphics.toColorInt

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true
        window.statusBarColor = "#FFC400".toColorInt()
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        setContent {
            Surface(color = MaterialTheme.colorScheme.background) {
                MVLApp()
            }
        }
        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightStatusBars = true
    }
}
