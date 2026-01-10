package com.habitiora.linkarium.ui.screens.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Policy
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.habitiora.linkarium.R
import com.habitiora.linkarium.core.exporters.ExportContent
import com.habitiora.linkarium.core.exporters.ExportFormat
import com.habitiora.linkarium.core.exporters.ExportRequest

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        viewModel.export(ExportRequest(
            ExportContent.GardenAndSeeds(listOf(1)),
            ExportFormat.Json,
            uri
        ))
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item{
            SettingsHeader("Exportar")
        }
        item {
            SettingsItem(
                title = "Exportar Test",
                icon = Icons.Outlined.Description,
                onClick = { launcher.launch("test.json") }
            )
        }
        item {
            SettingsHeader(stringResource(R.string.about))
        }

        item {
            SettingsItem(
                title = stringResource(R.string.app_version),
                subtitle = "1.0.0", // Podrías obtenerlo dinámicamente
                icon = Icons.Outlined.Info,
                onClick = { /* Opcional: mostrar changelog */ }
            )
        }

        item {
            SettingsItem(
                title = stringResource(R.string.app_description),
                subtitle = null,
                icon = Icons.Outlined.Description,
                onClick = { }
            )
        }

        item { HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp)) }

        item {
            SettingsHeader(stringResource(R.string.terms_and_conditions))
        }

        item {
            SettingsItem(
                title = stringResource(R.string.terms_and_conditions),
                icon = Icons.Outlined.Gavel,
                onClick = { /* Abrir link o pantalla */ }
            )
        }

        item {
            SettingsItem(
                title = stringResource(R.string.privacy_policy),
                icon = Icons.Outlined.Policy,
                onClick = { /* Abrir link o pantalla */ }
            )
        }

        item { HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp)) }

        item {
            SettingsHeader(stringResource(R.string.contact_us))
        }

        item {
            SettingsItem(
                title = stringResource(R.string.email),
                subtitle = "habitiora@gmail.com",
                icon = Icons.Outlined.Email,
                onClick = { /* Intent enviar email */ }
            )
        }

        item {
            SettingsItem(
                title = stringResource(R.string.github),
                subtitle = "andres2002002",
                icon = Icons.Outlined.Info, // O un icono de GitHub si tienes uno
                onClick = { /* Intent abrir navegador */ }
            )
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Linkarium",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "© 2025 Habitiora",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SettingsHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(top = 16.dp)
    )
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    onClick: () -> Unit
) {
    ListItem(
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
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        },
        modifier = Modifier.clickable { onClick() }
    )
}
