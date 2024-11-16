package com.example.chooseyouroutfit.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberAsyncImagePainter
import com.example.chooseyouroutfit.data.repository.ClothesRepository
import com.example.chooseyouroutfit.model.ClothesCategoryType
import com.example.chooseyouroutfit.ui.theme.ChooseYourOutfitTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class ChooseOutfitActivity : ComponentActivity() {
    private var imageShirtUris = mutableStateListOf<Uri>()
    private var imageTrousersUris = mutableStateListOf<Uri>()
    private var currentImageShirt = mutableStateOf<Uri?>(null)
    private var currentImageTrousers = mutableStateOf<Uri?>(null)
    private val CODR by inject<ClothesRepository>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadImagesFromDatabase()
        setContent {
            ChooseYourOutfitTheme {
                MainView()
            }
        }
    }

    @Composable
    fun MainView() {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 70.dp, end = 10.dp)
        ) {
            Column(Modifier.weight(6.5f)) {
                PutClothesOnCharacter(currentImageShirt)
                Spacer(Modifier.height(100.dp))
                PutClothesOnCharacter(currentImageTrousers)

            }
            Column(Modifier.weight(3.5f)) {
                ShowImages(imageShirtUris, currentImageShirt)
                Spacer(Modifier.height(100.dp))
                ShowImages(imageTrousersUris, currentImageTrousers)

            }
        }
        ReturnToMain()

    }

    private fun loadImagesFromDatabase() {
        lifecycleScope.launch(Dispatchers.IO) {
            val clothes = CODR.getAllClothes()

            clothes.forEach{ clothesObject ->
                val category = clothesObject.category
                when (category) {
                    ClothesCategoryType.SHIRT.toString() -> imageShirtUris.add(clothesObject.uri)
                    // TODO - inne przypadki?
                    else -> {
                        // TODO - default zachowanie
                    }
                }
            }
        }
    }

    @Composable
    fun PutClothesOnCharacter(currentImage: MutableState<Uri?>) {
        if (currentImage.value != null) {
            Image(
                painter = rememberAsyncImagePainter(currentImage.value),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp),
                contentScale = ContentScale.FillBounds
            )
        }
    }

    @Composable
    fun ShowImages(Uris: List<Uri>, currentImage: MutableState<Uri?>) {
        LazyRow() {
            items(Uris) { uri ->
                IconButton(
                    onClick = {
                        if (currentImage.value == uri)
                            currentImage.value = null
                        else currentImage.value = uri
                    },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent),
                    modifier = Modifier
                        .width(80.dp)
                        .height(80.dp)
                        .padding(5.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .then(
                                if (currentImage.value == uri)
                                    Modifier.border(
                                        4.dp,
                                        Color.Black,
                                        shape = CircleShape
                                    ) else Modifier
                            ),
                        contentScale = ContentScale.FillBounds
                    )
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
                },
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent,
                contentColor = Color.Black
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