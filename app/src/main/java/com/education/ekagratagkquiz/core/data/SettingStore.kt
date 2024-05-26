package com.education.ekagratagkquiz.core.data


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.education.ekagratagkquiz.contribute_quiz.domain.model.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserStore(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("userToken")

        /*Save Ad Id or Android Id*/
        private val USER_TOKEN_KEY = stringPreferencesKey("user_token")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val IS_ADMIN_KEY = booleanPreferencesKey("is_admin")
        private val IS_SUBSCRIPTION_ACTIVE_KEY = booleanPreferencesKey("is_subscription_active")
    }

    val getUserDetail: Flow<FirebaseUser> = context.dataStore.data.map { preferences ->
        FirebaseUser(
            uid = preferences[USER_TOKEN_KEY] ?: "",
            displayName = preferences[USER_NAME_KEY] ?: ""
        )
    }

    val getAccessToken: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_TOKEN_KEY] ?: ""
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_TOKEN_KEY] = token
        }
    }
    val getUserName: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME_KEY] ?: ""
    }

    suspend fun saveUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = name
        }
    }



    val isSubscriptionActive: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_SUBSCRIPTION_ACTIVE_KEY] ?: false
    }

    suspend fun saveIsSubscriptionActive(active: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_SUBSCRIPTION_ACTIVE_KEY] = active
        }
    }

    val isAdmin: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_ADMIN_KEY] ?: false
    }

    suspend fun saveIsAdmin(token: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_ADMIN_KEY] = token
        }
    }
}