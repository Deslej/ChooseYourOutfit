package com.example.chooseyouroutfit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReusableDropdownMenu(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        var expanded by remember { mutableStateOf(false) }
        var selectedOptionText by remember { mutableStateOf(selectedOption) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                value = selectedOptionText,
                onValueChange = {},
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Normal
                    )
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color(0xFFB0B0B0),
                    unfocusedIndicatorColor = Color(0xFFB0B0B0),
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(),
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Box(
                                modifier = Modifier.background(Color(0xFFF5F5F5))
                            ) {
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.Black
                                )
                            }
                        },
                        onClick = {
                            selectedOptionText = option
                            onOptionSelected(option)
                            expanded = false
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = Color.Black,
                            leadingIconColor = Color.Transparent,
                            trailingIconColor = Color.Transparent,
                            disabledTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewStyledDropdownMenu() {
    ReusableDropdownMenu(
        label = "Choose an option",
        options = listOf("Option 1", "Option 2", "Option 3"),
        selectedOption = "Option 1",
        onOptionSelected = {}
    )
}