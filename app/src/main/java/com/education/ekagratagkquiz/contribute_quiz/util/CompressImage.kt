package com.education.ekagratagkquiz.contribute_quiz.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.CompletableFuture


const val MAX_IMAGE_SIZE = 1024

fun compressImage(context: Context, uri: Uri?, desiredQuality: Int = 80): CompletableFuture<Uri> {
    val deferred = CompletableFuture<Uri>()
    if (uri == null) {
        deferred.completeExceptionally(Exception("Failed to create compressed image URI"))
        return deferred
    }
    val resolver = context.contentResolver

    /*Compressed image targeted dimension width and height */
    val photoDimension = getPhotoDimensions(context,uri)
    val name = context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex != -1 && cursor.moveToFirst()) {
            cursor.getString(nameIndex)
        } else {
            null
        }
    }

    val bitmap = BitmapFactory.decodeStream(resolver.openInputStream(uri))
    val compressedBitmap =
        Bitmap.createScaledBitmap(bitmap, photoDimension.first, photoDimension.second, true) // Adjust dimensions as needed
    val outputStream = ByteArrayOutputStream()
    compressedBitmap.compress(Bitmap.CompressFormat.JPEG, desiredQuality, outputStream)
    val byteArray = outputStream.toByteArray()

    val newContentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        put(
            MediaStore.MediaColumns.RELATIVE_PATH,
            Environment.DIRECTORY_PICTURES + File.separator + "quiz"
        ) // Adjust directory as needed
    }

    val newImageUri =
        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, newContentValues)

    if (newImageUri != null) {
        resolver.openOutputStream(newImageUri).use { outputStreamX ->
            outputStreamX?.write(byteArray)
            deferred.complete(newImageUri)
        }
    } else {
        deferred.completeExceptionally(Exception("Failed to create compressed image URI"))
    }

    return deferred
}

fun getPhotoDimensions(context: Context, uri: Uri,maxSize: Int = MAX_IMAGE_SIZE): Pair<Int, Int> {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true // Only decode bounds for size information
    BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri), null, options)

    val originalWidth = options.outWidth
    val originalHeight = options.outHeight


    val targetWidth: Int
    val targetHeight: Int

    if (originalWidth > originalHeight) {
        targetWidth = maxSize
        val aspectRatio = originalHeight.toFloat() / originalWidth.toFloat()
        targetHeight = (targetWidth * aspectRatio).toInt()
    } else {
        targetHeight = maxSize
        val aspectRatio = originalWidth.toFloat() / originalHeight.toFloat()
        targetWidth = (targetHeight * aspectRatio).toInt()
    }

    return Pair(targetWidth, targetHeight)
}

