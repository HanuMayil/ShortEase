package com.example.shortease


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.shortease.ui.theme.ShortEaseTheme
import androidx.compose.material3.Text
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun HomeScreen(
    navController: NavController
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = lightColorScheme().background) {
            ShortEaseTheme {
                Image(painter = painterResource(R.drawable.shortease_logo), contentDescription = "App Logo")
                Text(
                    modifier = Modifier.clickable {
                        navController.navigate(route = Screen.MyVideos.route)
                    },
                    text = "Login With Google"
                )
            }
        }
    }
}

@Composable
@Preview
fun HomeScreenPreview() {
    HomeScreen(
        navController = rememberNavController()
    )
}