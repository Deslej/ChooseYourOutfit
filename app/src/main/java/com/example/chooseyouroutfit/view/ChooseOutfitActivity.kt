package com.example.chooseyouroutfit.view

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberAsyncImagePainter
import com.example.chooseyouroutfit.R
import com.example.chooseyouroutfit.data.repository.ClothesRepository
import com.example.chooseyouroutfit.model.ClothesCategoryType
import com.example.chooseyouroutfit.ui.theme.ChooseYourOutfitTheme
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class ChooseOutfitActivity : ComponentActivity() {
    private var imageShirtUris = mutableStateListOf<Uri>()
    private var imagePantsUris = mutableStateListOf<Uri>()
    private var imageTopUris = mutableStateListOf<Uri>()
    private var currentImageShirt = mutableStateOf<Uri?>(null)
    private var currentImageTrousers = mutableStateOf<Uri?>(null)
    private val CODR by inject<ClothesRepository>()
    private var rightShoulder :PoseLandmark? = null
    private var leftShoulder :PoseLandmark? = null
    private var leftHip :PoseLandmark? = null
    private var rightHip :PoseLandmark? = null
    var bitmap: Bitmap? = null
    private lateinit var processedImage: MutableState<Bitmap>

    override fun onCreate(savedInstanceState: Bundle?) {
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.manekin)
        bitmap?.let {processedImage = mutableStateOf(it) }
        super.onCreate(savedInstanceState)
        loadImagesFromDatabase()
        posedetection()
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
                Image(
                    painter = BitmapPainter(processedImage.value.asImageBitmap()),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Column(Modifier.weight(3.5f).background(Color.Green)) {
                ShowImages(imageShirtUris, currentImageShirt)
                Spacer(Modifier.height(100.dp))
                ShowImages(imagePantsUris, currentImageTrousers)

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
                    ClothesCategoryType.SHIRT.displayName -> imageShirtUris.add(clothesObject.uri)
                    ClothesCategoryType.PANTS.displayName -> imagePantsUris.add(clothesObject.uri)
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
                        if (currentImage.value == uri){
                            currentImage.value = null
                            bitmap?.let {processedImage.value = it }}
                        else {
                            currentImage.value = uri
                            bitmap?.let {processedImage.value = it }
//                            drawShirtOnMannequin()
                        }
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
    fun posedetection(){
        val options = AccuratePoseDetectorOptions.Builder()
            .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
            .build()
        val poseDetector = PoseDetection.getClient(options)
        val image: InputImage
        try {
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.manekin)
            image = InputImage.fromBitmap(bitmap, 0)

            poseDetector.process(image).addOnSuccessListener {pose->
                leftShoulder=pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
                rightShoulder= pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
                leftHip= pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
                rightHip= pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
            }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
//    Do nakładania ale nie działa
//    fun drawShirtOnMannequin() {
//        // Sprawdzenie wymaganych danych
//        if (currentImageShirt.value == null || leftShoulder == null || rightShoulder == null || leftHip == null || rightHip == null) {
//            // Jeśli brakuje danych, nic nie rób
//            return
//        }
//
//        // Wczytanie obrazu koszulki
//        val shirtBitmap: Bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(currentImageShirt.value!!))
//
//        // Pobranie pozycji landmarków
//        val leftShoulderPos = leftShoulder!!.position
//        val rightShoulderPos = rightShoulder!!.position
//        val leftHipPos = leftHip!!.position
//        val rightHipPos = rightHip!!.position
//
//
//        // Tworzenie nowej bitmapy na podstawie istniejącego obrazu manekina
//        val mannequinBitmap = processedImage.value.copy(Bitmap.Config.ARGB_8888, true)
//
//        // Przygotowanie canvasu do rysowania
//        val canvas = Canvas(mannequinBitmap)
//        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
//
//        // Punkty źródłowe (koszulka)
//        val srcPoints = floatArrayOf(
//            leftShoulderPos.x, leftShoulderPos.y,  // Górny lewy róg
//            rightShoulderPos.x, rightShoulderPos.y,  // Górny prawy róg
//            leftHipPos.x, leftHipPos.y,  // Dolny lewy róg
//            rightHipPos.x, rightHipPos.y  // Dolny prawy róg
//        )
//
//        // Punkty docelowe na manekinie (dopasowanie do pozycji)
//        val destPoints = floatArrayOf(
//            leftShoulderPos.x-225 , leftShoulderPos.y-30,  // Górny lewy róg
//            rightShoulderPos.x-225 , rightShoulderPos.y-30,  // Górny prawy róg
//            leftHipPos.x-225 , leftHipPos.y-30,  // Dolny lewy róg
//            rightHipPos.x-225 , rightHipPos.y-30  // Dolny prawy róg
//        )
//
//        // Ustawienie macierzy transformacji
//        val matrix = Matrix()
//        matrix.setPolyToPoly(srcPoints, 0, destPoints, 0, 4)
//
//        // Rysowanie koszulki na manekinie
//        canvas.drawBitmap(shirtBitmap, matrix, paint)
//
//        // Aktualizacja przetworzonego obrazu
//        processedImage.value = mannequinBitmap
//    }



}