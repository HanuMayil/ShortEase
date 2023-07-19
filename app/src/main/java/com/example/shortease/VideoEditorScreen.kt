package com.example.shortease

import Jni.FFmpegCmd
import VideoHandle.CmdList
import VideoHandle.EpEditor
import VideoHandle.EpVideo
import VideoHandle.OnEditorListener
import android.annotation.SuppressLint
import android.content.Context
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
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import showPopup
import java.io.File


var shouldRenderContent by mutableStateOf(-1)
var videoDuration = -1f
lateinit var playerView: PlayerView
var startCropTime = 0f
var endCropTime = 0f
var audioVolume = 100f
var subtitleList = mutableListOf<PlayerSubtitles>()
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
                                            deferred = CompletableDeferred<Unit>()
                                            processSubtitles(
                                                videoId = videoId,
                                                context = context,
                                                subtitleList = subtitleList,
                                                completionCallback = {
                                                    deferred.complete(Unit)
                                                }
                                            )
//                                            deferred.await()
//                                            deferred = CompletableDeferred<Unit>()
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
                            text = "Crop Video",
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
                    showPopup(0f..videoDuration*1000, startCropTime..endCropTime,
                    onConfirm = {
                            userInput,
                            selectedPosition,
                            selectedFontSize,
                            startCropTime,
                            endCropTime -> Toast.makeText(context,
                            "User Input: $userInput, Position: $selectedPosition, Font Size: $selectedFontSize, Start Time: $startCropTime, End Time: $endCropTime",
                            Toast.LENGTH_SHORT
                        ).show()
                        subtitleList.add(PlayerSubtitles(userInput,selectedPosition, startCropTime, endCropTime))
                        val currentDirector = System.getProperty("user.dir")
                        println("Current directory: $currentDirector")
                        shouldRenderContent = -1
                    },
                    onCancel = {
                        shouldRenderContent = -1
                    })

                }
                else if (shouldRenderContent == R.drawable.music_note_icon) {
                    var value by remember { mutableStateOf(audioVolume) }

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
fun CropRangeSlider(
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

@SuppressLint("SuspiciousIndentation")
fun processSubtitles(context: Context, videoId: String, subtitleList: MutableList<PlayerSubtitles>,
                     completionCallback: () -> Unit) {
    val fileDir = File(context.filesDir, "output/$videoId/output-audio.mp4")
    var videoDir = File(context.filesDir, "videos/${videoId}")

    // Get font
    val fontPath = File("/app/src/main/res/font/")
    val fontFileName = "arialn.tff"
    val fontFile = File(fontPath,fontFileName)

    // Access the private function from EpVideo
    val epVideo: EpVideo = EpVideo("${fileDir.toString()}")
//    val addTextMethod = EpVideo::class.java.getDeclaredMethod("addText", Int::class.java, Int::class.java, Float::class.java, String::class.java, String::class.java,
//                                                                String::class.java, EpText.Time::class.java)
//    addTextMethod.isAccessible = true

    Log.d("youtube init", "name" + epVideo)

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
    val ffmpegCmd = "ffmpeg"
    if(subtitleList.size > 0) {
        println("ADDING THE FOLLOWING SUBTITLES:")
        subtitleList.forEach { item ->
            println("Subtitle: $item")
//        val epText = EpText(playerView.width/2, playerView.height - 10, 35.0F,
//            EpText.Color.Black, fontFile.absolutePath, item.userInput ,EpText.Time(item.startCropTime.toInt(),item.endCropTime.toInt()))
            val cmd = CmdList()
            cmd.append(ffmpegCmd).append("-i").append(videoDir.toString()).append("-vf")
                .append("drawtext=text='${item.userInput}':x=10:y=10:fontsize=24:fontcolor=white:enable='between(t,${item.startCropTime},${item.endCropTime})'")
                .append("-c:a").append("copy").append("-y").append(outFile.toString())

            execCmd(cmd,0, editorListener)
        }
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
data class PlayerSubtitles(val userInput: String, val selectedPosition: String,
                           val startCropTime: Float, val endCropTime: Float )