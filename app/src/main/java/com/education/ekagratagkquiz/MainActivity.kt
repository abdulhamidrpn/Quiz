package com.education.ekagratagkquiz

//import com.google.android.gms.ads.identifier.AdvertisingIdClient


// Used for the call to addCallback() within this snippet.
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.education.ekagratagkquiz.core.data.UserStore
import com.education.ekagratagkquiz.navigation.AppRoutes
import com.education.ekagratagkquiz.ui.theme.QuizTheme
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        var mRewardedAd: RewardedAd? = null
    }

    val TAG = MainActivity::class.java.simpleName

    @OptIn(DelicateCoroutinesApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Handle the splash screen transition.
        val splashScreen = installSplashScreen()
        setContent {
            val widthSizeClass = calculateWindowSizeClass(this).widthSizeClass

            val isExpandedScreen =
                (widthSizeClass == WindowWidthSizeClass.Expanded || widthSizeClass == WindowWidthSizeClass.Medium)
            Log.d(TAG, "onCreate: isExpanded screen is ${isExpandedScreen} w$widthSizeClass")
            QuizTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppRoutes(widthSizeClass = widthSizeClass)

                }
            }
        }


        val store = UserStore(this)
        CoroutineScope(Dispatchers.IO).launch {
            val adId = getAdvertisingId(applicationContext)
            Log.d(TAG, "onCreate: adId: $adId")
            store.saveToken(adId ?: "unknown")
        }

        requestNotificationPermission()
        setupFirebaseMessaging()
    }

    @SuppressLint("HardwareIds")
    private fun getAndroidId(): String? {
        return Settings.Secure.getString(
            applicationContext.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }


    private suspend fun getAdvertisingId(context: Context): String? {
        return withContext(Dispatchers.IO) {
            try {
                val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context.applicationContext)
                if (adInfo.id.isNullOrEmpty()
                    || adInfo.id.equals("00000000-0000-0000-0000-000000000000")
                    || adInfo.id?.contains("0000-0000-0000") == true
                )
                    getAndroidId()
                else {
                    adInfo.id
                }
            } catch (e: Exception) {
                // Handle exceptions
                getAndroidId()
            }
        }
    }


    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private fun requestNotificationPermission() {
        // Sets up permissions request launcher.
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    //Show a demo Notification
                } else {
                    Toast.makeText(
                        this,
                        "${getString(R.string.app_name)} can't post notifications without Notification permission",
                        Toast.LENGTH_LONG
                    ).show()
                    /*
                                    Snackbar.make(
                                        mViewBinding.containermain,
                                        String.format(
                                            String.format(
                                                getString(R.string.txt_error_post_notification),
                                                getString(R.string.app_name)
                                            )
                                        ),
                                        Snackbar.LENGTH_INDEFINITE
                                    ).setAction(getString(R.string.goto_settings)) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            val settingsIntent: Intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                                            startActivity(settingsIntent)
                                        }
                                    }.show()*/
                }
            }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }

    }

    private fun setupFirebaseMessaging(){

        /*From firebase with same topic user can get notification.*/
        FirebaseMessaging.getInstance().subscribeToTopic("notification")

        /*This is optional for individual devices*/
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                // this fail
                if (!task.isSuccessful) {
                    Log.d(
                        TAG,
                        "Fetching FCM registration token failed",
                        task.exception
                    )
                    return@addOnCompleteListener
                }
                //this token
                val token = task.result
                Log.d("PushNotificationService", "setupFirebaseMessaging: token: $token")
            }
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    QuizTheme {
        Greeting("Android")
    }
}