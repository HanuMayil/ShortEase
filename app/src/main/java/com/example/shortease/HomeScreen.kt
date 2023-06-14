package com.example.shortease

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.shortease.ui.theme.Shapes
import com.example.shortease.ui.theme.ShortEaseTheme
import com.example.shortease.ui.theme.colorPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    signInClicked: () -> Unit
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
                GoogleButton(signInClicked)
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
        navController = rememberNavController(),
        signInClicked = { }
    )
}

@ExperimentalMaterial3Api
@Composable
fun GoogleButton(
    signInClicked: () -> Unit?
) {
    ShortEaseTheme {
        Surface(
            modifier = Modifier.clickable { signInClicked() },
            shape = Shapes.large,
            color = colorPalette.ShortEaseRed,
        ) {
            Row (
                modifier = Modifier.padding(
                    start = 12.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = 12.dp
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center){
                Icon(
                    painter = painterResource(id = R.drawable.ic_google_logo),
                    contentDescription = "Google Button",
                    tint = colorPalette.ShortEaseWhite
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sign in with Google ",
                    color = colorPalette.ShortEaseWhite
                )
            }
        }
    }
}


@ExperimentalMaterial3Api
@Composable
@Preview
private fun GoogleButtonPreview () {
    GoogleButton(signInClicked = { })
}