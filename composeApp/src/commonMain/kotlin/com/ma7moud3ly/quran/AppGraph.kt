package com.ma7moud3ly.quran

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.ma7moud3ly.quran.features.home.HomeEvents
import com.ma7moud3ly.quran.features.home.HomeScreen
import com.ma7moud3ly.quran.features.reading.ReadingScreen
import com.ma7moud3ly.quran.features.recitation.config.RecitationConfigScreen
import com.ma7moud3ly.quran.features.recitation.config.RecitationEvents
import com.ma7moud3ly.quran.features.recitation.config.RecitationViewModel
import com.ma7moud3ly.quran.features.recitation.download.ConfirmDownloadDialog
import com.ma7moud3ly.quran.features.recitation.download.DownloadDialog
import com.ma7moud3ly.quran.features.recitation.play.PlaybackScreen
import com.ma7moud3ly.quran.features.search.SearchEvents
import com.ma7moud3ly.quran.features.search.SearchScreen
import com.ma7moud3ly.quran.features.search.chapter.PickChapterDialog
import com.ma7moud3ly.quran.features.search.reciter.PickReciterDialog
import com.ma7moud3ly.quran.features.settings.SettingsScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

private const val TAG = "AppGraph"

@Composable
fun AppGraph(
    navController: NavHostController = rememberNavController(),
    viewModel: RecitationViewModel = koinViewModel(),
    openPlaybackScreen: Boolean,
) {
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        if (openPlaybackScreen) {
            delay(500)
            navController.navigate(AppRoutes.Recitation.Playback)
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppRoutes.HomeScreen,
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
    ) {
        composable<AppRoutes.HomeScreen> {
            HomeScreen(
                uiEvents = {
                    when (it) {
                        is HomeEvents.Back -> {
                            navController.popBackStack()
                        }

                        is HomeEvents.OpenChapter -> {
                            val chapterId = it.chapter.id
                            navController.navigate(AppRoutes.ReadingScreen(chapterId))
                        }

                        is HomeEvents.OpenReciter -> {
                            val route = AppRoutes.Recitation.Config(
                                reciterId = it.reciter.id,
                                canChangeReciter = true
                            )
                            navController.navigate(route)
                        }

                        is HomeEvents.OpenHistory -> {
                            val history = it.history
                            val rout = if (history.isReading) {
                                AppRoutes.ReadingScreen(
                                    chapterId = history.chapterId,
                                    selectedVerseId = history.verseId
                                )
                            } else {
                                viewModel.setRecitation(it.history)
                                AppRoutes.Recitation.Playback
                            }
                            coroutineScope.launch {
                                delay(100)
                                navController.navigate(rout)
                            }
                        }

                        is HomeEvents.PlayChapter -> {
                            val route = AppRoutes.Recitation.Config(
                                chapterId = it.chapter.id,
                                canChangeChapter = true
                            )
                            navController.navigate(route)
                        }

                        is HomeEvents.Search -> {
                            navController.navigate(AppRoutes.SearchScreen)
                        }

                        is HomeEvents.OpenSettings -> {
                            navController.navigate(
                                AppRoutes.SettingsScreen(it.reading)
                            )
                        }
                    }
                }
            )
        }

        composable<AppRoutes.SearchScreen> {
            SearchScreen(
                uiEvents = {
                    when (it) {
                        is SearchEvents.OnBack -> {
                            navController.popBackStack()
                        }

                        is SearchEvents.OpenVerse -> {
                            val result = it.searchResult
                            val route = if (it.listen) {
                                AppRoutes.Recitation.Config(
                                    chapterId = result.chapterId,
                                    verseId = result.verseId,
                                    canChangeChapter = false,
                                    canChangeVerse = true
                                )
                            } else {
                                AppRoutes.ReadingScreen(
                                    result.chapterId,
                                    result.verseId
                                )
                            }
                            navController.navigate(route)
                        }

                        is SearchEvents.OpenChapter -> {
                            val route = if (it.listen) AppRoutes.Recitation.Config(
                                chapterId = it.chapterId,
                                canChangeChapter = false
                            ) else AppRoutes.ReadingScreen(it.chapterId)
                            navController.navigate(route)
                        }

                        is SearchEvents.OpenReciter -> {
                            val route = AppRoutes.Recitation.Config(
                                reciterId = it.reciter.id,
                                canChangeReciter = true
                            )
                            navController.navigate(route)
                        }
                    }
                }
            )
        }

        composable<AppRoutes.ReadingScreen> {
            ReadingScreen(
                onBack = {
                    navController.popBackStack()
                },
                onSettings = {
                    navController.navigate(AppRoutes.SettingsScreen())
                },
                onPlayVerse = { chapterId, verseId ->
                    val route = AppRoutes.Recitation.Config(
                        chapterId = chapterId,
                        verseId = verseId,
                        canChangeVerse = true,
                        canChangeChapter = false
                    )
                    navController.navigate(route)
                },
                onSelectChapter = { chapterId ->
                    val route = AppRoutes.ReadingScreen(chapterId)
                    navController.navigate(route) {
                        popUpTo(AppRoutes.HomeScreen)
                    }
                }
            )
        }

        composable<AppRoutes.Recitation.Config> { navBackStackEntry ->
            val config = navBackStackEntry.toRoute<AppRoutes.Recitation.Config>()
            LaunchedEffect(Unit) {
                viewModel.initConfig(config)
            }
            RecitationConfigScreen(
                viewModel = viewModel,
                canChangeChapter = config.canChangeChapter,
                canChangeReciter = config.canChangeReciter,
                canChangeVerse = config.canChangeVerse,
                recitationEvents = {
                    when (it) {
                        is RecitationEvents.OnBack -> {
                            navController.popBackStack()
                        }

                        is RecitationEvents.ChaptersDialog -> {
                            val route = AppRoutes.Recitation.Pick.Chapter(it.chapterId)
                            navController.navigate(route)
                        }

                        is RecitationEvents.RecitersDialog -> {
                            val route = AppRoutes.Recitation.Pick.Reciter(
                                reciterId = it.reciterId,
                                filter = it.filter
                            )
                            navController.navigate(route)
                        }

                        is RecitationEvents.ConfirmDownload -> {
                            navController.navigate(AppRoutes.Recitation.Download.Confirm)
                        }

                        is RecitationEvents.StartOnline -> {
                            navController.navigate(AppRoutes.Recitation.Playback)
                        }

                        is RecitationEvents.StartLocally -> {
                            navController.navigate(AppRoutes.Recitation.Playback)
                        }
                    }
                }
            )
        }
        dialog<AppRoutes.Recitation.Pick.Chapter> {
            val chapterId = it.toRoute<AppRoutes.Recitation.Pick.Chapter>().chapterId
            PickChapterDialog(
                selectedChapterId = chapterId ?: 1,
                onBack = { navController.popBackStack() },
                onSelectChapter = { chapterId ->
                    viewModel.getChapter(chapterId)
                    navController.popBackStack()
                }
            )
        }

        dialog<AppRoutes.Recitation.Pick.Reciter> {
            val reciterRoute = it.toRoute<AppRoutes.Recitation.Pick.Reciter>()
            PickReciterDialog(
                selectedReciterId = reciterRoute.reciterId.orEmpty(),
                filterReciters = reciterRoute.filter,
                onBack = { navController.popBackStack() },
                onSelectReciter = { reciter ->
                    viewModel.addReciter(reciter.id)
                    navController.popBackStack()
                }
            )
        }

        dialog<AppRoutes.Recitation.Download.Confirm> {
            ConfirmDownloadDialog(
                recitationFlow = viewModel.recitationFlow,
                onDownload = {
                    navController.popBackStack()
                    navController.navigate(AppRoutes.Recitation.Download)
                },
                onPlayOnline = {
                    viewModel.setOnlineDataSource()
                    navController.navigate(AppRoutes.Recitation.Playback)
                },
                onDismiss = {
                    navController.popBackStack(
                        AppRoutes.Recitation.Download.Confirm::class,
                        inclusive = true
                    )
                }
            )
        }

        dialog<AppRoutes.Recitation.Download> {
            val recitation by viewModel.recitationFlow.collectAsState()
            DownloadDialog(
                recitation = recitation,
                onSuccess = {
                    viewModel.updateDownloads()
                    val route = AppRoutes.Recitation.Playback
                    navController.navigate(route)
                },
                onDismiss = { navController.popBackStack() }
            )
        }

        composable<AppRoutes.Recitation.Playback> {
            PlaybackScreen(
                onBack = { navController.popBackStack() },
                onSettings = {
                    val route = AppRoutes.SettingsScreen(reading = false)
                    navController.navigate(route)
                }
            )
        }


        dialog<AppRoutes.SettingsScreen> {
            val reading = it.toRoute<AppRoutes.SettingsScreen>().reading
            SettingsScreen(
                reading = reading,
                onBack = { navController.popBackStack() }
            )
        }
    }
}