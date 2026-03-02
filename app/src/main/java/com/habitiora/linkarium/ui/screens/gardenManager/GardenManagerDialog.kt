package com.habitiora.linkarium.ui.screens.gardenManager

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component1
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component2
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.akari.uicomponents.textFields.AkariTextField
import com.akari.uicomponents.textFields.rememberAkariTextFieldConfig
import com.habitiora.linkarium.R
import com.habitiora.linkarium.core.ProcessStatus

// Design tokens para consistencia
// ─── Design Tokens ────────────────────────────────────────────────────────────
// Hereda la misma escala de espaciado que PlantSeedTokens e ItemSeedDefaults
private object DialogTokens {
    object Spacing {
        val XS = 4.dp
        val S = 8.dp
        val M = 16.dp
        val L = 24.dp
        val XL = 32.dp
    }

    val DialogPadding = 20.dp
    val DialogMinWidth = 320.dp
    val DialogMaxWidth = 560.dp
    val DialogElevation = 6.dp

    val IconSize = 22.dp
    val HeaderIconSize = 40.dp

    /** Grosor trazo de acento lateral — igual que ExpandablePanel */
    val AccentStrokeWidth = 3.dp

    /** Duración estándar — igual que ItemSeed / PlantSeed */
    const val AnimMs = 280
}

// ─── Gradient helpers (mismo sistema que SectionCard) ─────────────────────────
@Composable
private fun dialogHeaderGradient(): Brush =
    Brush.horizontalGradient(
        listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.07f),
            Color.Transparent
        )
    )

// ─── Entry point ──────────────────────────────────────────────────────────────
@Composable
fun GardenManagerDialog(
    viewModel: GardenManagerViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    val nameTextFieldValue by viewModel.nameTextFieldValue.collectAsState()
    val descriptionTextFieldValue by viewModel.descriptionTextFieldValue.collectAsState()
    val enabledButton by viewModel.isValidGarden.collectAsState()

    var showSuccessAnimation by remember { mutableStateOf(false) }

    ContentScreen(
        nameTextFieldValue = nameTextFieldValue,
        onNameChange = viewModel::setNameTextFieldValue,
        descriptionTextFieldValue = descriptionTextFieldValue,
        onDescriptionChange = viewModel::setDescriptionTextFieldValue,
        onDismissRequest = onDismiss,
        enabledButton = enabledButton,
        onSave = { viewModel.saveGarden(); showSuccessAnimation = true },
        showSuccessAnimation = showSuccessAnimation
    )
}

// ─── ContentScreen ────────────────────────────────────────────────────────────
@Composable
private fun ContentScreen(
    nameTextFieldValue: TextFieldValue,
    onNameChange: (TextFieldValue) -> Unit = {},
    descriptionTextFieldValue: TextFieldValue,
    onDescriptionChange: (TextFieldValue) -> Unit = {},
    onDismissRequest: () -> Unit = {},
    enabledButton: Boolean,
    onSave: () -> Unit,
    showSuccessAnimation: Boolean = false
) {
    val (nameFocusRequester, descriptionFocusRequester) = remember { FocusRequester.createRefs() }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(tween(DialogTokens.AnimMs)) + scaleIn(
                initialScale = 0.92f,
                animationSpec = tween(DialogTokens.AnimMs, easing = FastOutSlowInEasing)
            ),
            exit = fadeOut(tween(200)) + scaleOut(
                targetScale = 0.92f,
                animationSpec = tween(200, easing = FastOutSlowInEasing)
            )
        ) {
            ContentDialog(
                nameTextFieldValue = nameTextFieldValue,
                nameFocusRequester = nameFocusRequester,
                onNameChange = onNameChange,
                descriptionTextFieldValue = descriptionTextFieldValue,
                descriptionFocusRequester = descriptionFocusRequester,
                onDescriptionChange = onDescriptionChange,
                enabledButton = enabledButton,
                onDismiss = onDismissRequest,
                onSave = onSave,
                showSuccessAnimation = showSuccessAnimation
            )
        }
    }
}

// ─── ContentDialog ────────────────────────────────────────────────────────────
@Composable
private fun ContentDialog(
    nameTextFieldValue: TextFieldValue,
    nameFocusRequester: FocusRequester,
    onNameChange: (TextFieldValue) -> Unit = {},
    descriptionTextFieldValue: TextFieldValue,
    descriptionFocusRequester: FocusRequester,
    onDescriptionChange: (TextFieldValue) -> Unit = {},
    enabledButton: Boolean,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    showSuccessAnimation: Boolean
) {
    Card(
        modifier = Modifier
            .padding(DialogTokens.DialogPadding)
            .sizeIn(
                minWidth = DialogTokens.DialogMinWidth,
                maxWidth = DialogTokens.DialogMaxWidth
            ),
        shape = MaterialTheme.shapes.extraLarge, // coherente con shapes.large del sistema
        elevation = CardDefaults.cardElevation(defaultElevation = DialogTokens.DialogElevation),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // ── Cabecera con degradado (igual que SectionCard) ────────────
            DialogHeader(onDismiss = onDismiss)

            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // ── Cuerpo scrolleable ────────────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(DialogTokens.Spacing.M),
                verticalArrangement = Arrangement.spacedBy(DialogTokens.Spacing.M)
            ) {
                InfoCard()

                NameSection(
                    nameTextFieldValue = nameTextFieldValue,
                    focusRequester = nameFocusRequester,
                    onNameChange = onNameChange
                )

                DescriptionSection(
                    descriptionTextFieldValue = descriptionTextFieldValue,
                    focusRequester = descriptionFocusRequester,
                    onDescriptionChange = onDescriptionChange
                )

                AnimatedVisibility(visible = nameTextFieldValue.text.isNotEmpty()) {
                    CharacterCounter(
                        current = nameTextFieldValue.text.length,
                        max = 50,
                        label = stringResource(R.string.name)
                    )
                }
            }

            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // ── Acciones ──────────────────────────────────────────────────
            Column(
                modifier = Modifier.padding(
                    horizontal = DialogTokens.Spacing.M,
                    vertical = DialogTokens.Spacing.M
                )
            ) {
                ActionButtons(
                    enabled = enabledButton,
                    onSave = onSave,
                    onCancel = onDismiss,
                    showSuccessAnimation = showSuccessAnimation
                )
            }
        }
    }
}

// ─── DialogHeader ─────────────────────────────────────────────────────────────
/**
 * Cabecera con el mismo degradado horizontal de SectionCard.
 * Icono envuelto en contenedor con fondo primary.copy(alpha=0.12f),
 * igual que el GardenSelector en PlantSeedScreen.
 */
@Composable
private fun DialogHeader(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(dialogHeaderGradient())
            .padding(
                start = DialogTokens.Spacing.M,
                end = DialogTokens.Spacing.XS,
                top = DialogTokens.Spacing.M,
                bottom = DialogTokens.Spacing.M
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(DialogTokens.Spacing.M),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono decorativo — mismo patrón que GardenSelector
                Box(
                    modifier = Modifier
                        .size(DialogTokens.HeaderIconSize)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            shape = MaterialTheme.shapes.medium
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFlorist,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(DialogTokens.IconSize)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = stringResource(R.string.new_garden),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.new_garden_note),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.close),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ─── InfoCard ─────────────────────────────────────────────────────────────────
/**
 * Bloque informativo con trazo de acento lateral (mismo patrón que ExpandablePanel).
 */
@Composable
private fun InfoCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Trazo de acento lateral
            Box(
                modifier = Modifier
                    .width(DialogTokens.AccentStrokeWidth)
                    .height(72.dp)
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f))
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(DialogTokens.Spacing.M),
                horizontalArrangement = Arrangement.spacedBy(DialogTokens.Spacing.M),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(18.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(DialogTokens.Spacing.XS)) {
                    Text(
                        text = stringResource(R.string.whats_garden),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.whats_garden_answer),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// ─── NameSection ──────────────────────────────────────────────────────────────
@Composable
private fun NameSection(
    nameTextFieldValue: TextFieldValue,
    focusRequester: FocusRequester,
    onNameChange: (TextFieldValue) -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(DialogTokens.Spacing.XS)) {
        val config = rememberAkariTextFieldConfig {
            slots {
                label = { Text(stringResource(R.string.garden_name)) }
                placeholder = { Text(stringResource(R.string.garden_name_placeholder)) }
                leadingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Label,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            behavior { singleLine = true }
        }
        AkariTextField(
            modifier = Modifier.fillMaxWidth(),
            value = nameTextFieldValue,
            onValueChange = onNameChange,
            config = config,
            focusRequester = focusRequester
        )

        // Sugerencias cuando el campo está vacío
        AnimatedVisibility(
            visible = nameTextFieldValue.text.isEmpty(),
            enter = fadeIn(tween(DialogTokens.AnimMs)),
            exit = fadeOut(tween(DialogTokens.AnimMs))
        ) {
            Row(
                modifier = Modifier.padding(start = DialogTokens.Spacing.M),
                horizontalArrangement = Arrangement.spacedBy(DialogTokens.Spacing.XS),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = stringResource(R.string.garden_name_hint),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ─── DescriptionSection ───────────────────────────────────────────────────────
@Composable
private fun DescriptionSection(
    descriptionTextFieldValue: TextFieldValue,
    focusRequester: FocusRequester,
    onDescriptionChange: (TextFieldValue) -> Unit = {}
) {
    val config = rememberAkariTextFieldConfig {
        slots {
            label = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(DialogTokens.Spacing.S),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.garden_description))
                    // Chip "Opcional" — mismo estilo que el chip de tipo en ItemSeed
                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.tertiaryContainer,
                                shape = MaterialTheme.shapes.extraLarge
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.optional),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
            placeholder = { Text(stringResource(R.string.garden_description_placeholder)) }
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        behavior { maxLines = 5; minLines = 2 }
    }

    AkariTextField(
        modifier = Modifier.fillMaxWidth(),
        value = descriptionTextFieldValue,
        onValueChange = onDescriptionChange,
        config = config,
        focusRequester = focusRequester
    )
}

// ─── CharacterCounter ─────────────────────────────────────────────────────────
@Composable
private fun CharacterCounter(current: Int, max: Int, label: String) {
    val progress = (current.toFloat() / max.toFloat()).coerceIn(0f, 1f)
    val color = when {
        progress >= 0.9f -> MaterialTheme.colorScheme.error
        progress >= 0.7f -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(DialogTokens.Spacing.S),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .weight(1f)
                .height(3.dp)
                .clip(MaterialTheme.shapes.extraLarge),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Text(
            text = "$current/$max",
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

// ─── ActionButtons ────────────────────────────────────────────────────────────
@Composable
private fun ActionButtons(
    enabled: Boolean,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    showSuccessAnimation: Boolean
) {
    var isLoading by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(DialogTokens.Spacing.S)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DialogTokens.Spacing.S),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cancelar
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = onCancel,
                shape = MaterialTheme.shapes.large, // consistente con el sistema
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(DialogTokens.Spacing.XS))
                Text(text = stringResource(R.string.cancel), style = MaterialTheme.typography.labelLarge)
            }

            // Crear jardín — con estados animados
            Button(
                modifier = Modifier.weight(1f),
                onClick = { isLoading = true; onSave() },
                enabled = enabled && !isLoading,
                shape = MaterialTheme.shapes.large,
                contentPadding = PaddingValues(vertical = 12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                AnimatedContent(
                    targetState = when {
                        showSuccessAnimation -> ProcessStatus.Success(true)
                        isLoading -> ProcessStatus.Loading
                        else -> ProcessStatus.Waiting
                    },
                    transitionSpec = {
                        fadeIn(tween(DialogTokens.AnimMs)) togetherWith fadeOut(tween(DialogTokens.AnimMs))
                    },
                    label = "ButtonState"
                ) { state ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(DialogTokens.Spacing.XS),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        when (state) {
                            is ProcessStatus.Loading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Text(text = stringResource(R.string.creating), style = MaterialTheme.typography.labelLarge)
                            }

                            is ProcessStatus.Success -> {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(stringResource(R.string.creation_success), style = MaterialTheme.typography.labelLarge)
                            }

                            else -> {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(stringResource(R.string.create_garden), style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }
            }
        }

        // Aviso de validación — mismo patrón de trazo lateral que InfoCard
        AnimatedVisibility(
            visible = !enabled && !isLoading,
            enter = fadeIn(tween(DialogTokens.AnimMs)) + expandVertically(),
            exit = fadeOut(tween(DialogTokens.AnimMs)) + shrinkVertically()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium),
                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.45f)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .width(DialogTokens.AccentStrokeWidth)
                            .height(32.dp)
                            .background(MaterialTheme.colorScheme.error)
                    )
                    Row(
                        modifier = Modifier.padding(
                            horizontal = DialogTokens.Spacing.S,
                            vertical = DialogTokens.Spacing.XS
                        ),
                        horizontalArrangement = Arrangement.spacedBy(DialogTokens.Spacing.XS),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = stringResource(R.string.name_required),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}