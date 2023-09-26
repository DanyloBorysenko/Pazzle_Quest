package com.example.puzzle_quest.screens

import android.annotation.SuppressLint
import android.widget.Space
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.RowScopeInstance.weight
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.puzzle_quest.R
import com.example.puzzle_quest.data.CustomViewModel
import com.example.puzzle_quest.data.PuzzleQuestUiState
import com.example.puzzle_quest.ui.theme.Puzzle_QuestTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.InputStream

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    Puzzle_QuestTheme {
    }
}

@SuppressLint("ResourceType")
@Composable
fun HomeScreen(onImageClick: () -> Unit, onSelectedImageClick : (InputStream) -> Unit) {
    val imageResources = listOf(R.drawable.animal1, R.drawable.animal2, R.drawable.animal3)
    var currentIndex by remember {
        mutableStateOf(0)
    }
    var listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var allImagesVisible by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(listState) {
        val areAllImagesVisible = listState.layoutInfo.visibleItemsInfo.size == imageResources.size
        allImagesVisible = areAllImagesVisible
    }

    Background()
    Column (modifier = Modifier
        .padding(16.dp)
        .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5F),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically) {
            LazyRow (state = listState) {
                items(imageResources) {
                    ImageCard(
                        imageRes = it,
                        onClick = onSelectedImageClick,
                        modifier = Modifier
                            .padding(5.dp))
                }
            }
        }
        Spacer(modifier = Modifier.size(8.dp))
        IconButtons(
            modifier = Modifier.weight(0.2F),
            allImagesVisible = allImagesVisible,
            currentIndex = currentIndex,
            coroutineScope = coroutineScope,
            listState = listState,
            imageResourcesCount = imageResources.size
        ) {
            currentIndex = it
        }
        Spacer(modifier = Modifier.size(16.dp))
        Button(
            onClick = onImageClick,
            modifier = Modifier
                .weight(0.3f)
                .wrapContentSize()) {
            Text(text = stringResource(id = R.string.start_game_button_text))
        }
    }
}

@Composable
fun Background() {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.wood_background),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize())
    }
}

@SuppressLint("ResourceType")
@Composable
fun ImageCard(@DrawableRes imageRes: Int, onClick: (InputStream) -> Unit, modifier: Modifier = Modifier) {
    val inputStream : InputStream = LocalContext.current.resources.openRawResource(imageRes)
    Card (
        modifier = modifier.clickable { onClick(inputStream) },
        elevation = CardDefaults.cardElevation(5.dp))
    {
        Box(modifier = Modifier.fillMaxHeight()) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                contentScale = ContentScale.Fit)
        }
    }
}
@Composable
fun IconButtons(currentIndex: Int, allImagesVisible : Boolean, coroutineScope: CoroutineScope, listState: LazyListState, imageResourcesCount: Int, modifier: Modifier, changeIndex : (Int) -> Unit) {
    Row (
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
        if (!allImagesVisible && currentIndex != 0) {
            IconButton(
                onClick = {
                    val nextIndex = currentIndex - 1
                    if (nextIndex >= 0) {
                        coroutineScope.launch { listState.scrollToItem(index = nextIndex)}
                        changeIndex(nextIndex)
                    }
                }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.previousImage))
            }
        }
        Spacer(modifier = Modifier.weight(1F))
        if (!allImagesVisible && currentIndex != imageResourcesCount - 1) {
            IconButton(
                onClick = {
                    val nextIndex = currentIndex + 1
                    if (currentIndex < imageResourcesCount) {
                        coroutineScope.launch { listState.scrollToItem(index = nextIndex) }
                        changeIndex(nextIndex)
                    }
                }
            )
            {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = stringResource(id = R.string.nextImage))
            }
        }
    }
}