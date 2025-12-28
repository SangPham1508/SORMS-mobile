package com.example.sorms_app.data.datasource.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

class TokenManager(private val context: Context) {
    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val KEY_ACCOUNT_ID = stringPreferencesKey("account_id")
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        private val KEY_AVATAR_URL = stringPreferencesKey("avatar_url")
        private val KEY_ROLES = stringSetPreferencesKey("roles")
    }

    val accessTokenFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_ACCESS_TOKEN]
    }

    val refreshTokenFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_REFRESH_TOKEN]
    }

    val accountIdFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_ACCOUNT_ID]
    }

    val userNameFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_NAME]
    }

    val userEmailFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_EMAIL]
    }

    val avatarUrlFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_AVATAR_URL]
    }

    val rolesFlow: Flow<List<String>> = context.dataStore.data.map { prefs ->
        prefs[KEY_ROLES]?.toList() ?: emptyList()
    }

    suspend fun saveTokens(
        accessToken: String?, 
        refreshToken: String? = null,
        accountId: String? = null,
        userName: String? = null,
        userEmail: String? = null,
        avatarUrl: String? = null,
        roles: List<String>? = null
    ) {
        context.dataStore.edit { prefs ->
            if (accessToken != null) prefs[KEY_ACCESS_TOKEN] = accessToken else prefs.remove(KEY_ACCESS_TOKEN)
            if (refreshToken != null) prefs[KEY_REFRESH_TOKEN] = refreshToken else prefs.remove(KEY_REFRESH_TOKEN)
            if (accountId != null) prefs[KEY_ACCOUNT_ID] = accountId else prefs.remove(KEY_ACCOUNT_ID)
            if (userName != null) prefs[KEY_USER_NAME] = userName else prefs.remove(KEY_USER_NAME)
            if (userEmail != null) prefs[KEY_USER_EMAIL] = userEmail else prefs.remove(KEY_USER_EMAIL)
            if (avatarUrl != null) prefs[KEY_AVATAR_URL] = avatarUrl else prefs.remove(KEY_AVATAR_URL)
            if (roles != null) prefs[KEY_ROLES] = roles.toSet() else prefs.remove(KEY_ROLES)
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}


