package com.example.shortease

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
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
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.shortease.ui.theme.ShortEaseTheme
import com.example.shortease.ui.theme.colorPalette
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedVideos(
    navController: NavController,
    signOutClicked: () -> Unit?
) {
    val thumbnailItems = remember { mutableStateListOf<ThumbnailItem>() }

    val videos = remember { mutableStateListOf<File>() }

    val folderPath = "${LocalContext.current.filesDir}/videos"

//    val folderPath = "/data/user/0/com.example.shortease/files"
    // Retrieve videos from the specified folder
    LaunchedEffect(folderPath) {
        val folder = File(folderPath)
        Log.d("youtube init", "got into launched effect");
        Log.d("youtube init", "${folder}");
        Log.d("youtube init", "${folder.listFiles()}");
        if (folder.exists() && folder.isDirectory) {
            Log.d("youtube init", "got into for loop");
            val sub_folders =  folder.listFiles { file ->
                file.isDirectory
            }
            sub_folders?.forEach { sub_folders ->
                val videoFiles = sub_folders.listFiles { file ->
                    file.isFile && file.extension in listOf("mp4", "mkv", "avi")
                }
                Log.d("youtube init","Video File: ${videoFiles.size}")
                videos.addAll(videoFiles)
            }

            videos?.forEach { videoFile ->
                Log.d("youtube init","Video File: ${videoFile.name}")
            }
            sub_folders?.forEach { sub_folders ->
                Log.d("youtube init","Folders: ${sub_folders.name}")
            }
            
        }
    }

    var selected by remember { mutableStateOf(1) }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = colorPalette.ShortEaseWhite) {
            ShortEaseTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    TopAppBar(
                        colors = TopAppBarDefaults.largeTopAppBarColors(
                            containerColor = colorPalette.ShortEaseRed,
                            titleContentColor = colorPalette.ShortEaseWhite,
                        ),
                        title = {
                            Text(
                                text = "Saved Videos",
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
                            Image(
                                painter = painterResource(R.drawable.search),
                                contentDescription = "Search Icon",
                                Modifier
                                    .padding(start = 10.dp)
                                    .size(30.dp)
                            )
                        },
                        actions = {
                            IconButton(onClick = {
                                navController.navigate(route = Screen.HomeScreen.route)
                            }) {
                                Image(
                                    painter = painterResource(R.drawable.home_button),
                                    contentDescription = "Profile",
                                    Modifier.clickable { signOutClicked() }
                                )
                            }
                        }
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        LazyColumn {
                            items(videos) { video ->
                                VideoItem(video)
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(70.dp),
                            border = BorderStroke(width = 1.dp, color = colorPalette.ShortEaseRed),
                        ) {
                            Button(
                                onClick = {
                                    selected = 0;
                                    navController.navigate(route = Screen.MyVideos.route) },
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(Color.Transparent),
                                modifier = Modifier
                                    .clickable { selected = 0 }
                                    .background(
                                        if (selected == 0) colorPalette.ShortEaseRed
                                        else colorPalette.ShortEaseWhite
                                    ),
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.edit),
                                    contentDescription = "Edit Icon",
                                    colorFilter = if (selected == 0) ColorFilter.tint(colorPalette.ShortEaseWhite)
                                    else ColorFilter.tint(colorPalette.ShortEaseRed),
                                    modifier = Modifier
                                        .size(30.dp)
                                )
                            }
                        }
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(70.dp),
                            border = BorderStroke(width = 1.dp, color = colorPalette.ShortEaseRed),
                        ) {
                            Button(
                                onClick = { selected = 1 },
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(Color.Transparent),
                                modifier = Modifier
                                    .clickable { selected = 1 }
                                    .background(
                                        if (selected == 1) colorPalette.ShortEaseRed
                                        else colorPalette.ShortEaseWhite
                                    ),
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.save),
                                    contentDescription = "App Logo",
                                    colorFilter = if (selected == 1) ColorFilter.tint(colorPalette.ShortEaseWhite)
                                    else ColorFilter.tint(colorPalette.ShortEaseRed),
                                    modifier = Modifier
                                        .size(30.dp)
                                )
                            }
                        }
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(70.dp),
                            border = BorderStroke(width = 1.dp, color = colorPalette.ShortEaseRed),
                        ) {
                            Button(
                                onClick = { selected = 2 },
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(Color.Transparent),
                                modifier = Modifier
                                    .clickable { selected = 2 }
                                    .background(
                                        if (selected == 2) colorPalette.ShortEaseRed
                                        else colorPalette.ShortEaseWhite
                                    ),
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.share),
                                    contentDescription = "Share Logo",
                                    colorFilter = if (selected == 2) ColorFilter.tint(colorPalette.ShortEaseWhite)
                                    else ColorFilter.tint(colorPalette.ShortEaseRed),
                                    modifier = Modifier
                                        .size(30.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun VideoItem(video: File) {
    // Display video item here
    // Replace with your desired representation
    // You can use libraries like ExoPlayer or Glide to handle video loading and playback
    Log.d("youtube init","got into video item")
    Log.d("youtube init","Video Item: ${video}")
    // Example: Display video file name
    val context = LocalContext.current
    val videoId = video.toString().substringAfterLast("/videos/").substringBefore("/")
    val title =   video.toString().substringAfterLast("/").substringBeforeLast(".mp4")
    Log.d("youtube init","Video ID: ${videoId}")
    var thumbnail_pic = File(context.filesDir, "videos/${videoId}/thumbnail.jpg");
    val showDialog = remember { mutableStateOf(false) }

    // Show confirmation dialog
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = "Delete Video?") },
            text = { Text(text =  "Are you sure you want to delete: \n \"${title}\"?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                        var fileDirectory = File(context.filesDir, "videos/${videoId}")
                        fileDirectory.deleteRecursively()
                    },
                    colors = ButtonDefaults.buttonColors(colorPalette.ShortEaseRed)
                ) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog.value = false },
                    colors = ButtonDefaults.buttonColors(colorPalette.ShortEaseRed)
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }
    if(thumbnail_pic.isFile) {
        Log.d("youtube init","thumbail_pic: ${thumbnail_pic}")
        Column(
            modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
        ) {
            Image(
                painter = rememberImagePainter(thumbnail_pic),
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(272.dp),
            )
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        style = TextStyle(
                            color = colorPalette.ShortEaseRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(
                        painter = painterResource(R.drawable.trashcan),
                        contentDescription = "Delete Icon",
                        colorFilter = ColorFilter.tint(colorPalette.ShortEaseRed),
                        modifier = Modifier.size(24.dp)
                            .align(Alignment.CenterVertically)
                            .clickable {
                                showDialog.value = true
                            }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Image(
                        painter = painterResource(R.drawable.edit),
                        contentDescription = "Download Icon",
                        colorFilter = ColorFilter.tint(colorPalette.ShortEaseRed),
                        modifier = Modifier.size(20.dp).align(Alignment.CenterVertically)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = colorPalette.ShortEaseRed, thickness = 1.dp)
    }
}

@Composable
@Preview
private fun MyVideosPreview() {
    MyVideos(
        navController = rememberNavController(),
        signOutClicked = {  }
    )
}

