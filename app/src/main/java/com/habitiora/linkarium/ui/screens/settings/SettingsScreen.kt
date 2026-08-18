package com.habitiora.linkarium.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.habitiora.linkarium.R
import com.habitiora.linkarium.core.UriUtils.toUriSafe
import com.habitiora.linkarium.ui.navigation.Screens
import com.habitiora.linkarium.ui.utils.uirHelper.rememberUriHelper

// ─── Screen ───────────────────────────────────────────────────────────────────
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val isBiometricLockEnabled by viewModel.isBiometricLockEnabled.collectAsState()
    val uriHelper = rememberUriHelper()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {

        // ── Seguridad ────────────────────────────────────────────────────────
        item {
            SettingsGroupHeader(
                stringResource(R.string.settings_group_security),
                Icons.Outlined.Lock
            )
        }
        item {
            SettingsCard {
                SettingsItemToggle(
                    title = stringResource(R.string.biometric_lock_title),
                    subtitle = stringResource(R.string.biometric_lock_subtitle),
                    isChecked = isBiometricLockEnabled,
                    icon = Icons.Outlined.Fingerprint,
                    onToggle = { viewModel.updateBiometricLock(it) }
                )
            }
        }

        // ── Exportar / Importar ───────────────────────────────────────────────
        item { Spacer(Modifier.height(8.dp)) }
        item {
            SettingsGroupHeader(
                stringResource(R.string.settings_group_export_import),
                Icons.Outlined.SwapVert
            )
        }
        item {
            SettingsCard {
                SettingsItem(
                    title = stringResource(R.string.export_data),
                    subtitle = stringResource(R.string.export_subtitle),
                    icon = Icons.Outlined.FileDownload,
                    onClick = { viewModel.navigateTo(Screens.Export) }
                )
            }
        }

        // ── Acerca de ─────────────────────────────────────────────────────────
        item { Spacer(Modifier.height(8.dp)) }
        item {
            SettingsGroupHeader(
                title = stringResource(R.string.about),
                icon = Icons.Outlined.Info
            )
        }
        item {
            SettingsCard {
                SettingsItem(
                    title = stringResource(R.string.app_version),
                    subtitle = "1.0.0",
                    icon = Icons.Outlined.Info,
                    onClick = {}
                )
                SettingsDivider()
                SettingsItem(
                    title = stringResource(R.string.app_description),
                    icon = Icons.Outlined.Description,
                    onClick = {}
                )
            }
        }

        // ── Términos y condiciones ────────────────────────────────────────────
        item { Spacer(Modifier.height(8.dp)) }
        item {
            SettingsGroupHeader(
                title = stringResource(R.string.terms_and_conditions),
                icon = Icons.Outlined.Gavel
            )
        }
        item {
            SettingsCard {
                SettingsItem(
                    title = stringResource(R.string.terms_and_conditions),
                    icon = Icons.Outlined.Gavel,
                    onClick = {}
                )
                SettingsDivider()
                SettingsItem(
                    title = stringResource(R.string.privacy_policy),
                    icon = Icons.Outlined.Policy,
                    onClick = {}
                )
            }
        }

        // ── Contacto ─────────────────────────────────────────────────────────
        item { Spacer(Modifier.height(8.dp)) }
        item {
            SettingsGroupHeader(
                title = stringResource(R.string.contact_us),
                icon = Icons.Outlined.Email
            )
        }
        item {
            SettingsCard {
                SettingsItem(
                    title = stringResource(R.string.email),
                    subtitle = "support@veneros.dev",
                    icon = Icons.Outlined.Email,
                    onClick = {
                    /* mailto:support@veneros.dev?subject=Linkarium%20Bug%20Report */
                        val mailto = "mailto:support@veneros.dev?subject=Linkarium Bug Report".toUriSafe()
                        mailto?.let {
                            uriHelper.open(it)
                        }
                    }
                )
                SettingsDivider()
                SettingsItem(
                    title = stringResource(R.string.github),
                    subtitle = "andres2002002",
                    icon = Icons.Outlined.Code,
                    onClick = {
                        viewModel.openUri(
                            "https://github.com/andres2002002",
                            uriHelper::open
                        )
                    }
                )
            }
        }

        // ── Footer ────────────────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(32.dp))
            SettingsFooter()
            Spacer(Modifier.height(32.dp))
        }
    }
}

// ─── SettingsGroupHeader ──────────────────────────────────────────────────────
/**
 * Cabecera de grupo con degradado horizontal sutil — mismo sistema que SectionCard
 * y DialogHeader. Icono + etiqueta primary.
 */
@Composable
fun SettingsGroupHeader(
    title: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.04f),
                        Color.Transparent
                    )
                ),
                shape = MaterialTheme.shapes.medium
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

// ─── SettingsCard ─────────────────────────────────────────────────────────────
/**
 * Contenedor de grupo de items — shapes.large + surface con elevación 1dp,
 * coherente con SectionCard y GardenItem.
 */
@Composable
private fun SettingsCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth(), content = content)
    }
}

// ─── SettingsDivider ──────────────────────────────────────────────────────────
@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    )
}

// ─── SettingsItem ─────────────────────────────────────────────────────────────
/**
 * Ítem estándar con icono envuelto en contenedor primary.copy(alpha=0.12f),
 * el mismo patrón usado en GardenSelector, GardenItem y DialogHeader.
 */
@Composable
fun SettingsItem(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier
            .clip(MaterialTheme.shapes.large)
            .clickable { onClick() },
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        supportingContent = subtitle?.let {
            {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        },
        trailingContent = {
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(18.dp)
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

// ─── SettingsItemToggle ───────────────────────────────────────────────────────
@Composable
fun SettingsItemToggle(
    title: String,
    subtitle: String? = null,
    isChecked: Boolean,
    icon: ImageVector,
    onToggle: (Boolean) -> Unit
) {
    ListItem(
        modifier = Modifier.clip(MaterialTheme.shapes.large),
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        supportingContent = subtitle?.let {
            {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        },
        trailingContent = {
            Switch(
                checked = isChecked,
                onCheckedChange = onToggle
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

// ─── SettingsFooter ───────────────────────────────────────────────────────────
/**
 * Footer de créditos — mismo degradado radial del EmptyGardensMessage
 * pero más sutil, solo como halo detrás del icono.
 */
@Composable
private fun SettingsFooter() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Icono con halo sutil
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    Brush.radialGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            Color.Transparent
                        )
                    ),
                    shape = MaterialTheme.shapes.extraLarge
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.linkarium_logo_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                modifier = Modifier.size(48.dp)
            )
        }

        Text(
            text = "Linkarium",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            stringResource(R.string.footer_copyright),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}