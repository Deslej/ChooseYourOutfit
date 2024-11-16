package com.example.chooseyouroutfit.view

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chooseyouroutfit.R
import com.example.chooseyouroutfit.ui.theme.BodyColor
import com.example.chooseyouroutfit.ui.theme.ChooseYourOutfitTheme
import com.example.chooseyouroutfit.ui.theme.DullBrown

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
        val configuration = LocalConfiguration.current
        val painter = getBackgroundPainter(configuration.orientation)

        Image(
            painter = painter,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize(),
            contentDescription = "Main Background"
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 450.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CustomButton(
                text = stringResource(R.string.chooseOutfit),
                onClick = {
                    context.startActivity(Intent(context, ChooseOutfitActivity::class.java))
                }
            )

            CustomButton(
                text = stringResource(R.string.addItem),
                onClick = {
                    context.startActivity(Intent(context, AddClothesActivity::class.java))
                }
            )

            CustomButton(
                text = stringResource(R.string.seeWardrobe),
                onClick = {
                    context.startActivity(Intent(context, WardrobeActivity::class.java))
                }
            )

            CustomButton(
                text = stringResource(R.string.seeOutfits),
                onClick = {
                    context.startActivity(Intent(context, OutfitsActivity::class.java))
                }
            )
        }
    }

    @Composable
    fun CustomButton(text: String, onClick: () -> Unit) {
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(13.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BodyColor,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp)
                .border(
                    width = 2.dp,
                    color = DullBrown,
                    shape = RoundedCornerShape(13.dp)
                )
        ) {
            Text(
                text = text,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    @Composable
    private fun getBackgroundPainter(orientation: Int) = when (orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> painterResource(R.drawable.mainbackgroundlandscape)
        Configuration.ORIENTATION_PORTRAIT -> painterResource(R.drawable.mainbackgroundportrait)
        else -> painterResource(R.drawable.mainbackgroundportrait)
    }

    @Preview(showBackground = true)
    @Composable
    fun PreviewMainBackground() {
        MainBackground()
    }
}