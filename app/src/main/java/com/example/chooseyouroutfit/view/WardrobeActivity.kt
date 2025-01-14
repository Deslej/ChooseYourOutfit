package com.example.chooseyouroutfit.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.chooseyouroutfit.R
import com.example.chooseyouroutfit.data.entities.Clothes
import com.example.chooseyouroutfit.data.repository.ClothesRepository
import com.example.chooseyouroutfit.model.ClothesCategoryType
import com.example.chooseyouroutfit.model.SeasonType
import com.example.chooseyouroutfit.ui.components.ReusableActionButton
import com.example.chooseyouroutfit.ui.components.ReusableBackgroundWardrobe
import com.example.chooseyouroutfit.ui.components.ReusableDropdownMenu
import com.example.chooseyouroutfit.ui.components.ReusableReturnArrow
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
        var showResults by remember { mutableStateOf(false) }
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
                showResults = true
            }
        }

        LazyColumn(
            modifier = Modifier.padding(top = 70.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                ReusableTextField(
                    value = selectedName,
                    onValueChange = { selectedName = it },
                    placeholder = stringResource(R.string.name)
                )
            }

            item {
                ReusableTextField(
                    value = selectedColor,
                    onValueChange = { selectedColor = it },
                    placeholder = stringResource(R.string.color)
                )
            }

            item {
                ReusableTextField(
                    value = selectedMaterial,
                    onValueChange = { selectedMaterial = it },
                    placeholder = stringResource(R.string.material)
                )
            }

            item {
                CategoryDropDownMenu(categorySelection = { category ->
                    selectedCategory = category
                })
            }

            item {
                SeasonDropdownMenu(seasonSelection = { season ->
                    selectedSeason = season
                })
            }

            item {
                ReusableActionButton(
                    text = stringResource(R.string.search),
                    onClick = {
                        loadFilteredClothes()
                    },
                    isEnabled = true
                )
            }

            if (showResults) {
                items(filteredClothes) { clothes ->
                    ClothesCard(clothes, loadFilteredClothes = { loadFilteredClothes() })
                }
            }
        }
    }

    @Composable
    fun ClothesCard(clothes: Clothes, loadFilteredClothes: () -> Unit) {
        val coroutineScope = rememberCoroutineScope()

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = rememberImagePainter(clothes.uri),
                        contentDescription = "Clothes Image",
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = "${stringResource(R.string.name)} ${clothes.name}\"")
                        Text(text = "${stringResource(R.string.color)} ${clothes.color}\"")
                        Text(text = "${stringResource(R.string.material)} ${clothes.material}\"")
                        Text(text = "${stringResource(R.string.season)} ${clothes.season}\"")
                        Text(text = "${stringResource(R.string.category)} ${clothes.category}\"")
                    }
                }

                // Ikona kosza na śmieci w prawym dolnym rogu
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            CODR.deleteClothes(clothes) // Usuwanie ubrania
                            loadFilteredClothes() // Ponowne załadowanie filtrowanych ubrań
                        }
                    },
                    modifier = Modifier.align(Alignment.BottomEnd) // Pozycjonowanie w prawym dolnym rogu
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete, // Ikona kosza na śmieci
                        contentDescription = "Delete Clothes",
                        tint = Color.Gray // Kolor ikony na czerwono
                    )
                }
            }
        }
    }


    @Composable
    fun SeasonDropdownMenu(seasonSelection: (String) -> Unit) {
        val options = SeasonType.getSeasonNames()
        var selectedSeason by remember { mutableStateOf(options[0]) }

        ReusableDropdownMenu(
            label = stringResource(R.string.season),
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
            label = stringResource(R.string.category),
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
        MainWardrobeView()
    }
}