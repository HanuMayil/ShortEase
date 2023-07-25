package com.example.shortease

import Jni.FFmpegCmd
import Jni.VideoUitls
import VideoHandle.CmdList
import VideoHandle.EpEditor
import VideoHandle.EpVideo
import VideoHandle.OnEditorListener
import WebScraper
import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.Typeface
import android.media.MediaMetadataRetriever
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.viewmodel.compose.viewModel
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
import showPopup
import java.io.File
import java.text.DecimalFormat
import kotlin.math.absoluteValue

var shouldRenderContent by mutableStateOf(-1)
var videoDuration = -1f
lateinit var playerView: PlayerView
var startCropTime = 0f
var endCropTime = 0f
var audioVolume = 100f
var subtitleList = mutableListOf<PlayerSubtitles>()
var y = 0f
var metrics: Double? = -1.0
var apiCalled = false
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoEditorScreen(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val context = LocalContext.current
    val videoId = navBackStackEntry?.arguments?.getString("videoId")
    val viewModel: WebScraper = viewModel()
    LaunchedEffect(videoId) {
        if (!videoId.isNullOrEmpty()) {
            viewModel.callAPI(context = context, videoId = videoId)
        }
    }
    if(metrics == -1.0 && !apiCalled) {
        viewModel.response?.let {
            Log.d("heatmap time", it)
            metrics = it.toDoubleOrNull()
            apiCalled = true
        }
    }
    val videoPath = "${context.filesDir}/videos/${videoId}"
    val folder = File(videoPath)
    val folderContents = folder.listFiles()
    val folderContentNames = folderContents?.map { file -> file.name } ?: emptyList()
    var finalVideoPath = "${context.filesDir}/videos/${videoId}/${folderContentNames.getOrNull(0)}"
    val coroutineScope = CoroutineScope(Dispatchers.Default)

    fun YToRgb(y: Int): Color {
        val invertedY = 255 - y
        return Color(invertedY, invertedY, invertedY).copy(alpha = 0.8f)
    }

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
                                viewModel.response = null
                                resetVariables()
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
                                            if(subtitleList.size > 0 ){
                                                deferred = CompletableDeferred<Unit>()
                                                processSubtitles(
                                                    videoId = videoId,
                                                    context = context,
                                                    subtitleList = subtitleList,
                                                    completionCallback = {
                                                        deferred.complete(Unit)
                                                    }
                                                )
                                                deferred.await()
                                            }
                                            deferred = CompletableDeferred<Unit>()
                                            filterVideo(
                                                videoId = videoId,
                                                context = context,
                                                fileName = fileName,
                                                completionCallback = {
                                                    deferred.complete(Unit)
                                                }
                                            )
                                            deferred.await()
                                            val outputs = File(context.filesDir, "output/$videoId")
                                            val files = outputs.listFiles()
                                            files?.let {
                                                for (file in it) {
                                                    Log.d("publish test", file.name + "." + file.extension)
                                                    if (file.isFile && (file.name) != fileName) {
                                                        file.delete()
                                                    }
                                                }
                                            }
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
                    .height(170.dp)
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            when {
                                metrics == null -> {
                                    // If metrics is -1, show the message "Most replayed not available"
                                    Toast.makeText(context, getString(context, R.string.error_metrics), Toast.LENGTH_SHORT).show()
                                }

                                metrics!! < 0 -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = getString(context, R.string.fetching_metrics),
                                        color = colorPalette.ShortEaseWhite,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }

                                else -> {
                                    // If metrics is >= 0, render a button
                                    Button(onClick = {
                                        val metricsValue = (metrics!!) * videoDuration
                                        val adjustedStart = ((metricsValue - 10000.0).coerceAtLeast(0.0)).toFloat()
                                        val adjustedEnd = ((metricsValue + 10000.0).coerceAtMost(videoDuration.toDouble() * 1000)).toFloat()

                                        values = adjustedStart..adjustedEnd
                                        startCropTime = values.start
                                        endCropTime = values.endInclusive

                                        playerView.player?.seekTo(startCropTime.toLong())
                                    }) {
                                        Text(text = getString(context, R.string.apply_metrics))
                                    }
                                }
                            }
                        }
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
                    showPopup(0f..videoDuration*1000, startCropTime..endCropTime, subtitleList,
                        onConfirm = {
                                newSubtitleList ->
                            subtitleList = newSubtitleList
                            shouldRenderContent = -1
                            subtitleList.forEach{item -> println("SUBTITLE ITEM $item")}
                        })

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
                else if (shouldRenderContent == R.drawable.filter_icon) {
                    var sliderValue1 by remember { mutableStateOf(y) }
                    // Render your content here based on the condition
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.edit_filter),
                            color = Color.White,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                        Column {
                            Slider(
                                value = sliderValue1,
                                onValueChange = { newValue ->
                                    sliderValue1 = newValue
                                    y = newValue
                                },
                                valueRange = 0f..255f,
                                steps = 255,
                                colors = SliderDefaults.colors(
                                    thumbColor = colorPalette.ShortEaseWhite,
                                    activeTrackColor = colorPalette.ShortEaseRed,
                                    inactiveTrackColor = colorPalette.ShortEaseRed.copy(alpha = 0.2f)
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .size(20.dp)
                                    .background(YToRgb(sliderValue1.toInt()))
                            )
                        }
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

fun roundToOneDecimalPoint(number: Float): String {
    val df = DecimalFormat("#.#")
    return df.format(number)
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
        calculateTimestamp(context,values)
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

fun calculateTimestamp(context: Context, values: ClosedFloatingPointRange<Float>): String {
    val startPosition = values.start.toInt()
    val endPosition = values.endInclusive.toInt()
    return getString(context, R.string.start) + " ${startPosition/1000F} s, " + getString(context, R.string.end) + " ${endPosition/1000F} s"
}

private fun formatSliderValue(value: Float): String {
    val formattedValue = value.toInt()
    return formattedValue.toString()
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
private fun filterVideo(context: Context, videoId: String, fileName: String,  completionCallback: () -> Unit) {
    var fileDir = File(context.filesDir, "output/$videoId/output-subtitles.mp4")
    if (subtitleList.isEmpty()) {
        fileDir = File(context.filesDir, "output/$videoId/output-audio.mp4")
    }

    val epVideo: EpVideo = EpVideo("${fileDir.toString()}")
    Log.d("youtube init", "name" + epVideo)

    val outFile = File(context.filesDir, "output/$videoId/$fileName")
    val outputOption = EpEditor.OutputOption(outFile.toString())
    outputOption.frameRate = 30
    outputOption.bitRate = 10

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

    epVideo.addFilter("lutyuv=y='val-${y.toInt()}':u='val-0':v='val-0'")
    EpEditor.exec(epVideo, outputOption, editorListener)
}

fun resetVariables() {
    startCropTime = 0f
    endCropTime = 0f
    audioVolume = 100f
    metrics = -1.0
    y = 0f
    subtitleList.clear()
    apiCalled = false
}

fun processAudio(context: Context, videoId: String, completionCallback: () -> Unit) {
    val fileDir = File(context.filesDir, "output/$videoId/output-cropped.mp4")

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

    Log.d("audio volume", "${audioVolume/100f}")
    EpEditor.music(fileDir.toString(), fileDir.toString(), outFile.toString(), audioVolume/100f, 0.0F, editorListener)
}


fun processSubtitles(context: Context, videoId: String, subtitleList: MutableList<PlayerSubtitles>,
                     completionCallback: () -> Unit) {

    val fileDir = File(context.filesDir, "output/$videoId/output-audio.mp4")

    // Output file
    val outFile = File(context.filesDir, "output/$videoId/output-subtitles.mp4")
    val outputOption = EpEditor.OutputOption(outFile.toString())
    outputOption.frameRate = 30
    outputOption.bitRate = 10

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
    // Get dimension of video (not the player)
    val dimensions = getVideoDimensions(fileDir.toString())
    if (dimensions != null) {

        // Extract the width and height to determine relative positions
        val (width, height) = dimensions
        println("Video Dimensions: $width x $height")


        // Determine if there are subtitles
        println("ADDING THE FOLLOWING SUBTITLES:")

        // Create command
        val cmd = CmdList()
        val ffmpegCmd = "ffmpeg"

        cmd.append(ffmpegCmd).append("-i").append(fileDir.toString()).append("-vf")
        var textFilter = "[in]"
        // If there are multiple subtitles...
        subtitleList.forEachIndexed { index, item ->
            when (item.selectedPosition) {
                getString(context, R.string.top) -> textFilter += ("drawtext=fontfile='/system/fonts/Roboto-Regular.ttf':text='${item.userInput}':fontcolor=white:fontsize=${item.selectedFontSize}:box=1:boxcolor=black@0.5:boxborderw=5:x=($width-text_w)/2:y=10:enable='between(t,${item.startCropTime / 1000},${item.endCropTime / 1000})'")
                getString(context, R.string.middle) -> textFilter += ("drawtext=fontfile='/system/fonts/Roboto-Regular.ttf':text='${item.userInput}':fontcolor=white:fontsize=${item.selectedFontSize}:box=1:boxcolor=black@0.5:boxborderw=5:x=($width-text_w)/2:y=($height-text_h)/2:enable='between(t,${item.startCropTime / 1000},${item.endCropTime / 1000})'")
                getString(context, R.string.bottom) -> textFilter += ("drawtext=fontfile='/system/fonts/Roboto-Regular.ttf':text='${item.userInput}':fontcolor=white:fontsize=${item.selectedFontSize}:box=1:boxcolor=black@0.5:boxborderw=5:x=($width-text_w)/2:y=$height-th-10:enable='between(t,${item.startCropTime / 1000},${item.endCropTime / 1000})'")
            }
            if (index != subtitleList.size - 1) {
                textFilter += ","
            }
            println("Subtitle: $item")
        }
        cmd.append("$textFilter[out]")
        println("Subtitle: $textFilter")
        cmd.append("-codec:a").append("copy").append("-y").append(outFile.toString())

        execCmd(cmd, VideoUitls.getDuration(fileDir.toString()), editorListener)

    }
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
            .size(45.dp)
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


    // Load the custom font from the "res/font" directory
    val s = R.font.arialn.absoluteValue
    val x = R.font.arialn
    val customTypeface: Typeface? = ResourcesCompat.getFont(context, R.font.arialn)


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

private fun execCmd(cmd: CmdList, duration: Long, onEditorListener: OnEditorListener) {
    val cmds = cmd.toTypedArray()
    var cmdLog = ""
    for (ss in cmds) {
        cmdLog += cmds
    }
    Log.v("EpMediaF", "cmd:$cmdLog")

    FFmpegCmd.exec(cmds, duration, object : OnEditorListener {
        override fun onSuccess() {
            onEditorListener.onSuccess()
        }

        override fun onFailure() {
            onEditorListener.onFailure()
        }

        override fun onProgress(progress: Float) {
            onEditorListener.onProgress(progress)
        }
    })
}
fun getVideoDimensions(videoPath: String): Pair<Int, Int>? {
    return try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(videoPath)

        val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull()
        val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull()

        retriever.release()

        if (width != null && height != null && width > 0 && height > 0) {
            Pair(width, height)
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}

data class PlayerSubtitles(val userInput: String, val selectedPosition: String, val selectedFontSize: Int,
                           val startCropTime: Float, val endCropTime: Float )
