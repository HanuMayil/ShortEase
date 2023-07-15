package com.example.shortease

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
import com.example.shortease.ui.theme.colorPalette
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import java.io.File
import kotlin.properties.Delegates

var shouldRenderContent by mutableStateOf(-1)
var videoDuration = -1f
lateinit var playerView: PlayerView
var startCropTime = 0f
var endCropTime = 0f
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoEditorScreen(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val videoId = navBackStackEntry?.arguments?.getString("param1")
    val videoPath = "${LocalContext.current.filesDir}/videos/${videoId}"
    val folder = File(videoPath)
    val folderContents = folder.listFiles()
    val folderContentNames = folderContents?.map { file -> file.name } ?: emptyList()
    var finalVideoPath = "${LocalContext.current.filesDir}/videos/${videoId}/${folderContentNames.getOrNull(0)}"
    DisposableEffect(Unit) {
        onDispose {
            shouldRenderContent = -1
        }
    }

    Box (
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorPalette.ShortEaseBlack)
        ) {
            Surface(
                color = colorPalette.ShortEaseRed
            ) {
                TopAppBar(
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = colorPalette.ShortEaseRed,
                        titleContentColor = colorPalette.ShortEaseWhite,
                    ),
                    title = {
                        Text(
                            text = "Editor",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 32.sp
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navController.popBackStack()
                            }
                        ) {
                            Image(
                                painter = painterResource(R.drawable.back_button),
                                contentDescription = "Back",
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            navController.navigate(route = Screen.PreviewScreen.route)
                        }) {
                            Image(
                                painter = painterResource(R.drawable.forward_button),
                                contentDescription = "Forward Button",
                            )
                        }
                    }
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(colorPalette.ShortEaseWhite)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                if(folderContentNames.getOrNull(0) != null){
                    VideoPlayer(finalVideoPath);
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(colorPalette.ShortEaseBlack)
            ) {
                if (shouldRenderContent == R.drawable.scissors_icon) {
                    // Render your content here based on the condition
                    var range by remember { mutableStateOf(0f..videoDuration*1000) }
                    var values by remember { mutableStateOf(0f..videoDuration*1000) }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Crop Video",
                            color = Color.White,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                        RangeSliderComponent(
                            range = range,
                            values = values,
                            onRangeChanged = { newValues ->
                                values = newValues
                            })
                    }
                }
                else if (shouldRenderContent == R.drawable.text_icon) {
                    // Render your content here based on the condition
                    Text(
                        text = "text",
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else if (shouldRenderContent == R.drawable.music_note_icon) {
                    var value by remember { mutableStateOf(100f) }

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Edit Music",
                            color = Color.White,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                        SliderComponent(
                            value = value,
                            onValueChange = { newValue ->
                                value = newValue
                        })
                    }
                }
                else if (shouldRenderContent == R.drawable.effect_icon) {
                    // Render your content here based on the condition
                    Text(
                        text = "effect",
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else if (shouldRenderContent == R.drawable.filter_icon) {
                    // Render your content here based on the condition
                    Text(
                        text = "filter",
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
//
                    BottomBarButton(
                        iconResId = R.drawable.scissors_icon,
                        contentDescription = "Trimming",
                        selected = true // Optional: Specify if the icon is selected or not
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    BottomBarButton(
                        iconResId = R.drawable.text_icon,
                        contentDescription = "Subtitles",
                        selected = true // Optional: Specify if the icon is selected or not
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    BottomBarButton(
                        iconResId = R.drawable.music_note_icon,
                        contentDescription = "Music",
                        selected = true // Optional: Specify if the icon is selected or not
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    BottomBarButton(
                        iconResId = R.drawable.effect_icon,
                        contentDescription = "Effects",
                        selected = true // Optional: Specify if the icon is selected or not
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    BottomBarButton(
                        iconResId = R.drawable.filter_icon,
                        contentDescription = "Filter",
                        selected = true // Optional: Specify if the icon is selected or not
                    )
                    Spacer(modifier = Modifier.height(50.dp))
                }
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .align(Alignment.TopCenter)
                ) {
                    drawRect(
                        color = colorPalette.ShortEaseRed,
                        topLeft = Offset(0f, 0f),
                        size = Size(size.width, size.height)
                    )
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun RangeSliderComponent(
    range: ClosedFloatingPointRange<Float>,
    values: ClosedFloatingPointRange<Float>,
    onRangeChanged: (ClosedFloatingPointRange<Float>) -> Unit
) {
    val timestamp = remember(values) {
        calculateTimestamp(values)
    }

    Column {
        RangeSlider(
            value = values,
            onValueChange = { newValues ->
                onRangeChanged(newValues)
                playerView.player?.seekTo(newValues.start.toLong())
            },
            valueRange = range,
            steps = 100,
            colors = SliderDefaults.colors(
                thumbColor = colorPalette.ShortEaseWhite,
                activeTrackColor = colorPalette.ShortEaseRed,
                inactiveTrackColor = colorPalette.ShortEaseRed.copy(alpha = 0.2f)
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Text(
            text = timestamp,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterHorizontally),
            style = TextStyle(color = colorPalette.ShortEaseRed)
        )
    }
}
@Composable
fun SliderComponent(
    value: Float,
    onValueChange: (Float) -> Unit
) {
    val formattedValue = remember(value) {
        formatSliderValue(value)
    }

    Column {
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..100f,
            steps = 100,
            colors = SliderDefaults.colors(
                thumbColor = colorPalette.ShortEaseWhite,
                activeTrackColor = colorPalette.ShortEaseRed,
                inactiveTrackColor = colorPalette.ShortEaseRed.copy(alpha = 0.2f)
            ),
        )
        Text(
            text = "Audio Volume: $formattedValue",
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}



private fun calculateTimestamp(values: ClosedFloatingPointRange<Float>): String {
    val startPosition = values.start.toInt()
    val endPosition = values.endInclusive.toInt()
    return "Start: ${startPosition/1000F} s, End: ${endPosition/1000F} s"
}

private fun formatSliderValue(value: Float): String {
    val formattedValue = value.toInt()
    return formattedValue.toString()
}


@Composable
fun BottomBarButton(
    iconResId: Int,
    contentDescription: String,
    selected: Boolean
) {
    Image(
        painter = painterResource(iconResId),
        contentDescription = contentDescription,
        modifier = Modifier
            .size(50.dp)
            .padding(vertical = 4.dp)
            .clickable {
                shouldRenderContent = iconResId
            }
        ,
        alpha = if (selected) 1f else 0.6f // Optional: Customize the opacity of selected/unselected icons
    )
}

@Composable
@Preview
private fun VideoEditorPreview() {
    VideoEditorScreen(
        navController = rememberNavController()
    )
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
    LaunchedEffect(player) {
        player.prepare()
        player.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                super.onPlayerStateChanged(playWhenReady, playbackState)
                if (playbackState == Player.STATE_READY) {
                    if(videoDuration == -1f) {
                        endCropTime = player.duration.toFloat()
                        videoDuration = endCropTime / 1000
                    }
                    else {
                        Log.d("time", "testing")
                        val currentPosition = player.currentPosition / 1000f
                        // Check if the current position exceeds the endCropTime
                        if (currentPosition >= endCropTime) {
                            // Pause the player
                            player.playWhenReady = false
                            player.seekTo((endCropTime/1000).toLong())
                        }
                    }
                }
            }
        })
        player.playWhenReady = playWhenReady

    }
    DisposableEffect(Unit) {
        onDispose {
            player.clearMediaItems()
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