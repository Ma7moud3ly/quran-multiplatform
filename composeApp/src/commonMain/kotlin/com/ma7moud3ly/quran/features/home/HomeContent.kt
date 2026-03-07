package com.ma7moud3ly.quran.features.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma7moud3ly.quran.features.home.bookmarks.BookmarksPage
import com.ma7moud3ly.quran.features.home.bookmarks.testBookmarks
import com.ma7moud3ly.quran.features.home.index.ChaptersIndexPage
import com.ma7moud3ly.quran.features.home.index.testChaptersIndex
import com.ma7moud3ly.quran.features.home.reciters.RecitersPage
import com.ma7moud3ly.quran.features.home.reciters.testReciters
import com.ma7moud3ly.quran.model.Bookmark
import com.ma7moud3ly.quran.model.Chapter
import com.ma7moud3ly.quran.model.Reciter
import com.ma7moud3ly.quran.platform.MyBackHandler
import com.ma7moud3ly.quran.ui.AppTheme
import com.ma7moud3ly.quran.ui.MyScreen
import com.ma7moud3ly.quran.ui.MySurfaceColumn
import com.ma7moud3ly.quran.ui.MySurfaceRow
import com.ma7moud3ly.quran.ui.RoundButton
import com.ma7moud3ly.quran.ui.isCompactDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.ma7moud3ly.quran.resources.Res
import com.ma7moud3ly.quran.resources.history
import com.ma7moud3ly.quran.resources.home_about
import com.ma7moud3ly.quran.resources.home_history
import com.ma7moud3ly.quran.resources.home_search
import com.ma7moud3ly.quran.resources.home_settings
import com.ma7moud3ly.quran.resources.home_slogan
import com.ma7moud3ly.quran.resources.internet
import com.ma7moud3ly.quran.resources.logo
import com.ma7moud3ly.quran.resources.search
import com.ma7moud3ly.quran.resources.settings
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Preview
@Composable
private fun HomeScreenPreview() {
    AppTheme(darkTheme = true) {
        HomeScreenContent(
            chapters = { testChaptersIndex },
            reciters = { testReciters },
            bookmarksFlow = flow { testBookmarks },
            onDeleteBookmark = {},
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
            bookmarksFlow = flow { testBookmarks },
            onDeleteBookmark = {},
            uiEvents = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    chapters: () -> List<Chapter>,
    reciters: () -> List<Reciter>,
    bookmarksFlow: Flow<List<Bookmark>>,
    onDeleteBookmark: (Bookmark) -> Unit,
    uiEvents: (HomeEvents) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )
    val showAppBars = isCompactDevice()
    val pagerState = rememberPagerState(pageCount = { HomeTab.entries.size })
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
        modifier = Modifier
            .padding(8.dp)
            .then(
                if (showAppBars) Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                else Modifier
            ),
        topBar = {
            if (showAppBars) Header(
                scrollBehavior = scrollBehavior,
                pagerState = pagerState,
                onOpenSettings = {
                    uiEvents(HomeEvents.OpenSettings(reading = isRecitersPage().not()))
                },
                onOpenSearch = { uiEvents(HomeEvents.Search) },
                onOpenHistory = { uiEvents(HomeEvents.OpenHistory) },
                onOpenAbout = { uiEvents(HomeEvents.OpenAbout) }
            )
        }
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (showAppBars.not()) {
                SideMenu(
                    pagerState = pagerState,
                    onOpenSettings = {
                        val event = HomeEvents.OpenSettings(reading = isRecitersPage().not())
                        uiEvents(event)
                    },
                    onOpenSearch = { uiEvents(HomeEvents.Search) },
                    onOpenHistory = { uiEvents(HomeEvents.OpenHistory) },
                    onOpenAbout = { uiEvents(HomeEvents.OpenAbout) }
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

                    HomeTab.Bookmarks.index -> {
                        val bookmarks by bookmarksFlow.collectAsState(listOf())
                        BookmarksPage(
                            list = bookmarks,
                            onDeleteBookmark = onDeleteBookmark,
                            onOpenBookmark = {
                                uiEvents(HomeEvents.OpenBookmark(it))
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Header(
    scrollBehavior: TopAppBarScrollBehavior,
    pagerState: PagerState,
    onOpenSearch: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAbout: () -> Unit
) {
    Column {
        TopAppBar(
            modifier = Modifier.fillMaxWidth(),
            expandedHeight = 75.dp,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                scrolledContainerColor = MaterialTheme.colorScheme.background
            ),
            scrollBehavior = scrollBehavior,
            navigationIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Logo(size = 50.dp)
                    Text(
                        text = stringResource(Res.string.home_slogan),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            },
            actions = {
                MySurfaceRow(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)

                ) {
                    RoundButton(
                        icon = Res.drawable.search,
                        onClick = onOpenSearch,
                        background = Color.Transparent,
                        iconSize = 26.dp,
                        iconPadding = 0.dp
                    )
                    RoundButton(
                        icon = Res.drawable.history,
                        onClick = onOpenHistory,
                        background = Color.Transparent,
                        iconSize = 24.dp,
                        iconPadding = 0.dp
                    )
                    RoundButton(
                        icon = Res.drawable.settings,
                        onClick = onOpenSettings,
                        background = Color.Transparent,
                        iconSize = 22.dp,
                        iconPadding = 0.dp
                    )
                    RoundButton(
                        icon = Res.drawable.internet,
                        onClick = onOpenAbout,
                        background = Color.Transparent,
                        iconSize = 22.dp,
                        iconPadding = 0.dp
                    )
                }
            },
            title = {}
        )
        SectionTabs(
            pagerState = pagerState,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SideMenu(
    pagerState: PagerState,
    onOpenSearch: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onOpenHistory: () -> Unit = {},
    onOpenAbout: () -> Unit = {}
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

        HomeTab.entries.forEach { tab ->
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
        HeaderTextButton(
            text = Res.string.home_history,
            icon = Res.drawable.history,
            onClick = onOpenHistory
        )
        HeaderTextButton(
            text = Res.string.home_about,
            icon = Res.drawable.internet,
            onClick = onOpenAbout
        )
    }
}


@Composable
internal fun Logo(
    color: Color = Color.Transparent,
    animated: Boolean = true,
    size: Dp = 50.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orbit")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing)
        ),
        label = "orbit_angle"
    )

    val orbitRadius = (size / 2) + 4.dp
    val planetSize = 5.dp
    val planetShadow = MaterialTheme.colorScheme.onPrimary
    val orbitColor = MaterialTheme.colorScheme.secondary
    var showAnimation by remember { mutableStateOf(animated) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(size + 28.dp)
    ) {
        // Orbit ring
        Canvas(modifier = Modifier.matchParentSize()) {
            val radius = orbitRadius.toPx()
            drawCircle(
                color = orbitColor,//.copy(alpha = 0.15f),
                radius = radius,
                style = Stroke(
                    width = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(6f, 6f)
                    )
                )
            )
        }

        // Logo
        Surface(
            shape = CircleShape,
            color = color
        ) {
            Image(
                painter = painterResource(Res.drawable.logo),
                contentDescription = "logo",
                modifier = Modifier
                    .width(size)
                    .padding(1.dp)
                    .clickable(
                        enabled = animated,
                        onClick = { showAnimation = showAnimation.not() }
                    ),
            )
        }

        // Orbiting planet
        if (showAnimation) Canvas(modifier = Modifier.matchParentSize()) {
            val radius = orbitRadius.toPx()
            val rad = angle.toDouble() * PI / 180
            val cx = center.x + radius * cos(rad).toFloat()
            val cy = center.y + radius * sin(rad).toFloat()

            // Planet glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        planetShadow.copy(alpha = 0.6f),
                        planetShadow.copy(alpha = 0f)
                    ),
                    center = Offset(cx, cy),
                    radius = planetSize.toPx() * 2
                ),
                radius = planetSize.toPx() * 2,
                center = Offset(cx, cy)
            )

            // Planet dot
            drawCircle(
                color = Color.White,
                radius = planetSize.toPx(),
                center = Offset(cx, cy)
            )
        }
    }
}

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
        HomeTab.entries.forEach { tab ->
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