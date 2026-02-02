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

import com.saxpenguin.umamusumefuturesight.model.BannerCardInfo
import com.saxpenguin.umamusumefuturesight.model.SupportCardType

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
        // Characters still use simple string mapping in this implementation for now
        // But we can adapt it if needed. For now assuming characters.json is old format or similar
        val charaMap = loadCharacterMap("characters.json")
        val charaLookup = buildCharacterLookup(charaMap)
        
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
                val resolvedCards = charaNames.map { name ->
                    val (resName, _, resolvedName) = resolveImageUrlAndType(name, charaLookup) ?: Triple(null, null, null)
                    BannerCardInfo(
                        name = resolvedName ?: name,
                        type = SupportCardType.UNKNOWN, // Characters don't have types like Speed/Stamina in this context
                        imageUrl = resName
                    )
                }
                
                banners.add(
                    Banner(
                        id = "${baseId}-C",
                        name = resolvedCards.joinToString(" / ") { it.name },
                        type = BannerType.CHARACTER,
                        jpStartDate = jpStartDate,
                        jpEndDate = jpEndDate,
                        imageUrl = resolvedCards.firstOrNull()?.imageUrl,
                        featuredCards = resolvedCards
                    )
                )
            }

            if (cardNames.isNotEmpty()) {
                val normalizedCardNames = cardNames.map { stripCardPrefix(it) }
                
                val resolvedCards = normalizedCardNames.mapIndexed { idx, name ->
                     val rawName = if (idx < cardNames.size) cardNames[idx] else name
                     val (resName, typeStr, resolvedName) = resolveImageUrlAndType(name, cardLookup) ?: Triple(null, null, null)
                     val cardType = try {
                         if (typeStr != null) SupportCardType.valueOf(typeStr) else SupportCardType.UNKNOWN
                     } catch (e: Exception) {
                         SupportCardType.UNKNOWN
                     }
                     
                     BannerCardInfo(
                         name = resolvedName ?: rawName,
                         type = cardType,
                         imageUrl = resName
                     )
                }
                
                banners.add(
                    Banner(
                        id = "${baseId}-S",
                        name = resolvedCards.joinToString(" / ") { it.name },
                        type = BannerType.SUPPORT_CARD,
                        jpStartDate = jpStartDate,
                        jpEndDate = jpEndDate,
                        imageUrl = resolvedCards.firstOrNull()?.imageUrl,
                        featuredCards = resolvedCards
                    )
                )
            }

            banners
        }
    }

    private fun loadCharacterMap(fileName: String): Map<String, String> {
         val text = readAssetText(fileName) ?: return emptyMap()
         return runCatching {
             json.decodeFromString<Map<String, String>>(text)
         }.getOrDefault(emptyMap())
    }

    private fun buildCharacterLookup(source: Map<String, String>): Map<String, Triple<String, String, String>> {
        val lookup = mutableMapOf<String, Triple<String, String, String>>()
        for ((fileName, displayName) in source) {
            val normalized = normalizeName(displayName)
            val resourceName = fileName.removeSuffix(".png")
            val entry = Triple(resourceName, "UNKNOWN", displayName) // Store display name
            
            if (normalized.isNotEmpty() && !lookup.containsKey(normalized)) {
                lookup[normalized] = entry
            }
            // Add direct Name lookup
            lookup[displayName.trim()] = entry
            
            // Extract ID from filename: chara_stand_1124_112402.png -> 112402
            val parts = resourceName.split("_")
            if (parts.size >= 4) {
                 // parts[0]=chara, parts[1]=stand, parts[2]=1124, parts[3]=112402
                 val specificId = parts.lastOrNull()
                 if (!specificId.isNullOrEmpty()) {
                     lookup[specificId] = entry
                 }
            }
        }
        return lookup
    }

    private fun readAssetText(fileName: String): String? {
        return runCatching {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        }.getOrNull()
    }

    @Serializable
    private data class CardInfo(
        val name: String,
        val type: String
    )

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

    private fun buildLookup(source: Map<String, CardInfo>): Map<String, Triple<String, String, String>> {
        val lookup = mutableMapOf<String, Triple<String, String, String>>()
        for ((fileName, info) in source) {
            val normalized = normalizeName(info.name)
            val resourceName = fileName.removeSuffix(".png")
            val entry = Triple(resourceName, info.type, info.name) // Store display name

            if (normalized.isNotEmpty() && !lookup.containsKey(normalized)) {
                lookup[normalized] = entry
            }
            lookup[info.name.trim()] = entry
            
            // Extract ID from filename: support_card_s_30285.png -> 30285
            // format: support_card_s_{ID}.png
             val parts = resourceName.split("_")
             if (parts.size >= 4) {
                 // parts[0]=support, parts[1]=card, parts[2]=s, parts[3]=30285
                 val id = parts.lastOrNull()
                 if (!id.isNullOrEmpty()) {
                     lookup[id] = entry
                 }
             }
        }
        return lookup
    }


    private fun loadTimetable(fileName: String): List<TimetableEntry> {
        val text = readAssetText(fileName) ?: return emptyList()
        return runCatching {
            json.decodeFromString<List<TimetableEntry>>(text)
        }.getOrDefault(emptyList())
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

    private fun resolveImageUrlAndType(names: List<String>, lookup: Map<String, Triple<String, String, String>>): Triple<String?, String?, String?>? {
        // First try exact match (useful for IDs)
        for (name in names) {
            val trimmed = name.trim()
             val match = lookup[trimmed]
            if (match != null) {
                return Triple(toResourceUrl(match.first), match.second, match.third)
            }
        }
    
        val normalizedNames = names.map { normalizeName(it) }.filter { it.isNotEmpty() }
        for (normalized in normalizedNames) {
            val match = lookup[normalized]
            if (match != null) {
                return Triple(toResourceUrl(match.first), match.second, match.third)
            }
        }

        for (normalized in normalizedNames) {
            val match = lookup.entries.firstOrNull {
                it.key.contains(normalized) || normalized.contains(it.key)
            }
            if (match != null) {
                return Triple(toResourceUrl(match.value.first), match.value.second, match.value.third)
            }
        }

        return null
    }
    
    // Kept for single name resolution if needed internally
    private fun resolveImageUrlAndType(name: String, lookup: Map<String, Triple<String, String, String>>): Triple<String?, String?, String?>? {
        return resolveImageUrlAndType(listOf(name), lookup)
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
