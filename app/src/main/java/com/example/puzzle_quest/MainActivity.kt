package com.example.puzzle_quest

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.puzzle_quest.ui.theme.Puzzle_QuestTheme
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
    lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 30
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                Log.d(TAG, "Updated keys: " + configUpdate.updatedKeys)

                if (configUpdate.updatedKeys.contains(MAIN_LINK_KEY)
                    && configUpdate.updatedKeys.contains(PRIVACY_POLICY_KEY)
                ) {
                    remoteConfig.activate()
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Log.w(TAG, "Config update error with code: " + error.code, error)
            }
        })

        OneSignal.Debug.logLevel = LogLevel.VERBOSE
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID)
        CoroutineScope(Dispatchers.IO).launch {
            OneSignal.Notifications.requestPermission(true)
        }


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
                    val context = LocalContext.current
                    if (isInternetAvailable(context = context) && isSimCardAvailable(context = context)) {
                        ShowVebView(url = "https://www.youtube.com/")
//                        ShowVebView(url = remoteConfig.getString("main_link"))
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

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        }
    }
}

//@SuppressLint("SetJavaScriptEnabled")
//@Composable
//fun ShowVebView(url: String) {
//    var backEnabled by remember { mutableStateOf(false) }
//    var webView: WebView? = null
//
//    AndroidView(
//        factory = {
//            WebView(it).apply {
//                settings.javaScriptEnabled = true
//                webViewClient = object : WebViewClient() {
//                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
//                        if (view != null) {
//                            backEnabled = view.canGoBack()
//                        }
//                    }
//                }
//                loadUrl(url)
//                webView = this
//            }
//        }, update = { webView = it }
//    )
//    BackHandler(enabled = backEnabled) {
//        webView?.goBack()
//    }
//}
@Composable
fun WebViewComponent(url: String) {
    val context = LocalContext.current
    AndroidView(factory = { WebView(context) }) { webView ->
        webView.loadUrl(url)
    }
    fun injectJavaScript(webView: WebView, script: String) {
        webView.evaluateJavascript(script, null)
    }
}
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ShowVebView(url: String) {
    var backEnabled by remember { mutableStateOf(false) }
    var webView: WebView? = null

    AndroidView(
        factory = {
            WebView(it).apply {
                settings.javaScriptEnabled = true
                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        if (view != null) {
                            backEnabled = view.canGoBack()
                        }
                    }
                }
                loadUrl(url)
                webView = this
            }
        }, update = { webView = it }
    )
    BackHandler(enabled = backEnabled) {
        webView?.goBack()
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

