package com.example.shortease

sealed class Screen(val route: String) {
    object SignIn: Screen(route = "sign_in")
    object MyVideos: Screen(route = "my_videos")
}
