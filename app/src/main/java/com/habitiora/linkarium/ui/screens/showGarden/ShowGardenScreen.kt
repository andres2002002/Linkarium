package com.habitiora.linkarium.ui.screens.showGarden

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
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

// ─── TabRowGardens ────────────────────────────────────────────────────────────
/**
 * Fila de tabs con botón de nuevo jardín.
 * El botón usa el mismo contenedor primary sólido que AddNewGarden en GardensScreen
 * para señalizar que es un CTA.
 * El TabRow usa edgePadding = 0.dp para alinearse limpiamente con el botón.
 */
@Composable
fun TabRowGardens(
    selectedTabIndex: Int,
    collections: List<LinkGarden>,
    onCollectionSelected: (Int) -> Unit,
    navigateToAddGarden: () -> Unit
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        AddGardenButton(navigateToAddGarden = navigateToAddGarden)

        PrimaryScrollableTabRow(
            modifier         = Modifier.weight(1f),
            selectedTabIndex = selectedTabIndex,
            edgePadding      = 0.dp
        ) {
            collections.forEachIndexed { index, collection ->
                Tab(
                    selected = index == selectedTabIndex,
                    onClick  = { onCollectionSelected(index) },
                    text     = {
                        Text(
                            text       = collection.name,
                            style      = MaterialTheme.typography.labelLarge,
                            fontWeight = if (index == selectedTabIndex) FontWeight.SemiBold
                            else FontWeight.Normal,
                            maxLines   = 1
                        )
                    }
                )
            }
        }
    }
}

// ─── AddGardenButton ──────────────────────────────────────────────────────────
/**
 * Botón compacto de "Nuevo jardín".
 * Icono con fondo primary sólido — mismo patrón que AddNewGarden en GardensScreen
 * para mantener coherencia de CTA en toda la app.
 */
@Composable
fun AddGardenButton(
    modifier: Modifier = Modifier,
    navigateToAddGarden: () -> Unit
) {
    // Usamos Surface clickeable para obtener el ripple correcto sobre el fondo
    Surface(
        modifier       = modifier,
        onClick        = navigateToAddGarden,
        shape          = MaterialTheme.shapes.medium,
        color          = Color.Transparent,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier            = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            // Icono con fondo primary sólido — diferenciador de CTA
            Box(
                modifier         = Modifier
                    .size(28.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector      = Icons.Default.Add,
                    contentDescription = "Nuevo jardín",
                    tint             = MaterialTheme.colorScheme.onPrimary,
                    modifier         = Modifier.size(18.dp)
                )
            }
            Text(
                text  = "Nuevo",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ─── EmptyGardensMessage ──────────────────────────────────────────────────────
/**
 * Estado vacío premium:
 * - Icono LocalFlorist (coherente con el lenguaje de iconos del sistema)
 * - Tarjeta CTA con el mismo degradado horizontal de AddNewGarden / SectionCard
 * - AnimatedVisibility para entrada suave
 */
@Composable
fun EmptyGardensMessage(
    navigateToAddGarden: () -> Unit
) {
    Box(
        modifier         = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = true,
            enter   = fadeIn(tween(280)),
            exit    = fadeOut(tween(280))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier            = Modifier.padding(32.dp)
            ) {
                // Icono decorativo grande con fondo primary.copy(0.12f)
                Box(
                    modifier         = Modifier
                        .size(80.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            shape = MaterialTheme.shapes.extraLarge
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector      = Icons.Default.LocalFlorist,
                        contentDescription = null,
                        tint             = MaterialTheme.colorScheme.primary,
                        modifier         = Modifier.size(40.dp)
                    )
                }

                // Textos descriptivos
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text       = "Sin jardines aún",
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text  = "Crea tu primer jardín para empezar\na organizar tus enlaces.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Botón CTA — mismo estilo que AddNewGarden en GardensScreen
                Card(
                    onClick   = navigateToAddGarden,
                    shape     = MaterialTheme.shapes.large,
                    colors    = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier              = Modifier
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.07f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier         = Modifier
                                .size(36.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector      = Icons.Default.Add,
                                contentDescription = null,
                                tint             = MaterialTheme.colorScheme.onPrimary,
                                modifier         = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text       = "Crear primer jardín",
                            style      = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}