package com.example.shortease

import Permissions
import PreviewScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    startDestination: String,
    signInClicked: () -> Unit,
    signOutClicked: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(
            route = Screen.HomeScreen.route
        ) {
            HomeScreen(navController, signInClicked)
        }
        composable(
            route = Screen.MyVideos.route
        ) {
            MyVideos(navController, signOutClicked)
        }
        composable(
            route = Screen.Generate.route
        ) {
            Generate(navController)
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
        composable(
            route = Screen.Permissions.route
        ) {
            Permissions(navController)
        }
        composable(
            route = Screen.SavedVideos.route
        ) {
            SavedVideos(navController, signOutClicked)
        }
    }
}

