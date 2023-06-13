package com.example.shortease

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.shortease.ui.theme.Shapes
import com.example.shortease.ui.theme.ShortEaseTheme
import com.example.shortease.ui.theme.colorPalette
import com.google.firebase.auth.FirebaseAuth


@ExperimentalMaterial3Api
@Composable
fun GoogleButton() {
    var clicked by remember { mutableStateOf(false) }
    ShortEaseTheme {
        Button(
            onClick = { clicked = !clicked },
            shape = Shapes.large,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorPalette.ShortEaseRed
            )
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
                Text(text = "Sign in with Google ",
                    color = colorPalette.ShortEaseWhite)
                if(clicked) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .height(16.dp)
                            .width(16.dp),
                        strokeWidth = 2.dp,
                        color = colorPalette.ShortEaseWhite
                    )
                }
            }
        }
    }
}


@ExperimentalMaterial3Api
@Composable
@Preview
private fun GoogleButtonPreview () {
    GoogleButton()
}