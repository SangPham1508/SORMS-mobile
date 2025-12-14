package com.example.sorms_app.data.datasource.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

class TokenManager(private val context: Context) {
    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    val accessTokenFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_ACCESS_TOKEN]
    }

    val refreshTokenFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_REFRESH_TOKEN]
    }

    suspend fun saveTokens(accessToken: String?, refreshToken: String? = null) {
        context.dataStore.edit { prefs ->
            if (accessToken != null) prefs[KEY_ACCESS_TOKEN] = accessToken else prefs.remove(KEY_ACCESS_TOKEN)
            if (refreshToken != null) prefs[KEY_REFRESH_TOKEN] = refreshToken else prefs.remove(KEY_REFRESH_TOKEN)
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}


