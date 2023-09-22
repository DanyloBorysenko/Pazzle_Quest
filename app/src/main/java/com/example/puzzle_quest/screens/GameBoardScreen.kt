package com.example.puzzle_quest.screens

import PuzzleCell
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.puzzle_quest.R
import com.example.puzzle_quest.data.CustomViewModel
import com.example.puzzle_quest.data.PuzzleQuestUiState
import com.example.puzzle_quest.ui.theme.Puzzle_QuestTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun GameBoardScreenPreview() {
    Puzzle_QuestTheme {
        Puzzle(
            selectedImage = R.drawable.dinnerware,
            puzzleCell = PuzzleCell(1,0,0, 50),
            onPuzzleCellClicked = {})
    }
}

@Composable
fun Puzzle(
    @DrawableRes selectedImage: Int,
    puzzleCell: PuzzleCell,
    modifier: Modifier = Modifier,
    onPuzzleCellClicked : (PuzzleCell) -> Unit
) {
//    val update = updateTransition(targetState = puzzleCell.offsetState, label = "")
//    val animateOffset by update.animateIntOffset(label = "") {it}

    Box(modifier = modifier
        .offset { puzzleCell.offsetState }
        .clickable {
            if (puzzleCell.number != 0) {
                onPuzzleCellClicked(puzzleCell)
            }
        },
        contentAlignment = Alignment.Center
    ) {
        if (puzzleCell.number != 0) {
            Image(
                painter = painterResource(id = selectedImage),
                contentDescription = null,
                contentScale = ContentScale.Crop)
        }
    }
}

@Composable
fun GameBoardScreen(
    puzzleQuestUiState: PuzzleQuestUiState,
    viewModel: CustomViewModel,
    data: List<PuzzleCell>,
    onBackButtonPressed: () -> Unit,
    onPuzzleCellClicked: (PuzzleCell) -> Unit,
    modifier: Modifier = Modifier) {

    var boardSize by remember { mutableStateOf(IntSize.Zero) }
    val sizeOfPuzzleCell = getPuzzleCellSide(boardSize = boardSize)
    val sizeOfCellInDp = with(LocalDensity.current) { sizeOfPuzzleCell.toDp()}

    Column (modifier = modifier
        .fillMaxSize()
        .background(color = Color.Gray)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBackButtonPressed,
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.back_button_content_dis)
                )
            }
            Text(text = puzzleQuestUiState.stepCount.toString())

        }
        Box(modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                boardSize = it.size
            }
            .background(Color.LightGray)) {
            Layout(content = {
                data.forEach {
                    Puzzle(
                        selectedImage = puzzleQuestUiState.selectedImage,
                        modifier = Modifier
                            .size(sizeOfCellInDp)
                            .padding(1.dp),
                        puzzleCell = it.apply {
                            size = sizeOfPuzzleCell
                        }) { puzzleCell -> onPuzzleCellClicked(puzzleCell) }
                }
            }) { measurables, constraints ->
                val placeables = measurables.map {
                    it.measure(constraints)
                }
                layout(width = boardSize.width, height = boardSize.height) {
                    var x: Int
                    var y: Int
                    placeables.forEachIndexed { index, placeable ->
                        x = placeable.width * (index % 4)
                        y = placeable.height * (index / 4)

                        //put empty cell on lower zIndex
                        placeable.place(x, y, zIndex = if (index == placeables.size - 1) 0f else 1f)
                    }
                }
            }
        }
    }
    BackHandler(onBack = onBackButtonPressed)
    if (puzzleQuestUiState.startShufflePuzzles) {
        LaunchedEffect (Unit) {
            coroutineScope {
                launch { viewModel.shufflePuzzles() }
            }
            viewModel.stopShufflePuzzles()
        }
    }
    if (puzzleQuestUiState.isGameOver) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                TextButton(onClick = { viewModel.startGame() }) {
                    Text(text = stringResource(id = R.string.confirm_button_title))
            }},
            dismissButton = { TextButton(onClick = { onBackButtonPressed() }) {
                Text(text = stringResource(id = R.string.to_home_screen_button_title))
            }},
            title = {
            Text(
                text = stringResource(id = R.string.win_title))
            },
            text = {
                Text(text = stringResource(id = R.string.restart_question))
            }
        )
    }
}

private fun getPuzzleCellSide(boardSize : IntSize) : Int {
    val with = boardSize.width
    val height = boardSize.height
    return if (with < height)
        with / 4
    else height / 4
}