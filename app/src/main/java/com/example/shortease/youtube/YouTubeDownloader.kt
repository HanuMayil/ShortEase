package com.example.shortease.youtube

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import com.github.kiulian.downloader.YoutubeDownloader
import com.github.kiulian.downloader.downloader.YoutubeCallback
import com.github.kiulian.downloader.downloader.YoutubeProgressCallback
import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo
import com.github.kiulian.downloader.model.videos.VideoInfo
import com.github.kiulian.downloader.model.videos.formats.VideoFormat
import com.github.kiulian.downloader.model.videos.formats.VideoWithAudioFormat
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class YouTubeDownloader(private val context: Context) {
    private var downloaderProgress = 0
    val downloader = YoutubeDownloader()
    fun requestVideoDetail(videoId: String): List<VideoWithAudioFormat> {
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
        val video = response.data() ?: return emptyList()
        return video.videoWithAudioFormats()
    }

    fun downloadYouTubeVideo(
        videoId: String,
        videoTitle: String,
        format: VideoFormat,
        thumbnailURL: String,
        completionCallback: () -> Unit
    ) {
        downloader.config.maxRetries = 1000000
        val videoDir = File(context.filesDir, "videos")
        val outputDir = File(videoDir, videoId)
        if (!outputDir.exists()) {
            if (outputDir.mkdirs()) {
                val downloadRequest = RequestVideoFileDownload(format)
                    .callback(object : YoutubeProgressCallback<File> {
                        override fun onDownloading(progress: Int) {
                            Log.d("youtube init", "Downloaded $progress%")
                            downloaderProgress = progress
                        }

                        override fun onFinished(videoInfo: File) {
                            try {
                                val url = URL(thumbnailURL)
                                val connection = url.openConnection() as HttpURLConnection
                                connection.doInput = true
                                connection.connect()
                                val input: InputStream = connection.inputStream
                                val bitmap: Bitmap = BitmapFactory.decodeStream(input)

                                val file = File(outputDir, "thumbnail.jpg")
                                val fos = FileOutputStream(file)
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                                fos.flush()
                                fos.close()
                            } catch (e: Exception) {
                                Log.d("youtube init", "Thumbnail could not be saved")
                            }
                            Log.d("youtube init", "FINISHED DONE")
                            completionCallback()
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
