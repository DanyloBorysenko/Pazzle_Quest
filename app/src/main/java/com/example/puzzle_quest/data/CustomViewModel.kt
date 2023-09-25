package com.example.puzzle_quest.data

import PuzzleCell
import android.graphics.BitmapFactory
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.InputStream
import java.lang.RuntimeException
import kotlin.math.abs

class CustomViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PuzzleQuestUiState(bitmap = null))
    val uiState : StateFlow<PuzzleQuestUiState> = _uiState.asStateFlow()

    private val _shakeFlow = MutableSharedFlow<PuzzleCell>()
    val shakeFlow = _shakeFlow.asSharedFlow()

    private val _data = mutableListOf<PuzzleCell>()
    val data : List<PuzzleCell> = _data

    fun resetGame() {
        _data.clear()
        _uiState.value = PuzzleQuestUiState(bitmap = null)
    }

    fun startGame() {
        _data.clear()
        createPuzzles()
        _uiState.update { currentState ->
            currentState.copy(
                isHomeScreenShown = false,
                startShufflePuzzles = true,
                stepCount = 0,
                isGameOver = false)
        }
    }

    private fun createPuzzles(){
        for (i in 1..15) {
            _data.add(PuzzleCell(
                number = i,
                column = (i - 1) % 4,
                row = (i - 1) / 4,
                size = 0
            ))
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

    fun onPuzzleClicked(clickedPuzzle: PuzzleCell, isUserClicked: Boolean) {
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
            viewModelScope.launch {
                _shakeFlow.emit(clickedPuzzle)
            }
        }
        if (isUserClicked && neighbors.contains(clickedPuzzle)) {
            increaseStepCount()
            checkResult()
        }
    }

    private fun increaseStepCount() {
        _uiState.update { currentState ->
            currentState.copy(stepCount = currentState.stepCount + 1)
        }
    }

    private fun checkResult() {
        for (puzzle in _data) {
            if (puzzle.offsetState != IntOffset.Zero) {
                return
            }
        }
        _uiState.update { currentState ->
            currentState.copy(isGameOver = true)
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
        for (i in 0..50) {
            delay(5)
            val puzzleNearEmptyPuzzle = findPuzzlesNearEmptyPuzzle()
            onPuzzleClicked(puzzleNearEmptyPuzzle.random(), isUserClicked = false)
        }
    }
    fun stopShufflePuzzles() {
        _uiState.update { currentState ->
            currentState.copy(startShufflePuzzles = false)
        }
    }
    fun updateSelectedImage(inputStream: InputStream) {
        val bitMap = BitmapFactory.decodeStream(inputStream)
        _uiState.update { currentState ->
            currentState.copy(bitmap = bitMap)
        }
    }
}