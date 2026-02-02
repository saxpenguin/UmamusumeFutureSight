package com.saxpenguin.umamusumefuturesight.data

import android.content.Context
import com.saxpenguin.umamusumefuturesight.data.remote.model.CardInfo
import com.saxpenguin.umamusumefuturesight.data.remote.model.TimetableEntry
import com.saxpenguin.umamusumefuturesight.model.Banner
import com.saxpenguin.umamusumefuturesight.model.BannerCardInfo
import com.saxpenguin.umamusumefuturesight.model.BannerType
import com.saxpenguin.umamusumefuturesight.model.SupportCardType
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BannerDataProcessor @Inject constructor(
    private val context: Context
) {
    fun process(
        timetable: List<TimetableEntry>,
        charaMap: Map<String, String>,
        cardMap: Map<String, CardInfo>
    ): List<Banner> {
        if (timetable.isEmpty()) {
            return emptyList()
        }

        val charaLookup = buildCharacterLookup(charaMap)
        val cardLookup = buildLookup(cardMap)
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
                        type = SupportCardType.UNKNOWN,
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

    private fun buildCharacterLookup(source: Map<String, String>): Map<String, Triple<String, String, String>> {
        val lookup = mutableMapOf<String, Triple<String, String, String>>()
        for ((fileName, displayName) in source) {
            val normalized = normalizeName(displayName)
            val resourceName = fileName.removeSuffix(".png")
            val entry = Triple(resourceName, "UNKNOWN", displayName)
            
            if (normalized.isNotEmpty() && !lookup.containsKey(normalized)) {
                lookup[normalized] = entry
            }
            lookup[displayName.trim()] = entry
            
            val parts = resourceName.split("_")
            if (parts.size >= 4) {
                 val specificId = parts.lastOrNull()
                 if (!specificId.isNullOrEmpty()) {
                     lookup[specificId] = entry
                 }
            }
        }
        return lookup
    }

    private fun buildLookup(source: Map<String, CardInfo>): Map<String, Triple<String, String, String>> {
        val lookup = mutableMapOf<String, Triple<String, String, String>>()
        for ((fileName, info) in source) {
            val normalized = normalizeName(info.name)
            val resourceName = fileName.removeSuffix(".png")
            val entry = Triple(resourceName, info.type, info.name)

            if (normalized.isNotEmpty() && !lookup.containsKey(normalized)) {
                lookup[normalized] = entry
            }
            lookup[info.name.trim()] = entry
            
             val parts = resourceName.split("_")
             if (parts.size >= 4) {
                 val id = parts.lastOrNull()
                 if (!id.isNullOrEmpty()) {
                     lookup[id] = entry
                 }
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

    private fun resolveImageUrlAndType(names: List<String>, lookup: Map<String, Triple<String, String, String>>): Triple<String?, String?, String?>? {
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
    
    private fun resolveImageUrlAndType(name: String, lookup: Map<String, Triple<String, String, String>>): Triple<String?, String?, String?>? {
        return resolveImageUrlAndType(listOf(name), lookup)
    }

    private fun toResourceUrl(resourceName: String): String {
        return "android.resource://${context.packageName}/drawable/$resourceName"
    }
}
