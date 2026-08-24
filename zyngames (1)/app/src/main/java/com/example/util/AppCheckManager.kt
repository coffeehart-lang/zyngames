package com.example.util

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

/**
 * Manages Firebase AppCheck initialization with Play Integrity provider for production
 * and Debug provider for development builds to protect Firebase resources from abuse.
 */
object AppCheckManager {
    private const val TAG = "AppCheckManager"

    fun initialize(context: Context) {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
            val firebaseApp = FirebaseApp.getInstance()
            val firebaseAppCheck = FirebaseAppCheck.getInstance(firebaseApp)

            val providerFactory = if (BuildConfig.DEBUG) {
                DebugAppCheckProviderFactory.getInstance()
            } else {
                PlayIntegrityAppCheckProviderFactory.getInstance()
            }

            firebaseAppCheck.installAppCheckProviderFactory(providerFactory)
            Log.i(TAG, "Firebase AppCheck initialized successfully with ${if (BuildConfig.DEBUG) "Debug" else "PlayIntegrity"} provider.")
        } catch (e: Exception) {
            Log.w(TAG, "Firebase AppCheck initialization bypassed or deferred: ${e.message}")
        }
    }
}
