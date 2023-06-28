package com.example.shortease
import android.content.Context
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.ThumbnailDetails
import com.google.api.services.youtube.model.VideoListResponse
import com.google.api.services.youtube.model.VideoSnippet

class YouTubeApiClient(private val context: Context, private val apiKey: String) {
    private val HTTP_TRANSPORT: NetHttpTransport = NetHttpTransport()
    private val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()
    private val TAG = "YouTubeApiClient"

    fun fetchVideoThumbnails(): List<ThumbnailItem> {
        val youtube = YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, HttpRequestInitializer { })
            .setApplicationName("YourAppName")
            .build()

        val request = youtube.videos().list(listOf("snippet"))
        request.key = apiKey
        request.maxResults = 10  // Adjust as per your requirements

        val response: VideoListResponse = request.execute()
        val items = response.items

        val thumbnailItems = mutableListOf<ThumbnailItem>()
        for (item in items) {
            val snippet: VideoSnippet? = item.snippet
            if (snippet != null) {
                val title = snippet.title
                val thumbnailDetails: ThumbnailDetails? = snippet.thumbnails
                if (thumbnailDetails != null && thumbnailDetails.default != null) {
                    val thumbnailUrl: String? = thumbnailDetails.default.url
                    thumbnailUrl?.let {
                        thumbnailItems.add(ThumbnailItem(title, thumbnailUrl))
                    }
                }
            }
        }
        return thumbnailItems
    }
}

data class ThumbnailItem(val title: String, val thumbnailUrl: String)
