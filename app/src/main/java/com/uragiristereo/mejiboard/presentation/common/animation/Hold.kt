package com.uragiristereo.mejiboard.presentation.common.animation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

fun holdIn(durationMillis: Int = 300): EnterTransition = fadeIn(animationSpec = tween(durationMillis))

fun holdOut(durationMillis: Int = 300): ExitTransition = fadeOut(animationSpec = tween(durationMillis))
