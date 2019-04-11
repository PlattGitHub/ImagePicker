/**
 * Kotlin file with functions to modify orientation of images.
 */

package com.example.imagepicker

import android.graphics.Bitmap
import android.graphics.Matrix
import android.support.media.ExifInterface

private const val NINETY_DEG = 90F
private const val ONE_HUNDRED_EIGHTY_DEG = 180F
private const val TWO_HUNDRED_SEVENTY_DEG = 270F
private const val ZERO = 0
private const val MINUS_ONE = -1F
private const val PLUS_ONE = 1F

fun modifyOrientation(bitmap: Bitmap, imageAbsolutePath: String): Bitmap {
    val ei = ExifInterface(imageAbsolutePath)
    val orientation =
        ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotate(bitmap, NINETY_DEG)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotate(bitmap, ONE_HUNDRED_EIGHTY_DEG)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotate(bitmap, TWO_HUNDRED_SEVENTY_DEG)
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flip(
            bitmap,
            horizontal = true,
            vertical = false
        )
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> flip(
            bitmap,
            horizontal = false,
            vertical = true
        )
        else -> bitmap
    }
}

private fun rotate(bitmap: Bitmap, degrees: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degrees)
    return Bitmap.createBitmap(bitmap, ZERO, ZERO, bitmap.width, bitmap.height, matrix, true)
}

private fun flip(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap {
    val matrix = Matrix()
    val sx = if (horizontal) {
        MINUS_ONE
    } else {
        PLUS_ONE
    }
    val sy = if (vertical) {
        MINUS_ONE
    } else {
        PLUS_ONE
    }
    matrix.preScale(sx, sy)
    return Bitmap.createBitmap(bitmap, ZERO, ZERO, bitmap.width, bitmap.height, matrix, true)
}

