package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {
    companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val LOGGED_IN_USERNAME = stringPreferencesKey("logged_in_username")
        val LOGGED_IN_ROLE = stringPreferencesKey("logged_in_role")
    }

    val userPreferencesFlow: Flow<UserSession> = context.dataStore.data.map { preferences ->
        val isLoggedIn = preferences[IS_LOGGED_IN] ?: false
        val username = preferences[LOGGED_IN_USERNAME] ?: ""
        val role = preferences[LOGGED_IN_ROLE] ?: ""
        UserSession(isLoggedIn, username, role)
    }

    suspend fun saveSession(username: String, role: String) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = true
            preferences[LOGGED_IN_USERNAME] = username
            preferences[LOGGED_IN_ROLE] = role
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = false
            preferences[LOGGED_IN_USERNAME] = ""
            preferences[LOGGED_IN_ROLE] = ""
        }
    }
}

data class UserSession(
    val isLoggedIn: Boolean,
    val username: String,
    val role: String
)
