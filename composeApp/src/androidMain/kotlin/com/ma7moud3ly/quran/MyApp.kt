package com.ma7moud3ly.quran

import android.app.Application
import com.ma7moud3ly.quran.di.AppModule
import com.ma7moud3ly.quran.platform.AndroidApp
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level
import org.koin.ksp.generated.module


class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidApp.init(this)
        startKoin {
            androidLogger(Level.DEBUG) // Or Level.INFO, Level.ERROR
            androidContext(this@MyApp)
            modules(AppModule().module)
        }
    }
}