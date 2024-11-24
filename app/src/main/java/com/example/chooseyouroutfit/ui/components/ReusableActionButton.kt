package com.example.chooseyouroutfit.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ReusableActionButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    isEnabled: Boolean,
    containerColor: Color = Color.Transparent,
    textColor: Color = Color.Black,
    fontSize: TextUnit = 20.sp,
    borderColor: Color = Color.Black,
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        border = BorderStroke(1.dp, borderColor),
        enabled = isEnabled
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            color = textColor
        )
    }
}

@Preview
@Composable
fun PreviewReusableActionButton() {
    val context = LocalContext.current
    ReusableActionButton(
        text = "Click Me",
        onClick = {
        },
        isEnabled = true
    )
}