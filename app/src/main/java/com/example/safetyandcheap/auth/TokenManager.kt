package com.example.safetyandcheap.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val tokenKey = stringPreferencesKey("jwt_token")
    private var cachedToken: String? = null

    init {
        CoroutineScope(Dispatchers.IO).launch {
            cachedToken = dataStore.data
                .map { it[tokenKey] }
                .firstOrNull()
        }
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { prefs ->
            prefs[tokenKey] = token
        }
        cachedToken = token
    }

    suspend fun getToken(): String? {
        return cachedToken ?: dataStore.data
            .map { it[tokenKey] }
            .firstOrNull()
            .also { cachedToken = it }
    }

    suspend fun clearToken() {
        dataStore.edit { prefs ->
            prefs.remove(tokenKey)
        }
        cachedToken = null
    }

    fun getTokenSync(): String? = cachedToken
}