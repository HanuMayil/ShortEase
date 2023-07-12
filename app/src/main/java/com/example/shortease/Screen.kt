package com.example.shortease

sealed class Screen(val route: String) {
    object DebugScreen: Screen(route = "debug_screen")
    object HomeScreen: Screen(route = "home_screen")
    object MyVideos: Screen(route = "my_videos")
    object Generate: Screen(route = "generate")
    object PreviewScreen: Screen(route = "preview_screen")
    object VideoEditorScreen: Screen(route = "video_editor_screen")
    object Permissions: Screen(route = "permissions")
    object SavedVideos: Screen(route = "saved_videos")
}
