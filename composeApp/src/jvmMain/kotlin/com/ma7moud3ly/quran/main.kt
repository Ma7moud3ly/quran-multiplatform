package com.ma7moud3ly.quran

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.ma7moud3ly.quran.di.AppModule
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.core.context.GlobalContext.startKoin
import org.koin.ksp.generated.module
import quran.composeapp.generated.resources.Res
import quran.composeapp.generated.resources.app_name
import quran.composeapp.generated.resources.logo

fun main() = application {
    startKoin {
        modules(AppModule().module)
    }
    Window(
        onCloseRequest = ::exitApplication,
        title = stringResource(Res.string.app_name),
        icon = painterResource(Res.drawable.logo)
    ) {
        App()
    }
}