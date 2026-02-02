package com.saxpenguin.umamusumefuturesight.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.saxpenguin.umamusumefuturesight.model.UserResources
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_resources")

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val context: Context
) {
    private val JEWELS_KEY = intPreferencesKey("jewels")
    private val CHARACTER_TICKETS_KEY = intPreferencesKey("character_tickets")
    private val SINGLE_TICKETS_KEY = intPreferencesKey("single_tickets") // Support card tickets
    private val DAILY_INCOME_KEY = intPreferencesKey("daily_income")
    private val DATA_VERSION_KEY = androidx.datastore.preferences.core.longPreferencesKey("local_data_version")

    val userResourcesFlow: Flow<UserResources> = context.dataStore.data
        .map { preferences ->
            UserResources(
                jewels = preferences[JEWELS_KEY] ?: 0,
                characterTickets = preferences[CHARACTER_TICKETS_KEY] ?: 0,
                singleTickets = preferences[SINGLE_TICKETS_KEY] ?: 0,
                dailyJewelIncome = preferences[DAILY_INCOME_KEY] ?: 600
            )
        }

    suspend fun updateJewels(jewels: Int) {
        context.dataStore.edit { preferences ->
            preferences[JEWELS_KEY] = jewels
        }
    }

    suspend fun updateCharacterTickets(count: Int) {
        context.dataStore.edit { preferences ->
            preferences[CHARACTER_TICKETS_KEY] = count
        }
    }

    suspend fun updateSingleTickets(count: Int) {
        context.dataStore.edit { preferences ->
            preferences[SINGLE_TICKETS_KEY] = count
        }
    }

    suspend fun updateDailyJewelIncome(income: Int) {
        context.dataStore.edit { preferences ->
            preferences[DAILY_INCOME_KEY] = income
        }
    }

    val localDataVersionFlow: Flow<Long> = context.dataStore.data
        .map { preferences ->
            preferences[DATA_VERSION_KEY] ?: 0L
        }

    suspend fun updateLocalDataVersion(version: Long) {
        context.dataStore.edit { preferences ->
            preferences[DATA_VERSION_KEY] = version
        }
    }
}
