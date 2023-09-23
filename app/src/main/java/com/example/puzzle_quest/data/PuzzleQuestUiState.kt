package com.example.puzzle_quest.data

import androidx.annotation.DrawableRes
import com.example.puzzle_quest.R

data class PuzzleQuestUiState(
    val isHomeScreenShown: Boolean = true,
    val startShufflePuzzles : Boolean = true,
    val stepCount : Int = 0,
    val isGameOver : Boolean = false,
    @DrawableRes val selectedImage : Int = R.drawable.animal1
)
