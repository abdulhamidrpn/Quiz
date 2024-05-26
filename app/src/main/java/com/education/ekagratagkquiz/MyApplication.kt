package com.education.ekagratagkquiz

import android.app.Application
import android.content.Context
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application(){

    @Inject
    lateinit var context: Context

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        MobileAds.initialize(this)
    }
}