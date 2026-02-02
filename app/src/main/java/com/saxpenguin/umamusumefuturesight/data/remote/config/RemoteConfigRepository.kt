package com.saxpenguin.umamusumefuturesight.data.remote.config

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteConfigRepository @Inject constructor() {

    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600 // 1 hour, adjust for development (e.g., 0)
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        // Set default values if needed, e.g., remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
    }

    suspend fun fetchAndActivate(): Boolean {
        return try {
            remoteConfig.fetchAndActivate().await()
        } catch (e: Exception) {
            false
        }
    }

    fun getDataVersion(): Long {
        return remoteConfig.getLong("data_version")
    }

    companion object {
        const val KEY_DATA_VERSION = "data_version"
    }
}
