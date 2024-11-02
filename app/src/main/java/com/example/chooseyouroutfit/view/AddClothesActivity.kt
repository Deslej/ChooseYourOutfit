package com.example.chooseyouroutfit.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chooseyouroutfit.R
import com.example.chooseyouroutfit.ui.theme.ChooseYourOutfitTheme

class AddClothesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChooseYourOutfitTheme {
                BackgroundView()
            }
        }
    }

    @Composable
    fun BackgroundView() {
        val context = LocalContext.current
        val intentCameraXActivity = Intent(context, CameraActivity::class.java)
        Image(
            painter = painterResource(R.drawable.addingclothesbackground),
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize(),
            contentDescription = "Tło dodawania zdjec"
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Button(
                onClick = {
                    intentCameraXActivity.putExtra("location", "TEST")
                    startActivity(intentCameraXActivity)

                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                border = BorderStroke(1.dp, Color.Black)

            ) {
                Text(
                    text = stringResource(R.string.add_shirt),
                    fontSize = 25.sp,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    intentCameraXActivity.putExtra("location", "TEST")
                    startActivity(intentCameraXActivity)


                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Text(
                    text = stringResource(R.string.add_trousers),
                    fontSize = 25.sp,
                    color = Color.DarkGray
                )
            }
        }
        ReturnToMain()
    }

    @Composable
    fun ReturnToMain() {
        val context = LocalContext.current
        val intent = Intent(context, MainActivity::class.java)

        Card(
            modifier = Modifier
                .padding(10.dp)
                .clickable {
                    startActivity(intent)
                    finish()
                }, colors = CardDefaults.cardColors(
                containerColor = Color.Transparent, contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                modifier = Modifier.size(35.dp),
                contentDescription = "Strzałka powrotu"
            )
        }
    }
}
