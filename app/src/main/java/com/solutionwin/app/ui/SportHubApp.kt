package com.solutionwin.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.solutionwin.app.domain.AppSettings
import com.solutionwin.app.domain.ThemeMode
import com.solutionwin.app.ui.screens.CalendarScreen
import com.solutionwin.app.ui.screens.HomeScreen
import com.solutionwin.app.ui.screens.MoreScreen
import com.solutionwin.app.ui.screens.NotesScreen
import com.solutionwin.app.ui.screens.TacticsScreen

private enum class MainTab(val title: String, val symbol: String) {
    HOME("Home", "●"),
    CALENDAR("Calendar", "▦"),
    NOTES("Notes", "✎"),
    TACTICS("Tactics", "◇"),
    MORE("More", "＋"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportHubApp(
    settings: AppSettings,
    onThemeChanged: (ThemeMode) -> Unit,
    onDynamicColorChanged: (Boolean) -> Unit,
) {
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.HOME) }
    var moreDestination by rememberSaveable { mutableStateOf<String?>(null) }
    BackHandler(enabled = moreDestination != null) { moreDestination = null }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(moreDestination ?: selectedTab.title) },
                navigationIcon = {
                    if (moreDestination != null) {
                        androidx.compose.material3.TextButton(onClick = { moreDestination = null }) {
                            Text("Back")
                        }
                    }
                },
            )
        },
        bottomBar = {
            if (moreDestination == null) {
                NavigationBar {
                    MainTab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = tab == selectedTab,
                            onClick = { selectedTab = tab },
                            icon = { Text(tab.symbol) },
                            label = { Text(tab.title) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                moreDestination != null -> MoreScreen(
                    destination = moreDestination.orEmpty(),
                    settings = settings,
                    onThemeChanged = onThemeChanged,
                    onDynamicColorChanged = onDynamicColorChanged,
                )
                selectedTab == MainTab.HOME -> HomeScreen()
                selectedTab == MainTab.CALENDAR -> CalendarScreen()
                selectedTab == MainTab.NOTES -> NotesScreen()
                selectedTab == MainTab.TACTICS -> TacticsScreen()
                else -> MoreScreen(
                    destination = null,
                    settings = settings,
                    onOpen = { moreDestination = it },
                    onThemeChanged = onThemeChanged,
                    onDynamicColorChanged = onDynamicColorChanged,
                )
            }
        }
    }
}
