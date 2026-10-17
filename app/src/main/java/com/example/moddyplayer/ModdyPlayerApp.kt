package com.example.moddyplayer

import android.app.Application
import com.example.moddyplayer.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class ModdyPlayerApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@ModdyPlayerApp)
            modules(appModule)
        }
    }
}