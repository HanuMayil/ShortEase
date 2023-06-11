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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.node.modifierElementOf
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.shortease.ui.theme.ShortEaseTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ColorFilter

@Composable
fun MyVideos(
    navController: NavController
) {
    var selected by remember { mutableStateOf(0) }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = lightColorScheme().background) {
            ShortEaseTheme {
                Column(modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween) {
                    Row {
                        Surface(modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp), color = Color(red = 246, green = 57, blue = 89)) {
                            Box(contentAlignment = Alignment.Center){
                                Text(text = "My Videos",
                                    color = Color(red = 255, green = 255, blue = 255),
                                    fontSize = 25.sp,
                                    textAlign = TextAlign.Center, fontWeight = FontWeight.Bold
                                )
                            }
                            Box(contentAlignment = Alignment.CenterStart){
                                Image(
                                    painter = painterResource(R.drawable.search),
                                    contentDescription = "App Logo",
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
                            .height(70.dp), border = BorderStroke(width = 1.dp, color = Color(red = 246, green = 57, blue = 89)),
                        ) {
                            Button(
                                onClick = {selected = 0},
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(Color.Transparent),
                                modifier = Modifier
                                    .clickable { selected = 0 }
                                    .background(
                                        if (selected == 0) Color(
                                            red = 246,
                                            green = 57,
                                            blue = 89
                                        ) else Color(red = 255, green = 255, blue = 255)
                                    ),
                            ){
                                Image(
                                    painter = painterResource(R.drawable.edit),
                                    contentDescription = "App Logo",
                                    colorFilter = if(selected == 0) ColorFilter.tint(Color(red = 255, green = 255, blue = 255)) else ColorFilter.tint(Color(red = 246, green = 57, blue = 89)),
                                    modifier = Modifier
                                        .size(50.dp)
                                        .padding(start = 20.dp)
                                )
                            }
                        }
                        Surface(modifier = Modifier
                            .weight(1f)
                            .height(70.dp), border = BorderStroke(width = 1.dp, color = Color(red = 246, green = 57, blue = 89)),
                        ) {
                            Button(
                                onClick = {selected = 1},
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(Color.Transparent),
                                modifier = Modifier
                                    .clickable { selected = 1 }
                                    .background(
                                        if (selected == 1) Color(
                                            red = 246,
                                            green = 57,
                                            blue = 89
                                        ) else Color(red = 255, green = 255, blue = 255)
                                    ),
                            ){
                                Image(
                                    painter = painterResource(R.drawable.save),
                                    contentDescription = "App Logo",
                                    colorFilter = if(selected == 1) ColorFilter.tint(Color(red = 255, green = 255, blue = 255)) else ColorFilter.tint(Color(red = 246, green = 57, blue = 89)),
                                    modifier = Modifier
                                        .size(50.dp)
                                        .padding(start = 20.dp)
                                )
                            }
                        }
                        Surface(modifier = Modifier
                            .weight(1f)
                            .height(70.dp), border = BorderStroke(width = 1.dp, color = Color(red = 246, green = 57, blue = 89)),
                        ) {
                            Button(
                                onClick = {selected = 2},
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(Color.Transparent),
                                modifier = Modifier
                                    .clickable { selected = 2 }
                                    .background(
                                        if (selected == 2) Color(
                                            red = 246,
                                            green = 57,
                                            blue = 89
                                        ) else Color(red = 255, green = 255, blue = 255)
                                    ),
                            ){
                                Image(
                                    painter = painterResource(R.drawable.share),
                                    contentDescription = "App Logo",
                                    colorFilter = if(selected == 2) ColorFilter.tint(Color(red = 255, green = 255, blue = 255)) else ColorFilter.tint(Color(red = 246, green = 57, blue = 89)),
                                    modifier = Modifier
                                        .size(50.dp)
                                        .padding(start = 20.dp)
                                        .fillMaxSize()
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