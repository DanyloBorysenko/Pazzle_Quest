package com.example.puzzle_quest.data

import PuzzleCell
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.lang.RuntimeException
import kotlin.math.abs

class CustomViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PuzzleQuestUiState())
    val uiState : StateFlow<PuzzleQuestUiState> = _uiState.asStateFlow()

    private val _data = mutableListOf<PuzzleCell>()
    val data : List<PuzzleCell> = _data

    fun changeScreen() {
        when (uiState.value.isHomeScreenShown) {
            true -> {
                startGame()
            }
            else -> {
                resetGame()
            }
        }
    }

    private fun resetGame() {
        _data.clear()
        _uiState.update { currentState ->
            currentState.copy(isHomeScreenShown = true)
        }
    }

    private fun startGame() {
        createPuzzles()
        _uiState.update { currentState ->
            currentState.copy(isHomeScreenShown = false)
        }
    }

    private fun createPuzzles(){
        for (i in 1..15) {
            _data.add(PuzzleCell(
                number = i,
                column = (i - 1) % 4,
                row = (i - 1) / 4,
                size = 0))
        }
        _data.add(
            PuzzleCell(
            number = 0,
            column = 3,
            row = 3,
            size = 0
            )
        )
    }

    fun onPuzzleClicked(clickedPuzzle: PuzzleCell) {
        val size = clickedPuzzle.size
        val emptyPuzzle = _data.find { it.number == 0 } ?: throw RuntimeException("Empty puzzle doesn't exist")
        if (clickedPuzzle == emptyPuzzle) return
        val neighbors = findPuzzlesNearEmptyPuzzle()
        if (clickedPuzzle.actualRow == emptyPuzzle.actualRow && neighbors.contains(clickedPuzzle)) {
            val isClickedPuzzleLeft = clickedPuzzle.actualColumn < emptyPuzzle.actualColumn
            if (isClickedPuzzleLeft) {
                clickedPuzzle.offsetState += IntOffset(x = size, y = 0)
                emptyPuzzle.offsetState -= IntOffset(x = size, y = 0)
            } else {
                clickedPuzzle.offsetState -= IntOffset(x = size, y = 0)
                emptyPuzzle.offsetState += IntOffset(x = size, y = 0)
            }
        } else if (clickedPuzzle.actualColumn == emptyPuzzle.actualColumn && neighbors.contains(clickedPuzzle)) {
            val isClickedPuzzleBelow = clickedPuzzle.actualRow < emptyPuzzle.actualRow
            if (isClickedPuzzleBelow) {
                clickedPuzzle.offsetState += IntOffset(x = 0, y = size)
                emptyPuzzle.offsetState -= IntOffset(x = 0, y = size)
            } else {
                clickedPuzzle.offsetState -= IntOffset(x = 0, y = size)
                emptyPuzzle.offsetState += IntOffset(x = 0, y = size)
            }
        } else {

        }
    }

    private fun findPuzzlesNearEmptyPuzzle() : List<PuzzleCell> {
        val emptyPuzzle = data.find { it.number == 0 }!!
        return data.filter {
            it != emptyPuzzle
                    && (it.actualColumn == emptyPuzzle.actualColumn && abs(it.actualRow - emptyPuzzle.actualRow) == 1
                    || it.actualRow == emptyPuzzle.actualRow && abs(it.actualColumn - emptyPuzzle.actualColumn) ==1)
        }
    }
    suspend fun shufflePuzzles() {
        for (i in 0..100) {
            delay(10)
            val puzzleNearEmptyPuzzle = findPuzzlesNearEmptyPuzzle()
            onPuzzleClicked(puzzleNearEmptyPuzzle.random())
        }
    }
    fun stopShufflePuzzles() {
        _uiState.update { currentState ->
            currentState.copy(startShufflePuzzles = false)
        }
    }
}