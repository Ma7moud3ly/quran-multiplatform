package com.ma7moud3ly.quran

import com.ma7moud3ly.quran.di.AppModule
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

fun initKoinForIOS() {
    startKoin {
        printLogger()
        modules(AppModule().module)
    }
}