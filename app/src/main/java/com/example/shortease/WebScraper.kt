import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class WebScraper : ViewModel() {
    // MutableState for the response of the API call
    var response by mutableStateOf<String?>(null)

    fun callAPI(videoUrl: String) {

        // Endpoint URL
        val endpoint = "https://w3xpatzhgt2c4qqvr4nyvkrrqe0yihcj.lambda-url.us-east-2.on.aws/"

        // JSON request body
        val requestBody = """
            {
                "video": "$videoUrl"
            }
        """.trimIndent()


        // Create the OkHttpClient
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // Connection timeout (10 seconds in this example)
            .readTimeout(40, TimeUnit.SECONDS)    // Read timeout (30 seconds in this example)
            .build()

        // Create the request with headers and the JSON body
        val request = Request.Builder()
            .url(endpoint)
            .header("x-api-key", "7DUzoimhjqZazwzFBFdj8V77Zv0U2Q90HgjYW4h0")
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
