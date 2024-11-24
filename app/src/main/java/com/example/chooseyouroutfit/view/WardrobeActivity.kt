package com.example.chooseyouroutfit.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chooseyouroutfit.R
import com.example.chooseyouroutfit.data.entities.Clothes
import com.example.chooseyouroutfit.data.repository.ClothesRepository
import com.example.chooseyouroutfit.model.ClothesCategoryType
import com.example.chooseyouroutfit.model.SeasonType
import com.example.chooseyouroutfit.ui.components.ReusableActionButton
import com.example.chooseyouroutfit.ui.components.ReusableBackgroundWardrobe
import com.example.chooseyouroutfit.ui.components.ReusableDropdownMenu
import com.example.chooseyouroutfit.ui.components.ReusableTextField
import com.example.chooseyouroutfit.ui.theme.ChooseYourOutfitTheme
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class WardrobeActivity : ComponentActivity() {

    private val CODR by inject<ClothesRepository>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChooseYourOutfitTheme {
                MainWardrobeView()
            }
        }
    }

    @Composable
    fun MainWardrobeView() {
        ReusableBackgroundWardrobe()
        FilterForm()
        ReturnToMain()
    }

    @Composable
    fun FilterForm() {
        var selectedName by remember { mutableStateOf("") }
        var selectedMaterial by remember { mutableStateOf("") }
        var selectedColor by remember { mutableStateOf("") }
        var selectedSeason by remember { mutableStateOf("") }
        var selectedCategory by remember { mutableStateOf("") }

        var filteredClothes by remember { mutableStateOf<List<Clothes>>(emptyList()) }
        val coroutineScope = rememberCoroutineScope()

        fun loadFilteredClothes() {
            coroutineScope.launch {
                filteredClothes = CODR.getFilteredClothes(
                    name = if (selectedName.isNotEmpty()) "%${
                        selectedName.uppercase().trim()
                    }%" else "%",
                    color = if (selectedColor.isNotEmpty()) "%${
                        selectedColor.uppercase().trim()
                    }%" else "%",
                    material = if (selectedMaterial.isNotEmpty()) "%${
                        selectedMaterial.uppercase().trim()
                    }%" else "%",
                    season = if (selectedSeason.isNotEmpty()) "%${selectedSeason.trim()}%" else "%",
                    category = if (selectedCategory.isNotEmpty()) "%${selectedCategory.trim()}%" else "%"
                )
            }
        }

        LaunchedEffect(Unit) {
            val clothesList = CODR.getAllClothes();
            filteredClothes = clothesList
        }

        Column(
            modifier = Modifier
                .padding(top = 70.dp, start = 16.dp, end = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ReusableTextField(
                value = selectedName,
                onValueChange = { selectedName = it },
                placeholder = "Name"
            )

            ReusableTextField(
                value = selectedColor,
                onValueChange = { selectedColor = it },
                placeholder = "Color"
            )

            ReusableTextField(
                value = selectedMaterial,
                onValueChange = { selectedMaterial = it },
                placeholder = "Material"
            )

            CategoryDropDownMenu(categorySelection = { category ->
                selectedCategory = category
            })

            SeasonDropdownMenu(seasonSelection = { season ->
                selectedSeason = season
            })

            ReusableActionButton(
                text = stringResource(R.string.search),
                onClick = {
                    loadFilteredClothes()
                },
                isEnabled = true
            )

//            ReusableImageGrid(clothes = filteredClothes)
        }
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
            label = "Select Season",
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

    @Preview(showBackground = true)
    @Composable
    fun PreviewMainBackground() {
        MainWardrobeView()
    }
}