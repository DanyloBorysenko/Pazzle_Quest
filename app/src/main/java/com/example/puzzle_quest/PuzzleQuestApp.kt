package com.example.puzzle_quest

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.puzzle_quest.data.CustomViewModel
import com.example.puzzle_quest.screens.GameBoardScreen
import com.example.puzzle_quest.screens.HomeScreen

@SuppressLint("UnrememberedMutableState")
@Composable
fun PuzzleQuestApp(viewModel: CustomViewModel = viewModel()) {
    val puzzleQuestUiState by viewModel.uiState.collectAsState()

    when (puzzleQuestUiState.isHomeScreenShown) {
        true -> {
            HomeScreen(
                puzzleQuestUiState = puzzleQuestUiState,
                onStartButtonClicked = {
                    viewModel.startGame() },
                onSelectedImageClick = {inputStream, imageRes ->
                    viewModel.updateSelectedImage(inputStream, imageRes)}
            )
        }
        else -> {
            GameBoardScreen(
                viewModel = viewModel,
                puzzleQuestUiState = puzzleQuestUiState,
                data = viewModel.data,
                onBackButtonPressed = {
                    viewModel.resetGame()
                },
                onPuzzleCellClicked = {
                    viewModel.onPuzzleClicked(clickedPuzzle = it, isUserClicked = true)
                },
                shakeState = viewModel.shakeFlow
            )
        }
    }
}