package com.saxpenguin.umamusumefuturesight.data.remote

import com.saxpenguin.umamusumefuturesight.data.remote.model.CardInfo
import com.saxpenguin.umamusumefuturesight.data.remote.model.TimetableEntry
import retrofit2.http.GET

interface BannerApiService {
    @GET("timetable.json")
    suspend fun getTimetable(): List<TimetableEntry>

    @GET("cards.json")
    suspend fun getCards(): Map<String, CardInfo>

    @GET("characters.json")
    suspend fun getCharacters(): Map<String, String>
}
