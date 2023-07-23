import android.graphics.ColorMatrix
import android.util.Log
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.example.shortease.endCropTime
import com.example.shortease.playerView
import com.example.shortease.startCropTime
import com.example.shortease.ui.theme.ShortEaseTheme
import com.example.shortease.ui.theme.colorPalette
import com.example.shortease.videoDuration
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
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
    val context = LocalContext.current
    val videoId = "zU9y354XAgM"
    val videoPath = "${context.filesDir}/videos/${videoId}"
    val folder = File(videoPath)
    val folderContents = folder.listFiles()
    val folderContentNames = folderContents?.map { file -> file.name } ?: emptyList()
    var finalVideoPath = "${context.filesDir}/videos/${videoId}/${folderContentNames.getOrNull(0)}"
    val coroutineScope = CoroutineScope(Dispatchers.Default)

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
                        text = stringResource(R.string.video_preview_header),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 24.sp
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
                            .padding(10.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if(folderContentNames.getOrNull(0) != null){
                            VideoPlayer(finalVideoPath)
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

@Composable
fun VideoPlayer(videoPath : String) {
    Log.d("video to see ", videoPath)
    val context = LocalContext.current
    val player = SimpleExoPlayer.Builder(context).build()
    playerView = PlayerView(context)
    val mediaItem = MediaItem.fromUri(videoPath)
    val playWhenReady by rememberSaveable {
        mutableStateOf(true)
    }
    player.setMediaItem(mediaItem)
    playerView.player = player

    val colorMatrix = ColorMatrix().apply {
        // Example: Increase video brightness by 50%
        setScale(10f, 10f, 10f, 1f)
    }

    LaunchedEffect(player) {
        player.prepare()
        Log.d("video to see ", videoPath)
        Log.d("time", videoPath)
        player.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                super.onPlayerStateChanged(playWhenReady, playbackState)
                if (playbackState == Player.STATE_READY) {
                    if(videoDuration == -1f) {
                        endCropTime = player.duration.toFloat()
                        videoDuration = endCropTime / 1000
                    }
                }
            }
        })
        player.playWhenReady = playWhenReady
        while (true) {
            if (endCropTime > 0f && (player.currentPosition.toFloat() > endCropTime)) {
                player.pause()
                player.seekTo(startCropTime.toLong())
            }
            else if(player.currentPosition.toFloat() < startCropTime) {
                player.seekTo(startCropTime.toLong())
            }
            delay(100) // Adjust the delay interval as needed
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            player.clearMediaItems()
            player.release()
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = {
                playerView
            }
        )
    }
}
