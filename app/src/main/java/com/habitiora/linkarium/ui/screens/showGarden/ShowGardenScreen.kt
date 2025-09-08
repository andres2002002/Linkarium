package com.habitiora.linkarium.ui.screens.showGarden

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.habitiora.linkarium.domain.model.LinkGarden
import com.habitiora.linkarium.domain.model.LinkGardenWithSeeds
import com.habitiora.linkarium.domain.model.LinkSeed
import com.habitiora.linkarium.ui.components.EmptyMessage

@Composable
fun ShowGardenScreen(
    viewModel: ShowGardenViewModel = hiltViewModel()
) {
    val collections by viewModel.gardens.collectAsState()
    val selectedCollection by viewModel.selectedGarden.collectAsState()
    ContentScreen(
        modifier = Modifier.fillMaxWidth(),
        selectedCollection = selectedCollection,
        collections = collections,
        onCollectionSelected = { garden ->
            viewModel.setSelectedGardenId(garden.id)
        }
    )
}

@Composable
private fun ContentScreen(
    modifier: Modifier = Modifier,
    selectedCollection: LinkGardenWithSeeds,
    collections: List<LinkGarden>,
    onCollectionSelected: (LinkGarden) -> Unit
){
    val selectedTabIndex = remember(selectedCollection, collections){
        collections.indexOfFirst { it.id == selectedCollection.garden.id }.coerceIn(collections.indices)
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        if (collections.isEmpty()) {
            EmptyMessage(
                modifier = Modifier.weight(1f),
                message = "No Gardens"
            )
        }
        else {
            TabRowGardens(
                selectedTabIndex = selectedTabIndex,
                collections = collections,
                onCollectionSelected = { index ->
                    onCollectionSelected(collections[index.coerceIn(collections.indices)])
                }
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
    onCollectionSelected: (Int) -> Unit
){
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