package com.example.shortease

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.shortease.ui.theme.ShortEaseTheme
import com.example.shortease.ui.theme.colorPalette

@Composable
fun MyVideos(
    navController: NavController
) {
    var selected by remember { mutableStateOf(0) }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = colorPalette.ShortEaseWhite) {
            ShortEaseTheme {
                Column(modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween) {
                    Row {
                        Surface(modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp), color = colorPalette.ShortEaseRed) {
                            Box(contentAlignment = Alignment.Center){
                                Text(text = "My Videos",
                                    color = colorPalette.ShortEaseWhite,
                                    fontSize = 25.sp,
                                    textAlign = TextAlign.Center, fontWeight = FontWeight.Bold
                                )
                            }
                            Box(contentAlignment = Alignment.CenterStart){
                                Image(
                                    painter = painterResource(R.drawable.search),
                                    contentDescription = "Search Icon",
                                    modifier = Modifier
                                        .size(50.dp)
                                        .padding(start = 20.dp)
                                )
                            }
                        }
                    }
                    Row (modifier = Modifier
                        .weight(1f, false), horizontalArrangement = Arrangement.SpaceBetween){
                        Surface(modifier = Modifier
                            .weight(1f)
                            .height(70.dp), border = BorderStroke(width = 1.dp, color = colorPalette.ShortEaseRed),
                        ) {
                            Button(
                                onClick = {selected = 0},
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(Color.Transparent),
                                modifier = Modifier
                                    .clickable { selected = 0 }
                                    .background(
                                        if (selected == 0) colorPalette.ShortEaseRed
                                        else colorPalette.ShortEaseWhite
                                    ),
                            ){
                                Image(
                                    painter = painterResource(R.drawable.edit),
                                    contentDescription = "Edit Icon",
                                    colorFilter = if(selected == 0) ColorFilter.tint(colorPalette.ShortEaseWhite)
                                    else ColorFilter.tint(colorPalette.ShortEaseRed),
                                    modifier = Modifier
                                        .size(30.dp)
                                )
                            }
                        }
                        Surface(modifier = Modifier
                            .weight(1f)
                            .height(70.dp), border = BorderStroke(width = 1.dp, color = colorPalette.ShortEaseRed),
                        ) {
                            Button(
                                onClick = {selected = 1},
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(Color.Transparent),
                                modifier = Modifier
                                    .clickable { selected = 1 }
                                    .background(
                                        if (selected == 1) colorPalette.ShortEaseRed
                                        else colorPalette.ShortEaseWhite
                                    ),
                            ){
                                Image(
                                    painter = painterResource(R.drawable.save),
                                    contentDescription = "App Logo",
                                    colorFilter = if(selected == 1) ColorFilter.tint(colorPalette.ShortEaseWhite)
                                    else ColorFilter.tint(colorPalette.ShortEaseRed),
                                    modifier = Modifier
                                        .size(30.dp)
                                )
                            }
                        }
                        Surface(modifier = Modifier
                            .weight(1f)
                            .height(70.dp), border = BorderStroke(width = 1.dp, color = colorPalette.ShortEaseRed),
                        ) {
                            Button(
                                onClick = {selected = 2},
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(Color.Transparent),
                                modifier = Modifier
                                    .clickable { selected = 2 }
                                    .background(
                                        if (selected == 2) colorPalette.ShortEaseRed
                                        else colorPalette.ShortEaseWhite
                                    ),
                            ){
                                Image(
                                    painter = painterResource(R.drawable.share),
                                    contentDescription = "Share Logo",
                                    colorFilter = if(selected == 2) ColorFilter.tint(colorPalette.ShortEaseWhite)
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

@Composable
@Preview
private fun MyVideosPreview() {
    MyVideos(navController = rememberNavController())
}