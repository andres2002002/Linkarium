package com.habitiora.linkarium.ui.screens.gardensScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.habitiora.linkarium.core.ProcessStatus
import com.habitiora.linkarium.domain.model.LinkSeed
import com.habitiora.linkarium.ui.navigation.Screens
import com.habitiora.linkarium.ui.screens.showGarden.ShowSeedsScreen
import com.habitiora.linkarium.ui.utils.localNavigator.LocalNavigator
import com.habitiora.linkarium.ui.utils.localNavigator.navigateToRoute

@Composable
fun ShowSeedsOfGarden(
    viewModel: ShowSeedsOfGardenViewModel = hiltViewModel()
){
    val seeds = viewModel.seeds.collectAsLazyPagingItems()
    val navController = LocalNavigator.current

    ShowSeedsScreen(
        seeds = seeds,
        onEdit = { seed ->
            navController.navigateToRoute(Screens.PlantNew.createRoute(seed.id))
        },
        onDelete = { seed ->
            viewModel.onDeleteLinkSeed(seed)
        }
    )
}