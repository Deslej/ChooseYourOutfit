package com.example.chooseyouroutfit.view

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberAsyncImagePainter
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.example.chooseyouroutfit.R
import com.example.chooseyouroutfit.data.entities.Outfit
import com.example.chooseyouroutfit.data.entities.OutfitItem
import com.example.chooseyouroutfit.data.repository.ClothesRepository
import com.example.chooseyouroutfit.model.ClothesCategoryType
import com.example.chooseyouroutfit.ui.theme.ChooseYourOutfitTheme
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class ChooseOutfitActivity : ComponentActivity() {
    private var imageShirtUris = mutableStateListOf<Uri>()
    private var imageShortsUris = mutableStateListOf<Uri>()
    private var imageBlouseUris = mutableStateListOf<Uri>()
    private var imagePantsUris = mutableStateListOf<Uri>()
    private var currentImageTop = mutableStateOf<Uri?>(null)
    private var currentImageBottom = mutableStateOf<Uri?>(null)
    private val CODR by inject<ClothesRepository>()
    private var rightShoulderx :Float = 0f
    private var rightShouldery :Float = 0f
    private var leftShoulderx :Float = 0f
    private var leftShouldery :Float = 0f
    private var leftHipx :Float = 0f
    private var leftHipy :Float = 0f
    private var rightHipx :Float = 0f
    private var rightHipy :Float = 0f
    private var leftkneex :Float = 0f
    private var leftkneey :Float = 0f
    private var rightkneex :Float = 0f
    private var rightkneey :Float = 0f
    var tableWithPoints by mutableStateOf<PyObject?>(null)

    var bitmap: Bitmap? = null
    private lateinit var processedImage: MutableState<Bitmap>
    private val mapOfChoosenBitmapClothes: MutableMap<Bitmap, Int> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.manekin2)
        bitmap?.let {
            processedImage = mutableStateOf(it)
        }

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
        Image(
            painter = painterResource(R.drawable.fittingbackground),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize(),
            )
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
                    contentScale = ContentScale.FillHeight
                )
            }
            Column(Modifier.weight(3.5f)) {
                ShowImages(imageShirtUris, currentImageTop,1,"Shirt")
                ShowImages(imageShortsUris, currentImageBottom,2,"Shorts")
                ShowImages(imageBlouseUris, currentImageTop,1,"Blouse")
                ShowImages(imagePantsUris, currentImageBottom,3,"Pants")
            }
        }
        SaveOutfitButton()
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
                    ClothesCategoryType.BLOUSE.displayName -> imageBlouseUris.add(clothesObject.uri)
                    ClothesCategoryType.SHORTS.displayName -> imageShortsUris.add(clothesObject.uri)
                    else -> {
                        // TODO - default zachowanie
                    }
                }
            }
        }
    }

    @Composable
    fun ShowImages(
        uris: List<Uri>,
        currentImage: MutableState<Uri?>,
        index: Int,
        name: String
    ) {
        Box(
            modifier = Modifier
                .height(175.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.closet),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

            Text(
                text=name,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.TopCenter)
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 36.dp, start = 24.dp, end = 16.dp)
            ) {
                items(uris) { uri ->
                    IconButton(
                        onClick = {
                            if (currentImage.value == uri) {
                                currentImage.value = null
                                deleteLastBitmapClotheWithIndex(index)
                                drawClotheOnImage()
                            } else {
                                deleteLastBitmapClotheWithIndex(index)
                                currentImage.value = uri
                                modelUse(context = this@ChooseOutfitActivity, uri,index)
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent),
                        modifier = Modifier
                            .size(120.dp)
                            .padding(end = 20.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxHeight()
                                .then(
                                    if (currentImage.value == uri) {
                                        Modifier.border(4.dp, Color.Black, shape = CircleShape)
                                    } else Modifier
                                )
                        )
                    }
                }
            }
        }
    }

    private fun deleteLastBitmapClotheWithIndex(index: Int) {
        var bitmap :Bitmap? = null
        var lastValue = 0
        for ((k,v) in mapOfChoosenBitmapClothes){
            if (v == index){
                bitmap = k
            }else if(index != 1){
                if((v > lastValue) and (v != 1)){
                    lastValue = v
                    bitmap = k
                }
            }
        }
        bitmap?.let {
            mapOfChoosenBitmapClothes.remove(it)
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
    fun posedetection() {
        val options = AccuratePoseDetectorOptions.Builder()
            .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
            .build()
        val poseDetector = PoseDetection.getClient(options)
        val image: InputImage

        try {
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.manekin2)
            image = InputImage.fromBitmap(bitmap, 0)

            poseDetector.process(image).addOnSuccessListener { pose ->

                 val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
                 val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
                 val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
                 val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
                 val rightknee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
                 val leftknee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)

                leftShoulder?.let {
                    leftShoulderx = it.position.x
                    leftShouldery = it.position.y
                }
                rightShoulder?.let {
                    rightShoulderx = it.position.x
                    rightShouldery = it.position.y
                }
                leftHip?.let {
                    leftHipx = it.position.x
                    leftHipy = it.position.y
                }
                rightHip?.let {
                    rightHipx = it.position.x
                    rightHipy = it.position.y
                }
                leftknee?.let {
                    leftkneex = it.position.x
                    leftkneey = it.position.y
                }
                rightknee?.let {
                    rightkneex = it.position.x
                    rightkneey = it.position.y
                }

            }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun modelUse(context: Context,uri: Uri,index: Int){
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {

                val modelPath = copyAssetToInternalStorage("KeyPointsModel.tflite", context)
                val py = Python.getInstance()
                val segmenter = py.getModule("getPoints")
                val realPath = getRealPathFromURI(uri, context)
                tableWithPoints = segmenter.callAttr("getClothePoints", realPath, modelPath)

                applyClothingToMannequin(uri,index)
            }
        }
    }
    fun copyAssetToInternalStorage(assetFileName: String, context: Context): String {
        val file = File(context.filesDir, assetFileName)
        if (!file.exists()) {
            context.assets.open(assetFileName).use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
        return file.absolutePath
    }

    fun getRealPathFromURI(contentUri: Uri, context: Context): String? {
        val cursor = context.contentResolver.query(contentUri, null, null, null, null)
        return if (cursor != null) {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            val path = if (idx != -1) cursor.getString(idx) else null
            cursor.close()
            path
        } else {
            null
        }
    }
    private fun applyClothingToMannequin(uri :Uri,index: Int) {

        val outerList = tableWithPoints?.asList()

        val clothingPoints2 = outerList?.map { point ->
            point.asList().map { it.toDouble() }
        }

        val flattenedPoints = clothingPoints2?.flatMap { it.map { coord -> coord.toFloat() } }?.toFloatArray()

        val clothingPoints = flattenedPoints ?: floatArrayOf()
        var mannequinPoints = floatArrayOf()
        if (uri==currentImageTop.value){
            mannequinPoints = floatArrayOf(
            leftShoulderx-20, leftShouldery-20,
            rightShoulderx+25, rightShouldery-25,
            leftHipx-10, leftHipy+5,
            rightHipx+15, rightHipy
        )
        }else if((uri == currentImageBottom.value)and (index==2)){
            mannequinPoints = floatArrayOf(
                leftkneex, leftkneey-90,
                rightkneex, rightkneey-90,
                leftHipx-20, leftHipy+45,
                rightHipx+20, rightHipy+45
                )
        }else if((uri == currentImageBottom.value) and (index == 3)){
            mannequinPoints = floatArrayOf(
                leftkneex+10, leftkneey+190,
                rightkneex, rightkneey+160,
                leftHipx-20, leftHipy,
                rightHipx+20, rightHipy
            )
        }

        val clothingBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))

        val transformedBitmap = transformClothingImage(clothingBitmap, clothingPoints, mannequinPoints)

        mapOfChoosenBitmapClothes.put(transformedBitmap,index)

        drawClotheOnImage()
    }

    private fun drawClotheOnImage(){
        bitmap?.let {
            val mannequinBitmap = it.copy(Bitmap.Config.ARGB_8888, true)
            for ((k,v) in mapOfChoosenBitmapClothes)
            {
                val canvas = Canvas(mannequinBitmap)
                val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                canvas.drawBitmap(k, 0f, 0f, paint)
            }

            processedImage.value = mannequinBitmap
        }
    }
    private fun transformClothingImage(
        clothingBitmap: Bitmap,
        srcPoints: FloatArray,
        destPoints: FloatArray
    ): Bitmap {
        val matrix = Matrix()
        matrix.setPolyToPoly(srcPoints, 0, destPoints, 0, 4)

        val transformedBitmap = Bitmap.createBitmap(
            processedImage.value.width,
            processedImage.value.height,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(transformedBitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        canvas.drawBitmap(clothingBitmap, matrix, paint)

        return transformedBitmap
    }

    @Composable
    fun SaveOutfitButton(modifier: Modifier = Modifier) {
        var showDialog by remember { mutableStateOf(false) }
        var outfitName by remember { mutableStateOf("") }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    Button(onClick = {
                        if (outfitName.isNotEmpty()) {
                            saveOutfitToDatabase(outfitName)
                            showDialog = false
                        }
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                },
                text = {
                    Column {
                        Text("Enter Outfit Name:")
                        TextField(
                            value = outfitName,
                            onValueChange = { outfitName = it }
                        )
                    }
                }
            )
        }

        if (currentImageTop.value != null && currentImageBottom.value != null) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 10.dp, start = 70.dp)
            ){
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.align(Alignment.BottomStart)
                ) {
                    Text("Save Outfit")
                }
            }

        }
    }
    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }


    private fun saveOutfitToDatabase(name: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val topId = currentImageTop.value?.let { uri -> CODR.getClothesIdByUri(uri.toString()) }
            val bottomId = currentImageBottom.value?.let { uri -> CODR.getClothesIdByUri(uri.toString()) }

            // Sprawdź, czy bitmapa manekina jest dostępna
            val mannequinBitmap = processedImage.value
            if (topId != null && bottomId != null && mannequinBitmap != null) {
                // Konwersja bitmapy na ByteArray
                val outfitImage = bitmapToByteArray(mannequinBitmap)

                // Tworzenie nowego outfitu z obrazem
                val newOutfit = Outfit(name = name, image = outfitImage) // Dodaj pole image w modelu Outfit
                val outfitId = CODR.insertOutfit(newOutfit)

                val outfitItems = listOf(
                    OutfitItem(outfitId = outfitId, clothesId = topId),
                    OutfitItem(outfitId = outfitId, clothesId = bottomId)
                )

                CODR.insertOutfitItems(outfitItems)

                withContext(Dispatchers.Main) {
                    showToast("Outfit został zapisany!")
                }
            } else {
                withContext(Dispatchers.Main) {
                    showToast("Trzeba wybrać więcej niż 1 część outfitu!")
                }
            }
        }
    }
    fun Context.showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()}

}