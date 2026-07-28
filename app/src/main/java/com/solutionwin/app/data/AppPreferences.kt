package com.solutionwin.app.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.solutionwin.app.domain.AppSettings
import com.solutionwin.app.domain.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "sporthub_settings")

@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val themeKey = stringPreferencesKey("theme")
    private val dynamicColorKey = booleanPreferencesKey("dynamic_color")

    val settings: Flow<AppSettings> = context.settingsDataStore.data
        .catch { error -> if (error is IOException) emit(emptyPreferences()) else throw error }
        .map { values ->
            AppSettings(
                themeMode = values[themeKey]
                    ?.let { stored -> runCatching { ThemeMode.valueOf(stored) }.getOrNull() }
                    ?: ThemeMode.SYSTEM,
                dynamicColor = values[dynamicColorKey] ?: true,
            )
        }

    suspend fun setTheme(mode: ThemeMode) {
        context.settingsDataStore.edit { it[themeKey] = mode.name }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        context.settingsDataStore.edit { it[dynamicColorKey] = enabled }
    }
}

private fun emptyPreferences() = androidx.datastore.preferences.core.emptyPreferences()
