package com.example.puzzle_quest.data

import android.graphics.Bitmap
import androidx.annotation.DrawableRes

data class PuzzleQuestUiState(
    val isHomeScreenShown: Boolean = true,
    val startShufflePuzzles : Boolean = true,
    val stepCount : Int = 0,
    val isGameOver : Boolean = false,
    @DrawableRes val selectedImage : Int? = null,
    val bitmap: Bitmap?
)
