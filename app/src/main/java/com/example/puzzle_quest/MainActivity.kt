package com.example.puzzle_quest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.puzzle_quest.ui.theme.Puzzle_QuestTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        setContent {
            Puzzle_QuestTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HideSystemBars()
                    PuzzleQuestApp()
                }
            }
        }
    }
}

//@Composable
//fun TransparentSystemBars() {
//    val systemUiController = rememberSystemUiController()
//    val useDarkIcons = !isSystemInDarkTheme()
//    DisposableEffect(systemUiController, useDarkIcons) {
//        systemUiController.setSystemBarsColor(
//            color = Color.Transparent,
//            darkIcons = useDarkIcons)
//        onDispose { }
//    }
//}
@Composable
fun HideSystemBars() {
    val systemUiController = rememberSystemUiController()
    systemUiController.isSystemBarsVisible = false
    systemUiController.isNavigationBarVisible = false
    systemUiController.isStatusBarVisible = false
    systemUiController.isNavigationBarContrastEnforced = false

}