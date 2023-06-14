package com.example.shortease

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.shortease.ui.theme.colorPalette
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults

@Composable
fun DebugScreen(
    navController: NavController
) {
    Surface(modifier = Modifier.fillMaxSize(), color = colorPalette.ShortEaseWhite) {
        Column(modifier = Modifier.fillMaxSize()) {

            Button(
                onClick = { navController.navigate(route = Screen.HomeScreen.route) },
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
                    text = "Home Screen"
                )
            }

            Button(
                onClick = { navController.navigate(route = Screen.MyVideos.route) },
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
                    text = "My Videos"
                )
            }

            Button(
                onClick = { navController.navigate(route = Screen.PreviewScreen.route) },
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
                    text = "Preview"
                )
            }
        }
    }
}


@Composable
@Preview
private fun DebugScreenPreview() {
    DebugScreen(navController = rememberNavController())
}
