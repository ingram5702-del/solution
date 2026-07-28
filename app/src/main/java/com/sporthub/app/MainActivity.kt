package com.sporthub.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sporthub.app.ui.MainViewModel
import com.sporthub.app.ui.SportHubApp
import com.sporthub.app.ui.theme.SportHubTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by viewModel.settings.collectAsStateWithLifecycle()
            SportHubTheme(settings = settings) {
                SportHubApp(
                    settings = settings,
                    onThemeChanged = viewModel::setTheme,
                    onDynamicColorChanged = viewModel::setDynamicColor,
                )
            }
        }
    }
}
