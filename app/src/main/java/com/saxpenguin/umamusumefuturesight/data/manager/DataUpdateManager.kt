package com.saxpenguin.umamusumefuturesight.data.manager

import com.saxpenguin.umamusumefuturesight.data.UserPreferencesRepository
import com.saxpenguin.umamusumefuturesight.data.remote.config.RemoteConfigRepository
import com.saxpenguin.umamusumefuturesight.data.remote.download.DataDownloader
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataUpdateManager @Inject constructor(
    private val remoteConfigRepository: RemoteConfigRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val dataDownloader: DataDownloader
) {

    suspend fun checkForUpdates(): Boolean {
        // 1. Fetch Remote Config
        if (!remoteConfigRepository.fetchAndActivate()) {
            return false // Failed to fetch config, skip update
        }

        // 2. Compare Versions
        val remoteVersion = remoteConfigRepository.getDataVersion()
        val localVersion = userPreferencesRepository.localDataVersionFlow.first()

        if (remoteVersion > localVersion) {
            // 3. Download Files
            val success = dataDownloader.downloadDataFiles()
            if (success) {
                // 4. Update Local Version
                userPreferencesRepository.updateLocalDataVersion(remoteVersion)
                return true // Updated successfully
            }
        }
        return false // No update needed or failed
    }
}
