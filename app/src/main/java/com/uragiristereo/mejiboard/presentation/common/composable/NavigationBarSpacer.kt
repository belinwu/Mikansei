package com.uragiristereo.mejiboard.presentation.common.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun NavigationBarSpacer(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.navigationBarsPadding())
}
