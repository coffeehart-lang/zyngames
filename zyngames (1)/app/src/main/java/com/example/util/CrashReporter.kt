package com.example.util

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Utility wrapper around Firebase Crashlytics to record breadcrumbs,
 * non-fatal exceptions, and diagnostic metadata for production monitoring.
 */
object CrashReporter {
    private const val TAG = "CrashReporter"

    fun log(message: String) {
        try {
            FirebaseCrashlytics.getInstance().log(message)
            Log.d(TAG, "[Breadcrumb] $message")
        } catch (e: Exception) {
            Log.d(TAG, "Crashlytics not ready: $message")
        }
    }

    fun setCustomKey(key: String, value: String) {
        try {
            FirebaseCrashlytics.getInstance().setCustomKey(key, value)
        } catch (e: Exception) {
            Log.w(TAG, "Could not set custom key: $key", e)
        }
    }

    fun setCustomKey(key: String, value: Int) {
        try {
            FirebaseCrashlytics.getInstance().setCustomKey(key, value)
        } catch (e: Exception) {
            Log.w(TAG, "Could not set custom key: $key", e)
        }
    }

    fun recordException(throwable: Throwable) {
        try {
            FirebaseCrashlytics.getInstance().recordException(throwable)
            Log.e(TAG, "Exception recorded to Crashlytics", throwable)
        } catch (e: Exception) {
            Log.e(TAG, "Could not record exception to Crashlytics", throwable)
        }
    }

    fun setUserId(userId: String) {
        try {
            FirebaseCrashlytics.getInstance().setUserId(userId)
        } catch (e: Exception) {
            Log.w(TAG, "Could not set user ID in Crashlytics", e)
        }
    }
}
