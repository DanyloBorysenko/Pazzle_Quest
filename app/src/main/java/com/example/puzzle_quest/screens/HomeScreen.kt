package com.example.puzzle_quest.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.puzzle_quest.R
import com.example.puzzle_quest.ui.theme.Puzzle_QuestTheme

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    Puzzle_QuestTheme {
        HomeScreen(onClick = {})
    }
}

@Composable
fun HomeScreen(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column (modifier = Modifier
        .padding(16.dp)
        .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier
            .weight(0.7f))
        {
            Image(
                painter = painterResource(id = R.drawable.animal1),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.border(
                    BorderStroke(5.dp, color = Color.Green),
                    shape = ShapeDefaults.Medium))
        }
        Spacer(modifier = Modifier.size(16.dp))
        Button(
            onClick = onClick,
            modifier = Modifier.weight(0.3f).wrapContentSize()) {
            Text(text = stringResource(id = R.string.start_game_button_text))
        }
    }
}