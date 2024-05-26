package com.education.ekagratagkquiz.core.util.admob

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.education.ekagratagkquiz.R

fun interstitialAdsContainer(activity: Activity, isActiveAd: Boolean = true) {
    if (isActiveAd) {
        return
    }

    val TAG = "TAGAD"
    val adRequest = AdRequest.Builder().build()

    InterstitialAd.load(activity, activity.getString(R.string.admob_interstitial),
        adRequest,
        object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                adError.toString().let { Log.d(TAG, it) }
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Ad was loaded.")
                interstitialAd.show(activity)
            }
        })

}