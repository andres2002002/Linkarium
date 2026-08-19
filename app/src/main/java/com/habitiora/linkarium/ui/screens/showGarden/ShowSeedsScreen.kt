package com.habitiora.linkarium.ui.screens.showGarden

import android.content.Context
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.habitiora.linkarium.R
import com.habitiora.linkarium.domain.model.LinkSeed
import com.habitiora.linkarium.ui.components.EmptyMessage
import com.habitiora.linkarium.ui.utils.clipBoardHelper.rememberClipboardHelper
import com.habitiora.linkarium.ui.utils.localWindowSizeClass.LocalWindowSizeClass
import com.habitiora.linkarium.ui.utils.uirHelper.rememberUriHelper

@Composable
fun ShowSeedsScreen(
    modifier: Modifier = Modifier,
    seeds: LazyPagingItems<LinkSeed>,
    onEdit: (LinkSeed) -> Unit,
    onDelete: (LinkSeed) -> Unit
){
    val clipboardHelper = rememberClipboardHelper()
    val uriHelper = rememberUriHelper()
    val windowSizeClass = LocalWindowSizeClass.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val defaultCover = getLocalDrawableUri(context, R.drawable.linkarium_logo_24)

    var showSelector by remember { mutableStateOf(false) }

    // ✅ Callbacks estables — evitar recrearlos en cada recomposición
    val callbacks = remember(onEdit, onDelete) {
        ItemSeedCallbacks(
            onDoubleTap = {},
            onLongPress = { showSelector = !showSelector },
            onCheckedChange = {},
            onEdit = onEdit,
            onDelete = onDelete
        )
    }

    // ✅ Cierra el selector cuando hay un cambio de estado de carga
    LaunchedEffect(seeds.loadState.refresh) {
        if (seeds.loadState.refresh is LoadState.Loading) showSelector = false
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        when{
            seeds.loadState.refresh is LoadState.Loading && seeds.itemCount == 0 -> {
                item {
                    LoadingComponent(
                        modifier = Modifier.fillParentMaxSize()
                    )
                }
            }
            seeds.loadState.refresh is LoadState.Error -> {
                val error = (seeds.loadState.refresh as LoadState.Error).error
                item {
                    EmptyMessage(
                        modifier = Modifier.fillParentMaxSize(),
                        message = error.localizedMessage ?: stringResource(R.string.error_load)
                    )
                }
            }
            seeds.itemCount == 0 -> {
                item {
                    EmptyMessage(
                        modifier = Modifier.fillParentMaxSize(),
                        message = stringResource(R.string.no_seeds_available)
                    )
                }
            }
            else -> {
                items(
                    count = seeds.itemCount,
                    key = { index -> seeds[index]?.id ?: index }
                ) { index ->
                    val seed = seeds[index]
                    if (seed != null) {
                        ItemSeed(
                            seed = seed,
                            defaultCoverUri = defaultCover,
                            clipboardHelper = clipboardHelper,
                            urlHelper = uriHelper,
                            scope = scope,
                            callbacks = callbacks,
                            showSelector = showSelector,
                            checked = false,
                            widthSizeClass = windowSizeClass.widthSizeClass
                        )
                    }
                }

                // ✅ Estado de carga adicional (paginación incremental)
                when (seeds.loadState.append) {
                    is LoadState.Loading -> {
                        item {
                            LoadingComponent(
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    is LoadState.Error -> {
                        item {
                            Text(
                                stringResource(R.string.error_load_more),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                    }

                    else -> Unit
                }
            }
        }
    }
}

fun getLocalDrawableUri(context: Context, @DrawableRes resId: Int): Uri {
    return "android.resource://${context.packageName}/$resId".toUri()
}

@Composable
private fun LoadingComponent(
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ){
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp)
        )
    }
}