package com.example.data.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

fun Uri.toMultipartBodyPart(
    context: Context,
    partName: String = "file"
): MultipartBody.Part? {
    val contentResolver = context.contentResolver

    val mimeType = contentResolver.getType(this) ?: "application/octet-stream"
    val mediaType = mimeType.toMediaTypeOrNull()

    var fileName = "upload_${System.currentTimeMillis()}"
    contentResolver.query(this, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1) fileName = cursor.getString(nameIndex)
        }
    }

    return try {
        contentResolver.openInputStream(this)?.use { inputStream ->
            val bytes = inputStream.readBytes()
            val requestBody = bytes.toRequestBody(mediaType)

            MultipartBody.Part.createFormData(partName, fileName, requestBody)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
