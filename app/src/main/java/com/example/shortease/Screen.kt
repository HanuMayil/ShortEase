package com.example.shortease

sealed class Screen(val route: String) {
    object HomeScreen: Screen(route = "home_screen")
    object MyVideos: Screen(route = "my_videos")
}
