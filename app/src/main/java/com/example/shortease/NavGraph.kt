package com.example.shortease

import PreviewScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route,
    ) {
        composable(
            route = Screen.HomeScreen.route
        ) {
            HomeScreen(navController)
        }
        composable(
            route = Screen.MyVideos.route
        ) {
            MyVideos(navController)
        }
        composable(
            route = Screen.PreviewScreen.route
        ) {
            PreviewScreen(navController)
        }
        composable(
            route = Screen.DebugScreen.route
        ) {
            DebugScreen(navController)
        }
        composable(
            route = Screen.VideoEditorScreen.route
        ) {
            VideoEditorScreen(navController)
        }
    }
}