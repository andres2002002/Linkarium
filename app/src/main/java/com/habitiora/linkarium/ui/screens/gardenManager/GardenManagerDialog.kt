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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component1
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component2
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.akari.uicomponents.textFields.AkariTextField
import com.akari.uicomponents.textFields.rememberAkariTextFieldConfig

// Design tokens para consistencia

// Design tokens para consistencia
private object DialogDesignTokens {
    val SpacingXSmall = 4.dp
    val SpacingSmall = 8.dp
    val SpacingMedium = 16.dp
    val SpacingLarge = 24.dp
    val SpacingXLarge = 32.dp

    val DialogPadding = 24.dp
    val DialogMinWidth = 320.dp
    val DialogMaxWidth = 560.dp
    val DialogElevation = 6.dp

    val IconSize = 24.dp
    val HeaderIconSize = 40.dp
}

@Composable
fun GardenManagerDialog(
    viewModel: GardenManagerViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    val nameTextFieldValue by viewModel.nameTextFieldValue.collectAsState()
    val descriptionTextFieldValue by viewModel.descriptionTextFieldValue.collectAsState()
    val enabledButton by viewModel.isValidGarden.collectAsState()

    // Manejamos el cierre con éxito
    var showSuccessAnimation by remember { mutableStateOf(false) }

    ContentScreen(
        nameTextFieldValue = nameTextFieldValue,
        onNameChange = viewModel::setNameTextFieldValue,
        descriptionTextFieldValue = descriptionTextFieldValue,
        onDescriptionChange = viewModel::setDescriptionTextFieldValue,
        onDismissRequest = onDismiss,
        enabledButton = enabledButton,
        onSave = {
            viewModel.saveGarden()
            showSuccessAnimation = true
        },
        showSuccessAnimation = showSuccessAnimation
    )
}

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
            enter = fadeIn(tween(250)) + scaleIn(
                initialScale = 0.9f,
                animationSpec = tween(250, easing = FastOutSlowInEasing)
            ),
            exit = fadeOut(tween(200)) + scaleOut(
                targetScale = 0.9f,
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
            .padding(DialogDesignTokens.DialogPadding)
            .sizeIn(
                minWidth = DialogDesignTokens.DialogMinWidth,
                maxWidth = DialogDesignTokens.DialogMaxWidth
            ),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(
            defaultElevation = DialogDesignTokens.DialogElevation
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DialogDesignTokens.DialogPadding)
        ) {
            // Header mejorado con icono decorativo
            DialogHeader(onDismiss = onDismiss)

            Spacer(modifier = Modifier.height(DialogDesignTokens.SpacingLarge))

            // Contenido scrolleable
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(DialogDesignTokens.SpacingMedium)
            ) {
                // Información contextual
                InfoCard()

                Spacer(modifier = Modifier.height(DialogDesignTokens.SpacingSmall))

                // Campos de entrada
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

                // Contador de caracteres para el nombre
                AnimatedVisibility(visible = nameTextFieldValue.text.isNotEmpty()) {
                    CharacterCounter(
                        current = nameTextFieldValue.text.length,
                        max = 50,
                        label = "nombre"
                    )
                }
            }

            Spacer(modifier = Modifier.height(DialogDesignTokens.SpacingLarge))

            // Botones de acción mejorados
            ActionButtons(
                enabled = enabledButton,
                onSave = onSave,
                onCancel = onDismiss,
                showSuccessAnimation = showSuccessAnimation
            )
        }
    }
}

@Composable
private fun DialogHeader(onDismiss: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(DialogDesignTokens.SpacingSmall)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(DialogDesignTokens.SpacingMedium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono decorativo
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(DialogDesignTokens.HeaderIconSize)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFlorist,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(DialogDesignTokens.IconSize)
                        )
                    }
                }

                Column {
                    Text(
                        text = "Nuevo Jardín",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Organiza tus enlaces",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(
                onClick = onDismiss,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar diálogo"
                )
            }
        }
    }
}

@Composable
private fun InfoCard() {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DialogDesignTokens.SpacingMedium),
            horizontalArrangement = Arrangement.spacedBy(DialogDesignTokens.SpacingMedium),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(20.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(DialogDesignTokens.SpacingXSmall)
            ) {
                Text(
                    text = "¿Qué es un jardín?",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "Los jardines te permiten categorizar y organizar tus semillas de enlaces. Por ejemplo: 'Trabajo', 'Personal', 'Aprendizaje'.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
private fun NameSection(
    nameTextFieldValue: TextFieldValue,
    focusRequester: FocusRequester,
    onNameChange: (TextFieldValue) -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(DialogDesignTokens.SpacingXSmall)
    ) {
        val config = rememberAkariTextFieldConfig {
            slots {
                label = {
                    Text(
                        text = "Nombre del jardín",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = DialogDesignTokens.SpacingXSmall)
                    )
                }
                placeholder = { Text("Ej: Mis Enlaces Favoritos") }
                leadingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Label,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            behavior {
                singleLine = true
            }
        }

        AkariTextField(
            modifier = Modifier.fillMaxWidth(),
            value = nameTextFieldValue,
            onValueChange = onNameChange,
            config = config,
            focusRequester = focusRequester
        )

        // Sugerencias
        AnimatedVisibility(visible = nameTextFieldValue.text.isEmpty()) {
            Row(
                modifier = Modifier.padding(
                    start = DialogDesignTokens.SpacingMedium,
                    top = DialogDesignTokens.SpacingXSmall
                ),
                horizontalArrangement = Arrangement.spacedBy(DialogDesignTokens.SpacingSmall)
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Sugerencias: Trabajo, Personal, Recursos de Estudio",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

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
                    horizontalArrangement = Arrangement.spacedBy(DialogDesignTokens.SpacingXSmall),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = DialogDesignTokens.SpacingXSmall)
                ) {
                    Text(
                        text = "Descripción",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        Text(
                            text = "Opcional",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(
                                horizontal = DialogDesignTokens.SpacingSmall,
                                vertical = 2.dp
                            )
                        )
                    }
                }
            }
            placeholder = { Text("Añade una descripción para este jardín...") }
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        behavior {
            maxLines = 5
            minLines = 2
        }
    }

    AkariTextField(
        modifier = Modifier.fillMaxWidth(),
        value = descriptionTextFieldValue,
        onValueChange = onDescriptionChange,
        config = config,
        focusRequester = focusRequester
    )
}

@Composable
private fun CharacterCounter(
    current: Int,
    max: Int,
    label: String
) {
    val progress = (current.toFloat() / max.toFloat()).coerceIn(0f, 1f)
    val color = when {
        progress >= 0.9f -> MaterialTheme.colorScheme.error
        progress >= 0.7f -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DialogDesignTokens.SpacingMedium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .weight(1f)
                .height(4.dp),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
        Spacer(modifier = Modifier.width(DialogDesignTokens.SpacingSmall))
        Text(
            text = "$current/$max",
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
private fun ActionButtons(
    enabled: Boolean,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    showSuccessAnimation: Boolean
) {
    // Estado de carga simulado
    var isLoading by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón cancelar
        OutlinedButton(
            onClick = onCancel,
            contentPadding = PaddingValues(
                horizontal = DialogDesignTokens.SpacingLarge,
                vertical = 12.dp
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(DialogDesignTokens.SpacingSmall))
            Text(
                text = "Cancelar",
                style = MaterialTheme.typography.labelLarge
            )
        }

        // Botón crear con animación
        Button(
            onClick = {
                isLoading = true
                onSave()
            },
            enabled = enabled && !isLoading,
            contentPadding = PaddingValues(
                horizontal = DialogDesignTokens.SpacingLarge,
                vertical = 12.dp
            ),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            AnimatedContent(
                targetState = when {
                    showSuccessAnimation -> "success"
                    isLoading -> "loading"
                    else -> "idle"
                },
                transitionSpec = {
                    fadeIn(tween(200)) togetherWith fadeOut(tween(200))
                },
                label = "button_state"
            ) { state ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(DialogDesignTokens.SpacingSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (state) {
                        "loading" -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = "Creando...",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        "success" -> {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "¡Creado!",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        else -> {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Crear Jardín",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }

    // Mensaje de ayuda cuando el botón está deshabilitado
    AnimatedVisibility(
        visible = !enabled && !isLoading,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = DialogDesignTokens.SpacingSmall),
            horizontalArrangement = Arrangement.End
        ) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = DialogDesignTokens.SpacingSmall,
                        vertical = DialogDesignTokens.SpacingXSmall
                    ),
                    horizontalArrangement = Arrangement.spacedBy(DialogDesignTokens.SpacingXSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "El nombre es requerido",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}