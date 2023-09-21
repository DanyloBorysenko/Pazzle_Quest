package com.example.puzzle_quest.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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
    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.wrapContentSize()) {
            Text(text = stringResource(id = R.string.start_game_button_text))
        }
    }
}
