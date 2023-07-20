package com.example.shortease

import VideoHandle.EpEditor
import VideoHandle.EpVideo
import VideoHandle.OnEditorListener
import android.content.Context
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.shortease.ui.theme.colorPalette
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.view.TextureView
import android.view.View
import java.io.File
import java.util.logging.Filter

var shouldRenderContent by mutableStateOf(-1)
var videoDuration = -1f
lateinit var playerView: PlayerView
var startCropTime = 0f
var endCropTime = 0f
var audioVolume = 100f
var red = 0f
var blue = 0f
var green = 0f
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoEditorScreen(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val context = LocalContext.current
    val videoId = navBackStackEntry?.arguments?.getString("videoId")
    val videoPath = "${context.filesDir}/videos/${videoId}"
    val folder = File(videoPath)
    val folderContents = folder.listFiles()
    val folderContentNames = folderContents?.map { file -> file.name } ?: emptyList()
    var finalVideoPath = "${context.filesDir}/videos/${videoId}/${folderContentNames.getOrNull(0)}"
    val coroutineScope = CoroutineScope(Dispatchers.Default)
    DisposableEffect(Unit) {
        onDispose {
            shouldRenderContent = -1
            videoDuration = -1f
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
                            text = stringResource(R.string.video_editor_header),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 24.sp
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navController.popBackStack()
                                resetVariables()
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
                                modifier = Modifier.clickable {
                                    if (videoId != null) {
                                        val fileName = folderContentNames.getOrNull(0) ?: "output"
                                        val thumbnail = File(context.filesDir, "output/${videoId}/thumbnail.jpg")
                                        thumbnail.delete()
                                        // Run the cropVideo function in the background using coroutines
                                        coroutineScope.launch {
                                            var deferred = CompletableDeferred<Unit>()
                                            cropVideo(
                                                videoId = videoId,
                                                context = context,
                                                fileName = fileName,
                                                completionCallback = {
                                                    deferred.complete(Unit)
                                                 }
                                            )
                                            deferred.await()
                                            deferred = CompletableDeferred<Unit>()
                                            processAudio(
                                                videoId = videoId,
                                                context = context,
                                                completionCallback = {
                                                    deferred.complete(Unit)
                                                })
                                            deferred.await()
                                            val sourceFile = File(context.filesDir, "videos/$videoId/thumbnail.jpg")
                                            val destinationFile = File(context.filesDir, "output/$videoId/thumbnail.jpg")
                                            if (sourceFile.exists()) {
                                                sourceFile.copyTo(destinationFile, overwrite = true)
                                            } else {
                                                Log.d("Cropping", "Source file does not exist: ${sourceFile.path}")
                                            }
                                            resetVariables()
                                        }

                                        // Navigate to a different page using the NavController
                                        navController.navigate("generate?videoId=$videoId")
                                    }
                                }
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
                    var values by remember { mutableStateOf(startCropTime..endCropTime) }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.edit_crop),
                            color = Color.White,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                        CropRangeSlider(
                            range = range,
                            values = values,
                            onRangeChanged = { newValues ->
                                values = newValues
                                startCropTime = values.start.toFloat()
                                endCropTime = values.endInclusive.toFloat()

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
                    var value by remember { mutableStateOf(audioVolume) }

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.edit_audio),
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
                                audioVolume = newValue
                                playerView.player?.setVolume(audioVolume/100)
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
                    var valueRed by remember { mutableStateOf(red) }
                    var valueBlue by remember { mutableStateOf(blue) }
                    var valueGreen by remember { mutableStateOf(green) }

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SliderComponentRGB(
                            value = valueRed,
                            RGB = "RED",
                            onValueChange = { newValue ->
                                valueRed = newValue
                                red = newValue
                            })
                        SliderComponentRGB(
                            value = valueBlue,
                            RGB = "BLUE",
                            onValueChange = { newValue ->
                                valueBlue = newValue
                                blue = newValue
                            })
                        SliderComponentRGB(
                            value = valueGreen,
                            RGB = "GREEN",
                            onValueChange = { newValue ->
                                valueGreen = newValue
                                green = newValue
                            })
                    }
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
fun CropRangeSlider(
    range: ClosedFloatingPointRange<Float>,
    values: ClosedFloatingPointRange<Float>,
    onRangeChanged: (ClosedFloatingPointRange<Float>) -> Unit
) {
    val context = LocalContext.current
    val timestamp = remember(values) {
        calculateTimestamp(context = context, values)
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
    onValueChange: (Float) -> Unit,
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
            text = stringResource(R.string.edit_audio_text) + " $formattedValue",
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
@Composable
fun SliderComponentRGB(
    value: Float,
    onValueChange: (Float) -> Unit,
    RGB: String
) {
    val formattedValue = remember(value) {
        formatSliderValue(value)
    }
    Column {
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = -10f..10f,
            steps = 1,
            colors = SliderDefaults.colors(
                thumbColor = colorPalette.ShortEaseWhite,
                activeTrackColor = colorPalette.ShortEaseRed,
                inactiveTrackColor = colorPalette.ShortEaseRed.copy(alpha = 0.2f)
            ),
        )
        Text(
            text = RGB,
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

private fun calculateTimestamp(context: Context, values: ClosedFloatingPointRange<Float>): String {
    val startPosition = values.start.toInt()
    val endPosition = values.endInclusive.toInt()
    return getString(context, R.string.start) + " ${startPosition/1000F} s, " + getString(context, R.string.end) + " ${endPosition/1000F} s"
}

private fun formatSliderValue(value: Float): String {
    val formattedValue = value.toInt()
    return formattedValue.toString()
}

fun processFilters(context: Context, videoId: String, fileName: String, filter: String, completionCallback: () -> Unit){
    val fileDir = File(context.filesDir, "output/$videoId/output-cropped.mp4")
    var videoDir = File(context.filesDir, "videos/${videoId}")

    val epVideo: EpVideo = EpVideo("${fileDir.toString()}")
    Log.d("youtube init", "name" + epVideo)

    val outFile = File(context.filesDir, "output/$videoId/output-filtered.mp4")

    val editorListener = object : OnEditorListener {
        override fun onSuccess() {
            // Implement your logic when the operation is successful
            Log.d("youtube init", "Finished")
            completionCallback()
        }

        override fun onFailure() {
            // Implement your logic when the operation fails
            Log.d("youtube init", "Failed")
        }

        override fun onProgress(progress: Float) {
            // Implement your logic to track the progress of the operation
            Log.d("youtube init", "Progress: $progress")
        }

    }
    Log.d("youtube init", "HERE")
    epVideo.addFilter(filter)
}

private fun cropVideo(context: Context, videoId: String, fileName: String,  completionCallback: () -> Unit) {
    var fileDir = File(context.filesDir, "output/${videoId}")
    var videoDir = File(context.filesDir, "videos/${videoId}")
    if(!fileDir.isDirectory) {
        fileDir.mkdirs()
    }
    var epVideo = EpVideo("${videoDir.toString()}/${fileName}")
    epVideo.clip(startCropTime/1000, (endCropTime- startCropTime)/1000)
    var outFile = File(fileDir, "output-cropped.mp4")
    val outputOption = EpEditor.OutputOption(outFile.toString())
    outputOption.frameRate = 30
    outputOption.bitRate = 10
    val editorListener = object : OnEditorListener {
        override fun onSuccess() {
            // Implement your logic when the operation is successful
            completionCallback()
        }

        override fun onFailure() {
            // Implement your logic when the operation fails
            Log.d("Cropping", "Failed")
        }

        override fun onProgress(progress: Float) {
            // Implement your logic to track the progress of the operation
            Log.d("Cropping", "Progress: ${progress}")
        }
    }
    try {
        // Video cropping code here
        EpEditor.exec(epVideo, outputOption, editorListener)
    } catch (e: Exception) {
        Log.e("Cropping", "Error occurred during video cropping: ${e.message}", e)
    }
}

fun resetVariables() {
    startCropTime = 0f
    endCropTime = 0f
    audioVolume = 100f
}

fun processAudio(context: Context, videoId: String, completionCallback: () -> Unit) {
    val fileDir = File(context.filesDir, "output/$videoId/output-cropped.mp4")
    var videoDir = File(context.filesDir, "videos/${videoId}")

    val epVideo: EpVideo = EpVideo("${fileDir.toString()}")
    Log.d("youtube init", "name" + epVideo)

    val outFile = File(context.filesDir, "output/$videoId/output-audio.mp4")

    val editorListener = object : OnEditorListener {
        override fun onSuccess() {
            // Implement your logic when the operation is successful
            Log.d("youtube init", "Finished")
            completionCallback()
        }

        override fun onFailure() {
            // Implement your logic when the operation fails
            Log.d("youtube init", "Failed")
        }

        override fun onProgress(progress: Float) {
            // Implement your logic to track the progress of the operation
            Log.d("youtube init", "Progress: $progress")
        }
    }

    Log.d("youtube init", "HERE")
    Log.d("audio volume", "${audioVolume/100f}")
    EpEditor.music(fileDir.toString(), fileDir.toString(), outFile.toString(), audioVolume/100f, 0.0F, editorListener)
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
