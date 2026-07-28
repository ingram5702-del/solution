package com.solutionwin.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solutionwin.app.data.AppPreferences
import com.solutionwin.app.domain.AppSettings
import com.solutionwin.app.domain.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferences: AppPreferences,
) : ViewModel() {
    val settings: StateFlow<AppSettings> = preferences.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppSettings(),
    )

    fun setTheme(mode: ThemeMode) = viewModelScope.launch { preferences.setTheme(mode) }
    fun setDynamicColor(enabled: Boolean) = viewModelScope.launch { preferences.setDynamicColor(enabled) }
}
