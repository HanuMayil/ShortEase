package com.example.shortease

import android.annotation.SuppressLint
import android.media.browse.MediaBrowser
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.widget.VideoView
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
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
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import java.io.File
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.StyledPlayerView
import androidx.activity.compose.setContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoEditorScreen(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val videoId = navBackStackEntry?.arguments?.getString("param1")

    val context = LocalContext.current
    val videoView = remember { VideoView(context) }
    val videoPath = "${LocalContext.current.filesDir}/videos/${videoId}"

    val folder = File(videoPath)
    val folderContents = folder.listFiles()
    val folderContentNames = folderContents?.map { file -> file.name } ?: emptyList()

    var finalVideoPath = "${LocalContext.current.filesDir}/videos/${videoId}/${folderContentNames.getOrNull(0)}"


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
//            Spacer(modifier = Modifier.weight(1f))
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
            .padding(vertical = 4.dp),
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
fun VideoPlayer(sampleVideo : String){
    Log.d("video to see ", sampleVideo)
    val context = LocalContext.current
    val player = SimpleExoPlayer.Builder(context).build()
    val playerView = PlayerView(context)
    val mediaItem = MediaItem.fromUri(sampleVideo)
    val playWhenReady by rememberSaveable {
        mutableStateOf(true)
    }
    player.setMediaItem(mediaItem)
    playerView.player = player
    LaunchedEffect(player) {
        player.prepare()
        player.playWhenReady = playWhenReady

    }
    AndroidView(factory = {
        playerView
    })
}