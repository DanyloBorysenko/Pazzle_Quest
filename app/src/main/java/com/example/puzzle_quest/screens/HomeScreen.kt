package com.example.puzzle_quest.screens

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.puzzle_quest.R
import com.example.puzzle_quest.ui.theme.Puzzle_QuestTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.io.InputStream

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    Puzzle_QuestTheme {
    }
}

@SuppressLint("ResourceType")
@Composable
fun HomeScreen(onClick: () -> Unit, onSelectedImageClick : (InputStream) -> Unit) {
    val imageResources = listOf(R.drawable.animal1, R.drawable.animal2, R.drawable.animal3)
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
            LazyRow {
                items(imageResources) {
                    ImageCard(imageRes = it, onClick = onSelectedImageClick, modifier = Modifier.padding(5.dp))
                }
            }
        }
        Spacer(modifier = Modifier.size(8.dp))
        Row (
            modifier = Modifier.weight(0.2F),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween) {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.nextImage))
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = stringResource(id = R.string.nextImage))
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        Button(
            onClick = onClick,
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