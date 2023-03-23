package com.example.myapplication.model

import android.graphics.Bitmap

data class DisplayItems(
    val pictures: ArrayList<Bitmap> = arrayListOf(),
    val icons: ArrayList<Bitmap> = arrayListOf(),
    val comments: ArrayList<String> = arrayListOf()
)
