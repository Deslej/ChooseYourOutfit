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
import com.example.chooseyouroutfit.data.entities.Image
import com.example.chooseyouroutfit.data.repository.ImageRepository
import com.example.chooseyouroutfit.ui.theme.ChooseYourOutfitTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class ChooseOutfitActivity : ComponentActivity() {
    private var imageShirtUris = mutableStateListOf<Uri>()
    private var imageTrousersUris = mutableStateListOf<Uri>()
    private var currentImageShirt = mutableStateOf<Uri?>(null)
    private var currentImageTrousers = mutableStateOf<Uri?>(null)
    private val IODR by inject<ImageRepository>()
    var im = mutableListOf<Image>()


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
            // Fetch images from the database
            im = IODR.getAllImages().toMutableList()

            // Here you would fill in the URIs for shirts and trousers
            // Assuming `im` contains objects with the URI for the shirt and trousers
            imageShirtUris.clear()
            imageTrousersUris.clear()

//            im.forEach { imageObject ->
//                // TODO: to fix after new db setup
//                // Assuming `imageObject` has a field `TypeClothe` to distinguish between shirts and trousers
//                imageObject.uri?.let { uri -> // Use let to safely unwrap the nullable Uri
//                    if (imageObject.TypeClothe == TypeClothe.SHIRT.typeClothe) {
//                        imageShirtUris.add(uri) // Only add if uri is not null
//                    } else {
//                        imageTrousersUris.add(uri) // Only add if uri is not null
//                    }
//                }
//            }
        }
    }

    private fun getImages(location: String, mutableList: MutableList<Uri>) {
        lifecycleScope.launch(Dispatchers.IO) {
//            val path = location
//
//            val selection = MediaStore.Files.FileColumns.RELATIVE_PATH + " like ? "
//            val selectionArgs = arrayOf("%$path%")
//
//            val externalUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//            val projection = arrayOf(
//                MediaStore.Files.FileColumns._ID,
//                MediaStore.Images.Media.DATE_TAKEN,
//                MediaStore.MediaColumns.TITLE,
//                MediaStore.Images.Media.MIME_TYPE,
//                MediaStore.MediaColumns.RELATIVE_PATH
//            )
//            val cursor = contentResolver.query(externalUri, projection, selection, selectionArgs, MediaStore.Images.Media.DATE_TAKEN)
//
//            cursor?.use {
//                val idColumn = it.getColumnIndex(MediaStore.MediaColumns._ID)
//                while (it.moveToNext()) {
//                    val photoUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, it.getString(idColumn))
//                    mutableList.add(photoUri)
//                }
//            }
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
                modifier = Modifier.size(35.dp),
                contentDescription = "Strzałka powrotu"
            )
        }
    }
}



