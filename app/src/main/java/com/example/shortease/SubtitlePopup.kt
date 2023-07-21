import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shortease.CropRangeSlider
import com.example.shortease.PlayerSubtitles
import com.example.shortease.ui.theme.colorPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun showPopup(
    range: ClosedFloatingPointRange<Float>,
    values: ClosedFloatingPointRange<Float>,
    currentSubtitles: MutableList<PlayerSubtitles>,
    onConfirm: (MutableList<PlayerSubtitles>) -> Unit) {
    val context = LocalContext.current
    var userInput by remember { mutableStateOf("") }
    var isPopupOpen by remember { mutableStateOf(true) }
    var expandedPosition by remember { mutableStateOf(false) }
    var expandedFont by remember { mutableStateOf(false) }
    var isSubtitlesListDialogVisible by remember { mutableStateOf(false) }
    var userInputEmpty by remember { mutableStateOf(true) }
    val fontSizes = arrayOf(20,25,30,35,40)
    val position = arrayOf("Top", "Middle", "Bottom")
    var selectedPosition by remember { mutableStateOf(position[0]) }
    var selectedFontSize by remember { mutableStateOf(fontSizes[0]) }
    var startCropTime = 0f
    var endCropTime = values.endInclusive
    var range by remember { mutableStateOf(range) }
    var values by remember { mutableStateOf(values) }

    LaunchedEffect(userInput) {
        userInputEmpty = userInput.isEmpty()
    }

    if (isPopupOpen) {
        AlertDialog(
            onDismissRequest = { isPopupOpen = false },
            title = { Text(text = if (isSubtitlesListDialogVisible) "Current Subtitles" else "Create Subtitle") },
            confirmButton = {
                if(!isSubtitlesListDialogVisible) {
                    Button(
                        onClick = {
                            onConfirm(currentSubtitles)
                            isPopupOpen = false
                        },
                        colors = ButtonDefaults.buttonColors(colorPalette.ShortEaseRed)
                    ) {
                        Text("Confirm")
                    }
                }
            },
            dismissButton = {
                if (!isSubtitlesListDialogVisible) {
                    Button(
                        onClick = {
                            isSubtitlesListDialogVisible = true
                        },
                        colors = ButtonDefaults.buttonColors(colorPalette.ShortEaseRed)
                    ) {
                        Text("Current Subtitles")
                    }
                } else {
                        Button(
                            onClick = {
                                isSubtitlesListDialogVisible = false
                            },
                            colors = ButtonDefaults.buttonColors(colorPalette.ShortEaseRed)
                        ) {
                            Text("Back")
                        }
                    }
            },
            text = {
                    Column(modifier = Modifier.padding(16.dp)
                    ) {
                        if(!isSubtitlesListDialogVisible) {
                            Text("Please enter your text:")
                            Spacer(modifier = Modifier.height(8.dp))
                            TextField(
                                value = userInput,
                                onValueChange = { userInput = it},
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
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = expandedPosition
                                        )
                                    },
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
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = expandedFont
                                        )
                                    },
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

                            Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = {
                                        if (userInputEmpty) {
                                            Toast.makeText(context, "Please enter text before adding",Toast.LENGTH_SHORT).show()
                                        } else {
                                            // Your add subtitle logic here
                                            val newSubtitle = PlayerSubtitles(
                                                userInput = userInput,
                                                selectedPosition = selectedPosition,
                                                selectedFontSize = selectedFontSize,
                                                startCropTime = startCropTime,
                                                endCropTime = endCropTime
                                            )

                                            currentSubtitles.add(newSubtitle)
                                            Toast.makeText(context, "Successfully Added Subtitle", Toast.LENGTH_SHORT).show()
                                            userInput = "" // Clear the user input after adding the subtitle
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(colorPalette.ShortEaseRed),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                                ) {
                                    Text("Add Subtitle")
                                }
                        }
                        if (isSubtitlesListDialogVisible) {
                            SubtitlesListPage(
                                currentSubtitles = currentSubtitles,
                                onSubtitleRemoved = { index ->
                                    currentSubtitles.removeAt(index)
                                    Toast.makeText(context, "Successfully Removed Subtitle", Toast.LENGTH_SHORT).show()
                                    isSubtitlesListDialogVisible = false
                                }
                            )
                        }
                    }
            }
        )
    }
}

@Composable
fun SubtitlesListPage(
    currentSubtitles: MutableList<PlayerSubtitles>,
    onSubtitleRemoved: (Int) -> Unit
) {
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (currentSubtitles.isEmpty()) {
            Text("No subtitles found.")
        } else {
            currentSubtitles.forEachIndexed { index, subtitle ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = if (subtitle.userInput.length > 15) "${subtitle.userInput.take(15)}..." else subtitle.userInput,
                        modifier = Modifier.weight(1f),
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                onSubtitleRemoved(index)
                            }
                            .background(
                                color = colorPalette.ShortEaseRed,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center,
                        content = {
                            Text(
                                text = "x",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White ,
                                modifier = Modifier.offset(y = (-2).dp)
                            )
                        }
                    )
                }
            }
        }
    }
}
