package com.example.myapplication.viewmodel

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.DisplayPictures
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoadViewModel : ViewModel() {

    private val _pictures: MutableStateFlow<DisplayPictures> = MutableStateFlow(DisplayPictures())
    val pictures: StateFlow<DisplayPictures> = _pictures.asStateFlow()

    init {
        viewModelScope.launch {
            repeat(20) {
                val bitmap = createBitmap(480, 640)
                when (it.mod(3)) {
                    0 -> bitmap.eraseColor(Color.RED)
                    1 -> bitmap.eraseColor(Color.YELLOW)
                    2 -> bitmap.eraseColor(Color.BLUE)
                }
                _pictures.update { value ->
                    value + bitmap
                }
            }
        }
    }
}