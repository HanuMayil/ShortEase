import android.annotation.SuppressLint
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.shortease.ui.theme.ShortEaseTheme
import com.example.shortease.ui.theme.colorPalette
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import coil.compose.rememberImagePainter
import java.math.BigInteger
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.draw.clip
import com.example.shortease.R
import com.example.shortease.Screen
import com.example.shortease.ThumbnailItem
import com.example.shortease.YouTubeApiClient


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyVideos(
    navController: NavController,
    signOutClicked: () -> Unit?
) {
    val thumbnailItems = remember { mutableStateListOf<ThumbnailItem>() }
    val channelIconUrl = remember { mutableStateOf("") }

    DisposableEffect(Unit) {
        val scope = CoroutineScope(Dispatchers.Main)
        val channelId = "UCX6OQ3DkcsbYNE6H8uQQuVA"
        val y = YouTubeApiClient("AIzaSyCZ1aVkQw5j_ljA-AesWfHh0c6lnGQIq-A") // Replace with your API key
        val job = scope.launch {
            val fetchedThumbnailItems = y.fetchVideoThumbnails(channelId, channelIconUrl)
            thumbnailItems.addAll(fetchedThumbnailItems)
        }
        onDispose {
            job.cancel()
        }
    }

    var selected by remember { mutableStateOf(0) }
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
                                text = "My Videos",
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
                                Modifier.padding(start = 10.dp).size(30.dp)
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
                                        modifier = Modifier.size(36.dp).clip(CircleShape)
                                    )
                                }
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier.background(color = colorPalette.ShortEaseWhite)

                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Sign Out", textAlign = TextAlign.Center,
                                            style = TextStyle(color = colorPalette.ShortEaseRed, fontSize = 14.sp),
                                            modifier = Modifier.padding(horizontal = 20.dp))},
                                        onClick = {
                                            navController.navigate(route = Screen.HomeScreen.route)
                                            signOutClicked()
                                        }
                                    )
                                }
                            }
                        }
                    )


                    Box(modifier = Modifier.weight(1f)
                    ) {
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
                                    Text(
                                        text = thumbnailItem.title,
                                        style = TextStyle(
                                            color = colorPalette.ShortEaseRed,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                    Text(
                                        text = "Views: ${formatViewCount(thumbnailItem.viewCount)}",
                                        style = TextStyle(color = colorPalette.ShortEaseRed, fontSize = 14.sp),
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Divider(color = colorPalette.ShortEaseRed, thickness = 1.dp)
                                }
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
                                onClick = { selected = 0 },
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



// Function to format the view count with comma separators
fun formatViewCount(viewCount: BigInteger): String {
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)
    return numberFormat.format(viewCount.toLong())
}


@Composable
@Preview
private fun MyVideosPreview() {
    MyVideos(
        navController = rememberNavController(),
        signOutClicked = {  }
    )
}


