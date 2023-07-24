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
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.shortease.PlayerSubtitles
import com.example.shortease.R
import com.example.shortease.calculateTimestamp
import com.example.shortease.playerView
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
    val position = arrayOf(ContextCompat.getString(
        context,
        R.string.top
    ), ContextCompat.getString(
        context,
        R.string.middle
    ), ContextCompat.getString(
        context,
        R.string.bottom
    ))
    var selectedPosition by remember { mutableStateOf(position[0]) }
    var selectedFontSize by remember { mutableStateOf(fontSizes[0]) }
    var values by remember { mutableStateOf(values) }

    LaunchedEffect(userInput) {
        userInputEmpty = userInput.isEmpty()
    }

    if (isPopupOpen) {
        AlertDialog(
            onDismissRequest = { isPopupOpen = false },
            title = { Text(text = if (isSubtitlesListDialogVisible) ContextCompat.getString(
                context,
                R.string.subtitle_current
            ) else ContextCompat.getString(context, R.string.subtitle_create))
            },
            confirmButton = {
                if(!isSubtitlesListDialogVisible) {
                    Button(
                        onClick = {
                            onConfirm(currentSubtitles)
                            isPopupOpen = false
                        },
                        colors = ButtonDefaults.buttonColors(colorPalette.ShortEaseRed)
                    ) {
                        Text(ContextCompat.getString(context, R.string.confirm))
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
                        Text(ContextCompat.getString(context, R.string.subtitle_current))
                    }
                } else {
                        Button(
                            onClick = {
                                isSubtitlesListDialogVisible = false
                            },
                            colors = ButtonDefaults.buttonColors(colorPalette.ShortEaseRed)
                        ) {
                            Text(ContextCompat.getString(context, R.string.back))
                        }
                    }
            },
            text = {
                    Column(modifier = Modifier.padding(16.dp)
                    ) {
                        if(!isSubtitlesListDialogVisible) {
                            Text(ContextCompat.getString(context, R.string.subtitle_enter_text))
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
                            Text(ContextCompat.getString(context, R.string.subtitle_position))
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
                            Text(ContextCompat.getString(context, R.string.subtitle_font_size))
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

                            SubtitleRangeSlider(
                                range = range,
                                values = values,
                                onRangeChanged = { newValues ->
                                    values = newValues
                                })

                            Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = {
                                        if (userInputEmpty) {
                                            Toast.makeText(context, ContextCompat.getString(context, R.string.subtitle_enter_text_error),Toast.LENGTH_SHORT).show()
                                        } else {
                                            // Your add subtitle logic here
                                            val newSubtitle = PlayerSubtitles(
                                                userInput = userInput,
                                                selectedPosition = selectedPosition,
                                                selectedFontSize = selectedFontSize,
                                                startCropTime = values.start.toFloat(),
                                                endCropTime = values.endInclusive.toFloat()
                                            )

                                            currentSubtitles.add(newSubtitle)
                                            Toast.makeText(context, ContextCompat.getString(context, R.string.subtitle_added), Toast.LENGTH_SHORT).show()
                                            userInput = "" // Clear the user input after adding the subtitle
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(colorPalette.ShortEaseRed),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                                ) {
                                    Text(ContextCompat.getString(context, R.string.subtitle_add))
                                }
                        }
                        if (isSubtitlesListDialogVisible) {
                            SubtitlesListPage(
                                currentSubtitles = currentSubtitles,
                                onSubtitleRemoved = { index ->
                                    currentSubtitles.removeAt(index)
                                    Toast.makeText(context, ContextCompat.getString(context, R.string.subtitle_removed), Toast.LENGTH_SHORT).show()
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
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (currentSubtitles.isEmpty()) {
            Text(ContextCompat.getString(
                LocalContext.current,
                R.string.subtitle_none
            ))
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

@ExperimentalMaterial3Api
@Composable
fun SubtitleRangeSlider(
    range: ClosedFloatingPointRange<Float>,
    values: ClosedFloatingPointRange<Float>,
    onRangeChanged: (ClosedFloatingPointRange<Float>) -> Unit
) {
    val context = LocalContext.current
    val timestamp = remember(values) {
        calculateTimestamp(context,values)
    }
    Column {
        RangeSlider(
            value = values,
            onValueChange = { newValues ->
                onRangeChanged(newValues)
                playerView.player?.seekTo(newValues.start.toLong())
            },
            valueRange = range,
            steps = 100,
            colors = SliderDefaults.colors(
                thumbColor = colorPalette.ShortEaseWhite,
                activeTrackColor = colorPalette.ShortEaseRed,
                inactiveTrackColor = colorPalette.ShortEaseRed.copy(alpha = 0.2f)
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Text(
            text = timestamp,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterHorizontally),
            style = TextStyle(color = colorPalette.ShortEaseRed)
        )
    }
}
