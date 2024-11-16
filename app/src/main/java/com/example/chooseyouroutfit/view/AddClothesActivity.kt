package com.example.chooseyouroutfit.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chooseyouroutfit.R
import com.example.chooseyouroutfit.model.ClothesCategoryType
import com.example.chooseyouroutfit.model.ClothesHolder
import com.example.chooseyouroutfit.model.SeasonType
import com.example.chooseyouroutfit.ui.components.ReusableBackgroundWardrobe
import com.example.chooseyouroutfit.ui.components.ReusableDropdownMenu
import com.example.chooseyouroutfit.ui.components.ReusableTextField
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
        ReusableBackgroundWardrobe()
        AddItemForm()
        ReturnToMain()
    }

    // TODO - maybe would be better to name filed as selectedName, selectedColor etc ...
    @Composable
    fun AddItemForm() {
        var clothesHolder by remember { mutableStateOf<ClothesHolder?>(null) }
        val context = LocalContext.current
        val intentCameraXActivity = Intent(context, CameraActivity::class.java)

        var name by rememberSaveable { mutableStateOf("") }
        var color by rememberSaveable { mutableStateOf("") }
        var material by rememberSaveable { mutableStateOf("") }
        var season by rememberSaveable { mutableStateOf("") }
        var category by rememberSaveable { mutableStateOf("") }
        // val uri: Uri (?)

        Column(
            modifier = Modifier
                .padding(top = 70.dp, start = 16.dp, end = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ReusableTextField(
                value = name, onValueChange = { name = it }, placeholder = "Name"
            )
            ReusableTextField(
                value = color, onValueChange = { color = it }, placeholder = "Color"
            )
            ReusableTextField(
                value = material, onValueChange = { material = it }, placeholder = "Material"
            )
            CategoryDropDownMenu(categorySelection = { selectedCategory ->
                category = selectedCategory
            })
            SeasonDropdownMenu(seasonSelection = { selectedSeason ->
                season = selectedSeason
            })

            clothesHolder = ClothesHolder(
                name = name,
                color = color,
                season = season,
                material = material,
                category = category
            )

            Button(
                onClick = {
                    intentCameraXActivity.putExtra("objectClothes", clothesHolder)
                    startActivity(intentCameraXActivity)
                    finish()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                border = BorderStroke(1.dp, Color.Black),
                enabled = isFormValid(name, color, material, season, category)
            ) {
                Text(
                    text = stringResource(R.string.photo), fontSize = 25.sp, color = Color.DarkGray
                )
            }

            // TODO - pytanie gdzie dodamy ubranie
            Button(
                onClick = {
                    //    addItemToDatabase(clothesHolder)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                border = BorderStroke(1.dp, Color.Black),
                enabled = isFormValid(name, color, material, season, category)
            ) {
                Text(
                    text = stringResource(R.string.addItem),
                    fontSize = 25.sp,
                    color = Color.DarkGray
                )
            }
        }
    }

    private fun isFormValid(
        name: String,
        color: String,
        material: String,
        season: String,
        category: String
    ): Boolean {
        return name.isNotEmpty() && color.isNotEmpty() && material.isNotEmpty() && season.isNotEmpty() && category.isNotEmpty()
    }

    @Composable
    fun SeasonDropdownMenu(seasonSelection: (String) -> Unit) {
        val options = SeasonType.getSeasonNames()
        var selectedSeason by remember { mutableStateOf(options[0]) }

        ReusableDropdownMenu(
            label = "Select Season",
            options = options,
            selectedOption = selectedSeason,
            onOptionSelected = { seasonName ->
                seasonSelection(seasonName)
                selectedSeason = seasonName

            }
        )
    }

    @Composable
    fun CategoryDropDownMenu(categorySelection: (String) -> Unit) {
        val options = ClothesCategoryType.getCategoryNames()
        var selectedCategory by remember { mutableStateOf(options[0]) }

        ReusableDropdownMenu(
            label = "Select Category",
            options = options,
            selectedOption = selectedCategory,
            onOptionSelected = { categoryName ->
                categorySelection(categoryName)
                selectedCategory = categoryName
            }
        )
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
                modifier = Modifier.size(40.dp),
                contentDescription = "Return Arrow"
            )
        }
    }

    // TODO - metoda do dodawania do bazy? Teraz jest w CameraActivity
    // addItem

    @Preview(showBackground = true)
    @Composable
    fun PreviewMainBackground() {
        BackgroundView()
    }
}