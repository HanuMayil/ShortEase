package com.example.shortease

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    signInClicked: () -> Unit?
) {
    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route,
    ) {
        composable(
            route = Screen.HomeScreen.route
        ) {
            HomeScreen(navController, signInClicked)
        }
        composable(
            route = Screen.MyVideos.route
        ) {
            MyVideos(navController)
        }
    }
}