package com.example.chooseyouroutfit.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.chooseyouroutfit.R

@Composable
fun ReusableBackgroundWardrobe() {
    Image(
        painter = painterResource(R.drawable.addingclothesbackground),
        contentScale = ContentScale.FillBounds,
        modifier = Modifier.fillMaxSize(),
        contentDescription = "Background to add item / see wardrobe"
    )
}