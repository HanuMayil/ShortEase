import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.shortease.CropRangeSlider
import com.example.shortease.playerView
import com.example.shortease.ui.theme.colorPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun showPopup(
    range: ClosedFloatingPointRange<Float>,
    values: ClosedFloatingPointRange<Float>,
    onConfirm: (String, String, Int, Float, Float) -> Unit,
    onCancel: () -> Unit) {
    val context = LocalContext.current
    var userInput by remember { mutableStateOf("") }
    var selectedPosition by remember { mutableStateOf("Top") }
    var selectedFontSize by remember { mutableStateOf(8) }
    var isPopupOpen by remember { mutableStateOf(true) }
    var expandedPosition by remember { mutableStateOf(false) }
    var expandedFont by remember { mutableStateOf(false) }
    val fontSizes = arrayOf(8, 9, 10, 11, 12)
    val position = arrayOf("Top", "Middle", "Bottom")
    var startCropTime = 0f
    var endCropTime = 0f
    var range by remember { mutableStateOf(range) }
    var values by remember { mutableStateOf(values) }

    if (isPopupOpen) {
        AlertDialog(
            onDismissRequest = { isPopupOpen = false },
            title = { Text("Create Subtitle") },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirm(userInput, selectedPosition, selectedFontSize, startCropTime, endCropTime)
                        isPopupOpen = false
                },
                colors = ButtonDefaults.buttonColors(colorPalette.ShortEaseRed)) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        onCancel()
                        isPopupOpen = false },
                    colors = ButtonDefaults.buttonColors(colorPalette.ShortEaseRed)) {
                    Text("Cancel")
                }
            },
            text = {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Please enter your text:")
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = userInput,
                        onValueChange = { userInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = colorPalette.ShortEaseRed, // Set the desired background color here
                            unfocusedIndicatorColor = colorPalette.ShortEaseBlack // Set the desired background color here
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Position")
                    ExposedDropdownMenuBox(
                        expanded = expandedPosition,
                        onExpandedChange = {
                            expandedPosition = !expandedPosition
                        }
                    ) {
                        TextField(
                            value = selectedPosition,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPosition) },
                            modifier = Modifier.menuAnchor(),
                            colors = TextFieldDefaults.textFieldColors(
                                focusedIndicatorColor = colorPalette.ShortEaseRed,
                                unfocusedIndicatorColor = colorPalette.ShortEaseBlack
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expandedPosition,
                            onDismissRequest = { expandedPosition = false },
                        ) {
                            position.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(text = item) },
                                    onClick = {
                                        selectedPosition = item
                                        expandedPosition = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Font Size")
                    ExposedDropdownMenuBox(
                        expanded = expandedFont,
                        onExpandedChange = {
                            expandedFont = !expandedFont
                        }
                    ) {
                        TextField(
                            value = selectedFontSize.toString(),
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFont) },
                            modifier = Modifier.menuAnchor(),
                            colors = TextFieldDefaults.textFieldColors(
                                focusedIndicatorColor = colorPalette.ShortEaseRed,
                                unfocusedIndicatorColor = colorPalette.ShortEaseBlack
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expandedFont,
                            onDismissRequest = { expandedFont = false }
                        ) {
                            fontSizes.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(text = item.toString()) },
                                    onClick = {
                                        selectedFontSize = item
                                        expandedFont = false
                                    }
                                )
                            }
                        }
                    }

                    CropRangeSlider(
                        range = range,
                        values = values,
                        onRangeChanged = { newValues ->
                            values = newValues
                            startCropTime = values.start.toFloat()
                            endCropTime = values.endInclusive.toFloat()
                        })
                }
            }
        )
    }
}

