package com.example.chooseyouroutfit.view

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberAsyncImagePainter
import com.example.chooseyouroutfit.R
import com.example.chooseyouroutfit.data.entities.Clothes
import com.example.chooseyouroutfit.data.repository.ClothesRepository
import com.example.chooseyouroutfit.model.ClothesHolder
import com.example.chooseyouroutfit.ui.components.ReusableReturnArrow
import com.example.chooseyouroutfit.utils.getRealPathFromURI
import com.example.chooseyouroutfit.utils.runModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : ComponentActivity() {

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var previewView: androidx.camera.view.PreviewView
    private lateinit var clothesHolder: ClothesHolder
    private val CODR by inject<ClothesRepository>()
    var photoConfirmed by mutableStateOf(false)
    var photoAccepted by mutableStateOf(false)
    var lastPhotoUri by mutableStateOf<Uri?>(null)
    var isProcessing by mutableStateOf(false)
    var ableButtons by mutableStateOf(true)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            clothesHolder = intent?.getSerializableExtra("objectClothes") as ClothesHolder

            CameraApp { previewView ->
                this.previewView = previewView
                if (allPermissionsGranted()) {
                    startCamera()
                } else {
                    requestPermissions()
                }

            }
            if (photoConfirmed) {
                ableButtons = false
                AcceptPhoto(uri = lastPhotoUri)
                lastPhotoUri?.let { modelUse(this, it) }

            }
            if (photoAccepted) {
                addAcceptClothes()
            }

        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        // Create a timestamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        // Set up image capture listener, which is triggered after photo has been taken

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                    lifecycleScope.launch {
                        withContext(NonCancellable) {
                            output.savedUri?.let { uri ->
                                lastPhotoUri = uri
                                photoConfirmed = true
                            } ?: run {
                                Log.e(TAG, "Saved URI is null, cannot save to DB.")
                            }
                        }
                    }
                }
            }
        )
    }

    fun modelUse(context: Context, uri: Uri) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                isProcessing = true
                runModel(context, uri, "Model.tflite", "model", "cut_clothe_from_image")
                isProcessing = false
            }
        }
    }

    private fun getObject(uri: Uri, clothesHolder: ClothesHolder): Clothes {
        val clothesObject = Clothes(
            name = clothesHolder.name,
            category = clothesHolder.category,
            color = clothesHolder.color,
            season = clothesHolder.season,
            material = clothesHolder.material,
            uri = uri
        )
        return clothesObject
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PermissionChecker.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }


    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private val REQUIRED_PERMISSIONS =
            mutableListOf(Manifest.permission.CAMERA).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && it.value == false)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                val intent = Intent(this, AddClothesActivity::class.java)
                startActivity(intent)
                finish()
                Toast.makeText(baseContext, "Permission request denied", Toast.LENGTH_SHORT).show()
            } else {
                startCamera()
            }
        }

    @Composable
    fun CameraApp(onPreviewViewReady: (androidx.camera.view.PreviewView) -> Unit) {

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            var previewView: androidx.camera.view.PreviewView? by remember { mutableStateOf(null) }

            AndroidView(
                factory = { ctx ->
                    androidx.camera.view.PreviewView(ctx).also {
                        previewView = it
                        onPreviewViewReady(it)
                    }
                },
                modifier = Modifier.weight(1f)
            )

        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 30.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { takePhoto() },
                    enabled = ableButtons,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 8.dp),
                    border = BorderStroke(4.dp, Color.Black),
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .size(60.dp)
                ) {
                }
            }
        }
        ReturnToMain()
    }

    fun addAcceptClothes() {
        val intentAddClothesActivity = Intent(this, AddClothesActivity::class.java)
        lifecycleScope.launch {
            withContext(NonCancellable) {
                lastPhotoUri?.let {
                    CODR.insert(getObject(it, clothesHolder))
                }
            }
        }
        startActivity(intentAddClothesActivity)
        finish()
    }

    @Composable
    fun AcceptPhoto(uri: Uri?) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isProcessing) {
                        Text(
                            text = stringResource(R.string.waiting_for_cut),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(20.dp))
                        CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    } else {

                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = "Selected Photo",
                            modifier = Modifier
                                .border(
                                    width = 2.dp,
                                    color = Color.Blue,
                                    shape = RectangleShape
                                ),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(onClick = {
                            photoConfirmed = false
                            photoAccepted = true
                        }) {
                            Text(stringResource(R.string.accept))
                        }
                        Button(onClick = {
                            photoConfirmed = false
                            ableButtons = true
                            uri?.let {
                                val fdelete =
                                    File(getRealPathFromURI(it, this@CameraActivity) ?: "")
                                if (fdelete.exists()) {
                                    fdelete.delete()
                                }
                            }
                        }) {
                            Text(stringResource(R.string.ignore))
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ReturnToMain() {
        ReusableReturnArrow()
    }
}
