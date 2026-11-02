package com.example.p5

import android.app.Application
import com.example.p5.di.AppContainer
import com.example.p5.di.DefaultAppContainer

class MusicApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
