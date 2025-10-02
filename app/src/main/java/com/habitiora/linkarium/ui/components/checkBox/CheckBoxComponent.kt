package com.habitiora.linkarium.ui.components.checkBox

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

@Composable
fun CheckBoxComponent(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    iconSelected: @Composable () -> Unit,
    iconUnselected: (@Composable () -> Unit)? = null,
    shape: Shape = MaterialTheme.shapes.small,
    minSize: DpSize = DpSize(24.dp, 24.dp),
    enabled: Boolean = true
){
    Box(
        modifier = modifier
            .clip(shape)
            .sizeIn(minWidth = minSize.width, minHeight = minSize.height)
            .background(
                color = if (checked) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant,
                shape = shape
            )
            .border(
                width = 1.dp,
                color = if (checked) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.outline,
                shape = shape
            )
            .clickable(
                indication = ripple(
                    bounded = true,           // Hace que el ripple esté limitado al tamaño del elemento
                    color = MaterialTheme.colorScheme.primary // Color del ripple
                ),
                interactionSource = remember { MutableInteractionSource() },
                enabled = enabled,
                onClick = { onCheckedChange(!checked) }
            ),
        contentAlignment = Alignment.Center
    ){
        AnimatedVisibility(
            visible = checked,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            iconSelected()
        }
        if (iconUnselected != null){
            AnimatedVisibility(
                visible = !checked,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                iconUnselected()
            }
        }
    }
}