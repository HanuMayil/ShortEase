package com.example.shortease

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.shortease.ui.theme.ShortEaseTheme

@Composable
fun MyVideos(
    navController: NavController
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = lightColorScheme().background) {
            ShortEaseTheme {
                Row {
                    Text(text = "My Videos")
                    Text(
                        modifier = Modifier.clickable {
                            navController.popBackStack()
                        },
                        text = "Go Back To HomePage"
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun MyVideosPreview() {
    MyVideos(navController = rememberNavController())
}