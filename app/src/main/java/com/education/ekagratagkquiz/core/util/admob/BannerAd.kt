package com.education.ekagratagkquiz.core.util.admob

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.education.ekagratagkquiz.R
import com.education.ekagratagkquiz.core.data.UserStore


@Composable
fun AdmobBanner(
    modifier: Modifier = Modifier,
    landscape: Boolean = false,
    custom: Boolean = false,
    isSubscriptionActive: Boolean = true
) {
    Log.d("Subscription", "AdmobBanner: isSubscriptionActive = ${isSubscriptionActive}")

    if (isSubscriptionActive) {
        /*If subscription Active then it shouldn't show any ad.*/
        return
    }

    val configuration = LocalConfiguration.current

    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val DividerDp = 12.dp

    val columns = 2 // Adjust based on desired number of columns
    val approximateItemWidth = (screenWidth - (columns - 1) * DividerDp) / columns.toFloat()

    Log.d("TAGAD", "AdmobBanner: approximateItemWidth ${approximateItemWidth.value.toInt()} screenWidth $screenWidth")

    AndroidView(
        // on below line specifying width for ads.
        modifier = modifier,
        factory = { context ->
            AdView(context).apply {

                val adSize = if (landscape) AdSize.LEADERBOARD
                else if (custom) AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(context, approximateItemWidth.value.toInt() )
                else AdSize.BANNER

                setAdSize(adSize)

                adUnitId = context.getString(R.string.admob_banner_ad)
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}