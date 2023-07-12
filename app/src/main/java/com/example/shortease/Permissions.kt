import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import androidx.compose.foundation.layout.Column
import androidx.navigation.NavController

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Permissions(navController: NavController) {
    val storagePermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.READ_MEDIA_VIDEO,
            android.Manifest.permission.INTERNET
        )
    )

    if (storagePermissionState.allPermissionsGranted) {
        Text("Video storage granted")
    } else {
        Column {
            val textToShow = if (storagePermissionState.allPermissionsGranted) {
                "Video storage is important for this app. Please grant the permission."
            } else {
                "Video Storage permission required for this feature to be available. " +
                        "Please grant the permission"
            }
            Text(textToShow)
            Button(onClick = { storagePermissionState.launchMultiplePermissionRequest() }) {
                Text("Request permission")
            }
        }
    }

}