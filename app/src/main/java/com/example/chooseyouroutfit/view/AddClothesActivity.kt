package com.example.chooseyouroutfit.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chooseyouroutfit.R
import com.example.chooseyouroutfit.model.ClothesCategoryType
import com.example.chooseyouroutfit.model.ClothesHolder
import com.example.chooseyouroutfit.model.SeasonType
import com.example.chooseyouroutfit.ui.components.ReusableActionButton
import com.example.chooseyouroutfit.ui.components.ReusableBackgroundWardrobe
import com.example.chooseyouroutfit.ui.components.ReusableDropdownMenu
import com.example.chooseyouroutfit.ui.components.ReusableReturnArrow
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

        Column(
            modifier = Modifier
                .padding(top = 70.dp, start = 16.dp, end = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
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
                name = name.uppercase(),
                color = color.uppercase(),
                season = season,
                material = material.uppercase(),
                category = category
            )

            ReusableActionButton(
                text = stringResource(R.string.photo),
                onClick = {
                    intentCameraXActivity.putExtra("objectClothes", clothesHolder)
                    startActivity(intentCameraXActivity)
                    finish()
                },
                isEnabled = isFormValid(name, color, material, season, category)

            )

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
        ReusableReturnArrow()
    }

    @Preview(showBackground = true)
    @Composable
    fun PreviewMainBackground() {
        BackgroundView()
    }
}