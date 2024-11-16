package com.example.chooseyouroutfit.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.chooseyouroutfit.data.entities.Clothes
import com.example.chooseyouroutfit.data.repository.ClothesRepository
import com.example.chooseyouroutfit.model.ClothesCategoryType
import com.example.chooseyouroutfit.model.SeasonType
import com.example.chooseyouroutfit.ui.components.ReusableBackgroundWardrobe
import com.example.chooseyouroutfit.ui.components.ReusableDropdownMenu
import com.example.chooseyouroutfit.ui.components.ReusableTextField
import com.example.chooseyouroutfit.ui.theme.ChooseYourOutfitTheme
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
    fun ImageGrid(clothes: List<Clothes>, columns: Int = 4) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(clothes.size) { index ->
                ImageCard(imageUri = clothes[index].uri)
            }
        }
    }

    @Composable
    fun ImageCard(imageUri: Uri) {
        Card(
            modifier = Modifier
                .size(100.dp)
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
        ) {
            Image(
                painter = rememberImagePainter(imageUri),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }

    @Composable
    fun FilterForm() {
        var selectedMaterial by remember { mutableStateOf("") }
        var selectedColor by remember { mutableStateOf("") }
        var selectedSeason by remember { mutableStateOf("") }
        var selectedCategory by remember { mutableStateOf("") }

        var filteredClothes by remember { mutableStateOf<List<Clothes>>(emptyList()) }

        suspend fun loadFilteredClothes() {
            filteredClothes = CODR.getFilteredClothes(
                color = if (selectedColor.isNotEmpty()) "%${selectedColor.trim()}%" else "%",
                material = if (selectedMaterial.isNotEmpty()) "%${selectedMaterial.trim()}%" else "%",
                season = if (selectedSeason.isNotEmpty()) "%${selectedSeason.trim()}%" else "%",
                category = if (selectedCategory.isNotEmpty()) "%${selectedCategory.trim()}%" else "%",
            )
        }

        LaunchedEffect(selectedMaterial, selectedColor, selectedSeason, selectedCategory) {
            loadFilteredClothes()
        }

        Column(
            modifier = Modifier
                .padding(top = 70.dp, start = 16.dp, end = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ReusableTextField(
                value = selectedColor,
                onValueChange = { selectedColor = it },
                placeholder = "Select Color"
            )
            ReusableTextField(
                value = selectedMaterial,
                onValueChange = { selectedMaterial = it },
                placeholder = "Select Material"
            )
            CategoryDropDownMenu(categorySelection = { category ->
                selectedCategory = category
            })
            SeasonDropdownMenu(seasonSelection = { season ->
                selectedSeason = season
            })
            // TODO - tutaj cos sie wywala
//            ImageGrid(clothes = filteredClothes)
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