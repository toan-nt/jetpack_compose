package vtoan.n.jetpackcomposeexample.layout

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.launch
import vtoan.n.jetpackcomposeexample.R
import kotlin.math.absoluteValue

@Preview
@Composable
private fun PageExamplePreview() {
    PageExampleApp()
}

@Composable
fun PageExampleApp() {
    PageExampleView {
        HorizontalPagerExample()
        VerticalPagerExample()
        PagerScrollToItemExample()
        PageChangeExample()
        PagerWithTabsExample()
        PagerIndicator()
        PagerWithEffect()
    }
}

@Composable
private fun PageExampleView(content: @Composable () -> Unit) {
    Column (
        Modifier
            .fillMaxSize()
            .background(Color.Gray)
            .verticalScroll(rememberScrollState())
    ) {
        content()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HorizontalPagerExample() {
    Card(
        shape = RectangleShape, modifier = Modifier
            .height(128.dp)
            .padding(8.dp)
    ) {
        HorizontalPager(pageCount = 10, contentPadding = PaddingValues(start = 16.dp)) { page ->
            Text(
                text = "HorizontalPagerExample: $page",
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun VerticalPagerExample() {
    VerticalPager(pageCount = 10, Modifier.height(128.dp)) { page ->
        Text(
            text = "VerticalPagerExample: $page",
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Cyan)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PagerScrollToItemExample() {
    Card(
        shape = RectangleShape, modifier = Modifier
            .height(128.dp)
            .padding(8.dp)
    ) {

        Box {
            val pagerState = rememberPagerState()
            HorizontalPager(pageCount = 10, state = pagerState) { page ->
                Text(
                    text = "HorizontalPagerExample: $page",
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

            val coroutineScope = rememberCoroutineScope()
            Button(onClick = {
                Log.d("PagerScrollToItemExample", "Jump to Page 5")
                coroutineScope.launch {
                    // Call scroll to on pagerState
//                    pagerState.scrollToPage(5)
                    pagerState.animateScrollToPage(5)
                }
            }, modifier = Modifier.align(Alignment.BottomCenter)) {
                Text("Jump to Page 5")
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PageChangeExample() {
    val pagerState = rememberPagerState()

    LaunchedEffect(pagerState) {
        // Collect from the a snapshotFlow reading the currentPage
        snapshotFlow { pagerState.currentPage }.collect { page ->
            // Do something with each page change, for example:
            // viewModel.sendPageSelectedEvent(page)
            Log.d("Page change", "Page changed to $page")
        }
    }

    Card(
        shape = RectangleShape, modifier = Modifier
            .height(256.dp)
            .fillMaxWidth()
            .background(Color.LightGray)
            .padding(8.dp)
    ) {
        Column(Modifier.fillMaxSize()) {
            VerticalPager(
                pageCount = 10,
                state = pagerState,
                modifier = Modifier.height(128.dp)
            ) { page ->
                Text(text = "Page: $page", modifier = Modifier.fillMaxSize())
            }

            Column(modifier = Modifier
                .weight(0.1f)
                .fillMaxWidth()) {
                Text(text = "Current Page: ${pagerState.currentPage}")
                Text(text = "Target Page: ${pagerState.targetPage}")
                Text(text = "Settled Page Offset: ${pagerState.settledPage}")
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PagerWithTabsExample() {
    val pages = listOf("Movies", "Books", "Shows", "Fun")
    // [START android_compose_layouts_pager_tabs]
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier
        .fillMaxWidth()
        .height(256.dp)) {
        TabRow(selectedTabIndex = pagerState.currentPage) {
            pages.forEachIndexed { index, s ->
                Tab(text = { Text(text = s) }, selected = pagerState.currentPage == index, onClick = {
                    Log.d("PagerWithTabsExample", "selected page: $s")
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(page = index)
                    }
                })
            }
        }

        HorizontalPager(
            pageCount = pages.size,
            state = pagerState,
        ) { page ->
            Text("Page: ${pages[page]}", Modifier.fillMaxSize())
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun PagerIndicator() {
    Box(Modifier.height(256.dp)) {
        // [START android_compose_pager_indicator]
        val pageCount = 5
        val pagerState = rememberPagerState()
        val girlPhotos = listOf(
            R.drawable.china_girl_1,
            R.drawable.china_girl_2,
            R.drawable.china_girl_3,
            R.drawable.china_girl_4,
            R.drawable.china_girl_5
        )

        val fling = PagerDefaults.flingBehavior(
            state = pagerState,
            pagerSnapDistance = PagerSnapDistance.atMost(3)
        )

        HorizontalPager(
            pageCount = pageCount,
            state = pagerState,
            beyondBoundsPageCount = pageCount,
            flingBehavior = fling
        ) { page ->
            // Our page content
            Box() {
                Image(painter = painterResource(id = girlPhotos[page]), contentDescription = null, contentScale = ContentScale.Fit)
            }
        }
        Row(
            Modifier
                .height(16.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }
        // [END android_compose_pager_indicator]
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun PagerWithEffect() {
    // [START android_compose_layouts_pager_transformation]
    val pagerState = rememberPagerState()
    val girlPhotos = listOf(
        R.drawable.china_girl_1,
        R.drawable.china_girl_2,
        R.drawable.china_girl_3,
        R.drawable.china_girl_4,
        R.drawable.china_girl_5
    )
    HorizontalPager(pageCount = 5, state = pagerState, pageSize = TwoPagesPerViewport) { page ->
        Card(
            Modifier
                .fillMaxWidth()
                .height(200.dp)
                .graphicsLayer {
                    // Calculate the absolute offset for the current page from the
                    // scroll position. We use the absolute value which allows us to mirror
                    // any effects for both directions
                    val pageOffset = (
                            (pagerState.currentPage - page) + pagerState
                                .currentPageOffsetFraction
                            ).absoluteValue

                    // We animate the alpha, between 50% and 100%
                    alpha = lerp(
                        start = 0.5f,
                        stop = 1.0f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )
                }
        ) {
            // Card content
            Image(painter = painterResource(id = girlPhotos[page]), contentDescription = null, contentScale = ContentScale.Crop)
        }
    }
    // [END android_compose_layouts_pager_transformation]
}

@OptIn(ExperimentalFoundationApi::class)
private val TwoPagesPerViewport = object : PageSize {
    override fun Density.calculateMainAxisPageSize(
        availableSpace: Int,
        pageSpacing: Int
    ): Int {
        return (availableSpace - 2 * pageSpacing) / 2
    }
}


