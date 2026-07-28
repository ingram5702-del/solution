package com.solutionwin.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.solutionwin.app.ui.MainViewModel
import com.solutionwin.app.ui.SportHubApp
import com.solutionwin.app.ui.theme.SportHubTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    private var openedWebView = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WebUrlStore.getCachedUrl(this)?.let { cachedUrl ->
            openWebView(cachedUrl)
            return
        }

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

        FirebaseWebUrlChecker.checkUrl { url ->
            WebUrlStore.saveUrl(this, url)
            openWebView(url)
        }
    }

    private fun openWebView(url: String) {
        if (openedWebView || isFinishing || isDestroyed) return
        openedWebView = true
        startActivity(Intent(this, WebViewActivity::class.java).putExtra(WebViewActivity.EXTRA_URL, url))
        finish()
    }
}
