import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.shortease.R
import com.example.shortease.ui.theme.ShortEaseTheme
import com.example.shortease.ui.theme.colorPalette
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.shortease.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(
    navController: NavController
) {
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
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Image(
                            painter = painterResource(R.drawable.arrow_pointing_right_in_a_circle__1__3),
                            contentDescription = "Back",
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(route = Screen.HomeScreen.route)
                    }) {
                        Image(
                            painter = painterResource(R.drawable.home_1),
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
                        AndroidView(
                            factory = { videoView },
                            modifier = Modifier.fillMaxSize()
                        ) { view ->
                            view.setVideoPath("android.resource://${context.packageName}/${R.raw.shorts_example}")
                            view.start()
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
                                painter = painterResource(R.drawable.download),
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
                                painter = painterResource(R.drawable.upload),
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
