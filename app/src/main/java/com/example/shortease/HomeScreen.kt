package com.example.shortease


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.shortease.ui.theme.colorPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = colorPalette.ShortEaseWhite) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 150.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.shortease_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(320.dp)
                        .padding(bottom = 16.dp)
                )
                GoogleButton()
                Text(
                    modifier = Modifier.clickable {
                        navController.navigate(route = Screen.MyVideos.route)
                    },
                    text = "Login With Google"
                )
                Text(
                    modifier = Modifier.clickable {
                        navController.navigate(route = Screen.DebugScreen.route)
                    },
                    text = "Debug"
                )
            }
        }
    }
}

@Composable
@Preview
private fun HomeScreenPreview() {
    HomeScreen(
        navController = rememberNavController()
    )
}