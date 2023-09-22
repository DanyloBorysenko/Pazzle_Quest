package com.example.puzzle_quest.data

data class PuzzleQuestUiState(
    val isHomeScreenShown: Boolean = true,
    val startShufflePuzzles : Boolean = true,
    val stepCount : Int = 0,
    val isGameOver : Boolean = false
)
