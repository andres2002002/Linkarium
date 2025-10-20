package com.habitiora.linkarium.ui.screens.gardensScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.habitiora.linkarium.domain.model.LinkGarden
import com.habitiora.linkarium.ui.navigation.Screens
import com.habitiora.linkarium.ui.utils.localNavigator.LocalNavigator
import com.habitiora.linkarium.ui.utils.localNavigator.navigateToRoute

@Composable
fun GardensScreen(
    viewModel: GardensViewModel = hiltViewModel()
){
    val gardens by viewModel.gardens.collectAsState()
    val navController: NavHostController = LocalNavigator.current

    GardensContent(
        gardens = gardens,
        onClick = { id ->
            navController.navigateToRoute(Screens.ShowSeeds.createRoute(id))
        }
    )
}

@Composable
private fun GardensContent(
    gardens: List<LinkGarden>,
    onClick: (Long) -> Unit
){
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(gardens, key = { item -> item.id }) { item ->
            GardenItem(
                garden = item,
                onClick = onClick
            )
        }
    }
}

@Composable
private fun GardenItem(
    modifier: Modifier = Modifier,
    garden: LinkGarden,
    onClick: (Long) -> Unit
){
    Card(
        modifier = modifier,
        onClick = { onClick(garden.id) },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LeadingIcon(
                modifier = Modifier
                    .size(40.dp)
            )
            HeadContent(
                modifier = Modifier.weight(1f),
                garden = garden
            )
            TrailingIcon(
                onMoreClick = {}
            )
        }
    }
}

@Composable
private fun LeadingIcon(
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.fillMaxSize(0.8f),
            imageVector = Icons.Filled.Folder,
            contentDescription = null
        )
    }
}

@Composable
private fun HeadContent(
    modifier: Modifier = Modifier,
    garden: LinkGarden
){
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = garden.name,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
        garden.description.takeIf { it.isNotBlank() }?.let { description ->
            Text(
                text = description,
                style = MaterialTheme.typography.labelMedium,
                fontStyle = FontStyle.Italic
            )
        }
    }
}

@Composable
private fun TrailingIcon(
    modifier: Modifier = Modifier,
    onMoreClick: () -> Unit
){
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onMoreClick
        ) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "More options"
            )
        }
    }
}

