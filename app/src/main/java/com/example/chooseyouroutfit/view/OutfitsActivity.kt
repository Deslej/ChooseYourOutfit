package com.example.chooseyouroutfit.view

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chooseyouroutfit.R
import com.example.chooseyouroutfit.data.repository.OutfitRepository
import com.example.chooseyouroutfit.ui.components.ReusableActionButton
import com.example.chooseyouroutfit.ui.components.ReusableBackgroundWardrobe
import com.example.chooseyouroutfit.ui.components.ReusableTextField
import com.example.chooseyouroutfit.ui.theme.ChooseYourOutfitTheme
import org.koin.android.ext.android.inject

class OutfitsActivity : ComponentActivity() {


    // TODO - zaimplemntowac odpowiednia metode w zaleznosci od filtrow
    private val OODR by inject<OutfitRepository>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChooseYourOutfitTheme {
                MainOutfitsView()
            }
        }
    }

    @Composable
    fun MainOutfitsView() {
        ReusableBackgroundWardrobe()
        FilterForm()
    }

    @Composable
    fun FilterForm() {
        var selectedName by remember { mutableStateOf("") }

        // TODO - jakies takie listy ktore bedzie potem mozna ladowac do ReusableImageGrid
//        var filteredClothes by remember { mutableStateOf<List<Clothes>>(emptyList()) }
//        var filteredOutfits by remember { mutableStateOf<List<Outfits>>(emptyList()) }


        fun loadFilteredOutfits() {
            // TODO - metoda do ladowania
        }


        Column(
            modifier = Modifier
                .padding(top = 70.dp, start = 16.dp, end = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // TODO - mozna jakies inne pola dodac do filtrowania
            ReusableTextField(
                value = selectedName,
                onValueChange = { selectedName = it },
                placeholder = "Name"
            )

            ReusableActionButton(
                text = stringResource(R.string.search),
                onClick = {
                    loadFilteredOutfits()
                },
                isEnabled = true
            )


            // TODO - tutaj lista do ladowania outfitow
//            ReusableImageGrid(lista)

        }
    }

    @Preview(showBackground = true)
    @Composable
    fun PreviewMainBackground() {
        MainOutfitsView()
    }
}