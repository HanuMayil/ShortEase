package com.example.shortease

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.shortease.ui.theme.ShortEaseTheme

class MainActivity : ComponentActivity() {
    lateinit var navController: NavHostController;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShortEaseTheme {
                navController = rememberNavController()
                SetupNavGraph(navController = navController)
            }
        }
    }
}