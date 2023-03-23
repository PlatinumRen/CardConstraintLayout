package com.example.myapplication.model

import android.graphics.Bitmap

data class DisplayPictures(
    private val pictures: ArrayList<Bitmap> = arrayListOf()
) {
    operator fun plus(bitmap: Bitmap): DisplayPictures {
        pictures.add(bitmap)
        return this
    }

    fun toArrayList(): ArrayList<Bitmap> = pictures
}
