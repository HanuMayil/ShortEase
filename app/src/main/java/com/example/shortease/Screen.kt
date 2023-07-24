package com.example.shortease

sealed class Screen(val route: String) {
    object DebugScreen: Screen(route = "debug_screen")
    object HomeScreen: Screen(route = "home_screen")
    object MyVideos: Screen(route = "my_videos?channelId={channelId}")
    object Generate: Screen(route = "generate?videoId={videoId}")
    object PreviewScreen: Screen(route = "preview_screen?videoId={videoId}")
    object VideoEditorScreen: Screen(route = "video_editor_screen?videoId={videoId}")
    object Permissions: Screen(route = "permissions")
}
