package com.example.shortease
import androidx.compose.runtime.MutableState
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.SearchListResponse
import com.google.api.services.youtube.model.SearchResult
import com.google.api.services.youtube.model.Thumbnail
import com.google.api.services.youtube.model.Video
import com.google.api.services.youtube.model.VideoListResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigInteger
import java.net.URL

class YouTubeApiClient(private val apiKey: String) {
    private val HTTP_TRANSPORT: NetHttpTransport = NetHttpTransport()
    private val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()
    private val TAG = "YouTubeApiClient"
    suspend fun fetchVideoThumbnails(channelId: String, channelIconUrl: MutableState<String>): List<ThumbnailItem> = withContext(Dispatchers.IO) {
        val youtube = YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, HttpRequestInitializer { })
            .setApplicationName("YourAppName")
            .build()

        val searchRequest = youtube.search().list(mutableListOf("snippet"))
        searchRequest.key = apiKey
        searchRequest.channelId = channelId
        searchRequest.type = mutableListOf("video")  // Only retrieve videos
        searchRequest.videoDuration = "medium"  // Include videos of any duration
        searchRequest.maxResults = 10  // Adjust as per your requirements

        val searchResponse: SearchListResponse = searchRequest.execute()
        val searchItems: List<SearchResult> = searchResponse.items

        if (searchItems.isNotEmpty()) {
            val doc = URL("https://www.youtube.com/channel/$channelId").readText()
            val regex = Regex("<meta property=\"og:image\" content=\"([^\"]+)\"")
            val matchResult = regex.find(doc)
            channelIconUrl.value = matchResult?.groupValues?.get(1).toString()
        }

        val thumbnailItems = mutableListOf<ThumbnailItem>()
        for (searchItem in searchItems) {
            val videoId = searchItem.id.videoId

            // Fetch video details including view count
            val videosRequest = youtube.videos().list(mutableListOf("statistics"))
            videosRequest.key = apiKey
            videosRequest.id = mutableListOf(videoId)
            val videosResponse: VideoListResponse = videosRequest.execute()
            val videoItems: List<Video> = videosResponse.items

            // Process video details
            if (videoItems.isNotEmpty()) {
                val video = videoItems[0]
                val snippet = searchItem.snippet
                val title = snippet.title
                val thumbnails: Thumbnail? = snippet.thumbnails?.high
                val thumbnailUrl: String? = thumbnails?.url
                val viewCount = video.statistics.viewCount

                if (thumbnailUrl != null) {
                    thumbnailItems.add(ThumbnailItem(title, thumbnailUrl, viewCount))
                }
            }
        }

        return@withContext thumbnailItems
    }
}

data class ThumbnailItem(val title: String, val thumbnailUrl: String, val viewCount: BigInteger)