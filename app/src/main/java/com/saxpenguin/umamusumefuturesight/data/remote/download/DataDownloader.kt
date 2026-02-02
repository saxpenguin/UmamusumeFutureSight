package com.saxpenguin.umamusumefuturesight.data.remote.download

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataDownloader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val okHttpClient: OkHttpClient
) {

    private val filesToDownload = listOf(
        "cards.json",
        "characters.json",
        "timetable.json"
    )

    private val baseUrl = "https://umamusumefuturesight.web.app" // Adjust if using custom domain or different hosting

    suspend fun downloadDataFiles(): Boolean = withContext(Dispatchers.IO) {
        var success = true
        for (fileName in filesToDownload) {
            val url = "$baseUrl/$fileName"
            val file = File(context.filesDir, fileName)
            if (!downloadFile(url, file)) {
                success = false
                break // Stop on first failure, or continue to try others?
            }
        }
        success
    }

    private fun downloadFile(url: String, destination: File): Boolean {
        val request = Request.Builder().url(url).build()
        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return false
                val source = response.body?.source() ?: return false
                val sink = destination.sink().buffer()
                sink.writeAll(source)
                sink.close()
                return true
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }
}
