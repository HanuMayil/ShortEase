import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.example.shortease.ui.theme.colorPalette

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun showUploadPopup(onConfirm: (title: String, description: String, tags: List<String>) -> Unit) {
    val context = LocalContext.current
    // Create a reference to the SoftwareKeyboardController
    val keyboardController = LocalSoftwareKeyboardController.current

    // Use rememberUpdatedState to make sure the keyboardController is up-to-date
    val updatedKeyboardController = rememberUpdatedState(keyboardController)

    var titleInput by remember { mutableStateOf("") }
    var descriptionInput by remember { mutableStateOf("") }
    var tagsInput by remember { mutableStateOf("") }
    var isPopupOpen by remember { mutableStateOf(true) }

    var titleInputEmpty by remember { mutableStateOf(true) }


    LaunchedEffect(titleInput) {
        titleInputEmpty = titleInput.isEmpty()
    }

    if (isPopupOpen) {
        AlertDialog(
            onDismissRequest = { isPopupOpen = false },
            title = { Text(text = "Upload Shorts") },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirm(titleInput, descriptionInput, stringToList(tagsInput))
                        // Hide the soft keyboard
                        updatedKeyboardController.value?.hide()

                        isPopupOpen = false
                    },
                    colors = ButtonDefaults.buttonColors(colorPalette.ShortEaseRed)
                ) {
                    Text("Upload")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        isPopupOpen = false
                    },
                    colors = ButtonDefaults.buttonColors(colorPalette.ShortEaseRed)
                ) {
                    Text("Cancel")
                }
            },
            text = {
                Column(modifier = Modifier.padding(16.dp)
                ) {

                    Text("Title:")
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = colorPalette.ShortEaseRed, // Set the desired background color here
                            unfocusedIndicatorColor = colorPalette.ShortEaseBlack // Set the desired background color here
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Description:")
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = descriptionInput,
                        onValueChange = { descriptionInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = colorPalette.ShortEaseRed, // Set the desired background color here
                            unfocusedIndicatorColor = colorPalette.ShortEaseBlack // Set the desired background color here
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Tags:")
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = tagsInput,
                        onValueChange = { tagsInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = colorPalette.ShortEaseRed, // Set the desired background color here
                            unfocusedIndicatorColor = colorPalette.ShortEaseBlack // Set the desired background color here
                        )
                    )
                }
            }
        )
    }
}

fun stringToList(input: String): List<String> {
    return input.split(" ").filter { it.isNotBlank() }
}

