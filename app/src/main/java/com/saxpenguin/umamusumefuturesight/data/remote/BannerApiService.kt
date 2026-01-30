package com.saxpenguin.umamusumefuturesight.data.remote

import com.saxpenguin.umamusumefuturesight.model.Banner
import retrofit2.http.GET

interface BannerApiService {
    @GET("banners") // Placeholder endpoint
    suspend fun getBanners(): List<Banner>
}
