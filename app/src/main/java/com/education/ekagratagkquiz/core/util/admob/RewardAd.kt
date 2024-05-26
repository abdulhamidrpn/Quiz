package com.education.ekagratagkquiz.core.util.admob

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.education.ekagratagkquiz.MainActivity.Companion.mRewardedAd
import com.education.ekagratagkquiz.R


fun loadRewardedAd(context: Context) {
    Log.d("TAGAD", "loadRewardAd: true")

    RewardedAd.load(context,
        context.getString(R.string.admob_rewarded),
        AdRequest.Builder().build(),
        object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mRewardedAd = null
            }

            override fun onAdLoaded(rewardedAd: RewardedAd) {
                mRewardedAd = rewardedAd
            }
        })
}

fun showRewardedAd(
    context: Context,
    isSubscriptionActive: Boolean = true,
    onAdDismissed: () -> Unit
) {
    Log.d("TAGAD", "showRewardedAd: true")
    if (isSubscriptionActive) {
        /*If subscription Active then it shouldn't show any ad.*/
        return
    }

    if (mRewardedAd != null) {
        mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdFailedToShowFullScreenContent(e: com.google.android.gms.ads.AdError) {
                mRewardedAd = null
            }

            override fun onAdDismissedFullScreenContent() {
                mRewardedAd = null

                loadRewardedAd(context)
                onAdDismissed()
            }
        }
        mRewardedAd?.show(context as Activity) {
            loadRewardedAd(context)
            onAdDismissed()
            mRewardedAd = null
        }
    }
}

fun removeRewarded() {
    mRewardedAd?.fullScreenContentCallback = null
    mRewardedAd = null
}


/*coroutineScope.launch {
    showRewardedAd(context) {
        Toast.makeText(context, "Rewarded Ad Shown!", Toast.LENGTH_SHORT).show()
    }
}*/
