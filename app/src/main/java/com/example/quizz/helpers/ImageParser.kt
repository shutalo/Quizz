package com.example.quizz.helpers

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream

object ImageParser {

    private val TAG = "ImageParser"

    fun bitMapToByteArray(bitmap: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        return baos.toByteArray()
    }

    fun byteArrayToBitMap(baos: ByteArray?): Bitmap? {
        return if(baos != null){
            BitmapFactory.decodeByteArray(baos, 0, baos.size)
        } else {
            Log.d(TAG,"Image string is null!")
            null
        }
    }
}