package com.example.chooseyouroutfit.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReusableTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = placeholder,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Normal
                )
            },
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFF5F5F5),
                focusedIndicatorColor = Color(0xFFB0B0B0),
                unfocusedIndicatorColor = Color(0xFFB0B0B0),
            ),
        )
    }
}

@Preview
@Composable
fun PreviewReusableTextField() {
    ReusableTextField(
        value = "Write something ...",
        onValueChange = {},
        placeholder = "Text"
    )
}