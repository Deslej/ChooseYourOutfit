package com.example.chooseyouroutfit.view

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.chooseyouroutfit.data.entities.Category
import com.example.chooseyouroutfit.data.repository.CategoryRepository
import com.example.chooseyouroutfit.data.repository.ClothesRepository
import com.example.chooseyouroutfit.ui.theme.ChooseYourOutfitTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class ChooseOutfitActivity : ComponentActivity() {

    private var imageShirtUris = mutableStateListOf<Uri>()
    private var imageTrousersUris = mutableStateListOf<Uri>()
    private var currentImageShirt = mutableStateOf<Uri?>(null)
    private var currentImageTrousers = mutableStateOf<Uri?>(null)
    private val CODR by inject<ClothesRepository>()
    private val CAODR by inject<CategoryRepository>()

    override fun onCreate(savedInstanceState: Bundle?) {
        val categoryObject = Category(name = "Trousers")
        val categoryObject2 = Category(name = "Shirt")
        lifecycleScope.launch {
            CAODR.insert(categoryObject)
            CAODR.insert(categoryObject2)
        }
        super.onCreate(savedInstanceState)
        loadImagesFromDatabase()
        setContent {
            ChooseYourOutfitTheme {
                MainView()
            }
        }
    }

    private fun loadImagesFromDatabase() {
        lifecycleScope.launch(Dispatchers.IO) {
            val clothes = CODR.getAllClothes()

            clothes.forEach { clothesObject ->
                val category = CAODR.getCategoryById(clothesObject.categoryId)
                if (category.name == "Shirt") {
                    imageShirtUris.add(clothesObject.uri)
                } else if (category.name == "Trousers") {
                    imageTrousersUris.add(clothesObject.uri)
                }
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

    @Composable
    fun PutClothesOnCharacter(currentImage: MutableState<Uri?>) {
        if (currentImage.value != null) {
            Image(
                painter = rememberAsyncImagePainter(currentImage.value),
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.FillBounds
            )
        }
    }

    @Composable
    fun ShowImages(Uris: List<Uri>, currentImage: MutableState<Uri?>) {
        val context = LocalContext.current
        LazyRow {
            items(Uris) { uri ->
                IconButton(
                    onClick = {
                        lifecycleScope.launch {
                            // Uruchamiamy model i przetwarzamy obraz
                            val processedBitmap = processImageWithModel(uri)

                            // Konwertujemy przetworzony bitmap do Uri
                            val processedUri = processedBitmap.toUri(context)

                            // Ustawiamy Uri jako aktualny obraz
                            currentImage.value = processedUri
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
                                    Modifier.border(4.dp, Color.Black, shape = CircleShape) else Modifier
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
            colors = CardDefaults.cardColors(containerColor = Color.Transparent, contentColor = Color.Black)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                modifier = Modifier.size(35.dp),
                contentDescription = "Strzałka powrotu"
            )
        }
    }

    fun Bitmap.toUri(context: Context): Uri? {
        return try {
            val file = File(context.cacheDir, "processed_image_${System.currentTimeMillis()}.png")
            file.outputStream().use { outputStream ->
                this.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
            Uri.fromFile(file)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun resizeBitmapTo640x640(bitmap: Bitmap): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, 640, 640, true)
    }

    private fun loadModelFile(): ByteBuffer {
        val assetManager = assets
        val fileDescriptor = assetManager.openFd("last_float32.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private suspend fun processImageWithModel(uri: Uri): Bitmap {
        // Używamy trybu IO do wczytania obrazu w tle
        val context = this@ChooseOutfitActivity
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream) ?: return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        inputStream?.close()

        // Skalowanie obrazu do rozmiaru modelu
        val resizedBitmap = resizeBitmapTo640x640(bitmap)

        // Używamy interpreter TFLite w tle
        val interpreter = Interpreter(loadModelFile(), Interpreter.Options())

        // Konwersja obrazu na ByteBuffer
        val inputBuffer = convertBitmapToByteBuffer(resizedBitmap)

        // Przygotowanie bufora na dane wyjściowe modelu
        val outputBuffer = ByteBuffer.allocateDirect(640 * 640 * 3 * 4)
        outputBuffer.order(ByteOrder.nativeOrder())

        // Uruchamiamy model
        interpreter.run(inputBuffer, outputBuffer)
        interpreter.close()

        // Przekształcanie wyników modelu w obraz
        outputBuffer.rewind()
        Log.d("ModelOutput", "Przykładowe wartości wyjściowe modelu:")
        for (i in 0 until 10) {
            Log.d("ModelOutput", "Wartość $i: ${outputBuffer.float}")
        }

        return convertByteBufferToBitmap(outputBuffer, 640, 640)
    }


    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(640 * 640 * 3 * 4)
        byteBuffer.order(ByteOrder.nativeOrder())
        val pixels = IntArray(640 * 640)
        bitmap.getPixels(pixels, 0, 640, 0, 0, 640, 640)  // Pobieramy piksele obrazu

        // Iteracja po pikselach i konwersja do wartości RGB
        for (pixel in pixels) {
            val r = ((pixel shr 16) and 0xFF) / 255.0f  // Rozdzielanie czerwonego kanału
            val g = ((pixel shr 8) and 0xFF) / 255.0f   // Rozdzielanie zielonego kanału
            val b = (pixel and 0xFF) / 255.0f           // Rozdzielanie niebieskiego kanału

            // Dodajemy te wartości do bufora jako dane float
            byteBuffer.putFloat(r)
            byteBuffer.putFloat(g)
            byteBuffer.putFloat(b)
        }

        byteBuffer.rewind()  // Przywracamy wskaźnik bufora na początek
        return byteBuffer
    }



    private fun convertByteBufferToBitmap(byteBuffer: ByteBuffer, width: Int, height: Int): Bitmap {
        val pixels = IntArray(width * height)
        byteBuffer.rewind()

        for (i in pixels.indices) {
            if (byteBuffer.remaining() >= 3 * 4) {
                val r = ((byteBuffer.float * 255) + 50).toInt().coerceIn(0, 255)
                val g = ((byteBuffer.float * 255) + 50).toInt().coerceIn(0, 255)
                val b = ((byteBuffer.float * 255) + 50).toInt().coerceIn(0, 255)

                pixels[i] = (0xFF shl 24) or (r shl 16) or (g shl 8) or b
            }
        }

        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
    }
}
