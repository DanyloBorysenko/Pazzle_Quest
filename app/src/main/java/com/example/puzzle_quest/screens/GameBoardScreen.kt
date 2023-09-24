package com.example.puzzle_quest.screens

import PuzzleCell
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffset
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.ripple.rememberRipple
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
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.puzzle_quest.R
import com.example.puzzle_quest.data.CustomViewModel
import com.example.puzzle_quest.data.PuzzleQuestUiState
import com.example.puzzle_quest.ui.theme.Puzzle_QuestTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun GameBoardScreenPreview() {
    Puzzle_QuestTheme {
        Puzzle(
            puzzleCell = PuzzleCell(1,0,0, 50, R.drawable.img_1),
            onPuzzleCellClicked = {},
            sizeOfCellInDp = 60.dp, modifier = Modifier)
    }
}

@Composable
fun Puzzle(
    sizeOfCellInDp: Dp,
    puzzleCell: PuzzleCell,
    modifier: Modifier,
    onPuzzleCellClicked : (PuzzleCell) -> Unit
) {
    val update = updateTransition(targetState = puzzleCell.offsetState, label = "")
    val animateOffset by update.animateIntOffset(label = "") {it}

    Box(modifier = modifier
        .size(sizeOfCellInDp)
        .offset {
            animateOffset
        }
        .clickable (interactionSource = remember {
            MutableInteractionSource()
        },
            indication = if (puzzleCell.number == 0) null else rememberRipple(
                bounded = true,
                radius = 100.dp,
                color = Color.Green
            )
        ) {
            if (puzzleCell.number != 0) {
                onPuzzleCellClicked(puzzleCell)
            }
        },
        contentAlignment = Alignment.Center)
    {
        if (puzzleCell.imageRes != null) {
            Image(
                painter = painterResource(id = puzzleCell.imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GameBoardScreen(
    puzzleQuestUiState: PuzzleQuestUiState,
    viewModel: CustomViewModel,
    shakeState: SharedFlow<PuzzleCell>,
    data: List<PuzzleCell>,
    onBackButtonPressed: () -> Unit,
    onPuzzleCellClicked: (PuzzleCell) -> Unit,
    modifier: Modifier = Modifier) {

    val configuration = LocalConfiguration.current
    val smallestSide = getSmallestSide(configuration)
    val sizeOfPuzzle : Int = with(LocalDensity.current) {smallestSide.roundToPx()} / 4
    val sizeOfCellInDp = with(LocalDensity.current) { sizeOfPuzzle.toDp()}


    var puzzleToShake by remember {
        mutableStateOf<PuzzleCell?>(null)
    }

    LaunchedEffect(Unit) {
        shakeState.collectLatest {
            puzzleToShake = it
        }
    }

    Column (modifier = modifier
        .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.weight(0.2F)
                .fillMaxWidth()
                .padding(16.dp), verticalAlignment = Alignment.CenterVertically,
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
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(id = R.string.step_count_text))
            Spacer(modifier = Modifier.size(10.dp))
            AnimatedContent(
                targetState = puzzleQuestUiState.stepCount,
                transitionSpec = {
                    slideIntoContainer(
                        towards = AnimatedContentScope.SlideDirection.Up,
                        animationSpec = tween(durationMillis = 500)
                    ) with ExitTransition.None
                }
            ) { targetCount ->
                Text(
                    text = "$targetCount",
                    fontSize = 25.sp
                )
            }
//            Text(
//                text = puzzleQuestUiState.stepCount.toString())

        }
        Box(modifier = Modifier.weight(0.8F)
            ) {
            Layout(content = {
                data.forEach {
                    Puzzle(
                        sizeOfCellInDp = sizeOfCellInDp,
                        puzzleCell = it.apply {
                            size = sizeOfPuzzle
                        }, modifier = Modifier.shake(
                            enabled = it == puzzleToShake,
                            correctionX = it.offsetState.x.toFloat() / it.size,
                            correctionY = it.offsetState.y.toFloat() / it.size,
                            shakeFinished = {
                                puzzleToShake = null
                            }
                        )
//                            .padding(1.dp)
                    )
                    { puzzleCell -> onPuzzleCellClicked(puzzleCell) }
                }
            }) { measurables, constraints ->
                val placeables = measurables.map {
                    it.measure(constraints)
                }
                layout(
                    width = smallestSide.roundToPx(),
                    height = smallestSide.roundToPx()
                ) {
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
fun Modifier.shake(
    enabled: Boolean,
    correctionX: Float,
    correctionY: Float,
    shakeFinished: () -> Unit
) = composed(
    factory = {
        val scale by animateFloatAsState(
            targetValue = if (enabled) 1f else 0.9f,
            animationSpec = repeatable(
                iterations = 5,
                animation = tween(durationMillis = 50, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            finishedListener = {
                shakeFinished()
            }
        )

        Modifier.graphicsLayer(
            transformOrigin = TransformOrigin(
                pivotFractionX = 0.5f + if (!correctionX.isNaN()) correctionX else 0f,
                pivotFractionY = 0.5f + +if (!correctionY.isNaN()) correctionY else 0f
            ),
            scaleX = if (enabled) scale else 1f,
            scaleY = if (enabled) scale else 1f
        )
    }
)
private fun getSmallestSide(configuration: Configuration) : Dp {
    val width = configuration.screenWidthDp.dp - 80.dp
    val height = configuration.screenHeightDp.dp - 80.dp
    return if (width < height) width else height
}