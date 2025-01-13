package com.example.chooseyouroutfit.ui.components

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.chooseyouroutfit.view.MainActivity

@Composable
fun ReusableReturnArrow() {
    val context = LocalContext.current
    val intent = Intent(context, MainActivity::class.java)

    Card(
        modifier = Modifier
            .padding(10.dp)
            .clickable {
                context.startActivity(intent)
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        )
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            modifier = Modifier.size(40.dp),
            contentDescription = "Return Arrow"
        )
    }
}