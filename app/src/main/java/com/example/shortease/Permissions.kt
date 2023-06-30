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
    val cameraPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.RECORD_AUDIO,
        )
    )

    if (cameraPermissionState.allPermissionsGranted) {
        Text("Camera permission Granted")
    } else {
        Column {
            val textToShow = if (cameraPermissionState.allPermissionsGranted) {
                "The camera is important for this app. Please grant the permission."
            } else {
                "Camera permission required for this feature to be available. " +
                        "Please grant the permission"
            }
            Text(textToShow)
            Button(onClick = { cameraPermissionState.launchMultiplePermissionRequest() }) {
                Text("Request permission")
            }
        }
    }

}