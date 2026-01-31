package com.habitiora.linkarium

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import com.habitiora.linkarium.core.AuthState
import com.habitiora.linkarium.ui.scaffold.LinkariumGuard
import com.habitiora.linkarium.ui.scaffold.SecurityViewModel
import com.habitiora.linkarium.ui.theme.LinkariumTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private val viewModel: SecurityViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition {
            viewModel.authState.value is AuthState.Loading
        }
        
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            LinkariumTheme {
                LinkariumGuard(windowSizeClass = windowSizeClass)
            }
        }
    }
}