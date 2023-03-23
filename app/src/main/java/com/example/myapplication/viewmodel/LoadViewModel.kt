package com.example.myapplication.viewmodel

import android.graphics.Color
import androidx.core.graphics.createBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.DisplayItems
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoadViewModel : ViewModel() {

    private val _pictures: MutableStateFlow<DisplayItems> = MutableStateFlow(DisplayItems())
    val pictures: StateFlow<DisplayItems> = _pictures.asStateFlow()

    init {
        viewModelScope.launch {
            repeat(100) {
                val picture = createBitmap(1280, 960)
                when (it.mod(3)) {
                    0 -> picture.eraseColor(Color.RED)
                    1 -> picture.eraseColor(Color.YELLOW)
                    2 -> picture.eraseColor(Color.BLUE)
                }
                val icon = createBitmap(300, 300)
                icon.eraseColor(Color.GRAY)
                val comment: String = when (it.mod(3)) {
                    0 -> "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                    1 -> "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"
                    2 -> "ccccccccccccccccccccccccccccccc"
                    else -> "dddddddddddddddddddddddddddd"
                }
                _pictures.update { value ->
                    value.copy(
                        pictures = value.pictures.apply { add(picture) },
                        icons = value.icons.apply { add(icon) },
                        comments = value.comments.apply { add(comment) }
                    )
                }
            }
        }
    }
}