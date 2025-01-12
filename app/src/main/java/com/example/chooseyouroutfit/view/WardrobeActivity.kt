package com.example.chooseyouroutfit.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
                    name = if (selectedName.isNotEmpty()) "%${selectedName.uppercase().trim()}%" else "%",
                    color = if (selectedColor.isNotEmpty()) "%${selectedColor.uppercase().trim()}%" else "%",
                    material = if (selectedMaterial.isNotEmpty()) "%${selectedMaterial.uppercase().trim()}%" else "%",
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
                    placeholder = "Name"
                )
            }

            item {
                ReusableTextField(
                    value = selectedColor,
                    onValueChange = { selectedColor = it },
                    placeholder = "Color"
                )
            }

            item {
                ReusableTextField(
                    value = selectedMaterial,
                    onValueChange = { selectedMaterial = it },
                    placeholder = "Material"
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
                        Text(text = "Name: ${clothes.name}")
                        Text(text = "Color: ${clothes.color}")
                        Text(text = "Material: ${clothes.material}")
                        Text(text = "Season: ${clothes.season}")
                        Text(text = "Category: ${clothes.category}")
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

    @Preview(showBackground = true)
    @Composable
    fun PreviewMainBackground() {
        MainWardrobeView()
    }
}