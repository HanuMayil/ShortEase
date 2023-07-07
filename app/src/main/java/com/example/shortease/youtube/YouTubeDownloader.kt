package com.example.shortease.youtube

import android.content.Context
import android.media.MediaMetadataRetriever
import android.util.Log
import android.widget.Toast
import com.github.kiulian.downloader.YoutubeDownloader
import com.github.kiulian.downloader.downloader.YoutubeCallback
import com.github.kiulian.downloader.downloader.YoutubeProgressCallback
import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo
import com.github.kiulian.downloader.downloader.response.Response
import com.github.kiulian.downloader.model.videos.VideoInfo
import java.io.File

class YouTubeDownloader(private val context: Context) {
    private var downloaderProgress = 0
    val downloader = YoutubeDownloader()

    fun downloadYouTubeVideo(videoId: String, videoTitle: String) {
        downloader.config.maxRetries = 1000000

        val request = RequestVideoInfo(videoId)
            .callback(object : YoutubeCallback<VideoInfo?> {
                override fun onFinished(videoInfo: VideoInfo?) {
                    Log.d("youtube init", "Finished parsing")
                }

                override fun onError(throwable: Throwable) {
                    Log.d("youtube init", "Error: " + throwable.message)
                }
            })
            .async()
        val response = downloader.getVideoInfo(request)
        val video = response.data() ?: return

        val videoDir = File(context.filesDir, "videos")
        val outputDir = File(videoDir, videoId)
        if (!outputDir.exists()) {
            if (outputDir.mkdirs()) {
                val format = video.bestVideoWithAudioFormat()

                val downloadRequest = RequestVideoFileDownload(format)
                    .callback(object : YoutubeProgressCallback<File> {
                        override fun onDownloading(progress: Int) {
                            Log.d("youtube init", "Downloaded $progress%")
                            downloaderProgress = progress
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
                    .renameTo(videoTitle)
                    .async()
                downloader.downloadVideoFile(downloadRequest)
            } else {
                Toast.makeText(context, "Failed to create directory", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Already Downloaded", Toast.LENGTH_SHORT).show()
        }
    }
}
