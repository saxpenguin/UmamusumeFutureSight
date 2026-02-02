package com.saxpenguin.umamusumefuturesight.data

import android.content.Context
import com.saxpenguin.umamusumefuturesight.model.Banner
import com.saxpenguin.umamusumefuturesight.model.BannerType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BannerSeedDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json
) {
    fun loadBanners(): List<Banner> {
        val timetable = loadTimetable("timetable.json")
        if (timetable.isEmpty()) {
            return emptyList()
        }
        val characterLookup = buildLookup(loadNameMap("characters.json"))
        val cardLookup = buildLookup(loadNameMap("cards.json"))
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy/M/d", Locale.US)

        return timetable.flatMapIndexed { index, entry ->
            val rowId = entry.id.trim()
            val jpStartDate = parseDate(entry.jpStartDate, dateFormatter)
            val jpEndDate = parseDate(entry.jpEndDate, dateFormatter)
            if (jpStartDate == null || jpEndDate == null) {
                return@flatMapIndexed emptyList<Banner>()
            }

            val baseId = if (rowId.isNotBlank()) rowId else "row-${index + 1}"
            val charaNames = normalizePool(entry.charaPool)
            val cardNames = normalizePool(entry.cardPool)
            val banners = mutableListOf<Banner>()

            if (charaNames.isNotEmpty()) {
                val imageUrl = resolveImageUrl(charaNames, characterLookup)
                banners.add(
                    Banner(
                        id = "${baseId}-C",
                        name = charaNames.joinToString(" / "),
                        type = BannerType.CHARACTER,
                        jpStartDate = jpStartDate,
                        jpEndDate = jpEndDate,
                        imageUrl = imageUrl
                    )
                )
            }

            if (cardNames.isNotEmpty()) {
                val normalizedCardNames = cardNames.map { stripCardPrefix(it) }
                val imageUrl = resolveImageUrl(normalizedCardNames, cardLookup)
                banners.add(
                    Banner(
                        id = "${baseId}-S",
                        name = cardNames.joinToString(" / "),
                        type = BannerType.SUPPORT_CARD,
                        jpStartDate = jpStartDate,
                        jpEndDate = jpEndDate,
                        imageUrl = imageUrl
                    )
                )
            }

            banners
        }
    }

    private fun readAssetText(fileName: String): String? {
        return runCatching {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        }.getOrNull()
    }

    private fun loadNameMap(fileName: String): Map<String, String> {
        val text = readAssetText(fileName) ?: return emptyMap()
        return runCatching {
            json.decodeFromString<Map<String, String>>(text)
        }.getOrDefault(emptyMap())
    }

    private fun loadTimetable(fileName: String): List<TimetableEntry> {
        val text = readAssetText(fileName) ?: return emptyList()
        return runCatching {
            json.decodeFromString<List<TimetableEntry>>(text)
        }.getOrDefault(emptyList())
    }

    private fun buildLookup(source: Map<String, String>): Map<String, String> {
        val lookup = mutableMapOf<String, String>()
        for ((fileName, displayName) in source) {
            val normalized = normalizeName(displayName)
            val resourceName = fileName.removeSuffix(".png")
            if (normalized.isNotEmpty() && !lookup.containsKey(normalized)) {
                lookup[normalized] = resourceName
            }
        }
        return lookup
    }

    private fun parseDate(value: String?, formatter: DateTimeFormatter): LocalDate? {
        val trimmed = value?.trim().orEmpty()
        if (trimmed.isEmpty()) {
            return null
        }
        return runCatching { LocalDate.parse(trimmed, formatter) }.getOrNull()
    }

    private fun normalizePool(pool: List<String>): List<String> {
        return pool
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    private fun stripCardPrefix(name: String): String {
        val trimmed = name.trim()
        val dashIndex = trimmed.indexOf('-')
        return if (dashIndex in 1..3) {
            trimmed.substring(dashIndex + 1).trim()
        } else {
            trimmed
        }
    }

    private fun normalizeName(name: String): String {
        return name
            .replace("\u3000", "")
            .replace(" ", "")
            .replace("·", "")
            .replace("・", "")
            .replace("*", "")
            .replace("（", "")
            .replace("）", "")
            .replace("(", "")
            .replace(")", "")
            .replace("復刻", "")
            .trim()
    }

    private fun resolveImageUrl(names: List<String>, lookup: Map<String, String>): String? {
        val normalizedNames = names.map { normalizeName(it) }.filter { it.isNotEmpty() }
        for (normalized in normalizedNames) {
            val match = lookup[normalized]
            if (match != null) {
                return toResourceUrl(match)
            }
        }

        for (normalized in normalizedNames) {
            val match = lookup.entries.firstOrNull {
                it.key.contains(normalized) || normalized.contains(it.key)
            }
            if (match != null) {
                return toResourceUrl(match.value)
            }
        }

        return null
    }

    private fun toResourceUrl(resourceName: String): String {
        return "android.resource://${context.packageName}/drawable/$resourceName"
    }

    @Serializable
    private data class TimetableEntry(
        val id: String = "",
        val jpStartDate: String = "",
        val jpEndDate: String = "",
        val twStartDate: String = "",
        val twEndDate: String = "",
        val charaPool: List<String> = emptyList(),
        val cardPool: List<String> = emptyList()
    )
}
