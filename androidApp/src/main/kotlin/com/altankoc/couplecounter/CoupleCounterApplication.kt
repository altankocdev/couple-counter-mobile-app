package com.altankoc.couplecounter

import android.app.Application
import com.altankoc.couplecounter.di.initKoin

class CoupleCounterApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin(this)
    }
}