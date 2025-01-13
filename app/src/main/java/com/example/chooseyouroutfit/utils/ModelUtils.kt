package com.example.chooseyouroutfit.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import java.io.File
import java.io.FileOutputStream

fun runModel(
    context: Context,
    uri: Uri,
    modelFileName: String,
    moduleName: String,
    functionName: String
): PyObject {
    val modelPath = copyAssetToInternalStorage(modelFileName, context)
    val py = Python.getInstance()
    val segmenter = py.getModule(moduleName)
    val realPath = getRealPathFromURI(uri, context)
    return segmenter.callAttr(functionName, realPath, modelPath)
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