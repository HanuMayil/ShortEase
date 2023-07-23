package com.example.shortease

import android.util.Log
import android.widget.Toast
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.shortease.ui.theme.ShortEaseTheme
import com.example.shortease.ui.theme.colorPalette
import com.example.shortease.youtube.YouTubeDownloader
import com.github.kiulian.downloader.model.videos.formats.VideoWithAudioFormat
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.math.BigInteger
import java.text.NumberFormat
import java.util.Locale
import java.util.concurrent.Executors
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat.getString
import coil.annotation.ExperimentalCoilApi
import com.example.shortease.R
import com.example.shortease.Screen
import com.example.shortease.ThumbnailItem
import com.example.shortease.YouTubeApiClient


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoilApi::class)
@Composable
fun MyVideos(
    navController: NavController,
    signOutClicked: () -> Unit?
) {

    var selectedTab = remember { mutableStateOf(R.drawable.download_icon) }
    var selected by remember { mutableStateOf(R.drawable.download_icon) }


    val thumbnailItems = remember { mutableStateListOf<ThumbnailItem>() }
    //tmp image
    val channelIconUrl = remember { mutableStateOf("https://www.digitary.net/wp-content/uploads/2021/07/Generic-Profile-Image.png") }
    val youtubeDownloader = YouTubeDownloader(LocalContext.current)
    val context = LocalContext.current
    DisposableEffect(Unit) {
//        val scope = CoroutineScope(Dispatchers.Main)
//        val channelId = "UCX6OQ3DkcsbYNE6H8uQQuVA"
//        val y = YouTubeApiClient("AIzaSyCZ1aVkQw5j_ljA-AesWfHh0c6lnGQIq-A") // Replace with your API key
//        val job = scope.launch {
//            val fetchedThumbnailItems = y.fetchVideoThumbnails(channelId, channelIconUrl)
//            thumbnailItems.addAll(fetchedThumbnailItems)
//        }
//        onDispose {
//            job.cancel()
//        }
        val fakeThumbnailItem: ThumbnailItem = ThumbnailItem(
            "10 Sec Timer",
            "https://i.ytimg.com/vi/zU9y354XAgM/hq720.jpg?sqp=-oaymwEcCOgCEMoBSFXyq4qpAw4IARUAAIhCGAFwAcABBg==&rs=AOn4CLDyiceF5hUqg8CSc85pQwJuvOxXkQ",
            BigInteger("1234567890")
        )
        val fakeThumbnailItem2: ThumbnailItem = ThumbnailItem(
            "Donkey Kong Gets Sturdy",
            "https://i.ytimg.com/vi/KZRrrNFzL2A/hqdefault.jpg?sqp=-oaymwEbCKgBEF5IVfKriqkDDggBFQAAiEIYAXABwAEG&rs=AOn4CLAj7qcSjXjcVtLgu7kFfPaXhohvvQ",
            BigInteger("1234567890")
        )
        thumbnailItems.add(fakeThumbnailItem)
        thumbnailItems.add(fakeThumbnailItem2)
        onDispose {}
    }

    var expanded by remember { mutableStateOf(false) }

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
                                text = if(selectedTab.value == R.drawable.download_icon) {
                                    stringResource(R.string.my_videos_header)
                                } else if(selectedTab.value == R.drawable.edit){
                                    stringResource(R.string.saved_videos_header)
                                } else {
                                       ""
                                       },
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
                            Image(
                                painter = painterResource(R.drawable.search),
                                contentDescription = "Search Icon",
                                Modifier
                                    .padding(start = 10.dp)
                                    .size(30.dp)
                            )
                        },
                        actions = {
                            Box(
                                modifier = Modifier
                                    .wrapContentSize(Alignment.TopEnd)
                            ) {
                                IconButton(
                                    onClick = { expanded = !expanded }
                                ) {
                                    Image(
                                        painter = rememberImagePainter(channelIconUrl.value),
                                        contentDescription = "Channel Icon",
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                    )
                                }
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier.background(color = colorPalette.ShortEaseWhite)
                                ) {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = stringResource(R.string.sign_out_button),
                                                textAlign = TextAlign.Center,
                                                style = TextStyle(color = colorPalette.ShortEaseRed, fontSize = 14.sp),
                                                modifier = Modifier.padding(horizontal = 20.dp)
                                            )
                                        },
                                        onClick = {
                                            navController.navigate(route = Screen.HomeScreen.route)
                                            signOutClicked()
                                        }
                                    )

                                    // Language selection dropdown item
                                    LanguagePicker(initialText = getString(context, R.string.select_language))
                                }
                            }
                        }
                    )

                    Box(modifier = Modifier.weight(1f)
                    ) {
                        if (selectedTab.value == R.drawable.download_icon) {
                            DownloadAvailableScreen(thumbnailItems)
                        } else if (selectedTab.value == R.drawable.edit){
                            savedVideosScreen(navController)
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
                                .height(55.dp),
                            border = BorderStroke(width = 1.dp, color = colorPalette.ShortEaseRed),
                        ) {
                            Button(
                                onClick = {
                                            selectedTab.value = R.drawable.download_icon;
                                            selected = R.drawable.download_icon
                                          },
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(Color.Transparent),
                                modifier = Modifier
                                    .clickable { selected = R.drawable.download_icon }
                                    .background(
                                        if (selected == R.drawable.download_icon) colorPalette.ShortEaseRed
                                        else colorPalette.ShortEaseWhite
                                    ),
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.download_icon),
                                    contentDescription = "Download Icon",
                                    colorFilter = if (selected == R.drawable.download_icon) ColorFilter.tint(colorPalette.ShortEaseWhite)
                                    else ColorFilter.tint(colorPalette.ShortEaseRed),
                                    modifier = Modifier
                                        .size(30.dp)
                                )
                            }
                        }
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(55.dp),
                            border = BorderStroke(width = 1.dp, color = colorPalette.ShortEaseRed),
                        ) {
                            Button(
                                onClick = { selectedTab.value = R.drawable.edit;
                                    selected =  R.drawable.edit },
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(Color.Transparent),
                                modifier = Modifier
                                    .clickable { selected = R.drawable.edit }
                                    .background(
                                        if (selected == R.drawable.edit) colorPalette.ShortEaseRed
                                        else colorPalette.ShortEaseWhite
                                    )
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.edit),
                                    contentDescription = "App Logo",
                                    colorFilter = if (selected ==  R.drawable.edit) ColorFilter.tint(colorPalette.ShortEaseWhite)
                                    else ColorFilter.tint(colorPalette.ShortEaseRed),
                                    modifier = Modifier
                                        .size(30.dp)
                                )
                            }
                        }
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(55.dp),
                            border = BorderStroke(width = 1.dp, color = colorPalette.ShortEaseRed),
                        ) {
                            Button(
                                onClick = { selectedTab.value = R.drawable.share;
                                    selected =  R.drawable.share},
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(Color.Transparent),
                                modifier = Modifier
                                    .clickable { selected = R.drawable.share }
                                    .background(
                                        if (selected == R.drawable.share) colorPalette.ShortEaseRed
                                        else colorPalette.ShortEaseWhite
                                    ),
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.share),
                                    contentDescription = "Share Logo",
                                    colorFilter = if (selected ==  R.drawable.share) ColorFilter.tint(colorPalette.ShortEaseWhite)
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



// Function to format the view count with comma separators
fun formatViewCount(viewCount: BigInteger): String {
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)
    return numberFormat.format(viewCount.toLong())
}

fun extractVideoId(url: String): String {
    val startIndex = url.indexOf("vi/") + 3
    val endIndex = url.indexOf("/", startIndex)
    if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
        return url.substring(startIndex, endIndex)
    }
    return ""
}
@Composable
fun DownloadAvailableScreen(thumbnailItems: List<ThumbnailItem>) {
    val context = LocalContext.current
    val youtubeDownloader = YouTubeDownloader(LocalContext.current)

        // Display the thumbnails in a LazyColumn
        LazyColumn(
            modifier = Modifier
                .fillMaxSize())
        {
            itemsIndexed(thumbnailItems) { _, thumbnailItem ->
                Column(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                ) {
                    Image(
                        painter = rememberImagePainter(thumbnailItem.thumbnailUrl),
                        contentDescription = thumbnailItem.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(272.dp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = thumbnailItem.title,
                                style = TextStyle(
                                    color = colorPalette.ShortEaseRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = "Views: ${formatViewCount(thumbnailItem.viewCount)}",
                                style = TextStyle(color = colorPalette.ShortEaseRed, fontSize = 14.sp),
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                        val isPopupOpen = remember { mutableStateOf(false) }
                        var videoId = remember { mutableStateOf(extractVideoId(thumbnailItem.thumbnailUrl)) }
                        var formats = remember { mutableStateOf(emptyList<VideoWithAudioFormat>()) }
                        var finishedDownload = remember { mutableStateOf(false) }
                        var videoDirExists = remember {mutableStateOf(false)}
                        Column(
                            verticalArrangement = Arrangement.Center,
                        ) {
                            var videoDir = File("${context.filesDir}/videos/${videoId.value}")
                            videoDirExists.value = videoDir.exists()

                            if(videoDirExists.value) {
                                finishedDownload.value = File(videoDir, "thumbnail.jpg").exists()
                                if(finishedDownload.value) {
                                    Image(
                                        painter = painterResource(R.drawable.check),
                                        contentDescription = "Check Icon",
                                        colorFilter = ColorFilter.tint(colorPalette.ShortEaseRed),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                else {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .size(24.dp),
                                        strokeWidth = 2.dp,
                                        color = colorPalette.ShortEaseRed

                                    )
                                }
                            }
                            else {
                                val savedVideosHeaderText = stringResource(R.string.download_failed)
                                Image(
                                    painter = painterResource(R.drawable.download_icon),
                                    contentDescription = "Download Icon",
                                    colorFilter = ColorFilter.tint(colorPalette.ShortEaseRed),
                                    modifier = Modifier
                                        .clickable {
                                            if (videoId.value != "") {
                                                formats.value =
                                                    youtubeDownloader.requestVideoDetail(
                                                        videoId.value
                                                    )
                                                if(formats.value.isNotEmpty()) {
                                                    isPopupOpen.value = true
                                                } else {
                                                    Toast
                                                        .makeText(
                                                            context,
                                                            savedVideosHeaderText,
                                                            Toast.LENGTH_SHORT
                                                        )
                                                        .show()
                                                }
                                            } else {
                                                Toast
                                                    .makeText(
                                                        context,
                                                        savedVideosHeaderText,
                                                        Toast.LENGTH_SHORT
                                                    )
                                                    .show()
                                            }
                                        }
                                        .size(24.dp)
                                )
                            }
                        }

                        if (isPopupOpen.value) {
                            AlertDialog(
                                onDismissRequest = { isPopupOpen.value = false },
                                title = { Text(text = stringResource(R.string.select_format)) },
                                confirmButton = {},
                                text = {
                                    Column {
                                        formats.value.forEach { format ->
                                            Button(
                                                onClick = {
                                                    videoDirExists.value = true
                                                    val backgroundDispatcher: CoroutineDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
                                                    CoroutineScope(Dispatchers.Main).launch {
                                                        withContext(backgroundDispatcher) {
                                                            val deferred = CompletableDeferred<Unit>()
                                                            youtubeDownloader.downloadYouTubeVideo(
                                                                videoId = videoId.value,
                                                                videoTitle = thumbnailItem.title,
                                                                format = format,
                                                                thumbnailURL = thumbnailItem.thumbnailUrl,
                                                                completionCallback = {
                                                                    deferred.complete(Unit)
                                                                }
                                                            )
                                                            deferred.await()
                                                        }
                                                    }.invokeOnCompletion {
                                                        finishedDownload.value = true
                                                    }
                                                    isPopupOpen.value = false
                                                },
                                                modifier = Modifier
                                                    .padding(16.dp)
                                                    .height(64.dp)
                                                    .fillMaxWidth(),
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = colorPalette.ShortEaseRed,
                                                    contentColor = colorPalette.ShortEaseWhite
                                                )

                                            ) {
                                                Text(
                                                    text = format.videoQuality().toString()
                                                )
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = colorPalette.ShortEaseRed, thickness = 1.dp)
                }
            }
        }
}

@Composable
fun savedVideosScreen(navController: NavController) {
    val videos = remember { mutableStateListOf<File>() }

    val folderPath = "${LocalContext.current.filesDir}/videos"

//   val folderPath = "/data/user/0/com.example.shortease/files"
    // Retrieve videos from the specified folder
    LaunchedEffect(folderPath) {
        val folder = File(folderPath)
        Log.d("youtube init", "got into launched effect");
        Log.d("youtube init", "${folder}");
        Log.d("youtube init", "${folder.listFiles()}");
        if (folder.exists() && folder.isDirectory) {
            Log.d("youtube init", "got into for loop");
            val sub_folders = folder.listFiles { file ->
                file.isDirectory
            }
            sub_folders?.forEach { sub_folders ->
                val videoFiles = sub_folders.listFiles { file ->
                    file.isFile && file.extension in listOf("mp4", "mkv", "avi")
                }
                Log.d("youtube init", "Video File: ${videoFiles.size}")
                videos.addAll(videoFiles)
            }

            videos?.forEach { videoFile ->
                Log.d("youtube init", "Video File: ${videoFile.name}")
            }
            sub_folders?.forEach { sub_folders ->
                Log.d("youtube init", "Folders: ${sub_folders.name}")
            }

        }
    }


    LazyColumn() {
        items(videos) { video ->
            VideoItemDisplay(video, navController)
        }
    }


}

@Composable
fun VideoItemDisplay(video: File, navController: NavController) {
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
    val showEditDialog = remember { mutableStateOf(false) }

    if (showEditDialog.value && !showDialog.value) {
        AlertDialog(
            onDismissRequest = { showEditDialog.value = false },
            title = { Text(text = stringResource(R.string.edit_video))},
            confirmButton = {
                Button(
                    onClick = {
                        showEditDialog.value = false
                        navController.navigate(route = "video_editor_screen?videoId=$videoId")
                    },
                    colors = ButtonDefaults.buttonColors(colorPalette.ShortEaseRed)
                ) {
                    Text(text = stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                Button(onClick = { showEditDialog.value = false },
                    colors = ButtonDefaults.buttonColors(colorPalette.ShortEaseRed)
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }
    // Show confirmation dialog
    if (showDialog.value && !showEditDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = stringResource(R.string.delete_video))},
            text = { Text(text = stringResource(R.string.delete_video_prompt) + " \n \"${title}\"?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                        var fileDirectory = File(context.filesDir, "videos/${videoId}")
                        fileDirectory.deleteRecursively()
                    },
                    colors = ButtonDefaults.buttonColors(colorPalette.ShortEaseRed)
                ) {
                    Text(text = stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                Button(onClick = { showDialog.value = false },
                    colors = ButtonDefaults.buttonColors(colorPalette.ShortEaseRed)
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }
    if(thumbnail_pic.isFile) {
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
                        modifier = Modifier
                            .size(24.dp)
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
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.CenterVertically)
                            .clickable {
                                showEditDialog.value = true
                            }
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

@Composable
fun LanguagePicker(
    initialText: String
) {
    var showLanguages by remember { mutableStateOf(false) }
    val context = LocalContext.current
    // Language options in the second dropdown
    val languages = listOf(
        "en" to getString(context, R.string.english),
        "ar" to getString(context, R.string.arabic),
        "es" to getString(context, R.string.spanish),
        "fr" to getString(context, R.string.french),
        "hi" to getString(context, R.string.hindi),
        "ta" to getString(context, R.string.tamil),
        "vi" to getString(context, R.string.vietnamese),
        "zh" to getString(context, R.string.mandarin),
    )

    // Function to handle language selection
    fun handleLanguageSelection(languageCode: String) {
        setLocale(context, languageCode)
        showLanguages = false
    }

    Box {
        // Set Languages button
        DropdownMenuItem(
            onClick = { showLanguages = !showLanguages },
            text = {
                Text(
                    text = initialText,
                    textAlign = TextAlign.Center,
                    style = TextStyle(color = colorPalette.ShortEaseRed, fontSize = 14.sp),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        )

        // Second dropdown with language options
        if (showLanguages) {
            Column {
                DropdownMenu(
                    expanded = true,
                    onDismissRequest = { showLanguages = false },
                    modifier = Modifier.background(color = colorPalette.ShortEaseWhite)
                ) {
                    languages.forEach { (languageCode, languageName) ->
                        DropdownMenuItem(
                            onClick = { handleLanguageSelection(languageCode) },
                            text = {
                                Text(
                                    text = languageName,
                                    textAlign = TextAlign.Center,
                                    style = TextStyle(color = colorPalette.ShortEaseRed, fontSize = 14.sp),
                                    modifier = Modifier.padding(horizontal = 20.dp)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}




fun setLocale(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)

    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)

    context.resources.updateConfiguration(config, context.resources.displayMetrics)

    // Restart the activity to apply the new configuration
    if (context is Activity) {
        context.recreate()
    }
}
