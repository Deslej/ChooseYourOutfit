package com.example.chooseyouroutfit.view

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chooseyouroutfit.R
import com.example.chooseyouroutfit.ui.theme.BodyColor
import com.example.chooseyouroutfit.ui.theme.ChooseYourOutfitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChooseYourOutfitTheme {
                MainBackground()
            }
        }
    }

    @Composable
    fun MainBackground() {
        val context = LocalContext.current
        val intentChooseOutfit = Intent(context, ChooseOutfitActivity::class.java)
        val intentAddClothesActivity = Intent(context, AddClothesActivity::class.java)
        val configuration = LocalConfiguration.current
        val painter = getBackgroundPainter(configuration.orientation)

        Image(
            painter = painter,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize(),
            contentDescription = "Tło główne"
        )
        Column(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Button(
                onClick = {
                    startActivity(intentChooseOutfit)
                    finish()
                          },
                colors = ButtonDefaults.buttonColors(containerColor = BodyColor),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Text(text = stringResource(R.string.chooseoutfit),
                    fontSize = 25.sp)
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    startActivity(intentAddClothesActivity)
                    finish()
                          },
                colors = ButtonDefaults.buttonColors(containerColor = BodyColor),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Text(text = stringResource(R.string.addoutfit),
                    fontSize = 25.sp)
            }
        }
    }

    @Composable
    private fun getBackgroundPainter(orientation: Int) = when (orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> painterResource(R.drawable.mainbackgroundlandscape)
        Configuration.ORIENTATION_PORTRAIT -> painterResource(R.drawable.mainbackgroundportrait)
        else -> painterResource(R.drawable.mainbackgroundportrait)
    }

}