package com.example.shortease.youtube

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shortease.ui.theme.ShortEaseTheme
import com.example.shortease.ui.theme.colorPalette
import com.github.kiulian.downloader.YoutubeDownloader
import com.github.kiulian.downloader.downloader.YoutubeCallback
import com.github.kiulian.downloader.downloader.YoutubeProgressCallback
import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo
import com.github.kiulian.downloader.downloader.response.Response
import com.github.kiulian.downloader.model.videos.VideoInfo
import com.github.kiulian.downloader.model.videos.formats.VideoWithAudioFormat
import java.io.File



class DownloadActivity : ComponentActivity() {
    var downloader_progress = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Surface(modifier = Modifier.fillMaxSize(), color = lightColorScheme().background) {
                    ShortEaseTheme {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .padding(top = 50.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .height(100.dp)
                                    .width(100.dp),
                                strokeWidth = 2.dp,
                                color = colorPalette.ShortEaseRed

                            )
                            Text(
                                text = "Downloading Video ${downloader_progress}",
                                color = colorPalette.ShortEaseRed,
                                fontSize = 22.sp,
                                modifier = Modifier
                                    .padding(top=60.dp)
                            )
                        }
                    }
                }
            }
        }
        // init downloader with default config
        val downloader = YoutubeDownloader()
        downloader.config.maxRetries = 1000000
        val videoId = "K8TPaOnpl8U" // for url https://www.youtube.com/watch?v=abc12345

        val request = RequestVideoInfo(videoId)
            .callback(object : YoutubeCallback<VideoInfo?> {
                override fun onFinished(videoInfo: VideoInfo?) {
                    Log.d("youtube init","Finished parsing")
                }

                override fun onError(throwable: Throwable) {
                    Log.d("youtube init","Error: " + throwable.message)
                }
            })
            .async()
        val response = downloader.getVideoInfo(request)
        val video = response.data() // will block thread

        Log.d("youtube init", video.toString())

        val outputDir = filesDir


//        var selectedFormat: VideoWithAudioFormat? = null
//
//        for (format in video.videoWithAudioFormats()) {
//            if (format.videoQuality().toString().contains("medium")) {
//                selectedFormat = format
//                Log.d("youtube init", "${format.videoQuality()}")
//                break
//            }
//        }
//
// //If no 720p format was found, select the best available format
//        if (selectedFormat == null) {
//            selectedFormat = video.videoWithAudioFormats().get(0)
//        }


        val format = video.bestVideoWithAudioFormat()

        // Async downloading with callback
        val download_request = RequestVideoFileDownload(format)
            .callback(object : YoutubeProgressCallback<File> {
                override fun onDownloading(progress: Int) {
                    Log.d("youtube init", "Downloaded $progress%")
                    downloader_progress = progress
                }

                override fun onFinished(videoInfo: File) {
                    Log.d("youtube init", "FINISHED DONE")

                }

                override fun onError(throwable: Throwable) {
                    Log.d("youtube init", "Error: ${throwable.localizedMessage}")

                }
            })
            .maxRetries(100000000)
            .saveTo(outputDir) // Set the output directory to your app's local storage
            .async()

        val download_response: Response<File> = downloader.downloadVideoFile(download_request)
        //val data: File = download_response.data() // This will block the current thread
        //Log.d("youtube init", data.toString())



    }
    fun downloadYouTubeVideo(context: Context, videoUrl: String, videoTitle: String) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse(videoUrl)

        val request = DownloadManager.Request(downloadUri)
            .setTitle(videoTitle)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$videoTitle.mp4")

        try {
            downloadManager.enqueue(request)
        } catch (e: Exception) {
            Log.d("failed to download", "Failed to enqueue download request", e)
        }
    }
}