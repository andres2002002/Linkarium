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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.habitiora.linkarium.core.ProcessStatus
import com.habitiora.linkarium.domain.model.LinkGarden
import com.habitiora.linkarium.domain.model.LinkGardenWithSeeds
import com.habitiora.linkarium.domain.model.LinkSeed
import com.habitiora.linkarium.ui.navigation.Screens
import com.habitiora.linkarium.ui.screens.gardenManager.GardenManagerDialog
import com.habitiora.linkarium.ui.utils.localNavigator.LocalNavigator
import com.habitiora.linkarium.ui.utils.localNavigator.navigateToRoute
import kotlin.math.absoluteValue

@Composable
fun ShowGardenScreen(
    viewModel: ShowGardenViewModel = hiltViewModel()
) {
    val collections by viewModel.gardens.collectAsState()
    val garden by viewModel.garden.collectAsState()
    val seeds = viewModel.seeds.collectAsLazyPagingItems()
    val openGardenDialog by viewModel.openGardenDialog.collectAsState()
    val navController: NavHostController = LocalNavigator.current

    if (openGardenDialog) {
        GardenManagerDialog(
            onDismiss = { viewModel.setOpenGardenDialog(false) }
        )
    }

    ContentScreen(
        modifier = Modifier.fillMaxWidth(),
        garden = garden,
        seeds = seeds,
        collections = collections,
        onCollectionSelected = { garden ->
            viewModel.setSelectedGardenId(garden.id)
        },
        navigateToAddGarden = {
            viewModel.setOpenGardenDialog(true)
        },
        onEdit = { seed ->
            navController.navigateToRoute(Screens.PlantNew.createRoute(seed.id))
        },
        onDelete = {
            viewModel.onDeleteLinkSeed(it)
        },
        onExport = { uri, context ->
            viewModel.exportGardens(uri, context)
        }
    )
}

@Composable
private fun ContentScreen(
    modifier: Modifier = Modifier,
    garden: LinkGarden?,
    seeds: LazyPagingItems<LinkSeed>,
    collections: List<LinkGarden>,
    onCollectionSelected: (LinkGarden) -> Unit,
    navigateToAddGarden: () -> Unit,
    onEdit: (LinkSeed) -> Unit,
    onDelete: (LinkSeed) -> Unit,
    onExport: (uri: Uri, context: Context) -> Unit
){
    val selectedTabIndex = remember(garden, collections){
        if (garden == null || collections.isEmpty()) return@remember 0
        collections.indexOfFirst { it.id == garden.id }.coerceIn(collections.indices)
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        if (collections.isEmpty() || garden == null) {
            EmptyGardensMessage(
                navigateToAddGarden = navigateToAddGarden
            )
        }
        else {
            TabRowGardens(
                selectedTabIndex = selectedTabIndex,
                collections = collections,
                onCollectionSelected = { index ->
                    onCollectionSelected(collections[index.coerceIn(collections.indices)])
                },
                navigateToAddGarden = navigateToAddGarden
            )
            ExportTest{ uri, context ->
                onExport(uri, context)
            }
            GardenContent(
                modifier = Modifier.weight(1f),
                seeds = seeds,
                indexSelected = selectedTabIndex,
                pages = collections.size,
                onPagedSelected = { index ->
                    onCollectionSelected(collections[index.coerceIn(collections.indices)])
                },
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
    onPagedSelected: (Int) -> Unit,
    onEdit: (LinkSeed) -> Unit,
    onDelete: (LinkSeed) -> Unit
){

    val pagerState = rememberPagerState { pages }

    // Solo actualiza cuando la página cambie realmente
    LaunchedEffect(pagerState.settledPage) {
        onPagedSelected(pagerState.settledPage)
    }

    // Solo mueve el pager si el índice externo cambia
    LaunchedEffect(indexSelected) {
        if (pagerState.currentPage != indexSelected) {
            pagerState.animateScrollToPage(indexSelected)
        }
    }

    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        userScrollEnabled = true
    ) { _ ->
        ShowSeedsScreen(
            modifier = Modifier.fillMaxSize(),
            seeds = seeds,
            onEdit = onEdit,
            onDelete = onDelete
        )
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

@Composable
private fun ExportTest(
    onExport: (uri: Uri, context: Context) -> Unit
){
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri: Uri? ->
        if (uri != null) {
            onExport(uri, context)
        }
    }

    Button(onClick = {
        launcher.launch("links_export.pdf")
    }) { Text("Exportar JSON") }
}