package com.habitiora.linkarium.ui.scaffold.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogApp(
    values: MessageValues,
    onDismissRequest: () -> Unit
){
    val type: DialogType = values.type
    BasicAlertDialog(
        modifier = Modifier,
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = type != DialogType.Loading,
            dismissOnClickOutside = type != DialogType.Loading,
            usePlatformDefaultWidth = type != DialogType.Loading,
        )
    ) {
        if(type == DialogType.Loading){
            Surface(
                modifier = Modifier.fillMaxSize(),
                tonalElevation = 0.dp,
                shadowElevation = 0.dp,
                color = MaterialTheme.colorScheme.surface,
            ) {
                CircularProgressIndicator()
            }
        } else {
            val iconSize: Dp = 64.dp
            val outSide: Dp = iconSize * 2/5
            val innerPadding = PaddingValues(
                start = 24.dp,
                top = 4.dp + (iconSize - outSide),
                end = 24.dp,
                bottom = 8.dp
            )
            BaseDialog(
                modifier = Modifier
                    .padding(top = outSide)
                    .sizeIn(
                        minWidth = 280.dp,
                        maxWidth = 560.dp,
                        minHeight = 160.dp,
                        maxHeight = Dp.Infinity,
                    ),
                innerPadding = innerPadding,
                icon = {
                    Icon(
                        modifier = Modifier.size(iconSize),
                        imageVector = type.imageVector,
                        contentDescription = type.contentDescription,
                        tint = type.tint
                    )
                },
                title = {
                    values.title?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                },
                content = {
                    values.message?.let {
                        Box(
                            modifier = Modifier.padding(top = 8.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier.padding(top = 8.dp).align(Alignment.End),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            values.buttons.entries.reversed().forEach { (text, onClick) ->
                                TextButton(
                                    onClick = onClick,
                                    contentPadding = PaddingValues(
                                        horizontal = 16.dp,
                                        vertical = 8.dp
                                    ),
                                ) {
                                    Text(
                                        text = text,
                                        style = MaterialTheme.typography.labelLarge,
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun BaseDialog(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues = PaddingValues(),
    icon: (@Composable () -> Unit)? = null,
    title: @Composable ColumnScope.() -> Unit,
    content: @Composable ColumnScope.() -> Unit,
    actions: (@Composable ColumnScope.() -> Unit)? = null
){
    Box(contentAlignment = Alignment.TopCenter) {
        Card(
            modifier = modifier,
            shape = MaterialTheme.shapes.large,
        ) {
            Column(
                modifier = Modifier.padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                title()
                content()
                actions?.invoke(this)
            }
        }
        icon?.invoke()
    }
}
