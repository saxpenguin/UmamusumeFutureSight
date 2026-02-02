package com.saxpenguin.umamusumefuturesight.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class TimetableEntry(
    val id: String = "",
    val jpStartDate: String = "",
    val jpEndDate: String = "",
    val twStartDate: String = "",
    val twEndDate: String = "",
    val charaPool: List<String> = emptyList(),
    val cardPool: List<String> = emptyList()
)

@Serializable
data class CardInfo(
    val name: String,
    val type: String
)
