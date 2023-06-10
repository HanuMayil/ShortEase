package com.example.shortease

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.shortease.ui.theme.ShortEaseTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShortEaseTheme {
                navController = rememberNavController()
                SetupNavGraph(navController = navController)
            }
        }

        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser

        Handler(Looper.getMainLooper()).postDelayed({
            if(user != null) {
                navController.navigate(route = Screen.MyVideos.route)
            }
            else {
                navController.navigate(route = Screen.HomeScreen.route)
            }
        }, 1000)
    }
}