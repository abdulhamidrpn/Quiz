package com.education.ekagratagkquiz.core.util

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.URL

fun formatTime(timeInMillis: Long): String {
    val seconds = (timeInMillis / 1000) % 60
    val minutes = (timeInMillis / (1000 * 60)) % 60
    val hours = timeInMillis / (1000 * 60 * 60)
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

fun getFileName(contentResolver: ContentResolver, uri: Uri): String {
    var fileName: String? = null
    // Query the content resolver for the display name of the file
    contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            fileName = cursor.getString(displayNameIndex)
        }
    }

    return fileName ?: ""
}

suspend fun getInputStreamFromUrl(pdfUrl: String): InputStream {
    return withContext(Dispatchers.IO) {
        val url = URL(pdfUrl)
        val connection = url.openConnection()
        connection.connectTimeout = 3000 // Set connection timeout in milliseconds
        connection.readTimeout = 3000 // Set read timeout in milliseconds
        connection.getInputStream()
    }
}