
import android.widget.VideoView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.shortease.R
import com.example.shortease.Screen
import com.example.shortease.ui.theme.ShortEaseTheme
import com.example.shortease.ui.theme.colorPalette
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

var isPlaying = false
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val videoId = navBackStackEntry?.arguments?.getString("videoId")
    Column(modifier = Modifier.fillMaxSize()) {
        Surface(color = colorPalette.ShortEaseRed
        ) {
            TopAppBar(
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = colorPalette.ShortEaseRed,
                    titleContentColor = colorPalette.ShortEaseWhite,
                ),
                title = {
                    Text(
                        text = "Preview",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 32.sp
                        )
                    )
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(route = Screen.MyVideos.route)
                    }) {
                        Image(
                            painter = painterResource(R.drawable.home_button),
                            contentDescription = "Home button",
                        )
                    }
                }
            )
        }

        Surface(modifier = Modifier.weight(1f), color = colorPalette.ShortEaseWhite) {
            ShortEaseTheme {
                val context = LocalContext.current
                val videoView = remember { VideoView(context) }
                Column(modifier = Modifier.fillMaxSize()) {


                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        if(!isPlaying && videoId != null) {
                            AndroidView(
                                factory = { context ->
                                    VideoView(context)
                                },
                                modifier = Modifier.fillMaxSize()
                            ) { videoView ->
                                val folder = File("${context.filesDir}/output/${videoId}/output-subtitles.mp4")
                                val folderContents = folder.listFiles()
                                val folderContentNames = folderContents?.map { file -> file.name } ?: emptyList()
                                val video = File(folder, folderContentNames.getOrNull(0) ?: "")
                                if(video.isFile) {
                                    videoView.setVideoPath(video.absolutePath)
                                    videoView.start()
                                    val scope = CoroutineScope(Dispatchers.Default)
                                    // Launch a coroutine
                                    scope.launch {
                                        // Wait for 30 seconds
                                        delay(30000)
                                        // Reset the variable
                                        isPlaying = false
                                    }
                                }
                            }
                        }
                    }

                    Row(modifier = Modifier
                        .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Button(

                            onClick = { /* Handle button click */ },
                            modifier = Modifier
                                .weight(1f)
                                .padding(top = 0.dp, start = 8.dp, end = 4.dp, bottom = 8.dp)
                                .height(64.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorPalette.ShortEaseRed,
                                contentColor = colorPalette.ShortEaseWhite
                            )

                        ) {
                            Image(
                                painter = painterResource(R.drawable.download_icon),
                                contentDescription = "Download",
                            )
                        }
                        Button(
                            onClick = { /* Handle button click */ },
                            modifier = Modifier
                                .weight(1f)
                                .padding(top = 0.dp, start = 8.dp, end = 4.dp, bottom = 8.dp)
                                .height(64.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorPalette.ShortEaseRed,
                                contentColor = colorPalette.ShortEaseWhite
                            )
                        ) {
                            Image(
                                painter = painterResource(R.drawable.upload_icon),
                                contentDescription = "Upload",
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun PreviewScreenPreview() {
    PreviewScreen(navController = rememberNavController())
}
