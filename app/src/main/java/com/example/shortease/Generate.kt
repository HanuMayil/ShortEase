package com.example.shortease

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.shortease.ui.theme.ShortEaseTheme
import com.example.shortease.ui.theme.colorPalette
import kotlinx.coroutines.delay
import java.io.File


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Generate( navController: NavController ) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val videoId = navBackStackEntry?.arguments?.getString("videoId")
    var fileDir = File(LocalContext.current.filesDir, "output/${videoId}/thumbnail.jpg")
    var fileCheckComplete by remember { mutableStateOf(false) }

    LaunchedEffect(videoId) {
        if (videoId != null) {
            while (!fileCheckComplete) {
                delay(5000)
                if (fileDir.exists()) {
                    // File exists
                    Log.d("Cropper", "File exists: ${fileDir.toString()}")
                    navController.navigate("preview_screen?videoId=$videoId")
                    fileCheckComplete = true // Set the flag to indicate file check completion
                } else {
                    // File does not exist
                    Log.d("Cropper", "File is being created")
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = lightColorScheme().background) {
            ShortEaseTheme {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(top= 50.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .height(100.dp)
                            .width(100.dp),
                        strokeWidth = 2.dp,
                        color = colorPalette.ShortEaseRed

                    )
                    Text(message(navController),
                        color = colorPalette.ShortEaseRed,
                        fontSize = 22.sp,
                        modifier = Modifier
                            .padding(top=60.dp)
                    )
                }
            }
        }
    }

}
@Composable
@Preview
fun GeneratorPreview(){
    Generate(navController = rememberNavController())
}

 fun message( navController: NavController): String {
    return if (navController.currentDestination?.route == Screen.MyVideos.route){
        "Generating..."
    }
    else {
         "Generating Preview..."
    }
}
