package com.habitiora.linkarium.ui.screens.gardensScreen

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.habitiora.linkarium.ui.screens.showGarden.ShowSeedsScreen

@Composable
fun ShowSeedsOfGarden(
    viewModel: ShowSeedsOfGardenViewModel = hiltViewModel()
){
    val seeds = viewModel.seeds.collectAsLazyPagingItems()

    ShowSeedsScreen(
        seeds = seeds,
        onEdit = { seed ->
            viewModel.navigateToPlantNew(seed.id)
        },
        onDelete = { seed ->
            viewModel.onDeleteLinkSeed(seed)
        }
    )
}