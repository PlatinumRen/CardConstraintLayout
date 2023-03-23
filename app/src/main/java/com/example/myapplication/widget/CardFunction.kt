package com.example.myapplication.widget

import android.graphics.Bitmap
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

sealed interface CardFunction {

    interface DisplayCardFunction : CardFunction {
        fun updateItem(pictures: ArrayList<Bitmap>)

        fun getStateFlow(): StateFlow<CardState>

        fun adjustLayout(newPosition: Int, isExtend: Boolean)
    }

    interface StaggerCardFunction : CardFunction {
        fun addPicture(picture: Bitmap)

        fun removePicture(picture: Bitmap)

        fun getSharedFlow(): SharedFlow<StaggerShared>
    }
}