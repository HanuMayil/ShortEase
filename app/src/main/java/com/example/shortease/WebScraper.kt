import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.ViewModel
import com.example.shortease.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class WebScraper : ViewModel() {
    // MutableState for the response of the API call
    var response by mutableStateOf<String?>(null)

    fun callAPI(context: Context, videoId: String) {

        // Endpoint URL
        val endpoint = getString(context, R.string.webscraper_endpoint)

        // JSON request body
        val requestBody = """
            {
                "video": "https://www.youtube.com/watch?v=$videoId"
            }
        """.trimIndent()

        // Create the OkHttpClient
        val client = OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .build()

        // Create the request with headers and the JSON body
        val request = Request.Builder()
            .url(endpoint)
            .header("x-api-key", getString(context, R.string.webscraper_api_key))
            .header("Content-Type", "application/json")
            .post(requestBody.toRequestBody("application/json; charset=utf-8".toMediaType()))
            .build()

        // Make the API call in a CoroutineScope to handle it asynchronously
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                withContext(Dispatchers.Main) {
                    this@WebScraper.response = responseBody
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}