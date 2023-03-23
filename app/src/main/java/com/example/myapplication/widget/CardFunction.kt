package com.example.myapplication.widget

import android.graphics.Bitmap
import com.example.myapplication.model.DisplayItems
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

sealed interface CardFunction {

    interface DisplayCardFunction : CardFunction {
        fun updateItem(items: DisplayItems)

        fun getStateFlow(): StateFlow<CardState>

        fun adjustLayout(newPosition: Int, isExtend: Boolean)

        fun showComment(position: Int)
    }

    interface StaggerCardFunction : CardFunction {
        fun addPicture(picture: Bitmap)

        fun removePicture(picture: Bitmap)

        fun getSharedFlow(): SharedFlow<StaggerShared>
    }
}