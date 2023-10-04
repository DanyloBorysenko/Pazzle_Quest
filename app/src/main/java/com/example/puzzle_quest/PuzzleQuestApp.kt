package com.example.puzzle_quest

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.puzzle_quest.data.CustomViewModel
import com.example.puzzle_quest.screens.GameBoardScreen
import com.example.puzzle_quest.screens.HomeScreen
import com.example.puzzle_quest.screens.LoadScreen
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.delay


@Composable
fun PuzzleQuestApp(
    mainActivity: MainActivity,
    viewModel: CustomViewModel = viewModel(),
    context: Context,
) {
    val puzzleQuestUiState by viewModel.uiState.collectAsState()

    LaunchedEffect(!puzzleQuestUiState.loadingDone) {
        if (isInternetAvailable(context)) {
            initRemoteConfig(mainActivity, viewModel)
        }
        delay(4000)
        viewModel.finishLoading()
    }

    // Display LoadScreen until the loading delay is over
    if (!puzzleQuestUiState.loadingDone) {
        LoadScreen()
    } else {
        when (puzzleQuestUiState.isHomeScreenShown) {
            true -> {
                if (isInternetAvailable(context)) {
                    initRemoteConfig(activity = mainActivity, viewModel = viewModel)
                }
                HomeScreen(
                    puzzleQuestUiState = puzzleQuestUiState,
                    onStartButtonClicked = {
                        viewModel.startGame()
                    },
                    onSelectedImageClick = { inputStream, imageRes ->
                        viewModel.updateSelectedImage(inputStream, imageRes)
                    }
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
}

private fun isInternetAvailable(context: Context): Boolean {
    val result: Boolean
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCapabilities = connectivityManager.activeNetwork ?: return false
    val actNw =
        connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
    result = when {
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
    return result
}

private fun initRemoteConfig(activity: MainActivity, viewModel: CustomViewModel) {
    val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
    val configSettings = remoteConfigSettings {
        minimumFetchIntervalInSeconds = 50
    }
    remoteConfig.setConfigSettingsAsync(configSettings)
    remoteConfig.fetchAndActivate()
        .addOnCompleteListener(activity) { task ->
            if (task.isSuccessful) {
                viewModel.updateRemoteLink(
                    remoteConfig.getString("info_link"))
            }
        }
}