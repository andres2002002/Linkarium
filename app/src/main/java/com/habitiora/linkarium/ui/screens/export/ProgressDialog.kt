package com.habitiora.linkarium.ui.screens.export

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.habitiora.linkarium.R
import com.habitiora.linkarium.core.exporters.ExportStatus

/**
 * Diálogo de progreso premium:
 * - Cabecera con degradado horizontal (igual que SectionCard / DialogHeader)
 * - Icono animado por estado con el contenedor primary.copy(alpha=0.12f) del sistema
 */
@Composable
fun ExportProgressDialog(
    status: ExportStatus,
    onDismiss: () -> Unit
) {
    if (status is ExportStatus.Idle) return

    AlertDialog(
        onDismissRequest = {},
        shape = MaterialTheme.shapes.extraLarge,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ExportTokens.Spacing.M)
            ) {
                // Icono de estado animado
                AnimatedContent(
                    targetState = status,
                    transitionSpec = {
                        fadeIn(tween(ExportTokens.AnimMs)) togetherWith
                                fadeOut(tween(ExportTokens.AnimMs))
                    },
                    label = "DialogIcon"
                ) { s ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = when (s) {
                                    is ExportStatus.Success -> MaterialTheme.colorScheme.primary.copy(
                                        alpha = 0.12f
                                    )

                                    is ExportStatus.Error -> MaterialTheme.colorScheme.errorContainer.copy(
                                        alpha = 0.4f
                                    )

                                    else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                },
                                shape = MaterialTheme.shapes.medium
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        when (s) {
                            is ExportStatus.InProgress -> CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )

                            is ExportStatus.Success -> Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )

                            is ExportStatus.Error -> Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )

                            else -> {}
                        }
                    }
                }
                Text(
                    text = stringResource(R.string.exporting_data),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(ExportTokens.Spacing.S)) {
                when (status) {
                    is ExportStatus.InProgress -> {
                        Text(
                            text = stringResource(
                                id = R.string.processing_status_export_in_progress,
                                status.current,
                                status.total
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        LinearProgressIndicator(
                            progress = { status.percentage },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(MaterialTheme.shapes.extraLarge)
                        )
                    }

                    is ExportStatus.Success -> {
                        // Trazo de acento lateral verde (éxito) — mismo patrón InfoCard
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Box(
                                    modifier = Modifier
                                        .width(ExportTokens.Spacing.XS / 2 + 1.dp)
                                        .height(40.dp)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                                Text(
                                    text = stringResource(R.string.export_success),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(ExportTokens.Spacing.S)
                                )
                            }
                        }
                    }

                    is ExportStatus.Error -> {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium),
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(48.dp)
                                        .background(MaterialTheme.colorScheme.error)
                                )
                                Text(
                                    text = status.exception.localizedMessage ?: stringResource(R.string.error_unknown),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(ExportTokens.Spacing.S)
                                )
                            }
                        }
                    }

                    else -> {}
                }
            }
        },
        confirmButton = {
            if (status !is ExportStatus.InProgress) {
                Button(
                    onClick = onDismiss,
                    shape = MaterialTheme.shapes.large
                ) {
                    Text(text = stringResource(R.string.close), style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    )
}
