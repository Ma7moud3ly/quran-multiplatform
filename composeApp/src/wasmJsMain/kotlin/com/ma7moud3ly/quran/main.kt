package com.ma7moud3ly.quran

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToBrowserNavigation
import androidx.navigation.toRoute
import com.ma7moud3ly.quran.di.AppModule
import kotlinx.browser.document
import org.koin.core.context.GlobalContext.startKoin
import org.koin.ksp.generated.module

@OptIn(ExperimentalComposeUiApi::class, ExperimentalBrowserHistoryApi::class)
fun main() {
    startKoin {
        modules(AppModule().module)
    }
    val body = document.body ?: return
    ComposeViewport(body) {
        App(
            onNavHostReady = { navController ->
                navController.bindToBrowserNavigation { entry ->
                    val route = entry.destination.route.orEmpty()
                    when {
                        entry.match(AppRoutes.ReadingScreen::class) -> {
                            val reading = entry.toRoute<AppRoutes.ReadingScreen>()
                            "#reading/${reading.chapterId}"
                        }

                        else -> {
                            // hide url variables
                            val shortRoute = route.substringBefore("?")
                            "#$shortRoute"
                        }
                    }

                }
            }
        )
    }
}

