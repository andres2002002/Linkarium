package com.habitiora.linkarium.ui.screens.showGarden

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.habitiora.linkarium.domain.model.LinkGarden
import com.habitiora.linkarium.domain.model.LinkGardenWithSeeds
import com.habitiora.linkarium.domain.model.LinkSeed
import com.habitiora.linkarium.ui.components.EmptyMessage
import com.habitiora.linkarium.ui.screens.gardenManager.GardenManagerDialog

@Composable
fun ShowGardenScreen(
    viewModel: ShowGardenViewModel = hiltViewModel()
) {
    val collections by viewModel.gardens.collectAsState()
    val selectedCollection by viewModel.selectedGarden.collectAsState()
    val openGardenDialog by viewModel.openGardenDialog.collectAsState()

    if (openGardenDialog) {
        GardenManagerDialog(
            onDismiss = { viewModel.setOpenGardenDialog(false) }
        )
    }

    ContentScreen(
        modifier = Modifier.fillMaxWidth(),
        selectedCollection = selectedCollection,
        collections = collections,
        onCollectionSelected = { garden ->
            viewModel.setSelectedGardenId(garden.id)
        },
        navigateToAddGarden = {
            viewModel.setOpenGardenDialog(true)
        }
    )
}

@Composable
private fun ContentScreen(
    modifier: Modifier = Modifier,
    selectedCollection: LinkGardenWithSeeds,
    collections: List<LinkGarden>,
    onCollectionSelected: (LinkGarden) -> Unit,
    navigateToAddGarden: () -> Unit
){
    val selectedTabIndex = remember(selectedCollection, collections){
        if (collections.isEmpty()) return@remember 0
        collections.indexOfFirst { it.id == selectedCollection.garden.id }.coerceIn(collections.indices)
    }
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
                selectedTabIndex = selectedTabIndex,
                collections = collections,
                onCollectionSelected = { index ->
                    onCollectionSelected(collections[index.coerceIn(collections.indices)])
                },
                navigateToAddGarden = navigateToAddGarden
            )
            if (selectedCollection.seeds.isEmpty()) {
                EmptyMessage(
                    modifier = Modifier.weight(1f),
                    message = "No Seeds"
                )
            }
            else {
                GardenContent(
                    modifier = Modifier.weight(1f),
                    seeds = selectedCollection.seeds
                )
            }
        }
    }
}

@Composable
private fun GardenContent(
    modifier: Modifier = Modifier,
    seeds: List<LinkSeed>
){
    LazyColumn(
        modifier = modifier
    ) {
        items(seeds, key = { it.id }) { seed ->
            Text(seed.name)
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
        TabRow(selectedTabIndex = selectedTabIndex) {
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
                contentDescription = "Add Garden"
            )
            Text(
                modifier = Modifier.padding(top = 2.dp),
                text = "New",
                style = MaterialTheme.typography.labelSmall
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