package com.habitiora.linkarium.ui.screens.showGarden

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.habitiora.linkarium.domain.model.LinkGarden
import com.habitiora.linkarium.domain.model.LinkSeed
import com.habitiora.linkarium.ui.navigation.Screens
import com.habitiora.linkarium.ui.screens.gardenManager.GardenManagerDialog
import com.habitiora.linkarium.ui.utils.localNavigator.LocalNavigator
import com.habitiora.linkarium.ui.utils.localNavigator.navigateToRoute

@Composable
fun ShowGardenScreen(
    viewModel: ShowGardenViewModel = hiltViewModel()
) {
    val collections by viewModel.gardens.collectAsState()
    val selectedPageIndex by viewModel.selectedPageIndex.collectAsState()
    val seeds = viewModel.seeds.collectAsLazyPagingItems()

    val navController: NavHostController = LocalNavigator.current

    ContentScreen(
        modifier = Modifier.fillMaxWidth(),
        selectedPageIndex = selectedPageIndex,
        seeds = seeds,
        collections = collections,
        onUserSwipedToPage = viewModel::onUserSwipedToPage,
        navigateToAddGarden = viewModel::onAddGarden,
        onEdit = { seed ->
            navController.navigateToRoute(Screens.PlantNew.createRoute(seed.id))
        },
        onDelete = viewModel::onDeleteLinkSeed
    )
}

@Composable
private fun ContentScreen(
    modifier: Modifier = Modifier,
    selectedPageIndex: Int,
    seeds: LazyPagingItems<LinkSeed>,
    collections: List<LinkGarden>,
    onUserSwipedToPage: (Int) -> Unit,
    navigateToAddGarden: () -> Unit,
    onEdit: (LinkSeed) -> Unit,
    onDelete: (LinkSeed) -> Unit,
){

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        if (collections.isEmpty()) {
            EmptyGardensMessage(
                navigateToAddGarden = navigateToAddGarden
            )
        }
        else {
            TabRowGardens(
                selectedTabIndex = selectedPageIndex,
                collections = collections,
                onCollectionSelected = onUserSwipedToPage,
                navigateToAddGarden = navigateToAddGarden
            )
            GardenContent(
                modifier = Modifier.weight(1f),
                seeds = seeds,
                indexSelected = selectedPageIndex,
                pages = collections.size,
                onUserSwipedToPage = onUserSwipedToPage,
                onEdit = onEdit,
                onDelete = onDelete
            )
        }
    }
}

@Composable
private fun GardenContent(
    modifier: Modifier = Modifier,
    seeds: LazyPagingItems<LinkSeed>,
    indexSelected: Int,
    pages: Int,
    onUserSwipedToPage: (Int) -> Unit,
    onEdit: (LinkSeed) -> Unit,
    onDelete: (LinkSeed) -> Unit
){

    val pagerState = rememberPagerState { pages }

    // Sincronizar: ViewModel -> PagerState
    LaunchedEffect(indexSelected) {
        if (pagerState.currentPage != indexSelected) {
            pagerState.animateScrollToPage(indexSelected)
        }
    }

    // Sincronizar: PagerState -> ViewModel (gestos del usuario)
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        snapshotFlow { pagerState.currentPage to pagerState.isScrollInProgress }
            .collect { (page, isScrolling) ->
                if (!isScrolling && page != indexSelected) {
                    onUserSwipedToPage(page)
                }
            }
    }

    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        pageSpacing = 32.dp,
        userScrollEnabled = true
    ) { page ->
        key(page) {
            ShowSeedsScreen(
                modifier = Modifier.fillMaxSize(),
                seeds = seeds,
                onEdit = onEdit,
                onDelete = onDelete
            )
        }
    }
}

@Composable
private fun TabRowGardens(
    selectedTabIndex: Int,
    collections: List<LinkGarden>,
    onCollectionSelected: (Int) -> Unit,
    navigateToAddGarden: () -> Unit
){
    Row(
        modifier = Modifier,
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ){
        AddGardenButton(
            modifier = Modifier,
            navigateToAddGarden = navigateToAddGarden
        )
        PrimaryScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            edgePadding = 0.dp
        ) {
            collections.forEachIndexed { index, collection ->
                Tab(
                    text = { Text(collection.name) },
                    selected = index == selectedTabIndex,
                    onClick = { onCollectionSelected(index) }
                )
            }
        }
    }
}

@Composable
private fun AddGardenButton(
    modifier: Modifier = Modifier,
    navigateToAddGarden: () -> Unit
){
    Box(
        modifier = modifier
            .padding(start = 4.dp)
            .clip(MaterialTheme.shapes.small)
            .clickable { navigateToAddGarden() }
            .padding(start = 8.dp, end = 8.dp)
            .size(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.weight(1f),
                imageVector = Icons.Default.Add,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = "Add Garden"
            )
            Text(
                modifier = Modifier.padding(top = 2.dp),
                text = "New",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun EmptyGardensMessage(
    navigateToAddGarden: () -> Unit
){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = navigateToAddGarden
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Garden")
            }
            Text(text = "Add Garden")
        }
    }
}