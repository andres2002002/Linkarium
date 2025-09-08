package com.habitiora.linkarium.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun EmptyMessage(
    modifier: Modifier = Modifier,
    message: String
){
    Box(modifier = modifier){
        Text(message)
    }
}
