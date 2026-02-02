package com.saxpenguin.umamusumefuturesight.data

import android.content.Context
import com.saxpenguin.umamusumefuturesight.data.remote.model.CardInfo
import com.saxpenguin.umamusumefuturesight.data.remote.model.TimetableEntry
import com.saxpenguin.umamusumefuturesight.model.Banner
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BannerSeedDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json,
    private val processor: BannerDataProcessor
) {
    fun loadBanners(): List<Banner> {
        val timetable = loadTimetable("timetable.json")
        if (timetable.isEmpty()) {
            return emptyList()
        }
        val charaMap = loadCharacterMap("characters.json")
        val cardMap = loadNameMap("cards.json")
        
        return processor.process(timetable, charaMap, cardMap)
    }

    private fun loadCharacterMap(fileName: String): Map<String, String> {
         val text = readAssetText(fileName) ?: return emptyMap()
         return runCatching {
             json.decodeFromString<Map<String, String>>(text)
         }.getOrDefault(emptyMap())
    }

    private fun readAssetText(fileName: String): String? {
        // Try reading from internal storage first (downloaded update)
        val file = java.io.File(context.filesDir, fileName)
        if (file.exists()) {
            return runCatching {
                file.readText()
            }.getOrNull()
        }

        // Fallback to assets
        return runCatching {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        }.getOrNull()
    }

    private fun loadNameMap(fileName: String): Map<String, CardInfo> {
        val text = readAssetText(fileName) ?: return emptyMap()
        
        // Try new format first
        return runCatching {
            json.decodeFromString<Map<String, CardInfo>>(text)
        }.recover {
            // Fallback to old format
            val oldMap = runCatching {
                json.decodeFromString<Map<String, String>>(text)
            }.getOrDefault(emptyMap())
            
            oldMap.mapValues { CardInfo(it.value, "UNKNOWN") }
        }.getOrDefault(emptyMap())
    }

    private fun loadTimetable(fileName: String): List<TimetableEntry> {
        val text = readAssetText(fileName) ?: return emptyList()
        return runCatching {
            json.decodeFromString<List<TimetableEntry>>(text)
        }.getOrDefault(emptyList())
    }
}
