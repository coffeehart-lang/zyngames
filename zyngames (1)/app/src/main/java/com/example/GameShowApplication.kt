package com.example

import android.app.Application
import com.example.util.AppCheckManager

class GameShowApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCheckManager.initialize(this)
    }
}
