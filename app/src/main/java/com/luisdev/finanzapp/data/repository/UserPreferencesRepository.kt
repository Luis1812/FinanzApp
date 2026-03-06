package com.luisdev.finanzapp.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val FIRST_NAME = stringPreferencesKey("first_name")
        val LAST_NAME = stringPreferencesKey("last_name")
        val PROFILE_PICTURE_PATH = stringPreferencesKey("profile_picture_path")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .map { preferences ->
            UserPreferences(
                firstName = preferences[PreferencesKeys.FIRST_NAME] ?: "",
                lastName = preferences[PreferencesKeys.LAST_NAME] ?: "",
                profilePicturePath = preferences[PreferencesKeys.PROFILE_PICTURE_PATH]
            )
        }

    suspend fun updateName(firstName: String, lastName: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FIRST_NAME] = firstName
            preferences[PreferencesKeys.LAST_NAME] = lastName
        }
    }

    suspend fun updateProfilePicture(path: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PROFILE_PICTURE_PATH] = path
        }
    }
}

data class UserPreferences(
    val firstName: String,
    val lastName: String,
    val profilePicturePath: String?
)
