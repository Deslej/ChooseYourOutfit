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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberAsyncImagePainter
import com.chaquo.python.PyObject
import com.chaquo.python.Python
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
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import java.io.File
import java.io.FileOutputStream
import android.graphics.Color as Cr

class ChooseOutfitActivity : ComponentActivity() {
    private var imageShirtUris = mutableStateListOf<Uri>()
    private var imagePantsUris = mutableStateListOf<Uri>()
    private var imageTopUris = mutableStateListOf<Uri>()
    private var currentImageShirt = mutableStateOf<Uri?>(null)
    private var currentImageTrousers = mutableStateOf<Uri?>(null)
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
    private val beforeProcessedImage: MutableMap<Bitmap, Int> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.manekin)
        bitmap?.let {
            processedImage = mutableStateOf(it)
            beforeProcessedImage[it] = 0
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
                ShowImages(imageShirtUris, currentImageShirt,1)
                Spacer(Modifier.height(100.dp))
                ShowImages(imagePantsUris, currentImageTrousers,2)

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
    fun ShowImages(Uris: List<Uri>, currentImage: MutableState<Uri?>,index :Int) {
        LazyRow() {
            items(Uris) { uri ->
                IconButton(
                    onClick = {
                        if (currentImage.value == uri){
                            currentImage.value = null
                            var bitmap :Bitmap? = null
                            for ((k,v) in beforeProcessedImage){
                                if (v == index){
                                    bitmap=k
                                }
                            }
                            bitmap?.let {
                                processedImage.value=it
                                beforeProcessedImage.remove(it)
                            }

                        }
                        else {
                            beforeProcessedImage.put(processedImage.value,index)
                            if (currentImage.value != null){
                                var bitmapD :Bitmap? = null
                                var bitmapG :Bitmap? = null
                                for ((k,v) in beforeProcessedImage){
                                    if (v == index){
                                        bitmapD=k
                                    }
                                }
                                bitmapD?.let {
                                    beforeProcessedImage.remove(it)
                                }
                                for ((k,v) in beforeProcessedImage){
                                    if( index == v) {
                                        bitmapG = k
                                    }
                                }
                                bitmapG?.let { processedImage.value=it }


                            }
                            currentImage.value = uri
                            beforeProcessedImage.put(processedImage.value,index)
                            modelUse(context = this@ChooseOutfitActivity,uri)
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
    fun posedetection() {
        val options = AccuratePoseDetectorOptions.Builder()
            .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
            .build()
        val poseDetector = PoseDetection.getClient(options)
        val image: InputImage

        try {
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.manekin)
            image = InputImage.fromBitmap(bitmap, 0)

            poseDetector.process(image).addOnSuccessListener { pose ->
                // Get landmarks
                 val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
                 val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
                 val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
                 val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
                 val rightknee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
                 val leftknee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)

                // Create a mutable bitmap to draw on it
                val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                val canvas = Canvas(mutableBitmap)
                val paint = Paint().apply {
                    color = Cr.RED  // Color for the landmarks
                    style = Paint.Style.FILL
                    strokeWidth = 5f
                }

                // Draw points for each landmark (checking if the landmark is not null)
                leftShoulder?.let {
                    leftShoulderx = it.position.x
                    leftShouldery = it.position.y
                    canvas.drawCircle(it.position.x, it.position.y, 10f, paint)
                }
                rightShoulder?.let {
                    rightShoulderx = it.position.x
                    rightShouldery = it.position.y
                    canvas.drawCircle(it.position.x, it.position.y, 10f, paint)
                }
                leftHip?.let {
                    leftHipx = it.position.x
                    leftHipy = it.position.y
                    canvas.drawCircle(it.position.x, it.position.y, 10f, paint)
                }
                rightHip?.let {
                    rightHipx = it.position.x
                    rightHipy = it.position.y
                    canvas.drawCircle(it.position.x, it.position.y, 10f, paint)
                }
                leftknee?.let {
                    leftkneex = it.position.x
                    leftkneey = it.position.y
                    canvas.drawCircle(it.position.x, it.position.y, 10f, paint)
                }
                rightknee?.let {
                    rightkneex = it.position.x
                    rightkneey = it.position.y
                    canvas.drawCircle(it.position.x, it.position.y, 10f, paint)
                }

                // Now 'mutableBitmap' contains the image with drawn landmarks
                // You can use 'mutableBitmap' for displaying in an ImageView or other purposes
                processedImage.value = mutableBitmap  // Example: showing the image in an ImageView
            }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun modelUse(context: Context,uri: Uri){
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {

                val modelPath = copyAssetToInternalStorage("KeyPointsModel.tflite", context)
                val py = Python.getInstance()
                val segmenter = py.getModule("getPoints")
                val realPath = getRealPathFromURI(uri, context)
                tableWithPoints = segmenter.callAttr("getClothePoints", realPath, modelPath)

                applyClothingToMannequin(uri)
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
    private fun applyClothingToMannequin(uri :Uri) {
        // Ensure landmarks and clothing points are available

        val outerList = tableWithPoints?.asList()

        val clothingPoints2 = outerList?.map { point ->
            point.asList().map { it.toDouble() } // Konwertuj każdy punkt do List<Double>
        }

        val flattenedPoints = clothingPoints2?.flatMap { it.map { coord -> coord.toFloat() } }?.toFloatArray()

        val clothingPoints = flattenedPoints ?: floatArrayOf()
        var mannequinPoints = floatArrayOf()
        if (uri==currentImageShirt.value){
            mannequinPoints = floatArrayOf(
            leftShoulderx-10, leftShouldery-25,
            rightShoulderx+10, rightShouldery-60,
            leftHipx+8, leftHipy,
            rightHipx-8, rightHipy
        )}else if(uri == currentImageTrousers.value){
            mannequinPoints = floatArrayOf(
                leftkneex, leftkneey-120,
                rightkneex, rightkneey-120,
                leftHipx-5, leftHipy+75,
                rightHipx-10, rightHipy+75
                )

        }


        val clothingBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))

        // Transform clothing image to fit the mannequin
        val transformedBitmap = transformClothingImage(clothingBitmap, clothingPoints, mannequinPoints)

        // Draw clothing on the mannequin
        val mannequinBitmap = processedImage.value.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mannequinBitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        canvas.drawBitmap(transformedBitmap, 0f, 0f, paint)

        // Update the processed image
        processedImage.value = mannequinBitmap
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
}