package com.uragiristereo.mikansei.feature.image.appbars

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.uragiristereo.mikansei.core.resources.R
import com.uragiristereo.mikansei.core.ui.WindowSize
import com.uragiristereo.mikansei.core.ui.rememberWindowSize
import com.uragiristereo.mikansei.feature.image.core.ImageLoadingState

@Composable
internal fun ImageTopAppBar(
    postId: Int,
    visible: Boolean,
    loading: ImageLoadingState,
    originalImageShown: Boolean,
    onNavigateBack: () -> Unit,
    onExpandClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onShareClick: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val windowSize = rememberWindowSize()

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        TopAppBar(
            elevation = 0.dp,
            backgroundColor = Color.Transparent,
            contentColor = Color.White,
            title = {
                Text(text = "#$postId")
            },
            navigationIcon = {
                IconButton(
                    onClick = onNavigateBack,
                    content = {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_back),
                            contentDescription = null,
                        )
                    },
                )
            },
            actions = {
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.displayCutoutPadding(),
                ) {
                    if (windowSize != WindowSize.COMPACT) {
                        if (!originalImageShown) {
                            IconButton(
                                onClick = onExpandClick,
                                content = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.open_in_full),
                                        contentDescription = null,
                                    )
                                },
                            )
                        }

                        if (loading == ImageLoadingState.FROM_EXPAND) {
                            IconButton(
                                onClick = { },
                                enabled = false,
                                content = {
                                    CircularProgressIndicator(
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(24.dp),
                                    )
                                },
                            )
                        }

                        IconButton(
                            onClick = onDownloadClick,
                            content = {
                                Icon(
                                    painter = painterResource(id = R.drawable.download),
                                    contentDescription = null,
                                )
                            },
                        )

                        IconButton(
                            onClick = onShareClick,
                            content = {
                                Icon(
                                    painter = painterResource(id = R.drawable.share),
                                    contentDescription = null,
                                )
                            },
                        )
                    }

                    IconButton(
                        onClick = onMoreClick,
                        content = {
                            Icon(
                                painter = painterResource(id = R.drawable.more_vert),
                                contentDescription = null,
                            )
                        },
                    )
                }
            },
            modifier = modifier
                .background(
                    brush = Brush.verticalGradient(colors = listOf(Color.Black, Color.Transparent)),
                    alpha = 0.5f,
                )
                .systemBarsPadding(),
        )
    }
}