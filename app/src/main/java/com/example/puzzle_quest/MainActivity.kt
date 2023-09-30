package com.example.puzzle_quest

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.puzzle_quest.ui.theme.Puzzle_QuestTheme
import com.google.accompanist.web.AccompanistWebChromeClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberSaveableWebViewState
import com.google.accompanist.web.rememberWebViewNavigator
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val ONESIGNAL_APP_ID = "7320b342-1e49-45cb-a1ee-6f3d9ccefa7c"

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 30
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    Log.d(TAG, "Config params updated: ${remoteConfig.getString(MAIN_LINK_KEY)}")
                } else {
                }
            }
        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate : ConfigUpdate) {
                Log.d(TAG, "UpdateListener before works: " + remoteConfig.getString(MAIN_LINK_KEY))

                if (configUpdate.updatedKeys.contains(MAIN_LINK_KEY) && configUpdate.updatedKeys.contains(
                        PRIVACY_POLICY_KEY)) {
                    remoteConfig.activate().addOnCompleteListener {
                        Log.d(TAG, "UpdateListener after activate: " + remoteConfig.getString(MAIN_LINK_KEY))
                    }
                }
            }

            override fun onError(error : FirebaseRemoteConfigException) {
                Log.w(TAG, "Config update error with code: " + error.code, error)
            }
        })

        OneSignal.Debug.logLevel = LogLevel.VERBOSE
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID)
        CoroutineScope(Dispatchers.IO).launch {
            OneSignal.Notifications.requestPermission(true)
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.apply {
            hide(WindowInsetsCompat.Type.statusBars())

            hide(WindowInsetsCompat.Type.navigationBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        setContent {
            Puzzle_QuestTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Log.d(TAG, "SetContentblok " + remoteConfig.getString(MAIN_LINK_KEY));
                    val context = LocalContext.current
                    if (isInternetAvailable(context = context) && isSimCardAvailable(context = context)) {
                        ShowVebView(activity = this, url = remoteConfig.getString(MAIN_LINK_KEY))
                    } else {
                        PuzzleQuestApp(urlForInfoButton = remoteConfig.getString("privacypolicy_link"))
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val MAIN_LINK_KEY = "main_link"
        private const val PRIVACY_POLICY_KEY = "privacypolicy_link"
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ShowVebView(activity: MainActivity , url: String) {
    val state = rememberSaveableWebViewState()
    val navigator = rememberWebViewNavigator()
    val chromeClient = remember {
        CustomChromeClient(activity)
    }
    LaunchedEffect(navigator, chromeClient) {
        val bundle = state.viewState
        if (bundle == null) {
            // This is the first time load, so load the home page.
            navigator.loadUrl(url)
        }
    }
    WebView(
        state = state,
        modifier = Modifier.fillMaxSize(),
        navigator = navigator,
        chromeClient = chromeClient,
        onCreated = {webView ->
            webView.settings.javaScriptEnabled = true
            webView.settings.domStorageEnabled = true
        })
}

private class CustomChromeClient(val activity: ComponentActivity): AccompanistWebChromeClient() {

    var customView: View? = null

    override fun onHideCustomView() {
        (activity.window.decorView as FrameLayout).removeView(this.customView)
        this.customView = null

    }

    override fun onShowCustomView(paramView: View, paramCustomViewCallback: CustomViewCallback) {
        if (this.customView != null) {
            onHideCustomView()
            return
        }
        this.customView = paramView
        (activity.window.decorView as FrameLayout).addView(this.customView, FrameLayout.LayoutParams(-1, -1))
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

private fun isSimCardAvailable(context: Context): Boolean {
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val simState = telephonyManager.simState
    return simState == TelephonyManager.SIM_STATE_READY
}

