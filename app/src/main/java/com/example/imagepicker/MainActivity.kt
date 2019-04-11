package com.example.imagepicker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Activity that implements basic functionality
 * of picking image from gallery and displaying it.
 *
 * @author Alexander Gorin
 */
class MainActivity : AppCompatActivity() {

    private var imageUri: Uri = Uri.EMPTY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageUri = savedInstanceState?.getParcelable(SAVED_IMAGE_URI) ?: Uri.EMPTY
        if (imageUri != Uri.EMPTY) {
            showPhoto(imageUri)
        }

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSION_READ_EXTERNAL_STORAGE
            )
        } else {
            button.isEnabled = true
        }

        button.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            intent.resolveActivity(packageManager)?.let {
                startActivityForResult(
                    intent, REQUEST_CODE
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let {
                        imageUri = it
                        showPhoto(it)
                    }
                } else {
                    Toast.makeText(this, getString(R.string.no_image), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION_READ_EXTERNAL_STORAGE -> {
                button.isEnabled =
                    (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (imageUri != Uri.EMPTY) {
            outState?.putParcelable(SAVED_IMAGE_URI, imageUri)
        }
    }

    private fun showPhoto(uri: Uri) {
        val (bitmap, path) = getBitmapAndPathFromUri(uri)
        val imageBitMap = modifyOrientation(bitmap, path)
        imageView.setImageBitmap(imageBitMap)
    }

    private fun getBitmapAndPathFromUri(uri: Uri): Pair<Bitmap, String> {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, filePathColumn, null, null, null)
        cursor.moveToFirst()
        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
        val filePath = cursor.getString(columnIndex)
        cursor.close()
        return Pair(BitmapFactory.decodeFile(filePath), filePath)
    }

    private companion object {
        const val REQUEST_CODE = 1
        const val REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 2
        const val SAVED_IMAGE_URI = "SAVED_IMAGE_URI"
    }
}

