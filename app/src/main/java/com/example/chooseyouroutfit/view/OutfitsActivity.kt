package com.example.chooseyouroutfit.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.chooseyouroutfit.data.entities.OutfitWithItems
import com.example.chooseyouroutfit.data.repository.OutfitRepository
import com.example.chooseyouroutfit.ui.components.ReusableActionButton
import com.example.chooseyouroutfit.ui.components.ReusableBackgroundWardrobe
import com.example.chooseyouroutfit.ui.components.ReusableTextField
import com.example.chooseyouroutfit.ui.theme.ChooseYourOutfitTheme
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class OutfitsActivity : ComponentActivity() {

    private val outfitRepository by inject<OutfitRepository>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChooseYourOutfitTheme {
                MainOutfitsView()
                ReturnToMain()
            }
        }
    }

    @Composable
    fun MainOutfitsView() {
        ReusableBackgroundWardrobe()
        FilterAndDisplayOutfits()
    }

    @Composable
    fun OutfitList(outfits: List<OutfitWithItems>, onOutfitDeleted: (OutfitWithItems) -> Unit) {
        if (outfits.isEmpty()) {
            Text(
                text = "Nie znaleziono outfitów.",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
            ) {
                items(outfits) { outfitWithItems ->
                    OutfitCard(outfitWithItems, onOutfitDeleted = onOutfitDeleted)
                }
            }
        }
    }

    @Composable
    fun FilterAndDisplayOutfits() {
        var searchQuery by remember { mutableStateOf("") }
        var outfits by remember { mutableStateOf<List<OutfitWithItems>>(emptyList()) }
        val coroutineScope = rememberCoroutineScope()

        // Funkcja do usuwania outfitów z listy
        val onOutfitDeleted: (OutfitWithItems) -> Unit = { deletedOutfit ->
            outfits = outfits.filter { it.outfit.outfitId != deletedOutfit.outfit.outfitId }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, top = 60.dp ,end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Pole do wyszukiwania
            ReusableTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = "Szukaj po nazwie"
            )

            // Przycisk wyszukiwania
            ReusableActionButton(
                text = "Szukaj",
                onClick = {
                    coroutineScope.launch {
                        outfits =if (searchQuery.isBlank()) {
                            outfitRepository.getAllOutfits() // Pobierz wszystkie outfity, jeśli pole jest puste
                        } else {
                            outfitRepository.getOutfitsByName(searchQuery)
                        }
                    }
                },
                isEnabled = true
            )

            // Lista wyników
            OutfitList(outfits = outfits, onOutfitDeleted = onOutfitDeleted)
        }
    }


    @Composable
    fun OutfitCard(outfitWithItems: OutfitWithItems, onOutfitDeleted: (OutfitWithItems) -> Unit) {
        var showDialog by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()

        // Zmienna imageBitmap powinna być tworzona raz, aby nie była tworzona ponownie przy każdym wywołaniu
        val imageBitmap = remember(outfitWithItems.outfit.image) {
            outfitWithItems.outfit.image?.let { byteArray ->
                BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)?.asImageBitmap()
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Wyświetlenie obrazu manekina
                    if (imageBitmap != null) {
                        Image(
                            bitmap = imageBitmap,
                            contentDescription = "manekin",
                            modifier = Modifier
                                .size(80.dp)
                                .padding(end = 16.dp)
                                .clickable {
                                    showDialog = true
                                } // Kliknięcie na obrazek powoduje wyświetlenie powiększonego obrazu
                        )
                    } else {
                        // Placeholder w przypadku braku obrazu
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .padding(end = 16.dp)
                                .background(Color.Gray)
                        ) {
                            Text(
                                text = "brak zdjęcia",
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Nazwa Outfitu: ${outfitWithItems.outfit.name}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Ubrania:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        outfitWithItems.clothes.forEach { clothes ->
                            Text(
                                text = "- ${clothes.name}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Spacer wypycha przycisk na prawo
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            // Usuwanie outfitu z bazy danych
                            outfitRepository.deleteOutfit(outfitWithItems.outfit)
                            // Po usunięciu, wywołujemy callback, aby zaktualizować UI
                            onOutfitDeleted(outfitWithItems)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd) // Przyciski ustawiamy w prawym dolnym rogu
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Outfit",
                        tint = Color.Gray
                    )
                }
            }
        }

        // Dialog wyświetlający powiększony obrazek i informacje o przedmiotach
        if (showDialog && imageBitmap != null) {
            Dialog(onDismissRequest = { showDialog = false }) {
                Surface(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(16.dp), // Zmniejszenie dialogu przez padding
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp) // Zaokrąglone rogi
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp) // Odstęp wewnętrzny w oknie dialogowym
                    ) {
                        // Obrazek manekina
                        Image(
                            bitmap = imageBitmap,
                            contentDescription = "manekin",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp) // Zwiększenie wysokości obrazu
                                .clip(RoundedCornerShape(16.dp)) // Zaokrąglone rogi dla obrazu
                        )

                        Spacer(modifier = Modifier.height(16.dp)) // Odstęp między obrazem a tekstem

                        // Informacje o przedmiotach
                        Text(
                            text = "Ubrania na manekinie:",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        outfitWithItems.clothes.chunked(2).forEach { rowClothes ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp) // Odstępy między elementami w wierszu
                            ) {
                                rowClothes.forEach { clothes ->
                                    Column(
                                        modifier = Modifier
                                            .weight(1f) // Równa szerokość dla obu elementów w wierszu
                                            .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp))
                                            .padding(8.dp)
                                    ) {
                                        Text(
                                            text = "Nazwa: ${clothes.name}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "Sezon: ${clothes.season}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "Materiał: ${clothes.material}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "Kolor: ${clothes.color}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }


                        // Zamknij przycisk
                        IconButton(
                            onClick = { showDialog = false },
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(top = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Close",
                                tint = Color.Black
                            )
                        }
                    }
                }
            }
        }

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
}