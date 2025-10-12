package com.habitiora.linkarium.ui.components.buttons

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlainTooltipFAB(
    modifier: Modifier = Modifier,
    tooltipText: String,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
){
    val tooltipState =  rememberTooltipState(isPersistent = false)
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
        tooltip = {
            PlainTooltip {
                Text(text = tooltipText)
            }
        },
        enableUserInput = true,
        state = tooltipState
    ) {
        FloatingActionButton(
            modifier = modifier,
            onClick = onClick,
        ) {
            icon.invoke()
        }
    }
}