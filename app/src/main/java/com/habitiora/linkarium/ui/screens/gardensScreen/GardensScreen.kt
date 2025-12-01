package com.habitiora.linkarium.ui.screens.gardensScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.akari.uicomponents.reorderableComponents.AkariReorderableItem
import com.akari.uicomponents.reorderableComponents.AkariReorderableLazyColumn
import com.akari.uicomponents.reorderableComponents.DragActivation
import com.akari.uicomponents.reorderableComponents.rememberAkariReorderableLazyState
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
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp).fillMaxSize(),
        gardens = gardens,
        onClick = { id ->
            navController.navigateToRoute(Screens.ShowSeeds.createRoute(id))
        },
        onMove = viewModel::moveGarden,
        onDragStart = viewModel::onDragStart,
        onDragEnd = viewModel::onDragEnd
    )
}

@Composable
private fun GardensContent(
    modifier: Modifier = Modifier,
    gardens: List<LinkGarden>,
    onClick: (Long) -> Unit,
    onMove: (Int, Int) -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit
){
    val state = rememberAkariReorderableLazyState<LinkGarden>(
        onMove = { from, to ->
            onMove(from, to)
        },
        onDragEnd = onDragEnd,
        onDragStart = onDragStart
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AddNewGarden(
            modifier = Modifier.fillMaxWidth(),
            onClick = {}
        )
        AkariReorderableLazyColumn(
            modifier = Modifier.weight(1f),
            items = gardens,
            state = state,
            key = { item -> item.id },
            contentPadding = PaddingValues(bottom = 64.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            dragActivation = DragActivation.LongPress
        ) { item, isDragging ->
            GardenItem(
                modifier = Modifier.akariDragHandle(),
                isDragging = isDragging,
                garden = item,
                onClick = onClick
            )
        }
    }
}

@Composable
private fun GardenItem(
    modifier: Modifier = Modifier,
    isDragging: Boolean = false,
    garden: LinkGarden,
    onClick: (Long) -> Unit
){
    Card(
        modifier = modifier,
        onClick = { onClick(garden.id) },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors().copy(
            containerColor = if (isDragging) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
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

@Composable
private fun AddNewGarden(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
){
    Card(
        modifier = modifier,
        onClick = { onClick() },
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
            Icon(
                modifier = Modifier.size(40.dp),
                imageVector = Icons.Default.Add,
                contentDescription = "Add new garden"
            )
            Text(
                text = "Add new garden",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}