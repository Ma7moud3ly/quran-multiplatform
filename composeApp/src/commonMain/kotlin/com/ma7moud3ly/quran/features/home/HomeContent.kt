package com.ma7moud3ly.quran.features.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma7moud3ly.quran.features.home.history.HistoryPage
import com.ma7moud3ly.quran.features.home.history.testHistory
import com.ma7moud3ly.quran.features.home.index.ChaptersIndexPage
import com.ma7moud3ly.quran.features.home.index.testChaptersIndex
import com.ma7moud3ly.quran.features.home.reciters.RecitersPage
import com.ma7moud3ly.quran.features.home.reciters.testReciters
import com.ma7moud3ly.quran.model.Chapter
import com.ma7moud3ly.quran.model.History
import com.ma7moud3ly.quran.model.Reciter
import com.ma7moud3ly.quran.platform.MyBackHandler
import com.ma7moud3ly.quran.ui.AppTheme
import com.ma7moud3ly.quran.ui.MyScreen
import com.ma7moud3ly.quran.ui.MySurfaceColumn
import com.ma7moud3ly.quran.ui.RoundButton
import com.ma7moud3ly.quran.ui.isCompactDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import quran.composeapp.generated.resources.Res
import quran.composeapp.generated.resources.app_name
import quran.composeapp.generated.resources.app_repo
import quran.composeapp.generated.resources.app_version
import quran.composeapp.generated.resources.bug_report
import quran.composeapp.generated.resources.home_search
import quran.composeapp.generated.resources.home_settings
import quran.composeapp.generated.resources.home_support
import quran.composeapp.generated.resources.home_support_long
import quran.composeapp.generated.resources.logo
import quran.composeapp.generated.resources.search
import quran.composeapp.generated.resources.settings


@Preview
@Composable
private fun HomeScreenPreview() {
    AppTheme(darkTheme = true) {
        HomeScreenContent(
            chapters = { testChaptersIndex },
            reciters = { testReciters },
            historyFlow = flow { testHistory },
            onDeleteHistory = {},
            uiEvents = {}
        )
    }
}

@Preview
@Composable
private fun HomeScreenPreviewLight() {
    AppTheme(darkTheme = false) {
        HomeScreenContent(
            chapters = { testChaptersIndex },
            reciters = { testReciters },
            historyFlow = flow { testHistory },
            onDeleteHistory = {},
            uiEvents = {}
        )
    }
}

@Composable
fun HomeScreenContent(
    chapters: () -> List<Chapter>,
    reciters: () -> List<Reciter>,
    historyFlow: Flow<List<History>>,
    onDeleteHistory: (History) -> Unit,
    uiEvents: (HomeEvents) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()
    fun isRecitersPage() = pagerState.currentPage == HomeTab.Reciters.index

    MyBackHandler {
        if (pagerState.currentPage == 0) {
            uiEvents(HomeEvents.Back)
        } else {
            coroutineScope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage - 1)
            }
        }
    }

    MyScreen(
        space = 8.dp,
        modifier = Modifier.padding(8.dp),
        topBar = {
            if (isCompactDevice()) Header(
                pagerState = pagerState,
                onOpenSettings = {
                    uiEvents(HomeEvents.OpenSettings(reading = isRecitersPage().not()))
                },
                onOpenSearch = { uiEvents(HomeEvents.Search) },
            )
        }
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isCompactDevice().not()) {
                SideMenu(
                    pagerState = pagerState,
                    onOpenSettings = {
                        uiEvents(HomeEvents.OpenSettings(reading = isRecitersPage().not()))
                    },
                    onOpenSearch = { uiEvents(HomeEvents.Search) },
                )
            }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                when (page) {
                    HomeTab.ChaptersIndex.index -> {
                        ChaptersIndexPage(
                            list = chapters(),
                            onOpenChapter = {
                                uiEvents(HomeEvents.OpenChapter(it))
                            },
                            onPlayChapter = {
                                uiEvents(HomeEvents.PlayChapter(it))
                            }
                        )
                    }

                    HomeTab.Reciters.index -> {
                        RecitersPage(
                            list = reciters(),
                            onOpenReciter = {
                                uiEvents(HomeEvents.OpenReciter(it))
                            }
                        )
                    }

                    HomeTab.History.index -> {
                        val history by historyFlow.collectAsState(listOf())
                        HistoryPage(
                            list = history,
                            onDeleteHistory = onDeleteHistory,
                            onOpenHistory = {
                                uiEvents(HomeEvents.OpenHistory(it))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(
    pagerState: PagerState,
    onOpenSearch: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Logo()
            Spacer(Modifier.width(8.dp))
            SectionSupport(Modifier.weight(1f))
            RoundButton(
                icon = Res.drawable.search,
                onClick = onOpenSearch,
                iconPadding = 6.dp
            )
            RoundButton(
                icon = Res.drawable.settings,
                onClick = onOpenSettings,
                iconPadding = 6.dp
            )
        }
        SectionTabs(
            pagerState = pagerState,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun SideMenu(
    pagerState: PagerState,
    onOpenSearch: () -> Unit = {},
    onOpenSettings: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    MySurfaceColumn(
        modifier = Modifier
            .width(150.dp)
            .fillMaxHeight()
            .padding(vertical = 8.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Logo(size = 100.dp)
        HeaderTextButton(
            text = Res.string.home_search,
            icon = Res.drawable.search,
            onClick = onOpenSearch
        )
        HeaderTextButton(
            text = Res.string.home_settings,
            icon = Res.drawable.settings,
            iconSize = 22.dp,
            onClick = onOpenSettings
        )
        SectionSupport(Modifier)

        homeTabs.forEach { tab ->
            ItemTab(
                text = tab.title,
                selected = tab.index == pagerState.currentPage,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(tab.index)
                    }
                }
            )
        }
    }
}

@Composable
private fun Logo(
    color: Color = Color.Transparent,
    size: Dp = 60.dp
) {
    Surface(
        shape = CircleShape,
        color = color,
        border = BorderStroke(
            width = 0.5.dp,
            color = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Icon(
            painter = painterResource(Res.drawable.logo),
            contentDescription = "logo",
            modifier = Modifier.width(size).padding(1.dp),
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SectionTabs(
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val selectedTabIndex by remember { derivedStateOf { pagerState.currentPage } }
    PrimaryTabRow(
        modifier = modifier,
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.Transparent,
        indicator = {
            TabRowDefaults.PrimaryIndicator(
                modifier = Modifier.tabIndicatorOffset(selectedTabIndex),
                color = MaterialTheme.colorScheme.secondary,
                width = 70.dp
            )
        }
    ) {
        homeTabs.forEach { tab ->
            ItemTab(
                text = tab.title,
                selected = tab.index == selectedTabIndex,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(tab.index)
                    }
                }
            )
        }
    }
}

@Composable
internal fun ItemTab(
    text: StringResource,
    fontSize: TextUnit = 16.sp,
    selectedColor: Color = MaterialTheme.colorScheme.secondary,
    unselectedContentColor: Color = MaterialTheme.colorScheme.tertiary,
    enabled: Boolean = true,
    selected: Boolean,
    onClick: () -> Unit
) {
    Tab(
        selected = selected,
        enabled = enabled,
        modifier = Modifier.height(40.dp),
        selectedContentColor = selectedColor,
        unselectedContentColor = unselectedContentColor,
        text = {
            Text(
                text = stringResource(text),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                fontSize = fontSize
            )
        },
        onClick = onClick,
    )
}

@Composable
private fun HeaderTextButton(
    text: StringResource,
    icon: DrawableResource,
    iconSize: Dp = 28.dp,
    iconPadding: Dp = 0.dp,
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.textButtonColors(
            containerColor = background,
            contentColor = color
        )
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = "",
            tint = color,
            modifier = Modifier.size(iconSize).padding(iconPadding)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = stringResource(text),
            style = MaterialTheme.typography.bodyLarge,
            color = color,
            modifier = Modifier
        )
    }
}

@Composable
private fun SectionSupport(modifier: Modifier) {
    val uriHandler = LocalUriHandler.current
    val appRepo = stringResource(Res.string.app_repo)
    fun onSupport() {
        uriHandler.openUri(appRepo)
    }
    if (isCompactDevice()) {
        Column(modifier = modifier) {
            Text(
                text = stringResource(Res.string.app_name) + " " +
                        stringResource(Res.string.app_version),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Normal
            )

            Text(
                text = stringResource(Res.string.home_support_long),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1,
                fontWeight = FontWeight.Normal,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 4.dp).clickable(onClick = ::onSupport)
            )
        }
    } else {
        HeaderTextButton(
            text = Res.string.home_support,
            icon = Res.drawable.bug_report,
            iconSize = 22.dp,
            onClick = ::onSupport
        )
    }
}