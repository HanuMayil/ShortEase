package com.example.shortease.youtube

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bawaviki.youtubedl_android.YoutubeDL
import com.bawaviki.youtubedl_android.YoutubeDLException
import com.bawaviki.youtubedl_android.YoutubeDLRequest
import com.example.shortease.ui.theme.ShortEaseTheme
import com.example.shortease.ui.theme.colorPalette
import java.io.File


class DownloadActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            YoutubeDL.getInstance().init(application, this)
            Log.e("init youtube", "Success to initialize youtubedl-android")
        } catch (e: YoutubeDLException) {
            Log.e("init youtube", "failed to initialize youtubedl-android", e)
        }
        val youtubeDLDir = this.filesDir
        setContent {
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
                                .padding(top = 50.dp),
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
                            Text(
                                text = "Downloading Video",
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
    }
}