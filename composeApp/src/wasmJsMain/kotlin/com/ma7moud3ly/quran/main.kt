package com.ma7moud3ly.quran

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.ma7moud3ly.quran.di.AppModule
import kotlinx.browser.document
import org.koin.core.context.GlobalContext.startKoin
import org.koin.ksp.generated.module

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoin {
        modules(AppModule().module)
    }
    ComposeViewport(document.body!!) {
        App()
    }
}