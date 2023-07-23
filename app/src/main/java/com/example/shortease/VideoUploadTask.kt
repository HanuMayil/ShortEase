import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.http.FileContent
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.Video
import com.google.api.services.youtube.model.VideoSnippet
import com.google.api.services.youtube.model.VideoStatus
import java.io.IOException

class VideoUploadTask(
    private val youtube: YouTube,
    private val videoTitle: String,
    private val videoDescription: String,
    private val videoTags: List<String>,
    private val videoPath: FileContent?
) : AsyncTask<Void, Void, String>() {

    override fun doInBackground(vararg params: Void?): String {
        val apiKey = "AIzaSyCZ1aVkQw5j_ljA-AesWfHh0c6lnGQIq-A" // Replace with your API Key

        try {
            // Create a new Video object with snippet and status information
            val videoObject = Video()
            val snippet = VideoSnippet()
            snippet.title = videoTitle
            snippet.description = videoDescription
            snippet.tags = videoTags
            videoObject.snippet = snippet

            // Set the status of the video to "public"
            val status = VideoStatus()
            status.privacyStatus = "public"
            videoObject.status = status

            val credential = GoogleCredential().setAccessToken(AccessTokenHolder.accessToken)

            val uploadedVideo = youtube.videos().insert(listOf("snippet", "status"), videoObject, videoPath)
                .setKey(apiKey)
                .setAccessToken(credential.accessToken)
                .execute()

            // Return the ID of the uploaded video
            return uploadedVideo.id

        } catch (e: UserRecoverableAuthIOException) {
            // Handle user authorization required (if necessary)
            throw e
        } catch (e: IOException) {
            // Handle other network or API-related exceptions
            throw e
        }
    }

    override fun onPostExecute(result: String) {
        // Video uploaded successfully, use the video ID if needed
        // result contains the ID of the uploaded video
        // Perform any additional tasks here after the upload completes
    }

    override fun onCancelled() {
        // Upload task cancelled
        // Handle cancellation if needed
    }
}
