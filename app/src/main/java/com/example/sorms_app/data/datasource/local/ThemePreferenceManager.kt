package com.example.sorms_app.data.datasource.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore by preferencesDataStore(name = "theme_prefs")

class ThemePreferenceManager(private val context: Context) {
    companion object {
        private val KEY_USE_DARK_THEME = booleanPreferencesKey("use_dark_theme")
    }

    val isDarkThemeFlow: Flow<Boolean> = context.themeDataStore.data.map { prefs ->
        prefs[KEY_USE_DARK_THEME] ?: false  // Default to light theme
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.themeDataStore.edit { prefs ->
            prefs[KEY_USE_DARK_THEME] = enabled
        }
    }

    suspend fun toggleTheme() {
        context.themeDataStore.edit { prefs ->
            val current = prefs[KEY_USE_DARK_THEME] ?: false
            prefs[KEY_USE_DARK_THEME] = !current
        }
    }
}

