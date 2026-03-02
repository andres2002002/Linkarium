package com.habitiora.linkarium.ui.screens.showGarden

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.habitiora.linkarium.R
import com.habitiora.linkarium.domain.model.LinkGarden
import com.habitiora.linkarium.domain.model.LinkSeed

@Composable
fun ShowGardenScreen(
    viewModel: ShowGardenViewModel = hiltViewModel()
) {
    val collections by viewModel.gardens.collectAsState()
    val selectedPageIndex by viewModel.selectedPageIndex.collectAsState()
    val seeds = viewModel.seeds.collectAsLazyPagingItems()

    ContentScreen(
        modifier = Modifier.fillMaxWidth(),
        selectedPageIndex = selectedPageIndex,
        seeds = seeds,
        collections = collections,
        onUserSwipedToPage = viewModel::onUserSwipedToPage,
        navigateToAddGarden = viewModel::onAddGarden,
        onEdit = { seed ->
            viewModel.navigateToPlantNew(seed.id)
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
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (collections.isEmpty()) {
            EmptyGardensMessage(
                navigateToAddGarden = navigateToAddGarden
            )
        } else {
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
) {

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

// ─── TabRowGardens ────────────────────────────────────────────────────────────
@Composable
private fun TabRowGardens(
    selectedTabIndex: Int,
    collections: List<LinkGarden>,
    onCollectionSelected: (Int) -> Unit,
    navigateToAddGarden: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AddGardenButton(navigateToAddGarden = navigateToAddGarden)

        PrimaryScrollableTabRow(
            modifier = Modifier.weight(1f),
            selectedTabIndex = selectedTabIndex,
            edgePadding = 0.dp,
            divider = {}
        ) {
            collections.forEachIndexed { index, collection ->
                Tab(
                    selected = index == selectedTabIndex,
                    onClick = { onCollectionSelected(index) },
                    text = {
                        Text(
                            text = collection.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = if (index == selectedTabIndex) FontWeight.SemiBold
                            else FontWeight.Normal
                        )
                    }
                )
            }
        }
    }
}

// ─── AddGardenButton ──────────────────────────────────────────────────────────
/**
 * Icono + etiqueta compacto.
 * Mismo contenedor primary.copy(alpha=0.12f) + shapes.medium que GardenSelector,
 * DialogHeader y GardenItem.
 */
@Composable
private fun AddGardenButton(
    modifier: Modifier = Modifier,
    navigateToAddGarden: () -> Unit
) {
    Column(
        modifier = modifier
            .width(56.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable { navigateToAddGarden() }
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    shape = MaterialTheme.shapes.medium
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.new_garden),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }
        Text(
            text = stringResource(R.string.new_garden_button),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

// ─── EmptyGardensMessage ──────────────────────────────────────────────────────
/**
 * Empty state premium con entrada animada.
 * Doble capa de fondo radial + icono LocalFlorist — mismo lenguaje visual
 * que el resto del sistema.
 */
@Composable
private fun EmptyGardensMessage(
    navigateToAddGarden: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(tween(280)) + scaleIn(
                initialScale = 0.92f,
                animationSpec = tween(280)
            ),
            exit = fadeOut(tween(200)) + scaleOut(
                targetScale = 0.92f,
                animationSpec = tween(200)
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Icono con halo radial + contenedor cuadrado
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            brush = Brush.radialGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                    Color.Transparent
                                )
                            ),
                            shape = MaterialTheme.shapes.extraLarge
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                shape = MaterialTheme.shapes.large
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFlorist,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                // Textos
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.without_gardens),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = stringResource(R.string.create_first_garden),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Botón CTA — coherente con ActionButtons del diálogo
                Button(
                    onClick = navigateToAddGarden,
                    shape = MaterialTheme.shapes.large,
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.new_garden),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}