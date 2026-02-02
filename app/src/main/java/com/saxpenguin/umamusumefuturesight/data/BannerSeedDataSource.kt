package com.saxpenguin.umamusumefuturesight.data

import android.content.Context
import com.saxpenguin.umamusumefuturesight.model.Banner
import com.saxpenguin.umamusumefuturesight.model.BannerType
import dagger.hilt.android.qualifiers.ApplicationContext
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
        val timetable = readAssetText("timetable.csv") ?: return emptyList()
        val characterLookup = buildLookup(loadNameMap("characters.json"))
        val cardLookup = buildLookup(loadNameMap("cards.json"))
        val rows = parseCsv(timetable).drop(1)
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy/M/d", Locale.US)

        return rows.flatMapIndexed { index, row ->
            val rowId = row.getOrNull(0).orEmpty().trim()
            val jpStartDate = parseDate(row.getOrNull(1), dateFormatter)
            val jpEndDate = parseDate(row.getOrNull(2), dateFormatter)
            if (jpStartDate == null || jpEndDate == null) {
                return@flatMapIndexed emptyList<Banner>()
            }

            val baseId = if (rowId.isNotBlank()) rowId else "row-${index + 1}"
            val charaPool = row.getOrNull(5).orEmpty()
            val cardPool = row.getOrNull(6).orEmpty()
            val charaNames = splitNames(charaPool)
            val cardNames = splitNames(cardPool)
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

    private fun splitNames(raw: String): List<String> {
        return raw
            .replace("\r", "")
            .split("\n")
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

    private fun parseCsv(content: String): List<List<String>> {
        val rows = mutableListOf<List<String>>()
        val currentRow = mutableListOf<String>()
        val currentField = StringBuilder()
        var inQuotes = false
        var index = 0

        while (index < content.length) {
            val char = content[index]
            when (char) {
                '"' -> {
                    if (inQuotes && index + 1 < content.length && content[index + 1] == '"') {
                        currentField.append('"')
                        index++
                    } else {
                        inQuotes = !inQuotes
                    }
                }
                ',' -> {
                    if (inQuotes) {
                        currentField.append(char)
                    } else {
                        currentRow.add(currentField.toString())
                        currentField.setLength(0)
                    }
                }
                '\n' -> {
                    if (inQuotes) {
                        currentField.append(char)
                    } else {
                        currentRow.add(currentField.toString())
                        currentField.setLength(0)
                        rows.add(currentRow.toList())
                        currentRow.clear()
                    }
                }
                '\r' -> {
                    if (inQuotes) {
                        currentField.append(char)
                    }
                }
                else -> currentField.append(char)
            }
            index++
        }

        if (currentField.isNotEmpty() || currentRow.isNotEmpty()) {
            currentRow.add(currentField.toString())
            rows.add(currentRow.toList())
        }

        return rows
    }
}
