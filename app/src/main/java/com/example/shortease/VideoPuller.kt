package com.example.shortease
import android.content.Context
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.SearchListResponse
import com.google.api.services.youtube.model.SearchResult
import com.google.api.services.youtube.model.Thumbnail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class YouTubeApiClient(private val apiKey: String) {
    private val HTTP_TRANSPORT: NetHttpTransport = NetHttpTransport()
    private val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()
    private val TAG = "YouTubeApiClient"


    suspend fun fetchVideoThumbnails(channelId: String): List<ThumbnailItem> = withContext(Dispatchers.IO) {
        val youtube = YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, HttpRequestInitializer { })
            .setApplicationName("YourAppName")
            .build()

        val request = youtube.search().list(mutableListOf("snippet"))
        request.key = apiKey
        request.channelId = channelId
        request.type = mutableListOf("video")
        request.maxResults = 100000  // Adjust as per your requirements

        val response: SearchListResponse = request.execute()
        val items: List<SearchResult> = response.items

        val thumbnailItems = mutableListOf<ThumbnailItem>()
        for (item in items) {
            val snippet = item.snippet
            val title = snippet.title
            val thumbnails: Thumbnail? = snippet.thumbnails?.high
            val thumbnailUrl: String? = thumbnails?.url
            if (thumbnailUrl != null) {
                thumbnailItems.add(ThumbnailItem(title, thumbnailUrl))
            }
        }

        return@withContext thumbnailItems
    }
}

data class ThumbnailItem(val title: String, val thumbnailUrl: String)