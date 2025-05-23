package com.example.safetyandcheap

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SafetyAndCheapApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        var result = FirebaseApp.initializeApp(this)
        Log.d("FirebaseInit", "FirebaseApp initialized: ${result != null}")
    }
}